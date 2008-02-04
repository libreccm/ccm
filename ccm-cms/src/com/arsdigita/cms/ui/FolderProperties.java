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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ToggleLink;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;



/**
 * A pane for managing {@com.arsdigita.cms.Folder} properties.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #10 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class FolderProperties extends CMSContainer {

    public static final String versionId = "$Id: FolderProperties.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private FolderInfo m_folderInfo;
    private EditFolder m_editFolder;
    private ToggleLink m_editLink;


    public FolderProperties() {
        super();

        m_folderInfo = new FolderInfo();
        add(m_folderInfo);

        m_editFolder = new EditFolder();
        m_editFolder.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( m_editFolder.isCancelled(state) ) {
                        setDisplayMode(state);
                    }
                }
            });
        m_editFolder.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent event) throws FormProcessException {
                    PageState state = event.getPageState();
                    setDisplayMode(state);
                }
            });
        add(m_editFolder);

        m_editLink = m_folderInfo.getEditLink();
        m_editLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    if ( m_editLink.isSelected(state) ) {
                        setEditMode(state);
                    }
                }
            });
    }

    public void register(Page p) {
        super.register(p);
        setDefaultVisibility(p);
    }

    protected void setDefaultVisibility(Page p) {
        p.setVisibleDefault(m_folderInfo, true);
        p.setVisibleDefault(m_editFolder, false);
    }

    protected void setDisplayMode(PageState state) {
        m_editLink.setSelected(state, false);
        m_editFolder.setVisible(state, false);
        m_folderInfo.setVisible(state, true);
    }

    protected void setEditMode(PageState state) {
        m_editFolder.setVisible(state, true);
        m_folderInfo.setVisible(state, false);
    }







    public class FolderInfo extends CMSContainer {

        private ToggleLink m_editLink;


        public FolderInfo() {
            super();

            m_editLink = new ToggleLink("Edit");
            m_editLink.setClassAttr("actionLink");
            m_editLink.setIdAttr("edit_link");
            add(m_editLink);
        }

        public ToggleLink getEditLink() {
            return m_editLink;
        }

        public void generateXML(PageState state, Element parent) {
            if ( isVisible(state) ) {
                Element element = new Element("cms:folderInfo", CMS.CMS_XML_NS);

                Folder folder = getFolder(state);

                element.addAttribute("name", folder.getName());
                element.addAttribute("label", folder.getLabel());

                m_editLink.generateXML(state, element);

                exportAttributes(element);
                parent.addContent(element);
            }
        }

        protected Folder getFolder(PageState state) {
            ContentSection section = CMS.getContext().getContentSection();
            Folder folder = section.getRootFolder();
            return folder;
        }

    }

    public class EditFolder extends CMSForm {

        private final static String NAME   = "name";
        private final static String LABEL  = "label";
        private final static String SUBMIT = "submit";
        private final static String CANCEL = "cancel";

        private final static String ERROR_MSG =
            "Invalid name. A folder with that name already exists.";

        private TextField m_name;
        private TextField m_label;
        private Submit m_submit;
        private Submit m_cancel;

        public EditFolder() {
            super("Edit Folder");

            add(new Label(globalize("cms.ui.name_prompt")));
            m_name = new TextField(new TrimmedStringParameter(NAME));
            m_name.addValidationListener(new NotNullValidationListener());
            m_name.addValidationListener(new URLTokenValidationListener());
            add(m_name);

            add(new Label(globalize("cms.ui.label_prompt")));
            m_label = new TextField(new TrimmedStringParameter(LABEL));
            m_label.addValidationListener(new NotNullValidationListener());

            add(m_label);

            SimpleContainer c = new SimpleContainer();
            m_submit = new Submit(SUBMIT, globalize("cms.ui.save"));
            c.add(m_submit);
            m_cancel = new Submit(CANCEL, globalize("cms.ui.cancel"));
            c.add(m_cancel);
            add(c, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

            addInitListener(new FormInitListener() {
                    public void init(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        initializeFolder(state);
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
                        updateFolder(state);
                    }
                });

            // MP: verify that this is working correctly for updates.
            UniqueItemNameValidationListener un =
                new UniqueItemNameValidationListener(m_name, ERROR_MSG);
            addValidationListener(un);
        }

        public boolean isCancelled(PageState state) {
            return ( m_cancel.isSelected(state) );
        }

        protected void initializeFolder(PageState state) {
            Folder f = getFolder(state);

            m_name.setValue(state, f.getName());
            m_label.setValue(state, f.getLabel());
        }

        protected void updateFolder(PageState state) {
            String name = (String) m_name.getValue(state);
            String label = (String) m_label.getValue(state);

            Folder f = getFolder(state);
            f.setName(name);
            f.setLabel(label);
            f.save();
        }

        protected Folder getFolder(PageState state) {
            ContentSection section = CMS.getContext().getContentSection();
            return section.getRootFolder();
        }

    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(String key) {
        return ContentSectionPage.globalize(key);
    }

}
