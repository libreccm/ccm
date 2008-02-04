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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.cms.dispatcher.DefaultTemplateResolver;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.MultilingualItemResolver;
import com.arsdigita.cms.dispatcher.TemplateResolver;
import com.arsdigita.cms.lifecycle.PublishLifecycleListener;
import com.arsdigita.cms.publishToFile.PublishToFile;
import com.arsdigita.cms.publishToFile.PublishToFileListener;
import com.arsdigita.cms.ui.authoring.ItemCategoryExtension;
import com.arsdigita.cms.ui.authoring.ItemCategoryForm;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.util.StringUtils;
/**
 * A record containing server-session scoped configuration properties.
 *
 * Accessors of this class may return null.  Developers should take
 * care to trap null return values in their code.
 *
 * @see ContentSection#getConfig()
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentSectionConfig.java 1583 2007-05-25 15:32:13Z chrisgilbert23 $
 */
public final class ContentSectionConfig extends AbstractConfig {
    public static final String versionId =
        "$Id: ContentSectionConfig.java 1583 2007-05-25 15:32:13Z chrisgilbert23 $" +
        "$Author: chrisgilbert23 $" +
        "$DateTime: $";

    private static final Logger s_log = Logger.getLogger(ContentSectionConfig.class);

    private static Map s_skipAssetSteps = null; 

    private final Parameter m_templateRootPath;
    private final Parameter m_defaultItemTemplatePath;
    private final Parameter m_defaultFolderTemplatePath;
    private final Parameter m_languages;
    private final Parameter m_publishToFileClass;
    private final Parameter m_disableItemPfs;
    private final Parameter m_defaultItemResolverClass;
    private final Parameter m_defaultTemplateResolverClass;
    private final Parameter m_categoryAuthoringAddForm;
    private final Parameter m_useSectionCategories;
    private final Parameter m_itemAdapters;
    private final Parameter m_defaultSection;
    private final Parameter m_useStreamlinedCreation;
    private final Parameter m_dhtmlEditorConfig;
    private final Parameter m_dhtmlEditorPlugins;
    private final Parameter m_dhtmlEditorHiddenButtons;
    private final Parameter m_hideTemplatesTab;
    private final Parameter m_hideAdminTabs;
    private final Parameter m_hideTimezone;
    private final Parameter m_hideLaunchDate;
    private final Parameter m_requireLaunchDate;
    private final Parameter m_hideUDCTUI;
    private final Parameter m_hideFolderIndexCheckbox;
    private final Parameter m_defaultNotificationTime;
    private final Parameter m_publishLifecycleListenerClass;
    private final Parameter m_notifyAuthorOnLifecycle;
    private final Parameter m_saveTextCleansWordTags;
    private final Parameter m_hideAdditionalResourceFields;
    private final Parameter m_disableFileAssetExtraction;
    private final Parameter m_deleteWorkflowAfterPublication;
    private final Parameter m_soonExpiredTimespanMonths;
    private final Parameter m_soonExpiredTimespanDays;
    private final Parameter m_defaultTaskAlerts;
    private final Parameter m_unpublishedNotFound;
    private final Parameter m_linksOnlyInSameSubsite;
    private final Parameter m_categoryAuthoringExtension;
    private final Parameter m_hideResetLifecycleLink;
    private final Parameter m_scoreTitleAndKeywords;
    private final Parameter m_titleWeight;
    private final Parameter m_keywordWeight;    
    private final Parameter m_skipAssetSteps;
    private final Parameter m_mandatoryDescriptions;
    private final Parameter m_deleteLifecycleWhenComplete;
    private final Parameter m_deleteExpiryNotificationsWhenSent;
    private final Parameter m_deleteWorkflowNotificationsWhenSent;
    
