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
package com.arsdigita.loader;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.SiteNode;
// import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserAuthentication;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.security.KeyStorage;
import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeExtension;
import com.arsdigita.mimetypes.TextMimeType;
import com.arsdigita.portal.Portal;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.sitemap.SiteMap;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.CSVParameterReader;
import com.arsdigita.util.parameter.EmailParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.servlet.HttpHost;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Host;
import com.arsdigita.web.Web;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
// import java.util.Locale;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
/**
 * CoreLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/08/16 $
 * @version $Id: CoreLoader.java 1841 2009-03-05 07:52:42Z terry $
 */
public class CoreLoader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(CoreLoader.class);

    private EmailParameter m_email = new EmailParameter("waf.admin.email");

    private StringParameter m_screen = new StringParameter
        ("waf.admin.name.screen", Parameter.OPTIONAL, null) {
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

    private StringParameter m_given = new StringParameter
        ("waf.admin.name.given", Parameter.REQUIRED, null);

    private StringParameter m_family = new StringParameter
        ("waf.admin.name.family", Parameter.REQUIRED, null);

    private StringParameter m_password = new StringParameter
        ("waf.admin.password", Parameter.REQUIRED, null);

    private StringParameter m_question = new StringParameter
        ("waf.admin.password.question", Parameter.REQUIRED, null);

    private StringParameter m_answer = new StringParameter
        ("waf.admin.password.answer", Parameter.REQUIRED, null);

    private StringParameter m_dispatcher = new StringParameter
        ("waf.login.dispatcher", Parameter.OPTIONAL,
         "com.arsdigita.ui.login.SubsiteDispatcher");

    private StringParameter m_resource = new StringParameter
        ("waf.mime.resource", Parameter.OPTIONAL,
         "com/arsdigita/loader/mimetypes.properties");

    public CoreLoader() {
        register(m_email);
        register(m_screen);
        register(m_given);
        register(m_family);
        register(m_password);
        register(m_question);
        register(m_answer);
        register(m_dispatcher);
        register(m_resource);

        loadInfo();
    }

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

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                KeyStorage.KERNEL_KEY_STORE.init();
                loadHost();
                loadSubsite(loadKernel());
                loadBebop();
                loadWebDev();
                loadSiteMapAdminApp(loadAdminApp());
                loadPermissionsSiteNode();
                loadPortal();
                loadMimeTypes();
            }
        }.run();
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
        // But the currently given implementation requires the the loader
        // instruction here to let the code initialization time (i.e. at each
        // startup) work properly. If left out here instantiation in
        // c.ad.search.lucene.Initializer (public final static Loader LOADER)
        // doesn't work!
        com.arsdigita.search.lucene.Initializer.LOADER.load();
        //
        // As of version 6.6.0 release 2 refactored to the new initializer system
        //--com.arsdigita.search.lucene.LegacyInitializer.LOADER.load();
    }

    private SiteNode loadKernel() {
        // Create Root Site Node

        final SiteNode rootNode = SiteNode.createSiteNode(null, null);
        createSystemAdministrator();

        // Create Package Types and Instances
        PackageType subsite = PackageType.create
            ("acs-subsite", "ACS Subsite", "ACS Subsites",
             "http://arsdigita.com/acs-subsite/");
        PackageInstance subsiteInstance = subsite.createInstance("Main Site");

        // Mount instances.
        rootNode.mountPackage(subsiteInstance);

        return rootNode;
    }

    // Ensure that at least one User with universal "admin" permission
    // exists after installation.

    private void createSystemAdministrator() {
        final String DO_NOT_CREATE = "*do not create*";

        String emailAddress = getEmail();
        String screenName = getScreen();
        String givenName = getGiven();
        String familyName = getFamily();
        String password = getPassword();
        String passwordQuestion = getQuestion();
        String passwordAnswer = getAnswer();

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

        User sa = new User();
        sa.setPrimaryEmail(new EmailAddress(emailAddress));
        if (screenName != null &&
            screenName.length() > 0) {
            sa.setScreenName(screenName);
        }
        sa.getPersonName().setGivenName(givenName);
        sa.getPersonName().setFamilyName(familyName);

        // Save the system administrator's authentication credentials.
        UserAuthentication auth = UserAuthentication.createForUser(sa);
        auth.setPassword(password);
        auth.setPasswordQuestion(passwordQuestion);
        auth.setPasswordAnswer(passwordAnswer);

        // Grant the system administrator universal "admin" permission.

        PermissionService.grantPermission
            (new UniversalPermissionDescriptor
             (PrivilegeDescriptor.ADMIN, sa));

        s_log.info("Adding administrator: \"" + givenName + " " +
                   familyName + "\" <" + emailAddress + ">");
    }

    private void loadSubsite(SiteNode rootNode) {
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
    }

    private void loadBebop() {
        // Create Package Types and Instances

        PackageType bebop = PackageType.create
            ("bebop", "Bebop", "Bebops",
             "http://arsdigita.com/bebop/");
        bebop.createInstance("Bebop Service");

    }

    private void loadWebDev() {
        // Add the package type to the database
        PackageType packType = PackageType.create
            ("webdev-support", "WebDeveloper Support", "WebDeveloper Supports",
             "http://arsdigita.com/webdev-support");

        // Add the node and the package instance on that node.
        SiteNode node = SiteNode.createSiteNode("ds");
        // Specify the URL stub for this package instance.
        node.mountPackage(packType.createInstance("webdev-support"));

        // Map the package type to a dispatcher class
        packType.setDispatcherClass("com.arsdigita.webdevsupport.Dispatcher");
    }

