/*
 * Copyright (C) 2009 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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
package com.arsdigita.cms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Configuration parameter to configure a content section during startup.
 *
 * Configures parameter which are not persisted in the database and may be changes during each
 * startup of the system.
 *
 * @author pb
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public final class ContentSectionConfig extends AbstractConfig {

    private List m_defaultRoles;
    private List m_defaultWorkflows;

    /**
     * Private Logger instance for debugging purpose.
     */
    private static final Logger s_log = Logger.getLogger(ContentSectionConfig.class);

    /**
     * Private Object to hold one's own instance to return to users.
     */
    private static ContentSectionConfig s_config;

    /**
     * Returns the singleton configuration record for the content section environment.
     *
     * @return The <code>ContentSectionConfig</code> record; it cannot be null
     */
    public static synchronized ContentSectionConfig getInstance() {
        if (s_config == null) {
            s_config = new ContentSectionConfig();
            s_config.load();
        }

        return s_config;
    }

// /////////////////////////////////////////////////////////////////////////////
//
// Set of parameters controlling Overdue Task alerts:
// Currently there is no way to persist it nor to persist on a per section base.
// Therefore Initializer has to create overdue task alert mechanism using a
// configuration applied to every content section.
//
// /////////////////////////////////////////////////////////////////////////////
    /**
     * A list of workflow tasks, and the associated events for which alerts have to be sent.
     * Parameter name TASK_ALERTS in the old initializer system / enterprise.init Specifies when to
     * generate email alerts: by default, generate email alerts on enable, finish, and rollback
     * (happens on rejection) changes. There are four action types for each task type: enable,
     * disable, finish, and rollback. Example: (Note that the values below are based on the task
     * labels, and as such are not globalized.)
     * <pre>
     * taskAlerts = {
     *      { "Authoring",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Approval",
     *        { "enable", "finish", "rollback" }
     *      },
     *      { "Deploy",
     *        { "enable", "finish", "rollback" }
     *      }
     *  };
     * </pre>
     *
     * In the new Initializer system we use a specifically formatted String Array because we have no
     * List parameter. Format: - A string for each task to handle, possible values: Authoring,
     * Approval, Deploy - Each Task String: [taskName]:[alert_1]:...:[alert_n] The specially
     * formatted string is not handled by StringArray parameter, but forwarded untouched to the
     * initializer which has the duty to process it!
     *
     * Currently there is no way to persist taskAlerts section specific. So all sections have to
     * treated equally. Default values are provided here.
     */
    private final Parameter m_taskAlerts = new StringArrayParameter(
        "com.arsdigita.cms.section.task_alerts",
        Parameter.REQUIRED, new String[]{
        "Authoring:enable:finish:rollback",
        "Approval:enable:finish:rollback",
        "Deploy:enable:finish:rollback"}
    );

    /**
     * Should we send alerts about overdue tasks at all? Send alerts when a task is overdue (has
     * remained in the \"enabled\" state for a long time) Parameter SEND_OVERDUE_ALERTS in the old
     * initializer system, default false
     */
    private final Parameter m_sendOverdueAlerts = new BooleanParameter(
        "com.arsdigita.cms.section.send_overdue_alerts",
        Parameter.REQUIRED,
        false);

    /**
     * The time between when a task is enabled (i.e. it is made available for completion) and when
     * it is considered overdue (in HOURS).
     */
    // XXX Once the Duration of a Task can actually be maintained (in the UI,
    // or initialization parameters), we should use the value in the DB, and
    // get rid of this
    // Parameter name TASK_DURATION in the old initializer system.
    // Description: How long a task can remain \"enabled\" before it is
    // considered overdue (in hours)
    private final Parameter m_taskDuration = new IntegerParameter(
        "com.arsdigita.cms.section.task_duration",
        Parameter.REQUIRED,
        new Integer(96));

    /**
     * The time to wait between sending successive alerts on the same overdue task (in HOURS).
     * Parameter name OVERDUE_ALERT_INTERVAL in old initializer system Description: Time to wait
     * between sending overdue notifications on the same task (in hours)
     */
    private final Parameter m_alertInterval = new IntegerParameter(
        "com.arsdigita.cms.section.alert_interval",
        Parameter.REQUIRED,
        new Integer(24));

    /**
     * The maximum number of alerts to send about any one overdue task. Parameter name MAX_ALERTS in
     * old initializer system. Description: The maximum number of alerts to send that a single task
     * is overdue
     */
    private final Parameter m_maxAlerts = new IntegerParameter(
        "com.arsdigita.cms.section.max_alerts",
        Parameter.REQUIRED,
        new Integer(5));

