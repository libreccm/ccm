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
package com.arsdigita.cms;

/* May 2009:
 * This file serves as an information for developers how to replace
 * the URL resource: protocol extension (which is a application specific,
 * non-standard extension of the Java URL protocol) by supported,
 * standard compliant API.
 *
 * Look for:
 * // URL resource: protocol handler removal: START
 * Will be removed as soon as a stable release 6.6 is created.
 */
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.dispatcher.DefaultTemplateResolver;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.MultilingualItemResolver;
import com.arsdigita.cms.dispatcher.TemplateResolver;
import com.arsdigita.cms.lifecycle.PublishLifecycleListener;
import com.arsdigita.cms.publishToFile.PublishToFileConfig;
import com.arsdigita.cms.ui.authoring.ItemCategoryExtension;
import com.arsdigita.cms.ui.authoring.ItemCategoryForm;
import com.arsdigita.runtime.AbstractConfig;
// URL resource: protocol handler removal: START
// remove
// import com.arsdigita.util.UncheckedWrapperException;
// URL resource: protocol handler removal: END
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
// import com.arsdigita.util.parameter.ParameterError;
// URL resource: protocol handler removal: START
// new: import:
import com.arsdigita.util.parameter.ResourceParameter;
// URL resource: protocol handler removal: END
import com.arsdigita.util.parameter.SpecificClassParameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
// URL resource: protocol handler removal: START
// remove:
// import com.arsdigita.util.parameter.URLParameter;
// new: import:
import com.arsdigita.util.StringUtils;
// URL resource: protocol handler removal: END

// URL resource: protocol handler removal: START
// remove:
// import java.io.IOException;
// import java.net.MalformedURLException;
// import java.net.URL;
// URL resource: protocol handler removal: END
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null.  Developers should take care
 * to trap null return values in their code.
 *
 * @see ContentSection#getConfig()
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CMSConfig.java 2070 2010-01-28 08:47:41Z pboy $
 */
