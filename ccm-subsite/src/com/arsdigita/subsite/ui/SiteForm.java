/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.subsite.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.ui.CategoryPicker;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.subsite.Site;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.ui.UI;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TooManyListenersException;
import org.apache.log4j.Logger;

/**
 * Class creates the administration input form.
 *
 * Used by ControlCenterPanel to construct the 'create new site' and 'edit existing site' input
 * forms.
 */
public class SiteForm extends Form {

    /**
     * A logger instance.
     */
    private static final Logger s_log = Logger.getLogger(SiteForm.class);
    private SiteSelectionModel m_site;
    private BigDecimal siteDefaultRootPageID;
    /**
     * Input field subsite title
     */
    private TextField m_title;
    private TextField m_hostname;
    private TextArea m_description;
    private SingleSelect m_customFrontpageApp;
    private TextField m_styleDir;
    private CategoryPicker m_rootCategory;
    private SingleSelect m_themes;
    private SaveCancelSection m_buttons;
    private final static String DEFAULT_APP = "DEFAULT_APP";
    private final static String DEFAULT_APP_LABEL = "subsite.ui.default_app_label";
    private final static String DEFAULT_STYLE = "DEFAULT_STYLE";
    private final static String DEFAULT_STYLE_LABEL = "subsite.ui.default_style_label";
    private final static String OTHER_STYLE = "OTHER_STYLE";
    private final static String OTHER_STYLE_LABEL = "Other (type in box below)";