// ///////////////////////////////////////////////////////
//
// Set of parameters which specify a new content section
// to be created during next startup of the system. If
// the section already exists (created during previous
// startups) parameters are ignored and not processed.
//
// ///////////////////////////////////////////////////////
    /**
     * Staff Group Contains roles and associated privileges. In loading step a complete default
     * configuration is persisted in database, immutable at this point. See
     * contentsection.ContentSectionSetup.registerRoles() In enterprise.init: name roles, List of
     * roles to create.
     *
     * ** Not implemented yet! ** We need a new parameter type "list" which must have
     * multidimensional capabilities.
     */
//  private final StringParameter
//          m_staffGroup = new StringParameter(
//                         "com.arsdigita.cms.loader.section_staff_group",
//                         Parameter.REQUIRED,
//                         null);
    // Viewer group, set autonomously by ContentSection.create() method. We can
    // here specify, whether the first ( probalby only) content section should
    // have a public viewer, i.e. without registration and login.
    /**
     * Whether to make content viewable to 'The Public', ie non-registered users.
     *
     * Parameter name in the old initializer code: PUBLIC. Default true.
     */
    private final BooleanParameter m_isPublic = new BooleanParameter(
        "com.arsdigita.cms.section.is_public",
        Parameter.REQUIRED,
        true);

    /**
     * List of content types to register in the given content-section.
     *
     * Example: { "com.arsdigita.cms.contenttypes.Address",
     * "com.arsdigita.cms.contenttypes.Article", "com.arsdigita.cms.contenttypes.Contact" }
     *
     * Parameter name "TYPES" in the old initializer code, empty by default in the former
     * enterprise.init file. When the list is empty and the first default content section is
     * created, all installed content types will get registered. This behaviour should not be
     * altered without very good reasons.
     */
    private final Parameter m_contentTypeList = new StringArrayParameter(
        "com.arsdigita.cms.section.ctypes_list",
        Parameter.REQUIRED,
        new String[]{});

    // Page Resolver Class, set autonomously by ContentSection.create() method.
    // Item Resolver Class, configurable.
    /**
     * Name of the item resolver class to use for the section (defaults to
     * <pre>com.arsdigita.cms.dispatcher.MultilingualItemResolver</pre>).
     *
     * Default value (site-wide) is handled via the parameter
     * <pre>com.arsdigita.cms.default_item_resolver_class</pre>. Section-specific override can be
     * added here. Only do so if you are changing from the default for a specific content section.
     * The class must implement
     * <pre>com.arsdigita.cms.dispatcher.ItemResolver</pre>.
     *
     * Parameter name ITEM_RESOLVER_CLASS in the old initializer system. Description: The
     * ItemResolver class to use for the section (defaults to MultilingualItemResolver)
     */
    private final Parameter m_itemResolverClass = new StringParameter(
        "com.arsdigita.cms.section.item_resolver_class",
        Parameter.OPTIONAL, null);
                            // , "com.arsdigita.cms.dispatcher.MultilingualItemResolver"

    // Template Resolver Class, configurable.
    /**
     * Name of the template resolver class to use for the section (defaults to
     * <pre>com.arsdigita.cms.dispatcher.DefaultTemplateResolver</pre>)
     *
     * Default value (site-wide) is handled via the parameter
     * <pre>com.arsdigita.cms.default_template_resolver_class</pre>. Section-specific override can
     * be added here. Only do so if you are changing from the default for a specific content
     * section. The class must implement
     * <pre>com.arsdigita.cms.dispatcher.TemplateResolver</pre>.
     *
     * Parameter name TEMPLATE_RESOLVER_CLASS in the old initializer system.
     */
    private final Parameter m_templateResolverClass = new StringParameter(
        "com.arsdigita.cms.section.template_resolver_class",
        Parameter.OPTIONAL,
        null);
                               // "com.arsdigita.cms.dispatcher.DefaultTemplateResolver"  );

    // XML Generator Class, set autonomously by ContentSection.create() method.
    /**
     * Determins weather to use section specific category tree(s). Defaults to false, so standard
     * navigation is used. If set to true loader loads the categories from file(s) specified in the
     * next parameter ( m_categoryFileList )
     */
    private final Parameter m_useSectionCategories = new BooleanParameter(
        "com.arsdigita.cms.section.use_section_categories",
        Parameter.REQUIRED, false);

    /**
     * XML file containing the category tree to load for this content section. Usually not loaded {
     *
     * @see m_useSectionCategories). The files listed as default values are demo material and must
     * be replaced in a production environment.
     */
    private final Parameter m_categoryFileList = new StringArrayParameter(
        "com.arsdigita.cms.section.categories_toload",
        Parameter.REQUIRED,
        new String[]{"/WEB-INF/resources/article-categories.xml",
                     "/WEB-INF/resources/navigation-categories.xml"});
    // Category tree to load
    // categories = { "/WEB-INF/resources/article-categories.xml",
    //                "/WEB-INF/resources/navigation-categories.xml" };
    //   m_conf.initParameter(CATEGORIES,
    //          "XML file containing the category tree",
    //          List.class,
    //          Collections.EMPTY_LIST);

    /**
     * Constructor, do not instantiate this class directly!
     *
     * @see ContentSection#getConfig()
     */
    public ContentSectionConfig() {

        // parameters for alerts (notifications)
        register(m_taskAlerts);
        register(m_sendOverdueAlerts);
        register(m_taskDuration);
        register(m_alertInterval);
        register(m_maxAlerts);

        // register(m_staffGroup);    NOT IMPLEMENTED yet
        register(m_isPublic);
        register(m_itemResolverClass);
        register(m_templateResolverClass);
        register(m_contentTypeList);
        register(m_useSectionCategories);
        register(m_categoryFileList);

    }