public final class CMSConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(CMSConfig.class);
    /** Private Object to hold one's own instance to return to users. */
    private static CMSConfig s_config;

    /**
     * Returns the singleton configuration record for the content section
     * environment.
     *
     * @return The <code>ContentSectionConfig</code> record; it cannot be null
     */
    public static final synchronized CMSConfig getInstance() {
        if (s_config == null) {
            s_config = new CMSConfig();
            // deprecated
            // s_config.require("ccm-core/runtime.properties");
            // use instead:
            // read values from the persistent storage
            s_config.load();
        }

        return s_config;
    }
    /**
     * Storage (map) for method getAssetStepsToSkip(ContentType type) to store
     * mapping of steps that are deemed irrelevant for the passid in type. 
     */
    private static Map s_skipAssetSteps = null;
    /**
     * Item category add form speciofies Subclass of ItemCategoryForm
     * to use for the assign categories step.
     * Used in c.ad.cms.ui.authoring.ItemCategoryStep
     */
    private final Parameter m_categoryAuthoringAddForm = new SpecificClassParameter("com.arsdigita.cms.category_authoring_add_form",
            Parameter.REQUIRED,
            ItemCategoryForm.class,
            SimpleComponent.class);
    /**
     * Path for the default item template.  Path is relative to the 
     * Template Root path.
     */
    private final Parameter m_defaultItemTemplatePath = new StringParameter("com.arsdigita.cms.default_item_template_path",
            Parameter.REQUIRED, "/default/item.jsp");
    /**
     * Path for the default folder template.  Path is relative to the
     * Template Root path.
     */
    private final Parameter m_defaultFolderTemplatePath = new StringParameter("com.arsdigita.cms.default_folder_template_path",
            Parameter.REQUIRED, "/default/folder.jsp");
    /**
     * Path for the default template folder. Path is relative to webapp root
     */
    private final Parameter m_templateRootPath = new StringParameter("com.arsdigita.cms.template_root_path",
            Parameter.REQUIRED, "/packages/content-section/templates");
    // URL resource: protocol handler removal: START
    // remove:
    // try {
    //   m_itemAdapters = new URLParameter
    //       ("com.arsdigita.cms.item_adapters",
    //        Parameter.REQUIRED,
    //        new URL("resource:WEB-INF/resources/cms-item-adapters.xml"));
    // } catch (MalformedURLException ex) {
    //     throw new UncheckedWrapperException("Cannot parse URL", ex);
    // }
    // ADD:
    /**
     * Item Adapters File, path to an XML resource containing adapter
     * specifications. Path is relative to webapp root.
     */
    private final Parameter m_itemAdapters = new ResourceParameter("com.arsdigita.cms.item_adapters",
            Parameter.REQUIRED,
            "/WEB-INF/resources/cms-item-adapters.xml");
    // URL resource: protocol handler removal: END
    /**
     * Use streamlined content creation: upon item creation,
     * automatically open authoring steps and forward to the next step
     */
    private final Parameter m_useStreamlinedCreation = new BooleanParameter("com.arsdigita.cms.use_streamlined_creation",
            Parameter.REQUIRED, new Boolean(true));
    /**
     * DHTML Editor Configuration for use in CMS module, lists the
     * config object name and Javascript source location for its definition.
     */
    private final Parameter m_dhtmlEditorConfig = new DHTMLEditorConfigParameter("com.arsdigita.cms.dhtml_editor_config",
            Parameter.REQUIRED,
            new DHTMLEditor.Config("XinhaConfig",
            "/assets/xinha/CCMcmsXinhaConfig.js"));
    //   previous parameter definition:
    // > DHTMLEditor.Config.STANDARD); <
    //   didn't work because of broken unmarshalling (cf. similiar problem
    //   with ResourceParameter and patch provided by Brad). It work for
    //   HTMLArea, because configuration was hard coded into xsl(!).
    //   Additionally, we would like to use a specific configuration for cms
    //   to include cms specific functions (like access to internal .
    //   content items for links and internal image assets, which may not
    //   be accessable by other modules which use DHTMLeditor.
    //   Would be bad style to configure a cms specific parameter in core.
    /**
     * Defines which plugins to use, e.g.TableOperations,CSS
     * Format: [string,string,string]
     */
    private final Parameter m_dhtmlEditorPlugins = new StringArrayParameter("com.arsdigita.cms.dhtml_editor_plugins",
            Parameter.OPTIONAL,
            null);
    /**
     * Prevent undesirable functions from being made available,
     * eg images should only be added through the cms methods.
     */
    private final Parameter m_dhtmlEditorHiddenButtons = new StringArrayParameter("com.arsdigita.cms.dhtml_editor_hidden_buttons",
            Parameter.OPTIONAL,
            null);
    /**
     * Hide section admin tabs from users without administrative rights.
     */
    private final Parameter m_hideAdminTabs = new BooleanParameter("com.arsdigita.cms.hide_admin_tabs",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Hide Folder Index Checkbox from folder view
     */
    private final Parameter m_hideFolderIndexCheckbox = new BooleanParameter("com.arsdigita.cms.hide_folder_index_checkbox",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Hide launch date parameter on all forms and displays where it's used.
     */
    private final Parameter m_hideLaunchDate = new BooleanParameter("com.arsdigita.cms.hide_launch_date",
            Parameter.REQUIRED, new Boolean(true));
    /**
     * Require the launch date parameter to be set by the content author.
     */
    private final Parameter m_requireLaunchDate = new BooleanParameter("com.arsdigita.cms.require_launch_date",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Hide the templates tab on the item admin page.
     */
    private final Parameter m_hideTemplatesTab = new BooleanParameter("com.arsdigita.cms.hide_templates_tab",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Hide the upload file link in the editing of a text asset.
     */
    private final Parameter m_hideTextAssetUploadFile = new BooleanParameter("com.arsdigita.cms.hide_text_asset_upload_file",
            Parameter.REQUIRED,
            new Boolean(false));
    /**
     * Hide timezone labels (if, for example, all users will be in the
     * same timezone and such information would be unnecessary)
     */
    private final Parameter m_hideTimezone = new BooleanParameter("com.arsdigita.cms.hide_timezone",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Hide User Defined Content Types UI
     */
    private final Parameter m_hideUDCTUI = new BooleanParameter("com.arsdigita.cms.hide_udct_ui",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Specifies the name of the class to use as a PublishLifecycleListener
     */
    private final Parameter m_publishLifecycleListenerClass = new StringParameter("com.arsdigita.cms.publish_lifecycle_listener_class",
            Parameter.OPTIONAL, PublishLifecycleListener.class.getName());
    /**
     * Wether the Wysiwyg editor should clear the text of MSWord tags,
     * everytime the user clicks on 'Save'
     */
    private final Parameter m_saveTextCleansWordTags = new BooleanParameter("com.arsdigita.cms.save_text_cleans_word_tags",
            Parameter.OPTIONAL, new Boolean(false));
    /**
     * Hide Additional Resource Fields on RelatedLinkPropertyForm
     */
    private final Parameter m_hideAdditionalResourceFields = new BooleanParameter("com.arsdigita.cms.contentassets.ui."
            + "RelatedLinkPropertyForm.hideAdditionalResourceFields",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Get the search indexing not to process FileAssets,
     * eg to avoid PDF slowdowns
     */
    private final Parameter m_disableFileAssetExtraction = new BooleanParameter("com.arsdigita.cms.search.disableFileAssetExtraction",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Whether an item's workflow should be deleted, once the item
     * has been (re)published
     */
    private final Parameter m_deleteWorkflowAfterPublication = new BooleanParameter("com.arsdigita.cms.delete_workflow_after_publication",
            Parameter.REQUIRED, new Boolean(true));
    /**
     * Defines the number of days ahead that are covered in the
     * 'Soon Expired' tab
     */
    private final Parameter m_soonExpiredTimespanDays = new IntegerParameter("com.arsdigita.cms.soon_expired_timespan_days",
            Parameter.REQUIRED, new Integer(14));
    /**
     * Defines the number of months ahead that are covered in the
     * 'Soon Expired' tab
     */
    private final Parameter m_soonExpiredTimespanMonths = new IntegerParameter("com.arsdigita.cms.soon_expired_timespan_months",
            Parameter.REQUIRED, new Integer(1));
    /**
     * Does a redirect to the unpublished item generate not found error?
     */
    private final Parameter m_unpublishedNotFound = new BooleanParameter("com.arsdigita.cms.unpublished_not_found",
            Parameter.REQUIRED, new Boolean(true));
    /**
     * Links created through browse interfaces should only be within the
     * same subsite
     */
    private final Parameter m_linksOnlyInSameSubsite = new BooleanParameter("com.arsdigita.cms.browse_links_in_same_subsite_only",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Item category step extension hook: Subclass of ItemCategoryExtension
     * which adds extension actions for the category authoring step
     */
    private final Parameter m_categoryAuthoringExtension = new SpecificClassParameter("com.arsdigita.cms.category_authoring_extension",
            Parameter.REQUIRED,
            ItemCategoryExtension.class,
            ItemCategoryExtension.class);
    /**
     * Link available to reset lifecycle on republish. If false don't display
     * the link otherwise display.
     */
    private final Parameter m_hideResetLifecycleLink = new BooleanParameter("com.arsdigita.cms.hide_reset_lifecycle_link",
            Parameter.OPTIONAL, new Boolean(true));
    /**
     * Whether to include INPATH operators to contains clause in intermedia search
     */
    private final Parameter m_scoreTitleAndKeywords = new BooleanParameter("com.arsdigita.cms.search.score_title_and_keywords",
            Parameter.OPTIONAL,
            Boolean.FALSE);
    /**
     * Title Weight, the relative weight given to title element within
     * cms:item when ranking search results (only used by interMedia)
     */
    private final Parameter m_titleWeight = new IntegerParameter("com.arsdigita.cms.search.intermedia.title_weight",
            Parameter.OPTIONAL,
            new Integer(1));
    /**
     * Keyword Weight, the relative weight given to the dcKeywords element
     * within dublinCore element within cms:item element when ranking
     * search results (only used by interMedia)
     */
    private final Parameter m_keywordWeight = new IntegerParameter("com.arsdigita.cms.search.intermedia.keyword_weight",
            Parameter.OPTIONAL,
            new Integer(1));
    /**
     * Asset steps to skip, specify asset steps that are not relevant for
     * specific content types.
     * Each entry in the list is a : separated pair. The first string
     * is the className for the type (refer to classname column in contenttypes
     * table eg com.arsdigita.cms.contenttypes.MultiPartArticle
     * Second string is the name of the bebop step component
     * eg com.arsdigita.cms.contenttypes.ui.ImageStep
     */
    private final Parameter m_skipAssetSteps = new StringArrayParameter("com.arsdigita.cms.skip_asset_steps",
            Parameter.OPTIONAL,
            null);
    /**
     * Mandatory Descriptions Content types may refer to this to decide
     * whether to validate against empty descriptions
     */
    private final Parameter m_mandatoryDescriptions = new BooleanParameter("com.arsdigita.cms.mandatory_descriptions",
            Parameter.OPTIONAL, new Boolean(false));
    /**
     * Delete Finished Lifecycles. Decide whether lifecycles and their phases
     * should be deleted from the system when finished.
     */
    private final Parameter m_deleteLifecycleWhenComplete = new BooleanParameter("com.arsdigita.cms.delete_lifecycle_when_complete",
            Parameter.OPTIONAL, new Boolean(false));
    /**
     * Contacts for content items. Allows you to add a Contact authoring step
     * to all items
     */
    private final Parameter m_hasContactsAuthoringStep = new BooleanParameter("com.arsdigita.cms.has_contacts_authoring_step",
            Parameter.REQUIRED, new Boolean(false));
    /**
     * Ordering for nodes in assign category tree. Decide whether entries
     * should be ordered alphabetically or according to sort key
     * (maintained in category admin tab in content centre)
     * SortKey|Alphabetical is initialized in constructor! See below.
     */
    private final Parameter m_categoryTreeOrdering = new EnumerationParameter("com.arsdigita.cms.category_tree_order",
            Parameter.OPTIONAL, Category.SORT_KEY);
    /**
     * Allow content creation in section listing. Allows you to turn off
     * the ability to create content in the section listing
     */
    private final Parameter m_allowContentCreateInSectionListing = new BooleanParameter("com.arsdigita.cms.allow_content_create_in_section_listing",
            Parameter.REQUIRED,
            new Boolean(true));
    // ///////////////////////////////////////////
    // Notification related parameters
    // ///////////////////////////////////////////
    /**
     * Delete Sent Workflow Notifications. Decide whether successfully sent
     * notifications and messages should be deleted from the system
     */
    private final Parameter m_deleteWorkflowNotificationsWhenSent = new BooleanParameter("com.arsdigita.cms.delete_workflow_notification_when_sent",
            Parameter.OPTIONAL, new Boolean(false));
    /**
     * Decide whether successfully sent notifications and messages
     * should be deleted from the system
     */
    private final Parameter m_deleteExpiryNotificationsWhenSent = new BooleanParameter("com.arsdigita.cms.delete_expiry_notification_when_sent",
            Parameter.OPTIONAL, new Boolean(false));
    /**
     * Amount of time (in hours) before the expiration of a content item
     * that users in the Alert Recipient role are alerted via email
     */
    private final Parameter m_defaultNotificationTime = new IntegerParameter("com.arsdigita.cms.default_notification_time",
            Parameter.REQUIRED, new Integer(0));
    /**
     * Wether a content item's author should be notified
     * by the item's LifecycleListener; defaults to true
     */
    private final Parameter m_notifyAuthorOnLifecycle = new BooleanParameter("com.arsdigita.cms.notify_author_on_lifecycle",
            Parameter.OPTIONAL, new Boolean(true));
    // ////////////////////////////////////////////////////
    // Content Center (Workspace) config related parameters
    // ////////////////////////////////////////////////////
    /**
     * XML Mapping of the content center tabs to URLs, see
     * {@link ContentCenterDispatcher}
     */
    private final StringParameter m_contentCenterMap = new StringParameter(
            "com.arsdigita.cms.loader.content_center_map",
            Parameter.REQUIRED,
            "/WEB-INF/resources/content-center-map.xml");
    // ///////////////////////////////////////////
    // Content Section config related parameters
    // ///////////////////////////////////////////
    private final Parameter m_defaultSection = new StringParameter("com.arsdigita.cms.default_content_section",
            Parameter.REQUIRED, "content");
    // ///////////////////////////////////////////
    // Content Section creation parameters
    // XXX these are probably temporary parameters, as the
    // item/template resolvers will be determined by the successor
    // to SectionInitializer. However, it still may be useful to
    // keep these for the default values.
    // ///////////////////////////////////////////
    private final Parameter m_defaultItemResolverClass = new SpecificClassParameter("com.arsdigita.cms.default_item_resolver_class",
            Parameter.REQUIRED,
            MultilingualItemResolver.class,
            ItemResolver.class);
    private final Parameter m_defaultTemplateResolverClass = new SpecificClassParameter("com.arsdigita.cms.default_template_resolver_class",
            Parameter.REQUIRED,
            DefaultTemplateResolver.class,
            TemplateResolver.class);
    /////////////////////////////////////////////
    // ItemSearchWidget
    /////////////////////////////////////////////
    private final Parameter m_itemSearchDefaultTab = new StringParameter("com.arsdigita.cms.item_search.default_tab",
            Parameter.REQUIRED, "browse");

    /////////////////////////////////////////////
    // FolderBrowse
    /////////////////////////////////////////////
    private final Parameter m_folderBrowseListSize = new IntegerParameter("com.arsdigita.cms.folder_browse_list_size",
            Parameter.REQUIRED, 20);


    // ///////////////////////////////////////////
    // publishToFile package related parameter
    // ///////////////////////////////////////////
    // Moved to publishToFile.PublishToFileConfig as of version 6.0.2
    // private final Parameter m_disableItemPfs;
    // private final Parameter m_publishToFileClass;
    /**
     * Constructor, but do NOT instantiate this class directly.
     *
     * @see ContentSection#getConfig()
     **/
    public CMSConfig() {

        // Initialize m_categoryTreeOrdering parameter here!
        // 2 valid values at the moment - enumeration used rather than boolean
        // in case other possible orders are deemed valid
        ((EnumerationParameter) m_categoryTreeOrdering).put("SortKey", Category.SORT_KEY);
        ((EnumerationParameter) m_categoryTreeOrdering).put("Alphabetical", Category.NAME);


        register(m_templateRootPath);
        register(m_defaultItemTemplatePath);
        register(m_defaultFolderTemplatePath);
        register(m_categoryAuthoringAddForm);
        register(m_itemAdapters);
        register(m_useStreamlinedCreation);
        register(m_dhtmlEditorConfig);
        register(m_dhtmlEditorPlugins);
        register(m_dhtmlEditorHiddenButtons);
        register(m_hideTemplatesTab);
        register(m_hideAdminTabs);
        register(m_hideTimezone);
        register(m_hideLaunchDate);
        register(m_requireLaunchDate);
        register(m_hideUDCTUI);
        register(m_hideFolderIndexCheckbox);
        register(m_defaultNotificationTime);
        register(m_publishLifecycleListenerClass);
        register(m_notifyAuthorOnLifecycle);
        register(m_saveTextCleansWordTags);
        register(m_hideAdditionalResourceFields);
        register(m_disableFileAssetExtraction);
        register(m_deleteWorkflowAfterPublication);
        register(m_soonExpiredTimespanMonths);
        register(m_soonExpiredTimespanDays);
        register(m_unpublishedNotFound);
        register(m_linksOnlyInSameSubsite);
        register(m_categoryAuthoringExtension);
        register(m_hideResetLifecycleLink);
        register(m_keywordWeight);
        register(m_titleWeight);
        register(m_scoreTitleAndKeywords);
        register(m_skipAssetSteps);
        register(m_mandatoryDescriptions);
        register(m_deleteLifecycleWhenComplete);
        register(m_deleteExpiryNotificationsWhenSent);
        register(m_deleteWorkflowNotificationsWhenSent);
        register(m_categoryTreeOrdering);
        register(m_hasContactsAuthoringStep);
        register(m_hideTextAssetUploadFile);
        register(m_allowContentCreateInSectionListing);

        // Content Center (Workspace) config related parameters
        register(m_contentCenterMap);

        // Content Section config related parameters
        register(m_defaultSection);

        // Content Section creation parameters
        register(m_defaultItemResolverClass);
        register(m_defaultTemplateResolverClass);

        register(m_itemSearchDefaultTab);

        register(m_folderBrowseListSize);

        // publishToFile package related parameter
        // Moved to publishToFile.PublishToFileConfig as of version 6.0.2
        // register(m_disableItemPfs);
        // register(m_publishToFileClass);

        loadInfo();
    }

    public final String getTemplateRoot() {
        return (String) get(m_templateRootPath);
    }

    public final String getDefaultItemTemplatePath() {
        return (String) get(m_defaultItemTemplatePath);
    }

    public final String getDefaultFolderTemplatePath() {
        return (String) get(m_defaultFolderTemplatePath);
    }

    public final Class getDefaultItemResolverClass() {
        return (Class) get(m_defaultItemResolverClass);
    }

    public final Class getDefaultTemplateResolverClass() {
        return (Class) get(m_defaultTemplateResolverClass);
    }

    public final Class getCategoryAuthoringAddForm() {
        return (Class) get(m_categoryAuthoringAddForm);
    }

    public final InputStream getItemAdapters() {
        // URL resource: protocol handler removal: START
        // remove:
        //  try {
        //      return ((URL)get(m_itemAdapters)).openStream();
        //  } catch (IOException ex) {
        //      throw new UncheckedWrapperException("Cannot read stream", ex);
        //  }
        // ADD:
        return (InputStream) get(m_itemAdapters);
    }

    public final String getDefaultContentSection() {
        return (String) get(m_defaultSection);
    }

    public final boolean getUseStreamlinedCreation() {
        return ((Boolean) get(m_useStreamlinedCreation)).booleanValue();
    }

    public final DHTMLEditor.Config getDHTMLEditorConfig() {
        return (DHTMLEditor.Config) get(m_dhtmlEditorConfig);
    }

    public final String[] getDHTMLEditorPlugins() {
        return (String[]) get(m_dhtmlEditorPlugins);
    }

    public final String[] getDHTMLEditorHiddenButtons() {
        return (String[]) get(m_dhtmlEditorHiddenButtons);
    }

    public final boolean getHideTemplatesTab() {
        return ((Boolean) get(m_hideTemplatesTab)).booleanValue();
    }

    public final boolean getHideAdminTabs() {
        return ((Boolean) get(m_hideAdminTabs)).booleanValue();
    }

    public final boolean getHideTimezone() {
        return ((Boolean) get(m_hideTimezone)).booleanValue();
    }

    public final boolean getHideLaunchDate() {
        return ((Boolean) get(m_hideLaunchDate)).booleanValue();
    }

    public final boolean getRequireLaunchDate() {
        return ((Boolean) get(m_requireLaunchDate)).booleanValue();
    }

    public final boolean getHideUDCTUI() {
        return ((Boolean) get(m_hideUDCTUI)).booleanValue();
    }

    public final boolean getHideFolderIndexCheckbox() {
        return ((Boolean) get(m_hideFolderIndexCheckbox)).booleanValue();
    }

    public final int getDefaultNotificationTime() {
        return ((Integer) get(m_defaultNotificationTime)).intValue();
    }

    public final String getPublishLifecycleListenerClass() {
        return (String) get(m_publishLifecycleListenerClass);
    }

    public final boolean getNotifyAuthorOnLifecycle() {
        return ((Boolean) get(m_notifyAuthorOnLifecycle)).booleanValue();
    }

    public final boolean getSaveTextCleansWordTags() {
        return ((Boolean) get(m_saveTextCleansWordTags)).booleanValue();
    }

    public final boolean isHideAdditionalResourceFields() {
        return ((Boolean) get(m_hideAdditionalResourceFields)).booleanValue();
    }

    public final boolean getDisableFileAssetExtraction() {
        return ((Boolean) get(m_disableFileAssetExtraction)).booleanValue();
    }

    public final boolean getDeleteWorkflowAfterPublication() {
        return ((Boolean) get(m_deleteWorkflowAfterPublication)).booleanValue();
    }

    public final boolean getLinksOnlyInSameSubsite() {
        return ((Boolean) get(m_linksOnlyInSameSubsite)).booleanValue();
    }

    public final int getSoonExpiredMonths() {
        return ((Integer) get(m_soonExpiredTimespanMonths)).intValue();
    }

    public final int getSoonExpiredDays() {
        return ((Integer) get(m_soonExpiredTimespanDays)).intValue();
    }

    public final boolean isUnpublishedNotFound() {
        return ((Boolean) get(m_unpublishedNotFound)).booleanValue();
    }

    public final Class getCategoryAuthoringExtension() {
        return (Class) get(m_categoryAuthoringExtension);
    }

    // ///////////////////////////////////////////
    // publishToFile package related configuration
    // ///////////////////////////////////////////
    // Moved to publishToFile.PublishToFileConfig! Temporarily retained here
    // for backwards compatibility
    public final boolean getDisableItemPfs() {
        // return ((Boolean) get(m_disableItemPfs)).booleanValue();
        return PublishToFileConfig.getConfig().isItemPfsDisabled();
    }

    public final Class getPublishToFileClass() {
        // return (Class) get(m_publishToFileClass);
        return PublishToFileConfig.getConfig().getPublishListenerClass();
    }

    /**
     * Fetch the file name contaning XML Mapping of the content center tabs
     * to URLs
     * @return String containig file name including path component.
     */
    public String getContentCenterMap() {
        return (String) get(m_contentCenterMap);
    }

    /**
     * Internal class representing a DHTMLEditor configuration parameter. It 
     * creates a new DHMTLEditor Config object (internal class in DHTMLEditor).
     * 
     * XXX Method unmarshal is broken and currently does not work correctly. It
     * does not process default values provided by using 
     * DHTMLEditor.Config.Standard (see parameter m_dhtmlEditorConfig above).
     * May be a similiar problem as with ResourceParameter and default value,
     * see patch provided by pbrucha.
     * Best solution may be to remove this special parameter class and use a
     * string parameter instead to directly create a DHTMLEditor.Config object.
     * (pboy, 2010-09-02)
     */
    private class DHTMLEditorConfigParameter extends StringParameter {

        public DHTMLEditorConfigParameter(final String name,
                final int multiplicity,
                final Object defaultObj) {
            super(name, multiplicity, defaultObj);
        }

        /**
         * WARNING: Does not correctly process default values, see above!
         * @param value
         * @param errors
         * @return
         */
        @Override
        protected Object unmarshal(String value, ErrorList errors) {
            return DHTMLEditor.Config.valueOf(value);
        }
    }
    protected static HashMap extraXMLGenerators = new HashMap();

    /** Add one ExtraXMLGenerator to the list. */
    public static void registerExtraXMLGenerator(String type, ExtraXMLGenerator gen) {
        List gens = (List) extraXMLGenerators.get(type);
        if (gens == null) {
            gens = new LinkedList();
            extraXMLGenerators.put(type, gens);
        }
        // Store class reference so it can be recreated for each page.
        // This requires a fix to all components using extraXMLGenerators,
        // for example see the currently only one in core/cms: GreetingItemExtraXML 
        gens.add(gen.getClass()); // XXX assumes default ctor
    }

    /** Get the iterator of ExtraXMLGenerators. */
    public static Iterator getExtraXMLGeneratorsIterator() {
        return extraXMLGenerators.entrySet().iterator();
    }

    public final boolean hideResetLifecycleLink() {
        return ((Boolean) get(m_hideResetLifecycleLink)).booleanValue();
    }

    /**
     * The relative weight given to the dcKeywords element
     * within dublinCore element within cms:item element
     * when ranking search results
     * Only used by the interMedia query engine.
     **/
    public Integer getKeywordSearchWeight() {
        return (Integer) get(m_keywordWeight);
    }

    /**
     * The relative weight given to title element
     * within cms:item element when ranking search results
     * Only used by the interMedia query engine.
     **/
    public Integer getTitleSearchWeight() {
        return (Integer) get(m_titleWeight);
    }

    /**
     * Whether to include INPATH operators to contains clause in intermedia search
     *
     * NB - if true, INDEX MUST BE CREATED WITH PATH_SECTION_GROUP - upgrade 6.5.0 - 6.5.1
     *
     * @return
     */
    public boolean scoreKeywordsAndTitle() {
        return ((Boolean) get(m_scoreTitleAndKeywords)).booleanValue();
    }

    /**
     * for the given content type, returns a collection of 
     * steps that are deemed irrelevant for the type.
     * 
     * If no irrelevant steps, an empty set is returned.
     * 
     * Steps are the names of the bebop step components that 
     * are used by the authoring kit wizard
     * 
     * @param type
     * @return
     */
    public Collection getAssetStepsToSkip(ContentType type) {
        s_log.debug("getting asset steps to skip for type " + type.getClassName());
        if (s_skipAssetSteps == null) {
            // populate static map once based on config parameter value
            s_log.debug("loading skipsteps");
            s_skipAssetSteps = new HashMap();
            String[] skipSteps = (String[]) get(m_skipAssetSteps);
            if (skipSteps != null) {

                s_log.debug(skipSteps.length + " entries in parameter");
                for (int i = 0; i < skipSteps.length; i++) {
                    String[] pair = StringUtils.split(skipSteps[i], ':');
                    // 1st string is name of content type, 2nd string is name of asset step
                    s_log.debug("parameter read - type = " + pair[0] + " - step = " + pair[1]);
                    Collection typeSteps = (Collection) s_skipAssetSteps.get(pair[0]);
                    if (typeSteps == null) {
                        typeSteps = new HashSet();
                        s_skipAssetSteps.put(pair[0], typeSteps);

                    }
                    typeSteps.add(pair[1]);

                }
            }

        }
        Collection skipSteps = (Collection) s_skipAssetSteps.get(type.getClassName());
        if (skipSteps == null) {
            s_log.debug("no steps to skip");
            skipSteps = new HashSet();
        }
        return skipSteps;

    }

    /**
     * May be used by any content type creation form to decide whether to validate 
     * description field
     *
     */
    public boolean mandatoryDescriptions() {
        return ((Boolean) get(m_mandatoryDescriptions)).booleanValue();
    }

    /**
     * Used to decide whether lifecycles (and all asociated phases)
     * should be deleted from the system when complete
     * 
     * (Deleting lifecycle means that you lose a bit of historical information
     * eg when was this item unpublished)
     */
    public boolean deleteFinishedLifecycles() {
        return ((Boolean) get(m_deleteLifecycleWhenComplete)).booleanValue();
    }

    /**
     * Used to decide whether to delete old notification records
     * for expiry notifications.
     * 
     * If true, notifications and messages are deleted if the 
     * notification is successfully sent. Any send failures are 
     * retained
     * 
     */
    public boolean deleteExpiryNotifications() {
        return ((Boolean) get(m_deleteExpiryNotificationsWhenSent)).booleanValue();
    }

    /**
     * Used to decide whether to delete old notification records
     * for workflow notifications.
     * 
     * If true, notifications and messages are deleted if the 
     * notification is successfully sent. Any send failures are 
     * retained
     * 
     */
    public boolean deleteWorkflowNotifications() {
        return ((Boolean) get(m_deleteWorkflowNotificationsWhenSent)).booleanValue();
    }

    public String getCategoryTreeOrder() {
        return (String) get(m_categoryTreeOrdering);
    }

    /**
     * I'am not sure for what this method is. I found it here when I tried
     * figure out how add multiple parts to an ContentType, like ccm-cms-types-contact
     * and the Multipart article do. I think this method should not be here because
     * it is only needed by one specific contenttype. Because of this, I think that
     * this method and the contact are violating many rules of modern software design.
     * Jens Pelzetter, 2009-06-02.
     *
     * @return
     */
    public boolean getHasContactsAuthoringStep() {
        return ((Boolean) get(m_hasContactsAuthoringStep)).booleanValue();
    }

    public final boolean getHideTextAssetUploadFile() {
        return ((Boolean) get(m_hideTextAssetUploadFile)).booleanValue();
    }

    public final boolean getAllowContentCreateInSectionListing() {
        return ((Boolean) get(m_allowContentCreateInSectionListing)).booleanValue();
    }

    public String getItemSearchDefaultTab() {
        return (String) get(m_itemSearchDefaultTab);
    }

    public Integer getFolderBrowseListSize() {
        return (Integer) get(m_folderBrowseListSize);
    }
}
