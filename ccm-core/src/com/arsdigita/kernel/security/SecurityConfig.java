/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.kernel.security;

import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A record containing server-session scoped security configuration properties.
 *
 * Accessors of this class may return null. Developers should take care to trap
 * null return values in their code.
 *
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 * @version $Id: SecurityConfig.java 1471 2007-03-12 11:27:55Z chrisgilbert23 $
 */
public class SecurityConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(SecurityConfig.class);
    private static SecurityConfig s_config = null;
    private static String s_systemAdministratorEmailAddress = null;
    /**
     * Size of secret key in bytes. *
     */
    public static int SECRET_KEY_BYTES = 16;
    /**
     * The class name of the SecurityHelper implementation. Must implement
     * SecurityHelper interface
     */
    private final Parameter m_securityHelperClass = new SpecificClassParameter(
        "waf.security_helper_class", Parameter.REQUIRED,
        com.arsdigita.kernel.security.DefaultSecurityHelper.class,
        com.arsdigita.kernel.security.SecurityHelper.class);
//  /** This parameter is obsolete.                                           */
//  private final Parameter m_sessionTrackingMethod  = new StringParameter
//      ("waf.session_tracking_method", Parameter.REQUIRED, "cookie");
    /**
     * List of extensions excluded from authentication cookies. Authentication
     * is checked for all requests, but requests with one of these extensions
     * will never cause a new cookie to be set. Include a leading dot for each
     * extension.
     */
    private final Parameter m_excludedExtensions = new StringArrayParameter(
        "waf.excluded_extensions", Parameter.REQUIRED,
        new String[]{".jpg", ".gif", ".png", ".pdf"});
