/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.kernel.security;

import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.UserAuthentication;

import java.math.BigDecimal;

/**
 * A simple login module for LDAP.
 *
 * Currently not tested with LDAPS schema.
 *
 * The module uses the SSO login name for finding the user in the LDAP
 * repository. If no user with the an SSO name matching the provided user name
 * is found the methods of the login module will return false which means that
 * the module should be ignored.
 *
 * To use the module has to be added to the list of {@code LoginModule}s in the
 * {@link SecurityConfig}. An example configuration (line breaks for easier
 * reading, remove them for the properties file):
 *
 * <pre>
 * waf.login_config=Request:com.arsdigita.kernel.security.AdminLoginModule:sufficient,
 * Request:com.arsdigita.kernel.security.RecoveryLoginModule:sufficient,
 * Request:com.arsdigita.kernel.security.CookieLoginModule:requisite,
 * Register:com.arsdigita.kernel.security.JndiLoginModule:sufficient,
 * Register:com.arsdigita.kernel.security.LocalLoginModule:requisite,
 * Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite,
 * Register:com.arsdigita.kernel.security.CookieLoginModule:optional
 * </pre>
 *
 * Additionally three more settings have to be configured in the the security
 * configuration:
 *
 * <pre>
 * waf.ldap.connectionUrl=ldap://jp-fedora-server
 * waf.ldap.userBase=ou=users,dc=example,dc=org
 * waf.ldap.userSearch=(mail=%s)
 * </pre>
 *
 * The {@code connectionUrl} is the URL of the LDAP server to use.
 * {@code userBase} is the tree part in which the users are stored.
 * {@code userSearch} defines an LDAP filter for searching the user.
 * {@link String#format} is used to fill in the username.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class JndiLoginModule extends PasswordLoginModule implements LoginModule {

    private static final Logger LOGGER = Logger.getLogger(JndiLoginModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    private UserAuthentication userAuthentication;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void initialize(final Subject subject,
                           final CallbackHandler callbackHandler,
                           Map sharedState,
                           Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {

        try {
            userAuthentication = UserAuthentication
                .retrieveForSSOlogin(getUsername());
        } catch (DataObjectNotFoundException ex) {
            throw new FailedLoginException("User not found in LDAP.");
        }
        final boolean result = super.login();
        return result;
    }

    @Override
    public boolean commit() throws LoginException {
        LOGGER.debug("Commit");

        if (userAuthentication != null) {
            final BigDecimal userId = userAuthentication.getUser().getID();
            subject.getPrincipals().add(new PartyPrincipal(userId));
        }

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        LOGGER.debug("Aborting");
//        if (userAuthentication == null) {
//            return false;
//        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        LOGGER.debug("Logout");
//        if (userAuthentication == null) {
//            return false;
//        }
        return true;
    }

    @Override
    protected void checkPassword(final String username, final char[] password)
        throws LoginException {

        final SecurityConfig securityConfig = SecurityConfig.getConfig();
        final String connectionUrl = securityConfig.getLdapConnectionUrl();
        final String userBase = securityConfig.getLdapUserBase();
        final String userSearch = securityConfig.getLdapUserSearch();

        final Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, connectionUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "none");

        try {
            final DirContext context = new InitialDirContext(env);

            final String filter = String.format(userSearch, username);
            final SearchControls searchControls = new SearchControls();
            final NamingEnumeration<SearchResult> results = context.search(
                userBase, filter, searchControls
            );

            if (!results.hasMore()) {
                throw new FailedLoginException("Bad Username / password");
            }

            final SearchResult result = results.next();
            final String resultName = result.getName();
            final Name name;
            final NameParser parser = context.getNameParser("");
            if (result.isRelative()) {

                final Name contextName = parser.parse(
                    context.getNameInNamespace()
                );
                final Name baseName = parser.parse(userBase);

                final Name entryName = parser.parse(
                    new CompositeName(resultName).get(0)
                );

                name = contextName
                    .addAll(baseName)
                    .addAll(entryName);
            } else {
                try {
                    final URI userNameUri = new URI(resultName);
                    final String pathComponent = userNameUri.getPath();
                    if (pathComponent.length() < 1) {
                        throw new KernelLoginException(
                            "Unparsable absolute name."
                        );
                    }
                    name = parser.parse(pathComponent.substring(1));
                } catch (URISyntaxException ex) {
                    throw new KernelLoginException(ex);
                }
            }

            final String userDn = name.toString();
            context.addToEnvironment(Context.SECURITY_PRINCIPAL, userDn);
            context.addToEnvironment(Context.SECURITY_CREDENTIALS, password);

            try {
                context.getAttributes("", null);
            } catch (AuthenticationException ex) {
                LOGGER.info("LDAP login failed.");
                throw new FailedLoginException(
                    "Bad username / password for LDAP"
                );
            }
        } catch (NamingException ex) {
            throw new KernelLoginException(ex);
        }

        LOGGER.debug("Successfully checked password for LDAP.");
    }

}
