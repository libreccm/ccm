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
 *
 */
package com.arsdigita.cms.ui;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SectionLocaleCollection;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.Locale;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.TooManyListenersException;


/**
 * Contains the entire admin UI for a content section.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: SectionConfigurationPage.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SectionConfigurationPage extends CMSPage implements Resettable {

    private SectionInfo m_sectionInfo;
    private EditSection m_editSection;
    private AddLocale m_addLocale;
    private FolderProperties m_properties;

    private ToggleLink m_editLink;
    private ToggleLink m_addLink;

    private ToggleLink m_propertiesLink;


    /**
     * Create the page.
     */
    public SectionConfigurationPage() {
        super("Content Section Configuration", new CMSContainer());

        setIdAttr("section_configuration");

        addNavbar();

        add(new Label(GlobalizationUtil.globalize("cms.ui.content_section_configuration"),  false));

        m_sectionInfo = new SectionInfo();
        add(m_sectionInfo);

        m_editSection = new EditSection();
        m_editSection.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_editSection.isCancelled(state) ) {
                        setDisplayMode(state);
                    }
                }
            });
        m_editSection.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent event) throws FormProcessException {
                    PageState state = event.getPageState();
                    setDisplayMode(state);
                }
            });
        add(m_editSection);

        m_editLink = m_sectionInfo.getEditLink();
        m_editLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    if ( m_editLink.isSelected(state) ) {
                        setEditMode(state);
                    }
                }
            });

        m_addLocale = new AddLocale();
        m_addLocale.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_addLocale.isCancelled(state) ) {
                        setDisplayMode(state);
                    }
                }
            });
        m_addLocale.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent event) throws FormProcessException {
                    PageState state = event.getPageState();
                    setDisplayMode(state);
                }
            });
        add(m_addLocale);

        m_addLink = m_sectionInfo.getAddLink();
        m_addLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    if ( m_addLink.isSelected(state) ) {
                        setAddMode(state);
                    }
                }
            });

        m_properties = new FolderProperties();
        add(m_properties);

        m_propertiesLink = new ToggleLink("Properties");
        m_propertiesLink.setClassAttr("actionLink");
        m_propertiesLink.setIdAttr("properties_link");
        m_propertiesLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    if ( m_propertiesLink.isSelected(state) ) {
                        setPropertiesMode(state);
                    }
                }
            });
        add(m_propertiesLink);

        setDefaultVisibility();
    }

    protected void addNavbar() {
        ColumnPanel cp = new ColumnPanel(2);
        cp.setClassAttr("CMS Admin");

        ContentSectionNavbar csn = new ContentSectionNavbar();
        cp.add(csn, ColumnPanel.LEFT|ColumnPanel.TOP);

        DimensionalNavbar dn = new DimensionalNavbar();
        dn.setDelimiter(" - ");
        dn.add(new Link( new Label(GlobalizationUtil.globalize("cms.ui.my_workspace")),  ContentCenter.getURL()));
        dn.add(new Link( new Label(GlobalizationUtil.globalize("cms.ui.sign_out")),  Utilities.getLogoutURL()));
        // FIXME: Write online help, for the time being, do not offer a link
        // dn.add(new Link( new Label(GlobalizationUtil.globalize("cms.ui.help")),  "help"));
        dn.setClassAttr("top-right");
        cp.add(dn, ColumnPanel.RIGHT|ColumnPanel.TOP);

        add(cp);
    }

    protected void setDefaultVisibility() {
        setVisibleDefault(m_sectionInfo, true);
        setVisibleDefault(m_editSection, false);
        setVisibleDefault(m_addLocale, false);
        setVisibleDefault(m_propertiesLink, true);
        setVisibleDefault(m_properties, false);
    }

    protected void setEditMode(PageState state) {
        m_addLink.setSelected(state, false);
        m_sectionInfo.setVisible(state, false);
        m_editSection.setVisible(state, true);
        m_addLocale.setVisible(state, false);
        m_properties.setVisible(state, false);
        m_propertiesLink.setVisible(state, false);
    }

    protected void setAddMode(PageState state) {
        m_editLink.setSelected(state, false);
        m_sectionInfo.setVisible(state, false);
        m_editSection.setVisible(state, false);
        m_addLocale.setVisible(state, true);
        m_properties.setVisible(state, false);
        m_propertiesLink.setVisible(state, false);
    }

    protected void setDisplayMode(PageState state) {
        m_editLink.setSelected(state, false);
        m_addLink.setSelected(state, false);
        m_propertiesLink.setSelected(state, false);
        m_sectionInfo.setVisible(state, true);
        m_editSection.setVisible(state, false);
        m_addLocale.setVisible(state, false);
        m_properties.setVisible(state, false);
        m_propertiesLink.setVisible(state, true);
    }

    protected void setPropertiesMode(PageState state) {
        m_editLink.setSelected(state, false);
        m_addLink.setSelected(state, false);
        m_sectionInfo.setVisible(state, false);
        m_editSection.setVisible(state, false);
        m_addLocale.setVisible(state, false);
        m_properties.setVisible(state, true);
        m_propertiesLink.setVisible(state, false);
    }


    public void reset(PageState state) {
        setDisplayMode(state);
    }






    public class SectionInfo extends CMSContainer {

        private ToggleLink m_editLink;

        private List m_list;
        private ToggleLink m_addLink;


        public SectionInfo() {
            super();

            m_editLink = new ToggleLink("Edit");
            m_editLink.setClassAttr("actionLink");
            m_editLink.setIdAttr("edit_link");
            add(m_editLink);

            m_list = new List(new LocalesListModelBuilder());
            m_list.setIdAttr("locales_list");
            add(m_list);

            m_addLink = new ToggleLink("Add locales");
            m_addLink.setClassAttr("actionLink");
            m_addLink.setIdAttr("add_locales_link");
            add(m_addLink);
        }

        public ToggleLink getEditLink() {
            return m_editLink;
        }

        public ToggleLink getAddLink() {
            return m_addLink;
        }

        public void generateXML(PageState state, Element parent) {
            if ( isVisible(state) ) {
                Element element = new Element("cms:sectionInfo", CMS.CMS_XML_NS);

                ContentSection section = getContentSection(state);

                element.addAttribute("url", section.getURL());
                element.addAttribute("name", section.getName());

                Locale locale = section.getDefaultLocale();
                if ( locale != null ) {
                    element.addAttribute("locale", locale.toJavaLocale().toString());
                }

                m_editLink.generateXML(state, element);

                // MP: Add this later.
                //m_list.generateXML(state, element);

                m_addLink.generateXML(state, element);

                exportAttributes(element);
                parent.addContent(element);
            }
        }

        protected ContentSection getContentSection(PageState state) {
            return CMS.getContext().getContentSection();
        }

    }


    public class EditSection extends CMSForm {

        private final static String NAME   = "name";
        private final static String DEFAULT_LOCALE = "locale";

        private TextField m_name;
        private SingleSelect m_locale;

        private Submit m_submit;
        private Submit m_cancel;


        public EditSection() {
            super("Edit Content Section");

            add(new Label(GlobalizationUtil.globalize("cms.ui.name")));
            m_name = new TextField(NAME);
            m_name.addValidationListener(new NotNullValidationListener());
            add(m_name);


            add(new Label(GlobalizationUtil.globalize("cms.ui.default_locale")));
            m_locale = new SingleSelect(new BigDecimalParameter(DEFAULT_LOCALE));
            m_locale.addOption(new Option(null, "-- select --"));
            try {
                m_locale.addPrintListener(new PrintListener() {
                        public void prepare(PrintEvent event) {
                            PageState state = event.getPageState();
                            SingleSelect target = (SingleSelect) event.getTarget();
                            addLocales(state, target);
                        }
                    });
            } catch (TooManyListenersException e) {
                throw new UncheckedWrapperException("Too many listeners", e);
            }
            add(m_locale);

            SimpleContainer c = new SimpleContainer();
            m_submit = new Submit("submit", "Save");
            c.add(m_submit);
            m_cancel = new Submit("cancel", "Cancel");
            c.add(m_cancel);
            add(c, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

            addInitListener(new FormInitListener() {
                    public void init(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        initializeContentSection(state);
                    }
                });

            addSubmissionListener(new FormSubmissionListener() {
                    public void submitted(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        if ( isCancelled(state) ) {
                            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.cancel_hit").localize());
                        }
                    }
                });

            addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        updateContentSection(state);
                    }
                });
        }

        protected void addLocales(PageState state, OptionGroup target) {
            ContentSection section = getContentSection(state);
            SectionLocaleCollection slc = section.getLocales();

            while ( slc.next() ) {
                Locale l = slc.getLocale();
                String label = l.toJavaLocale().getDisplayName();
                target.addOption(new Option(l.getID().toString(), label));
            }
        }

        protected void initializeContentSection(PageState state)
            throws FormProcessException {

            ContentSection section = getContentSection(state);

            m_name.setValue(state, section.getName());

            Locale locale = section.getDefaultLocale();
            if ( locale != null ) {
                m_locale.setValue(state, locale.getID());
            } else {
                m_locale.setValue(state, null);
            }
        }

        protected void updateContentSection(PageState state)
            throws FormProcessException {

            ContentSection section = getContentSection(state);
            String name = (String) m_name.getValue(state);

            BigDecimal localeId = (BigDecimal) m_locale.getValue(state);
            Locale locale = null;
            if ( localeId != null ) {
                locale = getLocale(localeId);
            }

            section.setName(name);
            section.setDefaultLocale(locale);
            section.save();
        }

        public boolean isCancelled(PageState state) {
            return ( m_cancel.isSelected(state) );
        }

        protected ContentSection getContentSection(PageState state) {
            ContentSection section = CMS.getContext().getContentSection();
            return section;
        }

        protected Locale getLocale(BigDecimal id) throws FormProcessException {
            Locale locale;
            OID oid = new OID(Locale.BASE_DATA_OBJECT_TYPE, id);
            try {
                locale = new Locale(oid);
            } catch (DataObjectNotFoundException e) {
                throw new FormProcessException(e);
            }
            return locale;
        }

    }



    public class AddLocale extends CMSForm {

        private final static String LOCALES = "locales";

        private CheckboxGroup m_locales;
        private Submit m_submit;
        private Submit m_cancel;


        public AddLocale() {
            super("Add Locales to a Content Section");

            add(new Label(GlobalizationUtil.globalize("cms.ui.locales")));
            m_locales = new CheckboxGroup(LOCALES);
            try {
                m_locales.addPrintListener(new PrintListener() {
                        public void prepare(PrintEvent event) {
                            PageState state = event.getPageState();
                            CheckboxGroup target = (CheckboxGroup) event.getTarget();
                            addLocales(state, target);
                        }
                    });
            } catch (TooManyListenersException e) {
                throw new UncheckedWrapperException("Too many listeners", e);
            }
            add(m_locales);

            SimpleContainer c = new SimpleContainer();
            m_submit = new Submit("submit", "Save");
            c.add(m_submit);
            m_cancel = new Submit("cancel", "Cancel");
            c.add(m_cancel);
            add(c, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

            addInitListener(new FormInitListener() {
                    public void init(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        initializeLocales(state);
                    }
                });

            addSubmissionListener(new FormSubmissionListener() {
                    public void submitted(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        if ( isCancelled(state) ) {
                            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.cancel_hit").localize());
                        }
                    }
                });

            addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        updateLocales(state);
                    }
                });
        }

        protected void addLocales(PageState state, OptionGroup target) {
            ContentSection section = getContentSection(state);

            DataCollection dc =
                SessionManager.getSession().retrieve(Locale.BASE_DATA_OBJECT_TYPE);
            SectionLocaleCollection slc = new SectionLocaleCollection(dc);

            while ( slc.next() ) {
                Locale l = slc.getLocale();
                target.addOption(new Option(l.getID().toString(),
                                            l.toJavaLocale().getDisplayName()));
            }
        }

        protected void initializeLocales(PageState state)
            throws FormProcessException {
            // Do nothing.
        }

        protected void updateLocales(PageState state)
            throws FormProcessException {

            ContentSection section = getContentSection(state);

            String[] locales = (String[]) m_locales.getValue(state);
            if ( locales != null ) {
                for ( int i = 0; i < locales.length; i++ ) {
                    Locale l = getLocale(new BigDecimal(locales[i]));
                    section.addLocale(l);
                }
            }

            section.save();
        }

        public boolean isCancelled(PageState state) {
            return ( m_cancel.isSelected(state) );
        }

        protected ContentSection getContentSection(PageState state) {
            ContentSection section = CMS.getContext().getContentSection();
            return section;
        }

        protected Locale getLocale(BigDecimal id) throws FormProcessException {
            Locale locale;
            OID oid = new OID(Locale.BASE_DATA_OBJECT_TYPE, id);
            try {
                locale = new Locale(oid);
            } catch (DataObjectNotFoundException e) {
                throw new FormProcessException(e);
            }
            return locale;
        }

    }

}
