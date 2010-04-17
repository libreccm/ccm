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


import java.math.BigDecimal;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.PageLocations;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Web;

/**
 * Displays all the content sections in table, with links to the admin
 * and public pages. Also displays a form for each content section to
 * create an object of a given type. The list of available types is
 * retrieved for each content section.
 *
 * <p>
 *
 * This class is a container for two other components: a form and a
 * table. The form represents the drop down list of the content types
 * available in a particular content section. It is an extension of
 * the {@link com.arsdigita.cms.ui.authoring.NewItemForm}. The table
 * displays each content section in one row, along with the specified
 * form. The same form is reused in every row of the table.
 *
 * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
 * @version $Id: ContentSectionContainer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContentSectionContainer extends CMSContainer {

    private ContentSectionTable m_table;
    private FormContainer m_formContainer;

    private SingleSelectionModel m_typeSel;
    private SingleSelectionModel m_sectionSel;

    /**
     * Constructs a new ContentSectionContainer which containts:
     *
     * <ul>
     *   <li> SimpleContainer (to contain the form)
     *   <ul>
     *   <li> Form (for creating a new content item in each section)
     *   </ul>
     *   <li> Table (Displays all content sections)
     * </ul>
     *
     * @param typeSel passthrough to {@link NewItemForm}
     * @param sectionSel passthrough to {@link NewItemForm}
     **/
    public ContentSectionContainer(SingleSelectionModel typeSel,
                                   SingleSelectionModel sectionSel) {
        super();

        m_typeSel = typeSel;
        m_sectionSel = sectionSel;

      	m_formContainer = new FormContainer();
       	add(m_formContainer);
        m_table = new ContentSectionTable();
        add(m_table);
    }

    public void register(Page p) {
        super.register(p);
       	p.setVisibleDefault(m_formContainer, false);
    }

    private class FormContainer extends CMSContainer {

        private StaticNewItemForm m_form;
        private BigDecimalParameter m_sectionIdParam;

        private FormContainer() {
            super();
            m_sectionIdParam = new BigDecimalParameter("sectionId");
            m_form = new StaticNewItemForm(m_sectionIdParam);

            m_form.addSubmissionListener(new FormSubmissionListener() {
                    /**
                     * Cancels the form if the user lacks the "create new items" privilege.
                     */
                    public void submitted(FormSectionEvent event)
                        throws FormProcessException {
                        PageState state = event.getPageState();
                        StaticNewItemForm form = (StaticNewItemForm) event.getSource();

                        ContentSection section = form.getContentSection(state);
                        SecurityManager sm = new SecurityManager(section);
                        Folder folder = null;
                        User user = Web.getContext().getUser();
                        if ( user != null ) {
                            folder = Folder.getUserHomeFolder(user,section);
                        }
                        if ( folder == null ) {
                            folder = section.getRootFolder();
                        }

                        if (! sm.canAccess(state.getRequest(), SecurityManager.NEW_ITEM, folder)) {
                            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.insufficient_privileges").localize());
                        }
                    }
                });

            m_form.addProcessListener(new FormProcessListener() {
                    /**
                     * Process listener: redirects to the authoring kit to create a new item.
                     */
                    public void process(FormSectionEvent e) throws FormProcessException {
                        StaticNewItemForm form = (StaticNewItemForm) e.getSource();
                        PageState state = e.getPageState();

                        BigDecimal typeId = form.getTypeID(state);
                        if( typeId != null ) {
                            BigDecimal sectionId = form.getContentSectionID(state);
                            m_sectionSel.setSelectedKey(state, sectionId);
                            m_typeSel.setSelectedKey(state, typeId);
                        }
                    }
                });

            add(m_form);
        }

        public void register(Page p) {
            super.register(p);
            p.addComponentStateParam(this, m_sectionIdParam);
        }

        public StaticNewItemForm getNewItemForm() {
            return m_form;
        }
    }

    private static class StaticNewItemForm extends NewItemForm {

        private Hidden m_sectionIDParamWidget;

        public StaticNewItemForm(BigDecimalParameter sectionParam) {
            super("StaticNewItemForm");
            m_sectionIDParamWidget = new Hidden(sectionParam);
            add(m_sectionIDParamWidget);
            setProcessInvisible(true);
        }


        /**
         * Sets the id of the content section in this form. This ID is
         * used to generate a list of available content types in the
         * section.
         *
         * @param state The current page state.
         * @param id The id of the ContentSection for which this form
         * should display a list of content types
         *
         * @pre ( state != null  && id != null )
         **/
        public void setSectionId(PageState state, BigDecimal id) {
            Assert.exists(id);
            m_sectionIDParamWidget.setValue(state, id);
        }


        /**
         * Retrieves the content section for this form given the specified
         * page state. This method will return null if there is no content
         * section.
         *
         * @param state The current page state.
         * @return The current content section or null if the section does
         * not exist
         *
         * @pre ( state != null )
         **/
        public ContentSection getContentSection(PageState state) {
            BigDecimal id = getContentSectionID(state);
            Assert.exists(id);
            ContentSection section;
            try {
                section = new ContentSection(id);
            } catch (DataObjectNotFoundException ex) {
                section = null;
            }
            return section;
        }


        /**
         * Retrieves the ID of the content section for this form given the
         * specified page state. This method will return null if no
         * content section id has been set.
         *
         * @param state The current page state.
         * @return The id of the content section or null if it has not
         * been set.
         *
         * @pre ( state != null )
         **/
        private BigDecimal getContentSectionID(PageState state) {
            return (BigDecimal) m_sectionIDParamWidget.getValue(state);
        }

    }


    /**
     * A table that displays all content sections, with links to their
     * locations and admin pages and a {@link NewItemForm} next to each
     * section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     * @version $Revision: #13 $ $DateTime: 2004/08/17 23:15:09 $
     **/
    private class ContentSectionTable extends Table {

        private static final String COLUMN_SECTION = "Section";
        private static final String COLUMN_LOCATION = "Public Site";
        private static final String COLUMN_ACTION = "Action";

        /**
         * Constructs a new ContentSectionTable, using a default table
         * model builder.
         **/
        private ContentSectionTable() {
            super();

            Label emptyView = new Label
                ("There are currently no content sections installed.");
            emptyView.setFontWeight(Label.ITALIC);
            setEmptyView(emptyView);

            setClassAttr("dataTable");

            // add columns to the table
            TableColumnModel columnModel = getColumnModel();
            TableColumn contentSectionColumn = new TableColumn(0, COLUMN_SECTION);
            TableColumn locationColumn = new TableColumn(1, COLUMN_LOCATION);
            TableColumn actionColumn = new TableColumn(2, COLUMN_ACTION);

            contentSectionColumn.setCellRenderer(new AdminURLTableCellRenderer());
            locationColumn.setCellRenderer(new URLTableCellRenderer());
            actionColumn.setCellRenderer(new ActionTableCellRenderer());

            columnModel.add(contentSectionColumn);
            columnModel.add(locationColumn);
            columnModel.add(actionColumn);

            setModelBuilder(new ContentSectionTableModelBuilder());
        }


        /**
         * An ContentSections table model builder
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         **/
        private class ContentSectionTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

            public TableModel makeModel(Table table, PageState state) {
                table.getRowSelectionModel().clearSelection(state);
                return new ContentSectionTableModel((ContentSectionTable) table, state);
            }
        }


        /**
         * An ContentSections table model
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         **/
        private class ContentSectionTableModel implements TableModel {

            private ContentSectionTable m_table;
            private TableColumnModel m_columnModel;
            private PageState m_state;

            private ContentSectionCollection m_contentSections;
            private ContentSection m_section;

            private ContentSectionTableModel(ContentSectionTable table,
                                             PageState state) {
                m_table = table;
                m_columnModel = table.getColumnModel();
                m_state = state;

                // retrieve all Content Sections
                m_contentSections = getContentSectionCollection();
                PermissionService.filterObjects(m_contentSections,
                                                PrivilegeDescriptor.READ,
                                                Kernel.getContext().getParty().getOID());
            }


            /**
             * Returns a collection of ContentSections to display in this
             * table. This implementation orders the content sections by
             * <code>lower(label)</code>.
             **/
            protected ContentSectionCollection getContentSectionCollection() {
                ContentSectionCollection sections = ContentSection.getAllSections();
                sections.addOrder("lower(label)");
                return sections;
            }

            public int getColumnCount() {
                return m_columnModel.size();
            }

            public boolean nextRow() {
                if (m_contentSections.next()) {
                    m_section = m_contentSections.getContentSection();
                    return true;
                }
                return false;
            }

            /**
             * By default, we return null. For the section, location, and
             * action columns, we return the current Content Section if
             * there is one.
             *
             * @param columnIndex The index of the current column
             **/
            public Object getElementAt(int columnIndex) {
                if(m_columnModel == null || m_section == null) {
                    return null;
                }

                TableColumn tc = m_columnModel.get(columnIndex);
                String columnName = (String) tc.getHeaderValue();

                Object result = m_section;
                if (columnName.equals(COLUMN_SECTION) ||
                    columnName.equals(COLUMN_LOCATION) ||
                    columnName.equals(COLUMN_ACTION)) {
                    result = m_section;
                }
                return result;
            }

            public Object getKeyAt(int columnIndex) {
                return m_section.getID();
            }

            /**
             * Returns the table associated with this table model.
             **/
            protected Table getTable() {
                return m_table;
            }

            /**
             * Returns the current page state
             **/
            protected PageState getPageState() {
                return m_state;
            }
        }


        /**
         * Sets the hidden parameter in the form containers form to
         * the id of the current section. Then returns the form for
         * display, but only if the user has permission to create new
         * items in the current section.
         *
         * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
         **/
        private class ActionTableCellRenderer implements TableCellRenderer {
            public Component getComponent(Table table, PageState state, Object value,
                                          boolean isSelected, Object key,
                                          int row, int column) {
                ContentSection section = (ContentSection) value;
                Folder folder = null;
                User user = Web.getContext().getUser();
                if ( user != null ) {
                    folder = Folder.getUserHomeFolder(user,section);
                }
                if ( folder == null ) {
                    folder = section.getRootFolder();
                }
                // If the user has no access, return an empty Label
                SecurityManager sm = new SecurityManager(section);

                if (! sm.canAccess(state.getRequest(), SecurityManager.NEW_ITEM, folder) 
                		|| !ContentSection.getConfig().getAllowContentCreateInSectionListing()) {
                    return new Label("&nbsp;", false);
                } else {
                    // set the value of the sectionIdParameter in the form
                    // to this section
                    m_formContainer.getNewItemForm().setSectionId(state, section.getID());
                    return m_formContainer.getNewItemForm();
                }
            }
        }
    }


    /**
     * Generates the correct URL to the public pages for a content
     * section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     **/
    public static class URLTableCellRenderer implements TableCellRenderer {

        private static final String URL_STUB = "/";

        /**
         * The object passed in is the current content section. This
         * returns a Link whose name and target are the url to the
         * public pages.
         **/
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ContentSection section = (ContentSection) value;
            String baseUrl = getBaseURL();
            String name = section.getName();

            // If the user has no access, return a Label instead of a Link
            SecurityManager sm = new SecurityManager(section);

            if (sm.canAccess(state.getRequest(), SecurityManager.PUBLIC_PAGES)) {
                return new Link(baseUrl + name + "/", baseUrl + generateURL(name));
            } else {
                return new Label(baseUrl + name + "/", false);
            }
        }

        /**
         * Trims leading slashes, if any, on the specified string.
         *
         * @param string The string for which we want to remove
         * leading slashes
         **/
        protected String trimSlashes(String string) {
            while (string != null && string.length() > 0 &&
                   string.charAt(0) == '/') {
                string = string.substring(1);
            }
            return string;
        }

        /**
         * Generates the url for the specified prefix. Always returns
         * something that does not start with a forward slash.
         *
         * @param prefix The prefix of the URL
         **/
        protected String generateURL(String prefix) {
            return trimSlashes(prefix) + URL_STUB;
        }

        /**
         * Returns the current url stub from the webapp context and
         * the requested site node. Always returns something that ends
         * with a forward slash.
         **/
        protected String getBaseURL() {
            // Generate the base URL stub.
            StringBuffer buf = new StringBuffer(15);
            buf.append(Utilities.getWebappContext())
                .append(SiteNode.getRootSiteNode().getURL());
            String url = buf.toString();
            if (url.endsWith("/")) {
                return url;
            }
            return url + "/";
        }
    }


    /**
     * Generates the correct URL to the admin pages for a content
     * section.
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     **/
    public static class AdminURLTableCellRenderer extends URLTableCellRenderer {

        /**
         * The object passed in is the current content section
         **/
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            ContentSection section = (ContentSection) value;

            // If the user has no access, return a Label instead of a Link
            SecurityManager sm = new SecurityManager(section);

            if (sm.canAccess(state.getRequest(), SecurityManager.ADMIN_PAGES)) {
                return new Link(section.getName(),
                                generateURL(section.getPath() + "/"));
            } else {
                return new Label(section.getName(), false);
            }
        }

        /**
         * Generates the admin url for the specified prefix. Always
         * returns something that does not start with a forward slash.
         *
         * @param prefix The prefix of the URL
         **/
        protected String generateURL(String prefix) {
            return prefix + PageLocations.SECTION_PAGE;
        }
    }

}