    /**
     * Constructor create input widgets and adds them to form.
     *
     * @param name
     * @param site
     */
    public SiteForm(String name, SiteSelectionModel site) {

        //super(name, new SimpleContainer());
        super(name, new ColumnPanel(2));
        setClassAttr("simpleForm");
        setRedirecting(true);

        m_site = site;
        String defAppPath = UI.getRootPageURL();
        s_log.debug("defAppPath is: " + defAppPath);
        siteDefaultRootPageID = Application.retrieveApplicationForPath(defAppPath).getID();

        /* Setup text input field for subsite title property                  */
        m_title = new TextField(new StringParameter("title"));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setMetaDataAttribute("title", "Title");
        m_title.setHint((String) SubsiteGlobalizationUtil.globalize("subsite.ui.title.hint").
            localize());
        m_title.setSize(40);
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.title.label")));
        add(m_title);       // adds title input field to form


        /* Setup text input field for hostname property                       */
        m_hostname = new TextField(new StringParameter("hostname"));
        m_hostname.addValidationListener(new NotNullValidationListener());
        m_hostname.addValidationListener(new HostNameValidationListener());
        m_hostname.setMetaDataAttribute("title", "Hostname");
        m_hostname.setSize(40);
        m_hostname.setHint((String) SubsiteGlobalizationUtil.globalize("subsite.ui.hostname.hint").
            localize());
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.hostname.label")));
        add(m_hostname);       // adds hostname input field to form


        /* Setup text input area for description property                     */
        m_description = new TextArea(new StringParameter("description"));
        m_description.addValidationListener(new NotNullValidationListener());
        m_description.setMetaDataAttribute("title", "Description");
        m_description.setCols(45);
        m_description.setRows(4);
        m_description.setHint((String) SubsiteGlobalizationUtil.globalize(
            "subsite.ui.description.hint").localize());
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.description.label")));
        add(m_description);       // adds description input field to form


        /* Setup selection box for subsite start page (front page) Application
         * by URL                                                             */
        m_customFrontpageApp = new SingleSelect(
            new StringParameter("customFrontpageApp"));
        m_customFrontpageApp.setMetaDataAttribute("title", "Front Page (url)");
        // m_customFrontpageApp.setSize(40);
        m_customFrontpageApp.setHint((String) SubsiteGlobalizationUtil.globalize(
            "subsite.ui.customfrontpage.hint").
            localize());
        try {
            m_customFrontpageApp.addPrintListener(new FrontpageAppListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("This cannot happen", ex);
        }
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.customfrontpage.label")));
        add(m_customFrontpageApp);  // adds  selectfield start page to form


        /* Setup selection box for themes   */
        m_themes = new SingleSelect(new StringParameter("selectStyleDir"));
        m_themes.setMetaDataAttribute("title", "XSLT Directory");
        m_themes.setHint((String) SubsiteGlobalizationUtil.globalize("subsite.ui.theme.hint").
            localize());
        try {
            m_themes.addPrintListener(new ThemesListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("This cannot happen", ex);
        }
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.theme.label")));
        add(m_themes);  // adds themes selection box to form


        /* Setup text input field to manually enter a style direcotry       */
        m_styleDir = new TextField(new StringParameter("styleDir"));
        m_styleDir.setMetaDataAttribute("title", "XSLT Directory (Other)");
        m_styleDir.setSize(40);
        m_styleDir.setHint(
            "Enter the directory for the custom XSLT styles, or leave blank for the default styling.");
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.styledir.label")));
        add(m_styleDir);  // adds inputfield style dir to form


        /* Setup selection box for cagtegory domain                          */
        m_rootCategory = (CategoryPicker) Classes.newInstance(
            Subsite.getConfig().getRootCategoryPicker(),
            new Class[]{String.class},
            new Object[]{"rootCategory"});
        if (m_rootCategory instanceof Widget) {
            ((Widget) m_rootCategory).setMetaDataAttribute("title", "Root category");
            ((Widget) m_rootCategory).setHint((String) SubsiteGlobalizationUtil.globalize(
                "subsite.ui.root_category.hint").localize());
        }
        add(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.root_category.label")));
        add(m_rootCategory);  // adds domain category selection box to form

        m_buttons = new SaveCancelSection();
        m_buttons.getSaveButton().setButtonLabel(SubsiteGlobalizationUtil.globalize(
            "subsite.ui.save"));
        m_buttons.getSaveButton().setHint("Save the details in the form");
        m_buttons.getCancelButton().setButtonLabel(SubsiteGlobalizationUtil.globalize(
            "subsite.ui.cancel"));
        m_buttons.getCancelButton().setHint("Abort changes & reset the form");
        add(m_buttons);

        addSubmissionListener(new SiteSubmissionListener());
        addProcessListener(new SiteProcessListener());
        addInitListener(new SiteInitListener());
        addValidationListener(new SiteValidationListener());
    }

    /**
     *
     */
    private class SiteSubmissionListener implements FormSubmissionListener {

        public void submitted(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                m_site.clearSelection(state);
                throw new FormProcessException("cancel pressed");
            }
        }

    }

    /**
     * Validate the subsite form user input.
     */
    private class SiteValidationListener implements FormValidationListener {

        public void validate(FormSectionEvent e) {
            PageState state = e.getPageState();
            if (!m_buttons.getCancelButton().isSelected(state)) {
                FormData data = e.getFormData();
                // make sure that if a theme was typed in that the "other"
                // was selected in the theme selection box.  
                String styleDir = (String) m_styleDir.getValue(state);
                String themeDir = (String) m_themes.getValue(state);
                if (styleDir != null) {
                    styleDir = styleDir.trim();
                }

                // if the styleDir is null/empty then the themeDir must not
                // be null.  If the themeDir is set to "other" then we leave
                // need to make sure the styleDir is null
                if (OTHER_STYLE.equals(themeDir)) {
                    if (StringUtils.emptyString(styleDir)) {
                        data.addError(SubsiteGlobalizationUtil.globalize(
                            "subsite.ui.other_style_missing",
                            new String[]{OTHER_STYLE_LABEL}));
                    }
                } else {
                    if (!StringUtils.emptyString(styleDir)) {
                        data.addError(SubsiteGlobalizationUtil.globalize(
                            "subsite.ui.other_style_invalid",
                            new String[]{OTHER_STYLE_LABEL}));
                    }
                }

                /* Check whether a valid Root category has been selected. The
                 * default entry "-- pick one--" provides a null String
                 * ( null pointer exception).                                 */
                try {
                    Category testExist = m_rootCategory.getCategory(state);
                    String test = testExist.getDefaultDomainClass();
                } catch (Exception ex) {
                    data.addError(SubsiteGlobalizationUtil.globalize(
                        "subsite.ui.root_category_missing"));
                }

            }   // End if (!m_buttons ...)
        }  // End validate(FormSectionEvent e)

    }

    /**
     * Checks whether hostname is alreafy in use.
     */
    private class HostNameValidationListener implements ParameterListener {

        public void validate(ParameterEvent e) {
            ParameterData data = e.getParameterData();
            String hostname = (String) data.getValue();

            Site site = m_site.getSelectedSite(e.getPageState());
            if (hostname != null && hostname.toString().length() > 0) {
                DataCollection sites = SessionManager.getSession()
                    .retrieve(Site.BASE_DATA_OBJECT_TYPE);
                sites.addEqualsFilter("lower(" + Site.HOSTNAME + ")",
                                      hostname.toLowerCase());
                if (site != null) {
                    sites.addNotEqualsFilter(Site.ID, site.getID());
                }
                if (sites.size() > 0) {
                    data.addError(SubsiteGlobalizationUtil.globalize(
                        "subsite.ui.hostname_already_in_use"));
                }
            }

        }

    }

    /**
     * Initializes the form. (when a new input form is requested by user either by editing an
     * existing subsite or by creating a new one).
     */
    private class SiteInitListener implements FormInitListener {

        public void init(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            Site site = m_site.getSelectedSite(state);

            if (site == null) {
                m_title.setValue(state, null);
                m_hostname.setValue(state, null);
                m_description.setValue(state, null);
                m_customFrontpageApp.setValue(state, DEFAULT_APP);
                m_styleDir.setValue(state, null);
                m_themes.setValue(state, DEFAULT_STYLE);
                m_rootCategory.setCategory(state, null);
            } else {
                m_title.setValue(state, site.getTitle());
                m_hostname.setValue(state, site.getHostname());
                m_description.setValue(state, site.getDescription());

                // BigDecimal siteDefaultRootPageID
                BigDecimal currentFrontpageID = site.getFrontPage().getID();
                s_log.debug(" Site default frontpage is: " + siteDefaultRootPageID
                                + ", Current frontpage is: " + currentFrontpageID);
                m_customFrontpageApp.setValue(
                    state,
                    currentFrontpageID == siteDefaultRootPageID ? DEFAULT_APP
                        : currentFrontpageID.toString());

                String styleURL = site.getStyleDirectory();
                // if the value is in the config map, then styleDir is
                // empty and themeDir gets the value.  Otherwise, if the
                // value is null, themeDir gets DEFAULT and styleDir is
                // empty.  Otherwise, themeDir gets OTHER and sytleDir
                // gets the value
                String styleDir = null;
                String themeDir = null;
                if (Subsite.getConfig().getThemes().get(styleURL) != null) {
                    themeDir = styleURL;
                } else {
                    if (StringUtils.emptyString(styleURL)) {
                        // we want the default
                        themeDir = DEFAULT_STYLE;
                    } else {
                        themeDir = OTHER_STYLE;
                        styleDir = styleURL;
                    }
                }
                m_styleDir.setValue(state, styleDir);
                m_themes.setValue(state, themeDir);

                Category root = site.getRootCategory();
                m_rootCategory.setCategory(state, root);
            }
        }

    }

    /**
     *
     */
    private class SiteProcessListener implements FormProcessListener {

        public void process(FormSectionEvent e)
            throws FormProcessException {

            PageState state = e.getPageState();

            Category root = m_rootCategory.getCategory(state);

            Site site = m_site.getSelectedSite(state);

            String style = (String) m_styleDir.getValue(state);
            String theme = (String) m_themes.getValue(state);
            if (style != null) {
                style = style.trim();
            }
            String styleDir = style;
            if (StringUtils.emptyString(style)) {
                if (!OTHER_STYLE.equals(theme) && !DEFAULT_STYLE.equals(theme)) {
                    styleDir = theme;
                }
            }

            /* Pre-process selected frontpage application: retrieve application */
            String subsiteSelectedFrontpage = (String) m_customFrontpageApp
                .getValue(state);
            s_log.debug(" Site default frontpage ID is: " + siteDefaultRootPageID
                            + ", selected frontpage Value is: "
                            + subsiteSelectedFrontpage);
            Application frontpageApp;
            if (subsiteSelectedFrontpage.equals(DEFAULT_APP)) {
                s_log.debug("About to create frontpage app ID: " + DEFAULT_APP);
                frontpageApp = Application
                    .retrieveApplication(siteDefaultRootPageID);
            } else {
                s_log.debug("About to create frontpage app ID: "
                                + subsiteSelectedFrontpage);
                frontpageApp = Application
                    .retrieveApplication(new BigDecimal(subsiteSelectedFrontpage));
            }
            Assert.exists(frontpageApp, Application.class);
            s_log.debug("Created frontpage app ID: " + frontpageApp.getID());

            if (site == null) {   // (sub)site not yet exists, create new one  

                if (!siteDefaultRootPageID.equals(frontpageApp.getID())) {

                    // Previous version executed setRoot.... for newly created
                    // application, which were created for the purpose to serve
                    // as a dedicated front page application for the created 
                    // subsite with an added comment:
                    // "NB, explicitly don't set cat on shared front page!"
                    s_log.debug("Front page application ID: "
                                    + frontpageApp.getID());
                    s_log.debug("About to set cat on dedicated front page.");
                    Category.setRootForObject(frontpageApp, root);

                }

                // actually create a new subsite
                site = Site.create((String) m_title.getValue(state),
                                   (String) m_description.getValue(state),
                                   (String) m_hostname.getValue(state),
                                   styleDir,
                                   root,
                                   frontpageApp);

            } else {   // (sub)site already exists, modify mutable configuration 

                site.setTitle((String) m_title.getValue(state));
                site.setDescription((String) m_description.getValue(state));
                site.setHostname((String) m_hostname.getValue(state));
                site.setStyleDirectory(styleDir);
                site.setRootCategory(root);
                // XXX Check: Frontpage application was not mutable in previous
                // version! Check if we explicitly have to handle cat whether 
                // or not frontpage is shared. See comment above!
                site.setFrontPage(frontpageApp);

            }
            m_site.clearSelection(state);

            Application app = Application
                .retrieveApplicationForPath("/navigation/");
            Category.setRootForObject(app,
                                      root,
                                      site.getTemplateContext().getContext());

        }

    }

    /**
     *
     */
    private class FrontpageAppListener implements PrintListener {

        public void prepare(PrintEvent e) {
            final SingleSelect target = (SingleSelect) e.getTarget();
            // final PageState state = e.getPageState();
            ApplicationCollection customApps;

            // target.addOption(new Option(SELECT_APP, SELECT_APP_LABEL));
            target.addOption(new Option(DEFAULT_APP,
                                        (String) SubsiteGlobalizationUtil
                                        .globalize(DEFAULT_APP_LABEL).localize()));

            String[] customAppTypes = (String[]) Subsite.getConfig()
                .getFrontPageApplicationTypes();
            if (customAppTypes != null) {
                for (int i = 0; i < customAppTypes.length; i++) {
                    customApps = Application.retrieveAllApplications(
                        customAppTypes[i]);
                    while (customApps.next()) {
                        /* Create an entry for each application, consisting
                         * of the (BigDecimal) ID as value and the URL as
                         * label.                                         */
                        String appID = customApps.get(ACSObject.ID).toString();
                        target.addOption(new Option(appID,
                                                    (customApps.getPrimaryURL()
                                                     + "(" + appID + ")")));
                    }
                }

            }

        }

    }

    /**
     *
     */
    private class ThemesListener implements PrintListener {

        public void prepare(PrintEvent e) {
            SingleSelect target = (SingleSelect) e.getTarget();
            PageState state = e.getPageState();
            Map themes = Subsite.getConfig().getThemes();
            Set entrySet = themes.entrySet();
            target.addOption(new Option(DEFAULT_STYLE,
                                        (String) SubsiteGlobalizationUtil.globalize(
                                            DEFAULT_STYLE_LABEL).localize()));
            if (entrySet != null) {
                Iterator entries = entrySet.iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    target.addOption(new Option(entry.getKey().toString(),
                                                entry.getValue().toString()),
                                     state);
                }
            }
            target.addOption(new Option(OTHER_STYLE, OTHER_STYLE_LABEL));
        }

    }

}