//     private static final String XSL_ROOT = "/packages/acs-admin/xsl/";

    private Application loadAdminApp() {
        ApplicationType adminType = ApplicationType
            .createApplicationType("admin",
                                   "CCM Admin Application", 
                                   Admin.BASE_DATA_OBJECT_TYPE);
        adminType.setDispatcherClass("com.arsdigita.ui.admin.AdminDispatcher");
        adminType.setDescription("CCM user and group administration");
        
        Application admin = Application.createApplication(adminType,
                                                          "admin",
                                                          "CCM Admin",
                                                          null);

        return admin;
    }

    private void loadSiteMapAdminApp(Application parent) {
        ApplicationType sitemapType = ApplicationType
            .createApplicationType("sitemap",
                                   "SiteMap Admin Application",
                                   SiteMap.BASE_DATA_OBJECT_TYPE);
        sitemapType.setDispatcherClass("com.arsdigita.ui.sitemap.SiteMapDispatcher");
        sitemapType.setDescription("CCM sitemap administration");


        Application sitemap = Application.createApplication(sitemapType,
                                                            "sitemap",
                                                            "CCM Admin Sitemap",
                                                            parent);
    }

    private void loadPermissionsSiteNode() {
        SiteNode permissionsNode = SiteNode.createSiteNode("permissions");

        PackageType permissionsType;
        try {
            permissionsType = PackageType.findByKey("acs-permissions");
        } catch (DataObjectNotFoundException e) {
            permissionsType = PackageType.create
            ("acs-permissions", "ACS Permissions Package",
             "ACS Permissions Packages", "http://arsdigita.com/acs-permissions");
        }

        permissionsType.setDispatcherClass
            ("com.arsdigita.ui.permissions.PermissionsDispatcher");

        // Mount instances.
        PackageInstance permissionsInstance =
            permissionsType.createInstance("ACS Permissions");
        permissionsNode.mountPackage(permissionsInstance);
    }

    private void loadPortal() {
        s_log.info("Adding package type: portal");
        PackageType packageType = PackageType.create
            ("portal", "Portal", "Portals", "http://arsdigita.com/portal");

        ResourceType type = ResourceType.createResourceType
            ("Portal", Portal.BASE_DATA_OBJECT_TYPE);
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
            CSVParameterReader loader = new CSVParameterReader
                (new InputStreamReader(is), row.getParameters());

            while (loader.next()) {
                
                row.load(loader);

                s_log.info("Adding mimetype: " + row.getType() + " (" +
                           row.getLabel() + ")");
                MimeType mime = MimeType.createMimeType
                    (row.getType(), row.getJavaClass(), row.getObjectType());
                mime.setLabel(row.getLabel());
                mime.setFileExtension(row.getDefaultExtension());
                
                if (mime instanceof TextMimeType) {
                    ((TextMimeType) mime).setAllowINSOConvert
                        ("1".equals(row.getSizerOrINSO()));
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
            try { is.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

}
