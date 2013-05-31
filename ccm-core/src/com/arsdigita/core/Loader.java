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
package com.arsdigita.core;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.Charset;
import com.arsdigita.globalization.Locale;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
// import com.arsdigita.kernel.PackageInstance;
// import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.ResourceType;
// import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.security.KeyStorage;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeExtension;
import com.arsdigita.mimetypes.TextMimeType;
import com.arsdigita.portal.Portal;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.login.Login;
import com.arsdigita.ui.permissions.Permissions;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.CSVParameterReader;
import com.arsdigita.util.parameter.EmailParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;
import com.arsdigita.webdevsupport.WebDevSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 * Core Loader executes nonrecurring at install time and loads (installs
 * and initializes) the Core packages persistently into database.
 * 
 * NOTE: Configuration parameters used at load time MUST be part of Loader 
 * class and can not delegated to a Config object (derived from AbstractConfig).
 * They will (and can) not be persisted into an registry object (file).
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 * @version $Id: CoreLoader.java 1841 2009-03-05 07:52:42Z terry $
 */
public class Loader extends PackageLoader {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(Loader.class);
// /////////////////////////////////////////////////////////////////////////////
// Parameter Section
// /////////////////////////////////////////////////////////////////////////////
    private EmailParameter m_email = new EmailParameter("waf.admin.email");
    private StringParameter m_screen = new StringParameter("waf.admin.name.screen", Parameter.OPTIONAL, null) {
        @Override
        public Object getDefaultValue() {
            String email = getEmail();
            if (email == null) {
                return null;
            } else {
                int index = email.indexOf("@");
                if (index > 0) {
                    return email.substring(0, index);
                } else {
                    return email;
                }
            }
        }

    };
    private StringParameter m_given = new StringParameter("waf.admin.name.given", Parameter.REQUIRED, null);
    private StringParameter m_family = new StringParameter("waf.admin.name.family", Parameter.REQUIRED, null);
    private StringParameter m_password = new StringParameter("waf.admin.password", Parameter.REQUIRED, null);
    private StringParameter m_question = new StringParameter("waf.admin.password.question", Parameter.REQUIRED, null);
    private StringParameter m_answer = new StringParameter("waf.admin.password.answer", Parameter.REQUIRED, null);
    private StringParameter m_dispatcher = new StringParameter("waf.login.dispatcher", Parameter.OPTIONAL,
                                                               "com.arsdigita.ui.login.DummyDispatcher");
    //  "com.arsdigita.ui.login.SubsiteDispatcher");
    private StringParameter m_resource = new StringParameter("waf.mime.resource", Parameter.OPTIONAL,
                                                             "com/arsdigita/core/mimetypes.properties");
    /**
     * Recognized character sets
     */
    // In Old Initializer: CHARSETS as List.class
    //     charsets = {"ISO-8859-1","UTF-8"};
    private final Parameter m_charsets =
                            new StringArrayParameter(
            "waf.globalization.charsets",
            Parameter.REQUIRED,
            new String[]{"ISO-8859-1", "UTF-8"});
    /**
     * Each entry in the "locales" list is a 4-tuple of the form
     *   {language, country, variant, charset}
     * The charset must be one of the values specified in the "charsets"
     * parameter above.
     *
     * This parameter is only read once in the initial loading step and stored
     * in the database (g11n_locales). Subsequent modifications will have no effect!
     *
     */
    // In OLD Initializer: LOCALES as List.class
    private final Parameter m_locales =
                            new StringArrayParameter(
            "waf.globalization.locales",
            Parameter.REQUIRED,
            new String[]{"en: : :UTF-8", "en:GB: :UTF-8", "en:US: :UTF-8", "es: : :UTF-8", "es:ES: :UTF-8",
                         "da: : :UTF-8", "da:DK: :UTF-8", "de: : :UTF-8", "de:DE: :UTF-8", "fr: : :UTF-8",
                         "fr:FR: :UTF-8", "ru: : :UTF-8"
    });

// /////////////////////////////////////////////////////////////////////////////
// Parameter Section END
// /////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor, just registers parameters.
     */
    public Loader() {

        // Register defined parameters to the context by adding 
        // the parameter to a map of parameters
        register(m_email);
        register(m_screen);
        register(m_given);
        register(m_family);
        register(m_password);
        register(m_question);
        register(m_answer);
        register(m_dispatcher);
        register(m_resource);
        register(m_charsets);
        register(m_locales);

        // Probably not used anyway, because Loader parameters are not
        // persistent! (see note above)
        loadInfo();
    }

// /////////////////////////////////////////////////////////////////////////////
// Getter Section for Parameter Values
// /////////////////////////////////////////////////////////////////////////////
    private String getEmail() {
        return ((InternetAddress) get(m_email)).toString();
    }