// /////////////////////////////////////////////////////////////////////////////
// This section completely moved to com.arsdigita.ui.UIConfig.
// Configuration is not an Initializer task.
// Retained here during transition, should be removed when completed (2011-02)
// /////////////////////////////////////////////////////////////////////////////
//  /** Key for the root page of the site.                                    */
//  private final Parameter m_rootPage       = new StringParameter
//      ("waf.pagemap.root", Parameter.REQUIRED, "register/");
//  /** Key for the login page.                                               */
//  private final Parameter m_loginPage      = new StringParameter
//      ("waf.pagemap.login", Parameter.REQUIRED, "register/");
//  /** Key for the new user page.                                            */
//  private final Parameter m_newUserPage    = new StringParameter
//      ("waf.pagemap.newuser", Parameter.REQUIRED, "register/new-user");
//  /** Key for the logout page.                                              */
//  private final Parameter m_logoutPage     = new StringParameter
//      ("waf.pagemap.logout", Parameter.REQUIRED, "register/logout");
//  /** Key for the explain-cookies page.                                     */
//  private final Parameter m_cookiesPage    = new StringParameter
//      ("waf.pagemap.cookies", Parameter.REQUIRED,
//                              "register/explain-persistent-cookies");
//  /** Key for the change-password page. **/
//  private final Parameter m_changePage     = new StringParameter
//      ("waf.pagemap.change", Parameter.REQUIRED, "register/change-password");
//  /** Key for the recover-password page. **/
//  private final Parameter m_recoverPage    = new StringParameter
//      ("waf.pagemap.recover", Parameter.REQUIRED, "register/recover-password");
//  /** Key for the login-expired page.                                       */
//  private final Parameter m_expiredPage    = new StringParameter
//      ("waf.pagemap.expired", Parameter.REQUIRED, "register/login-expired");
//  private final Parameter m_workspacePage  = new StringParameter
//      ("waf.pagemap.workspace", Parameter.REQUIRED, "pvt/");
//  private final Parameter m_loginRedirectPage  = new StringParameter
//      ("waf.pagemap.login_redirect", Parameter.REQUIRED, "pvt/");
//  private final Parameter m_permissionPage = new StringParameter
//      ("waf.pagemap.permission", Parameter.REQUIRED, "permissions/");
//  private final Parameter m_permSinglePage = new StringParameter
//      ("waf.pagemap.perm_single", Parameter.REQUIRED, "permissions/one");
//  ////////////////////////////////////////////////////////////////////////////
    private final Parameter m_cookieDurationMinutes = new IntegerParameter(
        "waf.pagemap.cookies_duration_minutes", Parameter.OPTIONAL, null);
    private final Parameter m_cookieDomain = new StringParameter(
        "waf.cookie_domain", Parameter.OPTIONAL, null);
    private final Parameter m_loginConfig = new StringArrayParameter(
        "waf.login_config", Parameter.REQUIRED,
        new String[]{
            "Request:com.arsdigita.kernel.security.AdminLoginModule:sufficient",
            "Request:com.arsdigita.kernel.security.RecoveryLoginModule:sufficient",
            "Request:com.arsdigita.kernel.security.CookieLoginModule:requisite",
            "Register:com.arsdigita.kernel.security.LocalLoginModule:requisite",
            "Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite",
            "Register:com.arsdigita.kernel.security.CookieLoginModule:optional",
            "RegisterSSO:com.arsdigita.kernel.security.SimpleSSOLoginModule:requisite",
            "RegisterSSO:com.arsdigita.kernel.security.CookieLoginModule:optional",
//            "RegisterSAML:com.arsdigita.kernel.security.SamlLoginModule:requisite",
//            "RegisterSAML:com.arsdigita.kernel.security.CookieLoginModule:optional"
        });
    private final Parameter m_adminEmail = new StringParameter(
        "waf.admin.contact_email", Parameter.OPTIONAL, null);
    private final Parameter m_autoRegistrationOn = new BooleanParameter(
        "waf.auto_registration_on", Parameter.REQUIRED, Boolean.TRUE);
    private final Parameter m_userBanOn
                                = new BooleanParameter("waf.user_ban_on",
                                                       Parameter.REQUIRED,
                                                       Boolean.FALSE);
    private final Parameter m_enableQuestion = new BooleanParameter(
        "waf.user_question.enable", Parameter.REQUIRED, Boolean.FALSE);

    private final Parameter m_ldapConnectionUrl = new StringParameter(
        "waf.ldap.connectionUrl", Parameter.REQUIRED, "localhost"
    );
    
    private final Parameter m_ldapUserBase = new StringParameter(
        "waf.ldap.userBase", Parameter.REQUIRED, "ou=users,dc=example,dc=org"
    );
    
    private final Parameter m_ldapUserSearch = new StringParameter(
        "waf.ldap.userSearch", Parameter.REQUIRED, "(mail=%s)"
    );
    
    private final Parameter m_enableSaml = new BooleanParameter(
        "waf.enable_saml", Parameter.REQUIRED, Boolean.FALSE);
    private final Parameter m_oneLoginSaml2Strict = new BooleanParameter(
        "waf.onelogin.saml2.strict", Parameter.REQUIRED, Boolean.TRUE);
    private final Parameter m_oneLoginSaml2Debug = new BooleanParameter(
        "waf.onelogin.saml2.debug", Parameter.REQUIRED, Boolean.FALSE);
    private final Parameter m_oneLoginSaml2SpEntityId = new StringParameter(
        "waf.onelogin.saml2.sp.entityid",
        Parameter.REQUIRED,
        "http://localhost:8080/ccm-saml/metadata");