    /**
     * Do not instantiate this class directly.
     *
     * @see ContentSection#getConfig()
     **/
    public ContentSectionConfig() {
        m_templateRootPath = new StringParameter
            ("com.arsdigita.cms.template_root_path",
             Parameter.REQUIRED, "/packages/content-section/templates");
        m_defaultItemTemplatePath = new StringParameter
            ("com.arsdigita.cms.default_item_template_path",
             Parameter.REQUIRED, "/default/item.jsp");
        m_defaultFolderTemplatePath = new StringParameter
            ("com.arsdigita.cms.default_folder_template_path",
             Parameter.REQUIRED, "/default/folder.jsp");
        m_languages = new StringParameter
            ("com.arsdigita.cms.languages",
             Parameter.REQUIRED, "en,de,fr,nl,it,pt,es");
        m_publishToFileClass = new SpecificClassParameter
            ("com.arsdigita.cms.publish_to_file_class",
             Parameter.REQUIRED,
             PublishToFile.class,
             PublishToFileListener.class);

        m_linksOnlyInSameSubsite = new BooleanParameter
        ("com.arsdigita.cms.browse_links_in_same_subsite_only",
                Parameter.REQUIRED, new Boolean(false));

        m_defaultTaskAlerts = new StringArrayParameter
        	("com.arsdigita.cms.default_task_alerts",
        	  Parameter.REQUIRED, new String[] {
        		"Authoring:enable:finish:rollback",
        		"Approval:enable:finish:rollback",
        		"Deploy:enable:finish:rollback" }
        	);

        // XXX these are probably temporary parameters, as the
        // item/template resolvers will be determined by the successor
        // to SectionInitializer. However, it still may be useful to
        // keep these for the default values.
        m_defaultItemResolverClass = new SpecificClassParameter
            ("com.arsdigita.cms.default_item_resolver_class",
             Parameter.REQUIRED,
             MultilingualItemResolver.class,
             ItemResolver.class);
        m_defaultTemplateResolverClass = new SpecificClassParameter
            ("com.arsdigita.cms.default_template_resolver_class",
             Parameter.REQUIRED,
             DefaultTemplateResolver.class,
             TemplateResolver.class);

        m_categoryAuthoringAddForm = new SpecificClassParameter
            ("com.arsdigita.cms.category_authoring_add_form",
             Parameter.REQUIRED,
             ItemCategoryForm.class,
             SimpleComponent.class);

        // XXX: temporary parameter. will be removed when MapParameter
        // works and the p2fs initializer is converted away from the
        // legacy init
        m_disableItemPfs = new BooleanParameter
            ("com.arsdigita.cms.disable_item_pfs",
             Parameter.REQUIRED, new Boolean(false));

        // XXX: temporary parameter. will be removed when
        // SectionInitializer is replaced with a separate Section
        // loader app.
        m_useSectionCategories = new BooleanParameter
            ("com.arsdigita.cms.use_section_categories",
             Parameter.REQUIRED, new Boolean(true));

        try {
            m_itemAdapters = new URLParameter
                ("com.arsdigita.cms.item_adapters",
                 Parameter.REQUIRED,
                 new URL("resource:WEB-INF/resources/cms-item-adapters.xml"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse URL", ex);
        }

        m_defaultSection = new StringParameter
            ("com.arsdigita.cms.default_content_section",
             Parameter.REQUIRED, "content");

        m_useStreamlinedCreation = new BooleanParameter
            ("com.arsdigita.cms.use_streamlined_creation",
             Parameter.REQUIRED, new Boolean(false));

        m_dhtmlEditorConfig = new DHTMLEditorConfigParameter
            ("com.arsdigita.cms.dhtml_editor_config",
             Parameter.REQUIRED, 
             DHTMLEditor.Config.STANDARD);

        m_dhtmlEditorPlugins = new StringArrayParameter
            ("com.arsdigita.cms.dhtml_editor_plugins",
             Parameter.OPTIONAL,
             null);

		m_dhtmlEditorHiddenButtons = new StringArrayParameter
				   ("com.arsdigita.cms.dhtml_editor_hidden_buttons",
					Parameter.OPTIONAL,
					null);
					
        m_hideTemplatesTab = new BooleanParameter
            ("com.arsdigita.cms.hide_templates_tab",
             Parameter.REQUIRED, new Boolean(false));

        m_hideAdminTabs = new BooleanParameter
            ("com.arsdigita.cms.hide_admin_tabs",
             Parameter.REQUIRED, new Boolean(false));

        m_hideTimezone = new BooleanParameter
            ("com.arsdigita.cms.hide_timezone",
             Parameter.REQUIRED, new Boolean(false));

        m_hideLaunchDate = new BooleanParameter
        ("com.arsdigita.cms.hide_launch_date",
         Parameter.REQUIRED, new Boolean(true));

        m_requireLaunchDate = new BooleanParameter
        ("com.arsdigita.cms.require_launch_date",
         Parameter.REQUIRED, new Boolean(false));

        m_hideUDCTUI = new BooleanParameter
            ("com.arsdigita.cms.hide_udct_ui",
             Parameter.REQUIRED, new Boolean(false));

        m_hideFolderIndexCheckbox = new BooleanParameter
            ("com.arsdigita.cms.hide_folder_index_checkbox",
             Parameter.REQUIRED, new Boolean(false));

        m_defaultNotificationTime = new IntegerParameter
            ("com.arsdigita.cms.default_notification_time",
             Parameter.REQUIRED, new Integer(0));

        m_publishLifecycleListenerClass = new StringParameter
            ("com.arsdigita.cms.publish_lifecycle_listener_class",
             Parameter.OPTIONAL, PublishLifecycleListener.class.getName());

        m_notifyAuthorOnLifecycle = new BooleanParameter
            ("com.arsdigita.cms.notify_author_on_lifecycle",
             Parameter.OPTIONAL, new Boolean(true));

        m_saveTextCleansWordTags = new BooleanParameter
            ("com.arsdigita.cms.save_text_cleans_word_tags",
             Parameter.OPTIONAL, new Boolean(false));

        m_hideAdditionalResourceFields = new BooleanParameter
            ("com.arsdigita.cms.contentassets.ui.RelatedLinkPropertyForm.hideAdditionalResourceFields",
             Parameter.REQUIRED, new Boolean(false));

        m_disableFileAssetExtraction = new BooleanParameter
            ("com.arsdigita.cms.search.disableFileAssetExtraction",
             Parameter.REQUIRED, new Boolean(false));

        m_deleteWorkflowAfterPublication = new BooleanParameter
            ("com.arsdigita.cms.delete_workflow_after_publication",
             Parameter.REQUIRED, new Boolean(true));

        m_soonExpiredTimespanDays = new IntegerParameter
            ("com.arsdigita.cms.soon_expired_timespan_days",
             Parameter.REQUIRED, new Integer(1));
        
        m_soonExpiredTimespanMonths = new IntegerParameter
            ("com.arsdigita.cms.soon_expired_timespan_months",
             Parameter.REQUIRED, new Integer(0));
        
        m_unpublishedNotFound = new BooleanParameter
            ("com.arsdigita.cms.unpublished_not_found",
             Parameter.REQUIRED, new Boolean(true));

        m_categoryAuthoringExtension = new SpecificClassParameter
            ("com.arsdigita.cms.category_authoring_extension",
             Parameter.REQUIRED,
             ItemCategoryExtension.class,
             ItemCategoryExtension.class);
 	m_hideResetLifecycleLink = new BooleanParameter
            ("com.arsdigita.cms.hide_reset_lifecycle_link",
             Parameter.OPTIONAL, new Boolean(true));
	m_keywordWeight = new IntegerParameter
            ("com.arsdigita.cms.search.intermedia.keyword_weight",
             Parameter.OPTIONAL,
             new Integer(1));
        m_titleWeight = new IntegerParameter
            ("com.arsdigita.cms.search.intermedia.title_weight",
             Parameter.OPTIONAL,
             new Integer(1));
        m_scoreTitleAndKeywords = new BooleanParameter
            ("com.arsdigita.cms.search.score_title_and_keywords",
             Parameter.OPTIONAL,
             Boolean.FALSE);

        /**
         * each entry in the list is a : separated pair. The first string
	 * is the className for the type (refer to classname column in contenttypes table
	 * eg com.arsdigita.cms.contenttypes.MultiPartArticle
	 * Second string is the name of the bebop step component
	 * eg com.arsdigita.cms.contenttypes.ui.ImageStep 
         */
	m_skipAssetSteps = new StringArrayParameter
	    ("com.arsdigita.cms.skip_asset_steps",
	    Parameter.OPTIONAL,
	    null);
                              
	m_mandatoryDescriptions = new BooleanParameter
		    	("com.arsdigita.cms.mandatory_descriptions",
		    	Parameter.OPTIONAL, new Boolean(false));
	
	m_deleteLifecycleWhenComplete = new BooleanParameter
		("com.arsdigita.cms.delete_lifecycle_when_complete",
				Parameter.OPTIONAL, new Boolean(false));
	
	m_deleteExpiryNotificationsWhenSent = new BooleanParameter
	("com.arsdigita.cms.delete_expiry_notification_when_sent",
			Parameter.OPTIONAL, new Boolean(false));
	m_deleteWorkflowNotificationsWhenSent = new BooleanParameter
	("com.arsdigita.cms.delete_workflow_notification_when_sent",
			Parameter.OPTIONAL, new Boolean(false));
	
        register(m_templateRootPath);
        register(m_defaultItemTemplatePath);
        register(m_defaultFolderTemplatePath);
        register(m_languages);
        register(m_publishToFileClass);
        register(m_disableItemPfs);
        register(m_defaultItemResolverClass);
        register(m_defaultTemplateResolverClass);
        register(m_categoryAuthoringAddForm);
        register(m_useSectionCategories);
        register(m_itemAdapters);
        register(m_defaultSection);
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
        register(m_defaultTaskAlerts);
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

    public final String getLanguages() {
        return (String) get(m_languages);
    }

    public final Class getPublishToFileClass() {
        return (Class) get(m_publishToFileClass);
    }

    public final boolean getDisableItemPfs() {
        return ((Boolean) get(m_disableItemPfs)).booleanValue();
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

    public final boolean getUseSectionCategories() {
        return ((Boolean) get(m_useSectionCategories)).booleanValue();
    }

    public final InputStream getItemAdapters() {
        try {
            return ((URL)get(m_itemAdapters)).openStream();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot read stream", ex);
        }
    }

    public final String getDefaultContentSection() {
        return (String) get(m_defaultSection);
    }

    public final boolean getUseStreamlinedCreation() {
        return ((Boolean) get(m_useStreamlinedCreation)).booleanValue();
    }

    public final DHTMLEditor.Config getDHTMLEditorConfig() {
        return (DHTMLEditor.Config)get(m_dhtmlEditorConfig);
    }
    
    public final String[] getDHTMLEditorPlugins() {
        return (String[])get(m_dhtmlEditorPlugins);
    }

	public final String[] getDHTMLEditorHiddenButtons() {
		   return (String[])get(m_dhtmlEditorHiddenButtons);
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

    public final String[] getDefaultTaskAlerts() {
        return (String[]) get(m_defaultTaskAlerts);
    }
    
    public final boolean isUnpublishedNotFound() {
        return ((Boolean) get(m_unpublishedNotFound)).booleanValue();
    }

    public final Class getCategoryAuthoringExtension() {
        return (Class) get(m_categoryAuthoringExtension);    
    }

    private class SpecificClassParameter extends ClassParameter {

        private Class m_requiredClass;

        public SpecificClassParameter(final String name,
                                      final int multiplicity,
                                      final Object defaultObj,
                                      final Class requiredClass) {
            super(name, multiplicity, defaultObj);
            m_requiredClass = requiredClass;
        }

        // value != null
        protected Object unmarshal(String value, ErrorList errors) {
            Class theClass = (Class) super.unmarshal(value,errors);
            if (theClass != null) {
                if (!m_requiredClass.isAssignableFrom(theClass)) {
                    errors.add(new ParameterError(this, "class " + value + 
                                                  "  must implement : " + 
                                                  m_requiredClass.getName()));
                }
            }

            return theClass;
        }
    }

    private class DHTMLEditorConfigParameter extends StringParameter {
        public DHTMLEditorConfigParameter(final String name,
                                          final int multiplicity,
                                          final Object defaultObj) {
            super(name, multiplicity, defaultObj);
        }
        
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
         return ((Boolean)get(m_scoreTitleAndKeywords)).booleanValue();
    }
      
     
    /**
     * for the given content type, returns a collection of 
     * step that are deemed irrelevant for the type.
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
     	    String[] skipSteps = (String[])get(m_skipAssetSteps);
     	    if (skipSteps != null) {
     	
		s_log.debug(skipSteps.length + " entries in parameter");
		for (int i = 0; i < skipSteps.length; i++) {
		    String[] pair = StringUtils.split(skipSteps[i], ':');
	     	    // 1st string is name of content type, 2nd string is name of asset step
	     	    s_log.debug("parameter read - type = " + pair[0] + " - step = " + pair[1]);
	     	    Collection typeSteps = (Collection)s_skipAssetSteps.get(pair[0]);
	     	    if (typeSteps == null) {
	     		typeSteps = new HashSet();
	     		s_skipAssetSteps.put(pair[0], typeSteps);
	     					
	     	    }
	     	    typeSteps.add(pair[1]);
	     				   				
	        }
     	    }
     			
     	}
     	Collection skipSteps = (Collection)s_skipAssetSteps.get(type.getClassName());
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
             return ((Boolean)get(m_mandatoryDescriptions)).booleanValue();
        }
       
        /**
         * Used to decide whether lifecycles (and all asociated phases)
         * should be deleted from the system when complete
         * 
         * (Deleting lifecycle means that you lose a bit of historical information
         * eg when was this item unpublished)
         */
        public boolean deleteFinishedLifecycles() {
             return ((Boolean)get(m_deleteLifecycleWhenComplete)).booleanValue();
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
             return ((Boolean)get(m_deleteExpiryNotificationsWhenSent)).booleanValue();
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
             return ((Boolean)get(m_deleteWorkflowNotificationsWhenSent)).booleanValue();
        }
        
}
