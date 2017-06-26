/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table for showing the persons associated with an organisation.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonsTable extends Table implements
        TableActionListener {

    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitPersonsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_EDIT_LINK = "table_col_edit_link";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;
    private GenericOrganizationalUnitPersonSelector personSelector;

    public GenericOrganizationalUnitPersonsTable(
            final ItemSelectionModel itemModel,
            final GenericOrganizationalUnitPersonSelector personSelector) {
        super();
        m_itemModel = itemModel;
        this.personSelector = personSelector;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.persons.none")));
        TableColumnModel tabModel = getColumnModel();

        tabModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.genericorgaunit.persons.name"),
                TABLE_COL_EDIT));
        tabModel.add(new TableColumn(
                1,
                ContenttypesGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.genericorgaunit.persons.role")));
        tabModel.add(
                new TableColumn(
                        2,
                        ContenttypesGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.genericorgaunit.persons.status")));
        tabModel.add(
                new TableColumn(
                        3,
                        ContenttypesGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.genericorganunit.persons.edit"),
                        TABLE_COL_EDIT_LINK));
        tabModel.add(
                new TableColumn(
                        4,
                        ContenttypesGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.genericorgaunit.persons.delete"),
                        TABLE_COL_DEL));

        setModelBuilder(
                new GenericOrganizationalUnitTableModelBuilder(itemModel));

        tabModel.get(0).setCellRenderer(new EditCellRenderer());
        tabModel.get(3).setCellRenderer(new EditLinkCellRenderer());
        tabModel.get(4).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class GenericOrganizationalUnitTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private final ItemSelectionModel m_itemModel;

        public GenericOrganizationalUnitTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);
            return new GenericOrganizationalUnitTableModel(table,
                                                           state,
                                                           orgaunit);
        }

    }

    private class GenericOrganizationalUnitTableModel implements TableModel {

        private Table m_table;
        private GenericOrganizationalUnitPersonCollection m_personsCollection;
        private GenericPerson m_person;

        private GenericOrganizationalUnitTableModel(
                Table table,
                PageState state,
                GenericOrganizationalUnit orgaunit) {
            m_table = table;
            m_personsCollection = orgaunit.getPersons();
            s_log.debug(String.format("m_personsCollection.size() = %d", m_personsCollection.
                                      size()));
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_personsCollection != null)
                        && m_personsCollection.next()) {
                m_person = m_personsCollection.getPerson();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_person.getFullName();
                case 1:
                    RelationAttributeCollection role = new RelationAttributeCollection(
                            getRoleAttributeName(),
                            m_personsCollection.getRoleName());
                    if (role.next()) {
                        String roleName;
                        roleName = role.getName();
                        role.close();
                        return roleName;
                    } else {
                        return new Label(ContenttypesGlobalizationUtil.globalize(
                                "cms.ui.unknownRole"));
                    }
                case 2:
                    final GlobalizedMessage statusMsg = new GlobalizedMessage(
                            m_personsCollection.getStatus(),
                            getStatusAttributeName(),
                            new RelationAttributeResourceBundleControl());

                    return new Label(statusMsg);

//                    RelationAttributeCollection status = new RelationAttributeCollection(
//                            getStatusAttributeName(),
//                            m_personsCollection.getStatus());
//
////                    if (status.next()) {
//                        String statusName;
//                        statusName = status.getName();
//                        status.close();
//                        return statusName;
//                    } else {
//                        return new Label(ContenttypesGlobalizationUtil.globalize(
//                                "cms.ui.unknownStatus"));
//                    }
                case 3:
                    return new Label(ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.edit_assoc"));
                case 4:
                    return new Label(ContenttypesGlobalizationUtil.globalize(
                            "cms.ui.delete"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_person.getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(
                            state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        orgaunit);
            if (canEdit) {
                GenericPerson person;
                try {
                    person = new GenericPerson((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }
                ContentSection section = person.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(String.format("%s",
                                                   value.toString()),
                                     resolver.generateItemURL(state,
                                                              person,
                                                              section,
                                                              person.getVersion()));

                return link;
            } else {
                GenericPerson person;
                try {
                    person = new GenericPerson((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                Label label = new Label(String.format("%s",
                                                      value.toString(),
                                                      person.getLanguage()));

                return label;
            }
        }

    }

    private class EditLinkCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            SecurityManager securityManager = CMS.getSecurityManager(state);
            GenericOrganizationalUnit orgaUnit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        SecurityManager.EDIT_ITEM,
                                                        orgaUnit);

            if (canEdit) {
                ControlLink link = new ControlLink((Label) value);
                return link;
            } else {
                return (Label) value;
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager = Utilities.getSecurityManager(state);
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(
                            state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        SecurityManager.DELETE_ITEM,
                                                        orgaunit);
            if (canEdit) {
                ControlLink link = new ControlLink((Label)value);
                link.setConfirmation(ContenttypesGlobalizationUtil.
                        globalize(
                                "cms.contenttypes.ui.genericorgaunit.persons.confirm_delete"));
                return link;
            } else {
                return (Label) value;
            }
        }

    }

    @Override
    public void cellSelected(TableActionEvent event) {
        s_log.debug("Cell selected.");
        PageState state = event.getPageState();
        s_log.debug(String.format("RowKey = %s", event.getRowKey().toString()));
        s_log.debug(String.format("Selected column: %d", event.getColumn().
                                  intValue()));

        GenericPerson person = new GenericPerson(new BigDecimal(event.getRowKey().
                toString()));

        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) m_itemModel.
                getSelectedObject(state);

        GenericOrganizationalUnitPersonCollection persons = orga.getPersons();

        TableColumn col = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(col.getHeaderKey().toString())) {
        } else if (TABLE_COL_EDIT_LINK.equals(col.getHeaderKey().toString())) {
            while (persons.next()) {
                if (persons.getPerson().equals(person)) {
                    break;
                }
            }

            personSelector.setSelectedPerson(state, person);
            personSelector.setSelectedPersonRole(state, persons.getRoleName());
            personSelector.setSelectedPersonStatus(state, persons.getStatus());

            persons.close();

            personSelector.showEditComponent(state);

        } else if (TABLE_COL_DEL.equals(col.getHeaderKey().toString())) {
            s_log.debug("Removing person assoc...");
            orga.removePerson(person);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    protected String getRoleAttributeName() {
        return "GenericOrganizationalUnitRole";
    }

    protected String getStatusAttributeName() {
        return "GenericOrganizationalUnitMemberStatus";
    }

}