    private String getScreen() {
        return (String) get(m_screen);
    }

    private String getGiven() {
        return (String) get(m_given);
    }

    private String getFamily() {
        return (String) get(m_family);
    }

    private String getPassword() {
        return (String) get(m_password);
    }

    private String getQuestion() {
        return (String) get(m_question);
    }

    private String getAnswer() {
        return (String) get(m_answer);
    }

    private String getDispatcher() {
        return (String) get(m_dispatcher);
    }

    private String getResource() {
        return (String) get(m_resource);
    }

    /**
     * Retrieve systems recognized character sets.
     *
     * @return List of recognized character sets.
     */
    private List getCharsets() {
        String[] charsets = (String[]) get(m_charsets);
        return (List) Arrays.asList(charsets);
    }

    /**
     * Retrieve the list of supported locales
     *
     */
    private List getLocales() {

        /** Value of the locales parameter, a string array of
         4-tuple of locale values (see above)                             */
        String[] locales = (String[]) get(m_locales);

        if (locales != null) {
            ArrayList localeTupel = new ArrayList();
            for (int i = 0; i < locales.length; ++i) {
                String[] localeSet = StringUtils.split(locales[i], ':');
                localeTupel.add(Arrays.asList(localeSet));
            }
            return localeTupel;
        } else {

            return null;

        }
    }

// /////////////////////////////////////////////////////////////////////////////
// Getter Section for Parameter Values END
// /////////////////////////////////////////////////////////////////////////////
    public void run(final ScriptContext ctx) {
        s_log.debug("CoreLoader run method started.");
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                s_log.debug("CoreLoader: Going to init KeyStorage.");
                KeyStorage.KERNEL_KEY_STORE.init();

                s_log.debug("CoreLoader: Going to execute loadHost().");
                loadHost();

                //  s_log.debug("CoreLoader: Going to execute loadSubsite().");
                //  loadSubsite(loadKernel());

                s_log.debug("CoreLoader: Going to create System Administrator.");
                createSystemAdministrator();

                s_log.debug("CoreLoader: Going to execute loadLoginApp().");
                loadLoginApp();

                s_log.debug("CoreLoader: Going to execute loadAdminApp().");
                loadAdminApp();

                s_log.debug("CoreLoader: Going to execute loadPermissionsApp().");
                loadPermissionsApp();            // new style legacy free

                s_log.debug("CoreLoader: Going to execute loadWebDev().");
                loadWebDev();            // new style legacy free

                s_log.debug("CoreLoader: Going to execute loadPortal().");
                loadPortal();

                s_log.debug("CoreLoader: Going to execute loadMimeTypes().");
                loadMimeTypes();

                s_log.debug("CoreLoader: Going to execute loadGlobalization().");
                loadGlobalization();

            }

        }.run();
        s_log.debug("CoreLoader run method completed.");
    }

    /**
     * Subject to change.
     */
    public static void loadHost() {

        final HttpHost hhost = Web.getConfig().getHost();
        Assert.exists(hhost, HttpHost.class);

        final Host host = Host.retrieve(hhost);

        if (host == null) {
            Host.create(hhost.getName(), hhost.getPort());
        }

        // Loader for lucene search engine.
        // Used to invoke static class LoaderImpl - method load() in
        // com.arsdigita.search.lucene.IndexId. Same procedure is invoked by
        // the initializer each time the system starts. So it's redundant here.
        // Using initializer code is favourable because it may be conditionally
        // performed, depending on configuration (Lucene or Intermedia).
        // But the currently given implementation requires the loader
        // instruction here to let the code initialization time (i.e. at each
        // startup) work properly. If left out here instantiation in
        // c.ad.search.lucene.Initializer (public final static Loader LOADER)
        // doesn't work!
        com.arsdigita.search.lucene.Initializer.LOADER.load();

    }

    /**
     *   .
     *   Note: Loading of Subsite is currently required by Login
     *         module otherwise Login doesn't work!
     *
     * @param rootNode
     //   * @deprecated will be removed without replacement. Naot needed anymore
     */
    /*  private void loadSubsite(SiteNode rootNode) {
     s_log.debug("CoreLoader: Going to execute method loadSubsite().");
     String sDispatcher = "";

     PackageInstance packageInstance = rootNode.getPackageInstance();
     if (packageInstance == null) {
     throw new IllegalStateException
     ("No package instance mounted at the root node");
     }
     PackageType subsite = packageInstance.getType();

     // getType() returns a disconnected object.  To get a connected object
     // we do a findByKey(key).
     String packageKey = subsite.getKey();
     try {
     subsite = PackageType.findByKey(packageKey);
     } catch (DataObjectNotFoundException e) {
     throw new IllegalStateException
     ("Package Type with key \"" + packageKey + "\" was not found.\n");
     }

     // Set subsite dispatcher class.
     subsite.setDispatcherClass(getDispatcher());
     }  */