// //////////////////////////////////////////////////////////
//
// Processing of parameters which handle overdue notification
//
// //////////////////////////////////////////////////////////
    /**
     * Retrieve the list of workflow tasks and events for each tasks which should receive overdue
     * notification alerts
     */
    public final String[] getTaskAlerts() {
        return (String[]) get(m_taskAlerts);
    }

    /**
     * Retrieve whether to send overdue information for unfinished tasks.
     */
    public Boolean getSendOverdueAlerts() {
        return ((Boolean) get(m_sendOverdueAlerts)).booleanValue();
    }

    /**
     * Retrieve time between when a task is enabled and when it is considered overdue.
     */
    public Integer getTaskDuration() {
        return ((Integer) get(m_taskDuration)).intValue();
    }

    /**
     * Retrieve the time to wait between sending successive alerts on the same overdue task (in
     * HOURS).
     */
    public Integer getAlertInterval() {
        return (Integer) get(m_alertInterval);
    }

    /**
     * Retrieve the maximum number of alerts to send that a single task is overdue
     */
    public Integer getMaxAlerts() {
        return (Integer) get(m_maxAlerts);
    }

// ///////////////////////////////////////////////////////
//
// Processing of parameters which specify a new content
// section to be created during (next) startup of the
// system. The initializer has to check if it already
// exists and skip processing.
//
// ///////////////////////////////////////////////////////
    /**
     * Retrieve the STAFF GROUP, i.e. a set of roles (author, editor, publisher, manager) and
     * associated privileges for the content section to be created (m_contentSectionName).
     *
     * In loading step a complete default configuration is persisted in database, immutable at this
     * point. See contentsection.ContentSectionSetup.registerRoles() In enterprise.init: name roles,
     * List of roles to create.
     *
     * Set consists of a set of roles, for each role first field is the role name, second is the
     * description, third is a list of privileges, and (optional) fourth is the workflow task to
     * assign to.
     *
     * The set of roles constructed here is a complete set which reflects all functions of CMS and
     * forms a necessary base for operations. When the first content section is created and loaded
     * into database (during installation) this set is created, immutable by installer /
     * administrator. Additional content section may be created using a subset. For a very special
     * purpose a developer may alter the set.
     *
     * This method is typically used to construct the initial content section during installation.
     *
     * Not really implemented yet! We need a new parameter type "list" which must have
     * multidimensional capabilities.
     *
     * As a temporary measure a constant list is retrieved. Until now the list was burried in
     * enterprise.init and not user available for configuration. So it may turn into a permanent
     * solution.
     */
    /**
     * Changed: The forth field is not used anymore
     *
     */
    public List getDefaultRoles() {

        final List<String> AUTH_PRIVS = Arrays.asList(
            "new_item", "read_item", "preview_item", "edit_item",
            "categorize_items");
        final List<String> EDIT_PRIVS = Arrays.asList(
            "new_item", "read_item", "preview_item", "edit_item",
            "categorize_items", "delete_item", "approve_item");
        final List<String> PUBL_PRIVS = Arrays.asList(
            "new_item", "read_item", "preview_item", "edit_item",
            "categorize_items", "delete_item", "approve_item",
            "publish");
        final List<String> MNGR_PRIVS = Arrays.asList(
            "new_item", "read_item", "preview_item", "edit_item",
            "categorize_items", "delete_item", "approve_item",
            "publish",
            "staff_admin", "content_type_admin", "lifecycle_admin",
            "workflow_admin", "category_admin");
        final List<String> TRST_PRIVS = Arrays.asList(
            "new_item", "read_item", "preview_item", "edit_item",
            "categorize_items", "delete_item", "approve_item",
            "publish", "apply_alternate_workflows");

        m_defaultRoles = new ArrayList();

        m_defaultRoles.add(new ArrayList() {
            {
                add("Author");
                add("Creates new content");
                add(AUTH_PRIVS);
            }

        }
        );
        m_defaultRoles.add(new ArrayList() {
            {
                add("Editor");
                add("Reviews and approves the author's work");
                add(EDIT_PRIVS);
            }

        }
        );
        m_defaultRoles.add(new ArrayList() {
            {
                add("Publisher");
                add("Deploys the content to the web site");
                add(PUBL_PRIVS);
            }

        }
        );
        m_defaultRoles.add(new ArrayList() {
            {
                add("Manager");
                add("Manages the overall content section");
                add(MNGR_PRIVS);
            }

        }
        );
        m_defaultRoles.add(new ArrayList() {
            {
                add("Trusted User");
                add("A trusted user is allowed to create and publish items without review");
                add(TRST_PRIVS);
            }

        }
        );

        return (List) m_defaultRoles;

    }

    public List getDefaultWorkflows() {

        if (m_defaultWorkflows == null) {

            m_defaultWorkflows = new ArrayList();

            // Prodcution Workflow
            m_defaultWorkflows.add(
                new HashMap<String, Object>() {
                    {
                        put("name", "Redigierte Veröffentlichung");
                        put("description", "A process that involves creating and approving content.");
                        put("isDefault", "true");
                        put("tasks",
                            new ArrayList() {
                                {
                                    add(
                                        new HashMap<String, Object>() {
                                            {
                                                put("name", "Verfassen");
                                                put("description", "Create content.");
                                                put("type", "Author");
                                                put("role",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Author");
                                                        }

                                                    }
                                                );
                                            }

                                        }
                                    );
                                    add(
                                        new HashMap<String, Object>() {
                                            {
                                                put("name", "Überprüfen");
                                                put("description", "Approve content.");
                                                put("type", "Edit");
                                                put("role",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Editor");
                                                        }

                                                    }
                                                );
                                                put("dependOn",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Verfassen");
                                                        }

                                                    }
                                                );
                                            }

                                        }
                                    );
                                    add(
                                        new HashMap<String, Object>() {
                                            {
                                                put("name", "Veröffentlichen");
                                                put("description", "Deploy content.");
                                                put("type", "Deploy");
                                                put("role",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Publisher");
                                                        }

                                                    }
                                                );
                                                put("dependOn",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Überprüfen");
                                                        }

                                                    }
                                                );
                                            }

                                        }
                                    );
                                }

                            }
                        );
                    }

                }
            );

            // TrustedUser Workflow
            m_defaultWorkflows.add(
                new HashMap<String, Object>() {
                    {
                        put("name", "Direkte Veröffentlichung");
                        put("description", "Create and publish content without review");
                        put("isDefault", "false");
                        put("tasks",
                            new ArrayList() {
                                {
                                    add(
                                        new HashMap<String, Object>() {
                                            {
                                                put("name", "Verfassen");
                                                put("description", "Create content.");
                                                put("type", "Author");
                                                put("role",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Author");
                                                            add("Trusted User");
                                                        }

                                                    }
                                                );
                                            }

                                        }
                                    );
                                    add(
                                        new HashMap<String, Object>() {
                                            {
                                                put("name", "Veröffentlichen");
                                                put("description", "Deploy content.");
                                                put("type", "Deploy");
                                                put("role",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Publisher");
                                                            add("Trusted User");
                                                        }

                                                    }
                                                );
                                                put("dependOn",
                                                    new ArrayList<String>() {
                                                        {
                                                            add("Verfassen");
                                                        }

                                                    }
                                                );
                                            }

                                        }
                                    );
                                }

                            }
                        );
                    }

                }
            );
            /*        
             // Addiditonal Workflows
             m_defaultWorkflows.add(
             new HashMap<String, Object>() {{ put("name", "");
             put("description", "");
             put("tasks", 
             new ArrayList() {{ add(
             new HashMap<String, Object>() {{ put("name", "");
             put("description", "");
             put("type", "");
             put("role",  
             new ArrayList<String>() {{ add("");
             }}
             );
             put("dependOn", 
             new ArrayList<String>() {{ add("");
             }}
             );
             }}
             );
             }}
             );
             }}
             );
             */
        }

        return m_defaultWorkflows;
    }

    /**
     * Retrieve whether the content-section is publicly viewable (i.e. without registration and
     * login)
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
     * Retrieve whether to use section specific categories. If true they are loaded using the next
     * parameters file list {
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
     * Retrieve the list of files containing categories to load. In old Initialiser: Parameter name:
     * CATEGORIES Deskr. "XML file containing the category tree"
     */
    public List getCategoryFileList() {
        String[] catFiles = (String[]) get(m_categoryFileList);
        return Arrays.asList(catFiles);
    }

    /**
     * Retrieve the
     */
    public List getContentSectionsContentTypes() {
        String[] taskAlerts = (String[]) get(m_contentTypeList);
        return Arrays.asList(taskAlerts);
    }

}
