/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.cms;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

//  IMPLEMENTATION NOTE
//  Class is a result of migrating the old enterprise.init based 
//  initialization process. Functionality is   basically OK so far, but
//  code urgently needs cleaning up and a check for useful bits.

/**
 * Container for various configuration parameters for ccm-cms package loader.
 *
 * The parameters are basically immutable for users and administrators and only
 * accessible to developers and require recompilation. Specifying any of these
 * parameters during installation takes no effect at all! Parameters which have
 * to be modifiable must be included in Loader class itself!
 *
 * @author pb
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: LoaderConfig.java $
 */
public final class LoaderConfig extends AbstractConfig {

    /**
     * Local logger instance fpr debug support
     */
    private static final Logger s_log = Logger.getLogger(LoaderConfig.class);
    /**
     * Private Object to hold one's own instance to return to users.
     */
    private static LoaderConfig s_conf;

    /**
     * Returns the singleton configuration record for Loader configuration.
     *
     * @return The
     * <code>ContentSectionConfig</code> record; it cannot be null
     */
    public static synchronized LoaderConfig getInstance() {
        if (s_conf == null) {
            s_conf = new LoaderConfig();
            /*
             * Parameters are not stored in registry nor modified by
             * installation specification. It is not possible to process a
             * config object at Load time! s_conf.load();
             */
        }

        return s_conf;
    }
//  /**
//   * The name of the workspace package instance, i.e. URL of the workspace,
//   * where authors, editors and publishers are working and from which they
//   * can select a content-section to create and edit documents.
//   * Usually you won't modify it!
//   *
//   * NOTE Version 6.6.1 /2010-10-27)
//   * This parameter is not used in Loader. Perhaps we will make it configurable
//   * a migrating Workspace to new application style so it is retained here
//   * for the time being.
//   */
//  private StringParameter
//          m_workspaceURL = new StringParameter(
//                               "com.arsdigita.cms.loader.workspace_url",
//                               Parameter.REQUIRED,
//                               "content-center");
//  /**
//   * XML Mapping of the content center tabs to URLs, see
//   * {@link ContentCenterDispatcher}
//   * NOTE Version 6.6.1 /2010-10-27)
//   * Currently not used in Loader because it will not be persisted. Retained
//   * here for reference in case we will add persisting while migrating to
//   * new style application.
//   */
//  private final StringParameter
//          m_contentCenterMap = new StringParameter(
//                                   "com.arsdigita.cms.loader.content_center_map",
//                                   Parameter.REQUIRED,
//                                   "/WEB-INF/resources/content-center-map.xml");
// Update master object if upgrading from old versioning
// XXX: shouldn't we just gut this section (and
// VersioningUpgrader)? It is an upgrade fix from 5.1 or
// earlier, and relying on VersionedACSObject is
// deprecated
// (pboy): Default value is false and this value didn't change for
//  a very long period. Class can be excluded from source.
//      final boolean updateMaster =
//          ((Boolean)m_conf.getParameter(UPDATE_MASTER)).booleanValue();
//      if (updateMaster) {
//          VersioningUpgrader.updateMasterObject();
//      }
// XXX: ItemDispatcher is no longer used. Is the following
// still a valid enterprise.init parameter? Do we need to
// set ContentSectionServlet.s_cacheItems instead of the
// below (which is currently always true), or does this go
// away entirely?
//  final boolean cacheItems =
//          ((Boolean)m_conf.getParameter(CACHE_ITEMS)).booleanValue();
//      s_log.debug("Set cache items to " + cacheItems);
//      ItemDispatcher.setCacheItems(cacheItems);
    /**
     * Comma separated list of XML definition files for internal content types
     * (base types).
     *
     * Each internal content type (see package com/arsdigita/cms/contenttypes)
     * requires an entry in this list in order to get CMS Loader to load it into
     * permanent storage (equivalent to the Loader of each external content type
     * package).
     *
     * Each definition file name has to be fully qualified relative to
     * application (context) root. Example: contentTypeDefinitions = {
     * "/WEB-INF/content-types/Template.xml" };
     *
     * This parameter should be altered only by developers!
     */
    private final Parameter m_ctDefFiles =
            new StringArrayParameter(
            "com.arsdigita.cms.loader.internal_cts",
            Parameter.REQUIRED,
            // Generic*.xml added by Quasi in enterprise.init for
            // new generic Basetypes in addition to article
            new String[]{"/WEB-INF/content-types/GenericAddress.xml",
                "/WEB-INF/content-types/GenericArticle.xml",
                "/WEB-INF/content-types/GenericContact.xml",
                "/WEB-INF/content-types/GenericOrganizationalUnit.xml",
                "/WEB-INF/content-types/GenericPerson.xml",
                "/WEB-INF/content-types/Template.xml"});
    // ///////////////////////////////////////////////////////////////////////
    //
    // Parameters for creating a default content section at installation time.
    // In enterprise.init used by com.arsdigita.cms.installer.SectionInitializer
    // We list all information here, the code needs to create a section.
    //
    // ///////////////////////////////////////////////////////////////////////
    // Section Name, configured by Loader parameter
    // Root Folder, set autonomously by ContentSection.create() method
    // Template Folder, set autonomously by ContentSection.create() method
    /**
     * Staff Group Contains roles and associated privileges. In loading step a
     * complete default configuration is persisted in database, immutable at
     * this point. See contentsection.ContentSectionSetup.registerRoles() In
     * enterprise.init: name roles, List of roles to create.
     *
     * Not implemented yet! We need a new parameter type "list" which must have
     * multidimensional capabilities.
     */
    /*
     * private final Parameter m_staffGroup = new StringParameter(
     * "com.arsdigita.cms.loader.section_staff_group", Parameter.REQUIRED,
     * null);
     */
    private List m_defaultRoles;
    // Viewer group, set autonomously by ContentSection.create() method. We can
    // here specify, whether the first ( probalby only) content section should
    // have a public viewer, i.e. without registration and login.
    /**
     * Whether to make content viewable to 'The Public', ie non-registered
     * users.
     *
     * Parameter name in the old initializer code: PUBLIC. Default true.
     */
    private final BooleanParameter m_isPublic = new BooleanParameter(
            "com.arsdigita.cms.loader.section_is_public",
            Parameter.REQUIRED,
            true);
    // Page Resolver Class, set autonomously by ContentSection.create() method.
    // Item Resolver Class, configurable.
    /**
     * Name of the item resolver class to use for the section (defaults to
     * <pre>com.arsdigita.cms.dispatcher.MultilingualItemResolver</pre>).
     *
     * Default value (site-wide) is handled via the parameter
     * <pre>com.arsdigita.cms.default_item_resolver_class</pre>.
     * Section-specific override can be added here. Only do so if you are
     * changing from the default for a specific content section. The class must
     * implement
     * <pre>com.arsdigita.cms.dispatcher.ItemResolver</pre>.
     *
     * Parameter name ITEM_RESOLVER_CLASS in the old initializer system.
     * Description: The ItemResolver class to use for the section (defaults to
     * MultilingualItemResolver)
     */
    private final Parameter m_itemResolverClass = new StringParameter(
            "com.arsdigita.cms.loader.item_resolver_class",
            Parameter.OPTIONAL, null);
    // , "com.arsdigita.cms.dispatcher.MultilingualItemResolver"
    // Template Resolver Class, configurable.
    /**
     * Name of the template resolver class to use for the section (defaults to
     * <pre>com.arsdigita.cms.dispatcher.DefaultTemplateResolver</pre>)
     *
     * Default value (site-wide) is handled via the parameter
     * <pre>com.arsdigita.cms.default_template_resolver_class</pre>.
     * Section-specific override can be added here. Only do so if you are
     * changing from the default for a specific content section. The class must
     * implement
     * <pre>com.arsdigita.cms.dispatcher.TemplateResolver</pre>.
     *
     * Parameter name TEMPLATE_RESOLVER_CLASS in the old initializer system.
     */
    private final Parameter m_templateResolverClass = new StringParameter(
            "com.arsdigita.cms.loader.template_resolver_class",
            Parameter.OPTIONAL,
            null);
    // "com.arsdigita.cms.dispatcher.DefaultTemplateResolver"  );
    // XML Generator Class, set autonomously by ContentSection.create() method.
    /**
     * Determins weather to use section specific category tree(s). Defaults to
     * false, so standard navigation is used. If set to true loader loads the
     * categories from file(s) specified in the next parameter (
     * m_categoryFileList )
     */
    private final Parameter m_useSectionCategories = new BooleanParameter(
            "com.arsdigita.cms.loader.use_section_categories",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * XML file containing the category tree to load for this content section.
     * Usually not loaded {
     *
     * @see m_useSectionCategories). The files listed as default values are demo
     * material and must be replaced in a production environment.
     */
    private final Parameter m_categoryFileList = new StringArrayParameter(
            "com.arsdigita.cms.loader.section_categories_toload",
            Parameter.REQUIRED,
            new String[]{"/WEB-INF/resources/article-categories.xml",
                "/WEB-INF/resources/navigation-categories.xml"});
//  private final Parameter
//          m_widgetTypes = new StringArrayParameter(
//                  "com.arsdigita.cms.loader.widget_types",
//                  Parameter.REQUIRED,
//                  new String[] {"/WEB-INF/resources/article-categories.xml",
//                                "/WEB-INF/resources/navigation-categories.xml"}  );
    /**
     * List of widgets used in applications forms. Each widget is described by
     * application indicator, widget name (singular & plural), model class name
     * and model ui class name. These are really not user or administrator
     * configurabel and therefore not implemented as ccm parameter.
     */
    private static List widgetTypes = Arrays.asList(
            Arrays.asList("forms-cms", "Checkbox group", "Checkbox groups",
            "com.arsdigita.formbuilder.PersistentCheckboxGroup",
            "com.arsdigita.formbuilder.ui.editors.CheckboxGroupEditor"),
            Arrays.asList("forms-cms", "Date field", "Date fields",
            "com.arsdigita.formbuilder.PersistentDate",
            "com.arsdigita.formbuilder.ui.editors.DateForm"),
            Arrays.asList("forms-cms", "Hidden field", "Hidden fields",
            "com.arsdigita.formbuilder.PersistentHidden",
            "com.arsdigita.formbuilder.ui.editors.HiddenForm"),
            Arrays.asList("forms-cms", "Hidden ID Generator field",
            "Hidden ID Generator fields",
            "com.arsdigita.formbuilder.HiddenIDGenerator",
            "com.arsdigita.formbuilder.ui.editors.HiddenIDGeneratorForm"),
            Arrays.asList("forms-cms", "Multiple select box",
            "Multiple select boxes",
            "com.arsdigita.formbuilder.PersistentMultipleSelect",
            "com.arsdigita.formbuilder.ui.editors.MultipleSelectEditor"),
            Arrays.asList("forms-cms", "Password field", "Password fields",
            "com.arsdigita.formbuilder.PersistentPassword",
            "com.arsdigita.formbuilder.ui.editors.PasswordForm"),
            Arrays.asList("forms-cms", "Radio group", "Radio groups",
            "com.arsdigita.formbuilder.PersistentRadioGroup",
            "com.arsdigita.formbuilder.ui.editors.RadioGroupEditor"),
            Arrays.asList("forms-cms", "Single select box",
            "Single select boxes",
            "com.arsdigita.formbuilder.PersistentSingleSelect",
            "com.arsdigita.formbuilder.ui.editors.SingleSelectEditor"),
            Arrays.asList("forms-cms", "Submit button", "Submit buttons",
            "com.arsdigita.formbuilder.PersistentSubmit",
            "com.arsdigita.formbuilder.ui.editors.SubmitForm"),
            Arrays.asList("forms-cms", "Text area", "Text areas",
            "com.arsdigita.formbuilder.PersistentTextArea",
            "com.arsdigita.formbuilder.ui.editors.TextAreaForm"),
            Arrays.asList("forms-cms", "Text field", "Text fields",
            "com.arsdigita.formbuilder.PersistentTextField",
            "com.arsdigita.formbuilder.ui.editors.TextFieldForm"),
            Arrays.asList("forms-cms", "Data Driven Select Box",
            "Data Driven Select Boxes",
            "com.arsdigita.formbuilder.DataDrivenSelect",
            "com.arsdigita.formbuilder.ui.editors.DataDrivenSelectForm"),
            Arrays.asList("forms-cms", "Text Description", "Text Descriptions",
            "com.arsdigita.formbuilder.PersistentText",
            "com.arsdigita.formbuilder.ui.editors.TextForm"),
            Arrays.asList("forms-cms", "Text Heading", "Text Headings",
            "com.arsdigita.formbuilder.PersistentHeading",
            "com.arsdigita.formbuilder.ui.editors.HeadingForm"),
            Arrays.asList("forms-cms", "Section Break", "Section Break",
            "com.arsdigita.formbuilder.PersistentHorizontalRule",
            "com.arsdigita.formbuilder.ui.editors.HorizontalRuleForm"),
            Arrays.asList("forms-cms", "User Email Field", "User Email Fields",
            "com.arsdigita.formbuilder.PersistentEmailField",
            "com.arsdigita.formbuilder.ui.editors.EmailFieldForm"));
    /**
     * List of process listeners used in applications forms. Each listener is
     * described by application indicator, process name (singular & plural),
     * action class name and action ui class name. These are really not user or
     * administrator configurabel and therefore not implemented as ccm
     * parameter.
     */
    private static List processListenerTypes = Arrays.asList(
            Arrays.asList("forms-cms", "Confirmation email",
            "Confirmation emails",
            "com.arsdigita.formbuilder.actions.ConfirmEmailListener",
            "com.arsdigita.formbuilder.ui.editors.ConfirmEmailForm"),
            Arrays.asList("forms-cms", "URL redirect", "URL redirects",
            "com.arsdigita.formbuilder.actions.ConfirmRedirectListener",
            "com.arsdigita.formbuilder.ui.editors.ConfirmRedirectForm"),
            Arrays.asList("forms-cms", "Simple email", "Simple emails",
            "com.arsdigita.formbuilder.actions.SimpleEmailListener",
            "com.arsdigita.formbuilder.ui.editors.SimpleEmailForm"),
            Arrays.asList("forms-cms", "Templated email", "Templated emails",
            "com.arsdigita.formbuilder.actions.TemplateEmailListener",
            "com.arsdigita.formbuilder.ui.editors.TemplateEmailForm"),
            Arrays.asList("forms-cms", "Remote Server POST",
            "Remote Server POSTs",
            "com.arsdigita.formbuilder.actions.RemoteServerPostListener",
            "com.arsdigita.formbuilder.ui.editors.RemoteServerPostForm"),
            Arrays.asList("forms-cms", "XML email", "XML emails",
            "com.arsdigita.formbuilder.actions.XMLEmailListener",
            "com.arsdigita.formbuilder.ui.editors.XMLEmailForm"));
    private static List dataQueries = Arrays.asList(
            Arrays.asList("forms-cms",
            "com.arsdigita.formbuilder.DataQueryUsers",
            "List of all registered users"),
            Arrays.asList("forms-cms",
            "com.arsdigita.formbuilder.DataQueryPackages",
            "List of all installed packages"));

    //  Currently not a Loader task. There is no way to persist tasks preferences
    //  on a per section base.
    /**
     * When to generate email alerts: by default, generate email alerts on
     * enable, finish, and rollback (happens on rejection) changes. There are
     * four action types for each task type: enable, disable, finish, and
     * rollback. Note that the values below are based on the task labels, and as
     * such are not globalized.
     */
    /*
     * private final Parameter m_taskAlerts = new StringArrayParameter(
     * "com.arsdigita.cms.task_alerts", Parameter.REQUIRED, new String[] {
     * "Authoring:enable:finish:rollback", "Approval:enable:finish:rollback",
     * "Deploy:enable:finish:rollback" } );
     */
    // ///////////////////////////////////////////////////////////////////////
    //
    // Parameters controlling Overdue Task alerts:
    //
    // There seems to be no functionality to store these parameters on a
    // per section base of to store them in the database at all. So it is
    // currently not a loader task and commented out here.
    // ///////////////////////////////////////////////////////////////////////
//  /**
//   * sendOverdueAlerts: Should we send alerts about overdue tasks at all?
//   */
//  private final Parameter
//          m_sendOverdueAlerts = new BooleanParameter(
//                             "com.arsdigita.cms.loader.send_overdue_alerts",
//                             Parameter.REQUIRED,
//                             new Boolean(false)  );
//  /**
//   * taskDuration: The time between when a task is enabled (it is made
//   *               available for completion) and when it is
//   *               considered overdue (in HOURS)
//   */
//  private final Parameter
//          m_taskDuration = new IntegerParameter(
//                             "com.arsdigita.cms.loader.task_duration",
//                             Parameter.REQUIRED,
//                             new Integer(96)  );
//  /**
//   * alertInterval:  The time to wait between sending successive alerts on
//   *                 the same overdue task (in HOURS)
//   */
//  private final Parameter
//          m_overdueAlertInterval = new IntegerParameter(
//                             "com.arsdigita.cms.loader.overdue_alert_interval",
//                             Parameter.REQUIRED,
//                             new Integer(24)  );
//  /**
//   * maxAlerts:    The maximum number of alerts to send about any one
//   *               overdue task
//   */
//  private final Parameter
//          m_maxAlerts = new IntegerParameter(
//                             "com.arsdigita.cms.loader.mas_alerts",
//                             Parameter.REQUIRED,
//                             new Integer(5)  );
    /**
     * Standard Constructor.
     */
    public LoaderConfig() {

//          register(m_workspaceURL);
//          register(m_contentCenterMap);
        register(m_ctDefFiles);

        // Parameters for creating a content section
        register(m_isPublic);
        register(m_itemResolverClass);
        register(m_templateResolverClass);

        register(m_useSectionCategories);
        register(m_categoryFileList);

        // (Currently not a loader task)
//          register(m_taskAlerts);
        // Parameters controlling Overdue Task alerts:
        // (Currently not a loader task)
//          register(m_sendOverdueAlerts);
//          register(m_taskDuration);
//          register(m_overdueAlertInterval);
//          register(m_maxAlerts);

        // Does not work at load time!
        // loadInfo();

    }

    // //////////////////////////
    //
    // Getter Methods
    //
    // //////////////////////////
//  Not used as long as we use old style application (seee above)
//  /**
//   * Fetch name (URL) of the workspace package instance, e.g. content-center
//   * @return (URL) of the workspace package instance
//   */
//  public String getWorkspaceURL() {
//      return (String) get(m_workspaceURL);
//  }
//  Moved to CMSConfig as long as it is not a Loader task. (see above)
//  /**
//   * Fetch the file name contaning XML Mapping of the content center tabs
//   * to URLs
//   * @return String containig file name including path component.
//   */
//  public String  getContentCenterMap() {
//      return (String) get(m_contentCenterMap);
//  }
    public List getCTDefFiles() {
        String[] ctDefFiles = (String[]) get(m_ctDefFiles);
        return Arrays.asList(ctDefFiles);
    }

    /**
     * Retrieve whether the content-section is publicly viewable (i.e. without
     * registration and login)
     */
    public Boolean isPubliclyViewable() {
        return ((Boolean) get(m_isPublic)).booleanValue();
    }

    /**
     * Retrieve the item resolver class
     */
    public String getItemResolverClass() {
        return (String) get(m_itemResolverClass);
    }

    /**
     * Retrieve the template resolver class
     */
    public String getTemplateResolverClass() {
        return (String) get(m_templateResolverClass);
    }

    /**
     * Retrieve weather to use section specific categories. If true they are
     * loaded using the next parameters file list {
     *
     * @see getUseSectionCategories()}
     *
     * Default value is false, so standard navigation is used.
     * @return
     */
    public final boolean getUseSectionCategories() {
        return ((Boolean) get(m_useSectionCategories)).booleanValue();
    }

    /**
     * Retrieve the list of files containing categories to load. In old
     * Initialiser: Parameter name: CATEGORIES Deskr. "XML file containing the
     * category tree"
     */
    public List getCategoryFileList() {
        String[] catFiles = (String[]) get(m_categoryFileList);
        return Arrays.asList(catFiles);
    }

    public List getWidgetTypes() {
        return widgetTypes;
    }

    public List getProcessListenerTypes() {
        return processListenerTypes;
    }

    public List getDataQueries() {
        return dataQueries;
    }
}