//  /**
//   * Create Root Site Node for loadSubsite()
//   * @return root node
//   * @deprecated will be removed without replacement. Naot needed anymore
//   */
/*  private SiteNode loadKernel() {
     // Create Root Site Node
     s_log.debug("CoreLoader: Going to execute method loadKernel().");

     final SiteNode rootNode = SiteNode.createSiteNode(null, null);

     // Create Package Types and Instances
     s_log.debug("loadKernel: creating Package Types and Instances.");
     PackageType subsite = PackageType.create
     ("acs-subsite", "ACS Subsite", "ACS Subsites",
     "http://arsdigita.com/acs-subsite/");
     PackageInstance subsiteInstance = subsite.createInstance("Main Site");

     // Mount instances.
     s_log.debug("loadKernel: mount Instances.");
     rootNode.mountPackage(subsiteInstance);

     s_log.debug("CoreLoader: Going to complete method loadKernel().");
     return rootNode;
     } */
    /**
     * Ensure that at least one User with universal "admin" permission exists
     * after installation.
     */
    private void createSystemAdministrator() {
        s_log.debug("CoreLoader: execution of method createSystemAdministrator().");
        final String DO_NOT_CREATE = "*do not create*";

        String emailAddress = getEmail();
        String screenName = getScreen();
        String givenName = getGiven();
        String familyName = getFamily();
        String password = getPassword();
        String passwordQuestion = getQuestion();
        String passwordAnswer = getAnswer();
        s_log.debug("createSystemAdministrator: EmailAddr: " + emailAddress + "\n screenName: " + screenName
                    + "\n givenName: " + givenName);

        // Allow not creating system administrator account.
        // (Specified by setting parameter
        // systemAdministratorEmailAddress = "*do not create*").
        // This enables the administrator account to be made using
        // other initializers (e.g. LDAP).

        if (emailAddress.equals(DO_NOT_CREATE)) {
            s_log.warn("WARNING: System administrator account not created "
                       + "because email set to '*do not create*'.\n This "
                       + "will cause problems *unless* account is setup "
                       + "another way (for example, by an LDAP initializer).");
            return;
        }

        // Create the system administrator user.

        s_log.debug("createSystemAdministrator(): going to create new User.");
        User sa = new User();
        sa.setPrimaryEmail(new EmailAddress(emailAddress));
        if (screenName != null && screenName.length() > 0) {
            sa.setScreenName(screenName);
        }
        sa.getPersonName().setGivenName(givenName);
        sa.getPersonName().setFamilyName(familyName);

        // Save the system administrator's authentication credentials.
        s_log.debug("createSystemAdministrator(): going to save credentials.");
        UserAuthentication auth = UserAuthentication.createForUser(sa);
        auth.setPassword(password);
        auth.setPasswordQuestion(passwordQuestion);
        auth.setPasswordAnswer(passwordAnswer);

        // Grant the system administrator universal "admin" permission.

        s_log.debug("createSystemAdministrator(): going to grant admin perms.");
        PermissionService.grantPermission(new UniversalPermissionDescriptor(PrivilegeDescriptor.ADMIN, sa));

        // Add system administrator to site-wide administrator group
        GroupCollection groupColl = Group.retrieveAll();
        // FIXME: String for Site-wide Admininistrators is hardcoded because
        //        this group in inserted via sql-command during setup
        groupColl.filter("Site-wide Administrators");
        if (groupColl.next()) {
            groupColl.getGroup().addMember(sa);
        }
        groupColl.close();

        s_log.debug("Adding administrator: \"" + givenName + " " + familyName + "\" <" + emailAddress + ">");
        s_log.debug("CoreLoader: method createSystemAdministrator() completed.");

    }

    /**
     * Setup Login application. Loads type into database and instances the
     * single default instance.
     * Has to be public access in order to enable  script Upgrade664 to use it.
     * @return
     */
    public static void loadLoginApp() {

        ApplicationType loginType =
                        new ApplicationType("login",
                                            Login.BASE_DATA_OBJECT_TYPE);
        loginType.setDescription("CCM user login application");
        loginType.setSingleton(true);
        loginType.save();


        Application login = Application.createApplication(loginType,
                                                          "register",
                                                          "CCM Login",
                                                          null);
        login.setDescription("CCM login instance");

    }

    /**
     * Setup core Admin application. Loads type into database and instances the
     * single default instance.
     * Has to be public access in order to enable  script Upgrade664 to use it.
     */
    public static void loadAdminApp() {

        ApplicationType adminType =
                        new ApplicationType("admin",
                                            Admin.BASE_DATA_OBJECT_TYPE);
        adminType.setDescription("CCM user and group administration");
        adminType.setSingleton(true);
        adminType.save();


        Application admin = Application.createApplication(adminType,
                                                          "admin",
                                                          "CCM Admin",
                                                          null);
        admin.setDescription("CCM user and group administration instance");

    }

    /**
     * Setup core Admin application. Loads type into database and instances the
     * single default instance.
     * Has to be public access in order to enable  script Upgrade664 to use it.
     */
    public static void loadPermissionsApp() {

        /* NOTE:
         * The wording in the title parameter of ApplicationType determines
         * the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an hyphen
         * and converted to lower case.
         * Example: "Permissions" will become "permissions".
         */
        ApplicationType type =
                        new ApplicationType("Permissions",
                                            Permissions.BASE_DATA_OBJECT_TYPE);
        type.setDescription("CCM permissions administration");
        type.setSingleton(true);
        type.save();

        Application app = Application.createApplication(type,
                                                        "permissions",
                                                        "CCM Permissions",
                                                        null);
        app.setDescription("CCM permissions administration instance");
        app.save();

        return;
    }

    /**
     * Loads WebDeveloperSupport as a new style, legacy free application into
     * database and instantiate the (only) application instance.
     *
     * Public static access needed by upgrade script Upgrade664
     * @return webDevType ApplicationType
     */
    public static void loadWebDev() {

        ApplicationType webDevType =
                        new ApplicationType("WebDev Support",
                                            WebDevSupport.BASE_DATA_OBJECT_TYPE);
        webDevType.setDescription("WebDeveloper Support application");
        webDevType.setSingleton(true);
        webDevType.save();

        Application webDev = Application.createApplication(webDevType,
                                                           "ds",
                                                           "WebDeveloper Support",
                                                           null);
        webDev.setDescription("The default WEB developer service instance.");
        webDev.save();
    }

    /**
     * Load core's basic portal infrastructure.
     */
    private void loadPortal() {
        s_log.info("Adding resource type: portal");
        // ResourceType manages the entries in table application_types and
        // therefore actually creates a sort of new style legacy free
        // application type
        ResourceType type = ResourceType.createResourceType("Portal", Portal.BASE_DATA_OBJECT_TYPE);
        type.setDescription("A Portal!");
    }

    /**
     * Reads supported mime types from a file and ???.
     *
     * Run once during initial load.
     */
    private void loadMimeTypes() {
        ClassLoader cload = Thread.currentThread().getContextClassLoader();
        // get filename containing supported mime types as comma separated list
        String resource = getResource();
        InputStream is = cload.getResourceAsStream(resource);
        if (is == null) {
            throw new IllegalStateException("no such resource: " + resource);
        }

        try {
            MimeTypeRow row = new MimeTypeRow();
            CSVParameterReader loader = new CSVParameterReader(new InputStreamReader(is), row.getParameters());

            while (loader.next()) {

                row.load(loader);

                s_log.info("Adding mimetype: " + row.getType() + " (" + row.getLabel() + ")");
                MimeType mime = MimeType.createMimeType(row.getType(), row.getJavaClass(), row.getObjectType());
                mime.setLabel(row.getLabel());
                mime.setFileExtension(row.getDefaultExtension());

                if (mime instanceof TextMimeType) {
                    ((TextMimeType) mime).setAllowINSOConvert("1".equals(row.getSizerOrINSO()));
                }
                if (mime instanceof ImageMimeType) {
                    ((ImageMimeType) mime).setImageSizer(row.getSizerOrINSO());
                }
                String[] extensions =
                         StringUtils.split(row.getExtensions(), ',');
                for (int i = 0; i < extensions.length; i++) {
                    MimeTypeExtension ext =
                                      MimeTypeExtension.create(extensions[i],
                                                               mime.getMimeType());
                    ext.save();
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }

    private void loadGlobalization() throws ConfigError {
        List charsets = (List) getCharsets();
        if (charsets == null) {
            throw new ConfigError("You must specify at least one charset in the m_charsets "
                                  + "parameter of the core loader parameter section. "
                                  + "UTF-8 would be a good first choice.");
        }

        Map charsetMap = new HashMap();

        for (Iterator i = charsets.iterator(); i.hasNext();) {
            String charsetName = (String) i.next();
            s_log.debug("Dealing with charset name: " + charsetName);

            // Check if this is a valid charset.  Is there a better way to do
            // this? - vadimn@redhat.com, Mon 2002-07-29 14:47:41 -0400
            try {
                new OutputStreamWriter(new ByteArrayOutputStream(), charsetName);
            } catch (UnsupportedEncodingException ex) {
                throw new ConfigError(charsetName + " is not a supported charset");
            }
            Charset charset = new Charset();
            charset.setCharset(charsetName);
            charset.save();
            charsetMap.put(charsetName, charset);
        }

        List locales = (List) getLocales();

        if (locales == null) {
            throw new ConfigError("You must specify at least one locale in the m_locales "
                                  + "parameter of core loader parameter section. "
                                  + "The \"en\" locale is probably required.");
        }

        for (Iterator i = locales.iterator(); i.hasNext();) {
            List localeData = (List) i.next();
            String language = (String) localeData.get(0);
            String country = (String) localeData.get(1);
            String variant = (String) localeData.get(2);
            String charsetName = (String) localeData.get(3);
            Locale locale = new Locale(language, country, variant);

            Charset defaultCharset = (Charset) charsetMap.get(charsetName);
            if (defaultCharset == null) {
                throw new ConfigError("You must list " + charsetName + " in the \"m_charsets\" "
                                      + "parameter before using it in the \"m_locales\" " + "\" parameter.");
            }
            locale.setDefaultCharset(defaultCharset);
            locale.save();
        }
    }

}
