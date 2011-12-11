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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.london.util.ui.CategoryPicker;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.subsite.Site;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Classes;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import java.util.TooManyListenersException;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;


/**
 * Class creates the administration input form.
 * 
 */
public class SiteForm extends Form {
    
    private SiteSelectionModel m_site;

    /** Input field subsite title  */
    private TextField m_title;
    private TextField m_hostname;
    private TextArea m_description;
    private RadioGroup m_customHomepage;
    private TextField m_customHomepage_url;
    private TextField m_styleDir;
    private CategoryPicker m_rootCategory;
    private SingleSelect m_themes;
    private SaveCancelSection m_buttons;

    private final static String DEFAULT_STYLE = "DEFAULT_STYLE";
    private final static String DEFAULT_STYLE_LABEL = "Site Wide Default";
    private final static String OTHER_STYLE = "OTHER_STYLE";
    private final static String OTHER_STYLE_LABEL = "Other (type in box below)";

    /**
     * Constructor create input widgets and adds them to form.
     * 
     * @param name
     * @param site 
     */
    public SiteForm(String name, SiteSelectionModel site) {
        
        super(name, new SimpleContainer());
        setClassAttr("simpleForm");
        setRedirecting(true);
        
        m_site = site;

        /* Setup text input field for subsite title property                  */
        m_title = new TextField(new StringParameter("title"));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setMetaDataAttribute("title", "Title");
        m_title.setHint("Enter the title of the subsite, upto 80 characters");
        m_title.setSize(40);
        add(m_title);       // adds title input field to form

        /* Setup text input field for hostname property                       */
        m_hostname = new TextField(new StringParameter("hostname"));
        m_hostname.addValidationListener(new NotNullValidationListener());
        m_hostname.addValidationListener(new HostNameValidationListener());
        m_hostname.setMetaDataAttribute("title", "Hostname");
        m_hostname.setSize(40);
        m_hostname.setHint(
            "Enter the hostname for the subsite, eg business.example.com"
                          );
        add(m_hostname);       // adds hostname input field to form

        /* Setup text input area for description property                     */
        m_description = new TextArea(new StringParameter("description"));
        m_description.addValidationListener(new NotNullValidationListener());
        m_description.setMetaDataAttribute("title", "Description");
        m_description.setCols(45);
        m_description.setRows(4);
        m_description.setHint(
            "Enter a short description for the subsite, upto 4000 characters"
        );
        add(m_description);       // adds description input field to form

        /* Setup Radio selection group for subsite start page (front page)   */
        m_customHomepage = new RadioGroup("customHome");
        m_customHomepage.addOption(new Option(Boolean.FALSE.toString(),
                                              "Use main site homepage"));
        m_customHomepage.addOption(new Option(Boolean.TRUE.toString(),
                                              "Create custom homepage"));
        m_customHomepage.addValidationListener(new NotNullValidationListener());
        m_customHomepage.setHint(
            "Select to create a custom homepage for the subsite"
        );
        try {
            m_customHomepage.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        RadioGroup target = (RadioGroup)e.getTarget();
                        if (m_site.isSelected(e.getPageState())) {
                            target.setReadOnly();
                        }
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("cannot happen", ex);
        }
        add(m_customHomepage);  // adds Radio group start page to form

        /* Setup text inpout field for subsite start page (front page)   */
        m_customHomepage_url = new TextField(
                                   new StringParameter("customHomepage_url"));
        m_customHomepage_url.setMetaDataAttribute("title", 
                                                  "Front Page name (url)");
        m_customHomepage_url.setSize(40);
        m_customHomepage_url.setHint(
            "Enter the name of the custom homepage, i.e. the last part of url. "  +
            "  "
        );
        add(m_customHomepage_url);  // adds inputfield start page to form

        /* Setup selection box for themes   */
        m_themes  = new SingleSelect(new StringParameter("selectStyleDir"));
        m_themes.setMetaDataAttribute("title", "XSLT Directory");
        m_themes.setHint("Select an existing theme from the list, " +
                         "select 'Other' to type something below or " + 
                         "select 'Site Wide Default' to get the default " +
                         "themes.");
        try {
            m_themes.addPrintListener(new ThemesListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("This cannot happen", ex);
        }
        add(m_themes);  // adds themes selection box to form

        /* Setup text input field to manually enter a style direcotry       */
        m_styleDir = new TextField(new StringParameter("styleDir"));
        m_styleDir.setMetaDataAttribute("title", "XSLT Directory (Other)");
        m_styleDir.setSize(40);
        m_styleDir.setHint(
            "Enter the directory for the custom XSLT styles, "  +
            "or leave blank for the default styling"
        );
        add(m_styleDir);  // adds inputfield style dir to form

        /* Setup selection box for cagtegory domain                          */
        m_rootCategory = (CategoryPicker)Classes.newInstance(
            Subsite.getConfig().getRootCategoryPicker(),
            new Class[] { String.class },
            new Object[] { "rootCategory" });
        if (m_rootCategory instanceof Widget) {
            ((Widget)m_rootCategory)
                .setMetaDataAttribute("title", "Root category");
            ((Widget)m_rootCategory)
                .setHint("Select a root navigation category");
        }
        add(m_rootCategory);  // adds domain category selection box to form
        
        m_buttons = new SaveCancelSection();
        m_buttons.getSaveButton().setHint("Save the details in the form");
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
     * 
     */
    private class SiteValidationListener implements FormValidationListener {
        public void validate(FormSectionEvent e) {
            PageState state = e.getPageState();
            if (!m_buttons.getCancelButton().isSelected(state)) {
                FormData data = e.getFormData();
                // make sure that if a theme was typed in that the "other"
                // was selected in the theme selection box.  
                String styleDir = (String)m_styleDir.getValue(state);
                String themeDir = (String)m_themes.getValue(state);
                if (styleDir != null) {
                    styleDir = styleDir.trim();
                }

                // if the styleDir is null/empty then the themeDir must not
                // be null.  If the themeDir is set to "other" then we leave
                // need to make sure the styleDir is null
                if (OTHER_STYLE.equals(themeDir)) {
                    if (StringUtils.emptyString(styleDir)) {
                        data.addError(
                            "If you choose '" + OTHER_STYLE_LABEL +
                            "' for the XSL Directory Select then " +
                            "you need to provide a style in the Text Field");
                    }
                } else {
                    if (!StringUtils.emptyString(styleDir)) {
                        data.addError(
                            "In order to set a in the text field, " +
                            "the XSL Directory select box must say '" + 
                            OTHER_STYLE_LABEL + "'");
                    }
                }
            }
        }
    }

    /** 
     * 
     */
    private class HostNameValidationListener implements ParameterListener {
        public void validate(ParameterEvent e) {
            ParameterData data = e.getParameterData();
            String hostname = (String)data.getValue();
            
            Site site = m_site.getSelectedSite(e.getPageState());
            if (hostname != null && hostname.toString().length() > 0) {
                DataCollection sites = SessionManager.getSession()
                    .retrieve(Site.BASE_DATA_OBJECT_TYPE);
                sites.addEqualsFilter("lower("+Site.HOSTNAME+")",
                                      hostname.toLowerCase());
                if (site != null) {
                    sites.addNotEqualsFilter(Site.ID, site.getID());
                }
                if (sites.size() > 0) {
                    data.addError(
                        "The host name " + hostname + 
                        " is already used by another site");
                }
            }
            
        }
    }

    /** 
     * 
     */
    private class SiteInitListener implements FormInitListener {
        public void init(FormSectionEvent e) 
            throws FormProcessException {
            PageState state = e.getPageState();

            Site site = m_site.getSelectedSite(state);
            
            if (site == null) {
                m_title.setValue(state, null);
                m_description.setValue(state, null);
                m_hostname.setValue(state, null);
                m_styleDir.setValue(state, null);
                m_themes.setValue(state, DEFAULT_STYLE);
                m_rootCategory.setCategory(state, null);
            } else {
                Category root = site.getRootCategory();
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

                m_title.setValue(state, site.getTitle());
                m_description.setValue(state, site.getDescription());
                m_hostname.setValue(state, site.getHostname());
                m_styleDir.setValue(state, styleDir);
                m_themes.setValue(state, themeDir);
                m_rootCategory.setCategory(state, root);

                m_customHomepage.setValue(
                    state,
                    site.getFrontPage().getPrimaryURL()
                    .equals(Subsite.getConfig().getFrontPageParentURL()) ?
                    Boolean.FALSE.toString() :
                    Boolean.TRUE.toString());
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
            String style = (String)m_styleDir.getValue(state);
            String theme = (String)m_themes.getValue(state);
            if (style != null) {
                style = style.trim();
            }
            String styleDir = style;
            if (StringUtils.emptyString(style)) {
                if (!OTHER_STYLE.equals(theme) && !DEFAULT_STYLE.equals(theme)) {
                    styleDir = theme;
                }
            }
            if (site == null) {   // (sub)site not yet exists, create new one  

                /* Retrieve application type object */
                ApplicationType appType = ApplicationType
                    .retrieveApplicationTypeForApplication
                    (Subsite.getConfig().getFrontPageApplicationType());
                Assert.exists(appType,ApplicationType.class);
                
                /* Retrieve parent application object */
                Application parentApp =  Application
                    .retrieveApplicationForPath(
                        Subsite.getConfig().getFrontPageParentURL());
                Assert.exists(parentApp,Application.class);
                
                Application frontPage = null;
                if (Boolean.TRUE.toString()
                                .equals(m_customHomepage.getValue(state))) {
                    
                    // custom homepage selected - create one
                    // hostname hard coded as front page url
                    frontPage = Application.createApplication
                                          ( appType,
                                            (String)m_hostname.getValue(state),
                                            (String)m_title.getValue(state),
                                            parentApp
                                          );
                    frontPage.setDescription((String)m_description.getValue(state));
                    Category.setRootForObject(frontPage,root);
                    
                } else { 
                    
                    frontPage = parentApp;
                    // NB, explicitly don't set cat on shared front page!
                }
                
                // actually create a new subsite
                site = Site.create((String)m_title.getValue(state),
                                   (String)m_description.getValue(state),
                                   (String)m_hostname.getValue(state),
                                   styleDir,
                                   root,
                                   frontPage);
                
            } else {   // (sub)site already exists, modify mutable configuration 
                
                // FRONT_PAGE Application not mutable
                site.setTitle((String)m_title.getValue(state));
                site.setDescription((String)m_description.getValue(state));
                site.setHostname((String)m_hostname.getValue(state));
                site.setStyleDirectory(styleDir);
                site.setRootCategory(root);
                
            }
            m_site.clearSelection(state);
                        
            Application app = Application.retrieveApplicationForPath("/navigation/");
            Category.setRootForObject(app, 
                                      root, 
                                      site.getTemplateContext().getContext());

        }
    }


    /**
     * 
     */
    private class ThemesListener implements PrintListener {
        public void prepare(PrintEvent e) {
            SingleSelect target = (SingleSelect)e.getTarget();
            PageState state = e.getPageState();
            Map themes = Subsite.getConfig().getThemes();
            Set entrySet = themes.entrySet();
            target.addOption(new Option(DEFAULT_STYLE, DEFAULT_STYLE_LABEL));
            if (entrySet != null) {
                Iterator entries = entrySet.iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    target.addOption(new Option(entry.getKey().toString(),
                                                entry.getValue().toString()),
                                     state);
                }
            }
            target.addOption(new Option(OTHER_STYLE, OTHER_STYLE_LABEL));
        }
    }
}