//    private final Parameter m_oneLoginSaml2SpAssertationConsumerServiceUrl
//                                = new StringParameter(
//            "waf.onelogin.saml2.sp.assertion_consumer_service.url",
//            Parameter.REQUIRED,
//            "http://localhost:8080/ccm-saml/acs");
    private final Parameter m_oneLoginSaml2SpAssertationConsumerServiceBinding
                                = new StringParameter(
            "waf.onelogin.saml2.sp.assertion_consumer_service.binding",
            Parameter.REQUIRED,
            "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
//    private final Parameter m_oneLoginSaml2SpSingleLogoutServiceUrl
//                                = new StringParameter(
//            "waf.onelogin.saml2.sp.single_logout_service.url",
//            Parameter.REQUIRED,
//            "http://localhost:8080/ccm-saml/sls");
    private final Parameter m_oneLoginSaml2SpSingleLogoutServiceBinding
                                = new StringParameter(
            "waf.onelogin.saml2.sp.single_logout_service.binding",
            Parameter.REQUIRED,
            "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    private final Parameter m_oneLoginSaml2SpNameIdFormat = new StringParameter(
        "waf.onelogin.saml2.sp.nameidformat",
        Parameter.REQUIRED,
        "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
    private final Parameter m_oneLoginSaml2IdpEntityId = new StringParameter(
        "waf.onelogin.saml2.idp.entityid",
        Parameter.REQUIRED,
        "");
    private final Parameter m_oneLoginSaml2IdpSingleSignOnServiceUrl
                                = new StringParameter(
            "waf.onelogin.saml2.idp.single_sign_on_service.url",
            Parameter.REQUIRED,
            "");
    private final Parameter m_oneLoginSaml2IdpSingleSignOnServiceBinding
                                = new StringParameter(
            "waf.onelogin.saml2.idp.single_sign_on_service.binding",
            Parameter.REQUIRED,
            "");
    private final Parameter m_oneLoginSaml2IdpSingleLogoutServiceUrl
                                = new StringParameter(
            "waf.onelogin.saml2.idp.single_logout_service.url",
            Parameter.REQUIRED,
            "");
    private final Parameter m_oneLoginSaml2IdpSingleLogoutServiceResponseUrl
                                = new StringParameter(
            "waf.onelogin.saml2.idp.single_logout_service.response.url",
            Parameter.REQUIRED,
            "");
    private final Parameter m_oneLoginSaml2IdpSingleLogoutServiceBinding
                                = new StringParameter(
            "waf.onelogin.saml2.idp.single_logout_service.binding",
            Parameter.REQUIRED,
            "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");

    /**
     * Constructs an empty SecurityConfig object
     */
    public SecurityConfig() {

        register(m_securityHelperClass);
//      register(m_sessionTrackingMethod);
        register(m_excludedExtensions);

        register(m_cookieDomain);
        register(m_loginConfig);
        register(m_cookieDurationMinutes);
        register(m_adminEmail);
        register(m_autoRegistrationOn);
        register(m_userBanOn);
        register(m_enableQuestion);

        register(m_ldapConnectionUrl);
        register(m_ldapUserBase);
        register(m_ldapUserSearch);
        
        register(m_enableSaml);

        register(m_oneLoginSaml2Debug);
        register(m_oneLoginSaml2IdpEntityId);
        register(m_oneLoginSaml2IdpSingleLogoutServiceBinding);
        register(m_oneLoginSaml2IdpSingleLogoutServiceResponseUrl);
        register(m_oneLoginSaml2IdpSingleLogoutServiceUrl);
        register(m_oneLoginSaml2IdpSingleSignOnServiceBinding);
        register(m_oneLoginSaml2IdpSingleSignOnServiceUrl);
        register(m_oneLoginSaml2SpAssertationConsumerServiceBinding);
//        register(m_oneLoginSaml2SpAssertationConsumerServiceUrl);
        register(m_oneLoginSaml2SpEntityId);
        register(m_oneLoginSaml2SpNameIdFormat);
        register(m_oneLoginSaml2SpSingleLogoutServiceBinding);
//        register(m_oneLoginSaml2SpSingleLogoutServiceUrl);
        register(m_oneLoginSaml2Strict);

        loadInfo();
    }

    /**
     * Returns the singleton configuration record for the runtime environment.
     *
     * @return The <code>RuntimeConfig</code> record; it cannot be null
     */
    public static final synchronized SecurityConfig getConfig() {
        if (s_config == null) {
            s_config = new SecurityConfig();
            s_config.load();
        }

        return s_config;
    }

    /**
     *
     * @return
     */
    public final Class getSecurityHelperClass() {
        return (Class) get(m_securityHelperClass);
    }

//  /**
//   * Obsolete!
//   * @return
//   */
//  public final String getSessionTrackingMethod() {
//      return (String) get(m_sessionTrackingMethod);
//  }
    /**
     *
     * @return
     */
    public final List getExcludedExtensions() {
        return Arrays.asList((String[]) get(m_excludedExtensions));
    }

    public String getCookieDomain() {
        return (String) get(m_cookieDomain);
    }

    String[] getLoginConfig() {
        return (String[]) get(m_loginConfig);
    }

    Integer getCookieDurationMinutes() {
        return (Integer) get(m_cookieDurationMinutes);
    }

    boolean isUserBanOn() {
        return ((Boolean) get(m_userBanOn)).booleanValue();
    }

    public String getAdminContactEmail() {
        String email = (String) get(m_adminEmail);
        if (email == null || email.trim().length() == 0) {
            email = getSystemAdministratorEmailAddress();
        }
        return email;
    }

    public Boolean getEnableQuestion() {
        return (Boolean) get(m_enableQuestion);
    }

    private static synchronized String getSystemAdministratorEmailAddress() {
        if (s_systemAdministratorEmailAddress == null) {
            ObjectPermissionCollection perms = PermissionService.
                getGrantedUniversalPermissions();
            perms.addEqualsFilter("granteeIsUser", Boolean.TRUE);
            perms.clearOrder();
            perms.addOrder("granteeID");
            if (perms.next()) {
                s_systemAdministratorEmailAddress = perms.getGranteeEmail().
                    toString();
                perms.close();
            } else {
                // Haven't found anything.  We don't want to repeat this query
                // over and over again.
                s_systemAdministratorEmailAddress = "";
            }
        }
        return s_systemAdministratorEmailAddress;
    }

    public final boolean isAutoRegistrationOn() {
        return ((Boolean) get(m_autoRegistrationOn)).booleanValue();
    }

    public final String getLdapConnectionUrl() {
        return (String) get(m_ldapConnectionUrl);
    }
    
    public final String getLdapUserBase() {
        return (String) get(m_ldapUserBase);
    }
    
    public final String getLdapUserSearch() {
        return (String) get(m_ldapUserSearch);
    }
    
    public final boolean getEnableSaml() {
        return (Boolean) get(m_enableSaml);
    }

    public final Boolean getOneLoginSaml2Strict() {
        return (Boolean) get(m_oneLoginSaml2Strict);
    }

    public final Boolean getOneLoginSaml2Debug() {
        return (boolean) get(m_oneLoginSaml2Debug);
    }

    public final String getOneLoginSaml2SpEntityId() {
        return (String) get(m_oneLoginSaml2SpEntityId);
    }

//    public final String getOneLoginSaml2SpAssertationConsumerServiceUrl() {
//        return (String) get(m_oneLoginSaml2SpAssertationConsumerServiceUrl);
//    }

    public final String getOneLoginSaml2SpAssertationConsumerServiceBinding() {
        return (String) get(m_oneLoginSaml2SpAssertationConsumerServiceBinding);
    }

//    public final String getOneLoginSaml2SpSingleLogoutServiceUrl() {
//        return (String) get(m_oneLoginSaml2SpSingleLogoutServiceUrl);
//    }

    public final String getOneLoginSaml2SpSingleLogoutServiceBinding() {
        return (String) get(m_oneLoginSaml2SpSingleLogoutServiceBinding);
    }

    public final String getOneLoginSaml2SpNameIdFormat() {
        return (String) get(m_oneLoginSaml2SpNameIdFormat);
    }

    public final String getOneLoginSaml2IdpEntityId() {
        return (String) get(m_oneLoginSaml2IdpEntityId);
    }

    public final String getOneLoginSaml2IdpSingleSignOnServiceUrl() {
        return (String) get(m_oneLoginSaml2IdpSingleSignOnServiceUrl);
    }

    public final String getOneLoginSaml2IdpSingleSignOnServiceBinding() {
        return (String) get(m_oneLoginSaml2IdpSingleSignOnServiceBinding);
    }

    public final String getOneLoginSaml2IdpSingleLogoutServiceUrl() {
        return (String) get(m_oneLoginSaml2IdpSingleLogoutServiceUrl);
    }

    public final String getOneLoginSaml2IdpSingleLogoutServiceResponseUrl() {
        return (String) get(m_oneLoginSaml2IdpSingleLogoutServiceResponseUrl);
    }

    public final String getOneLoginSaml2IdpSingleLogoutServiceBinding() {
        return (String) get(m_oneLoginSaml2IdpSingleLogoutServiceBinding);
    }
    
    

}
