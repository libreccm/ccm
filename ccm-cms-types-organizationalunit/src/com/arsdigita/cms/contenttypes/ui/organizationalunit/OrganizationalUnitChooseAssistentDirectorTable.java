/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center for Social Policy Research of the University of Bremen
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
package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Person;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Table used to select the assistent director of
 * an organizational unit.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitChooseAssistentDirectorTable extends Table implements TableActionListener {

    private static final String COL_PERSON = "Person";
    private static final String COL_CHOOSE_PERSON = "Select";
    private ItemSelectionModel m_selOrgaUnit;
    private OrganizationalUnitChooseAssistentDirectorStep m_parent;

    public OrganizationalUnitChooseAssistentDirectorTable(ItemSelectionModel selOrgaUnit, OrganizationalUnitChooseAssistentDirectorStep parent) {

        super();

        this.m_parent = parent;
        this.m_selOrgaUnit = selOrgaUnit;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(0, COL_PERSON));
        model.add(new TableColumn(1, COL_CHOOSE_PERSON));

        setEmptyView(new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.assistentdirector.noPersonsAvailable")));
        setModelBuilder(new OrganizationalUnitChooseAssistentDirectorTableModelBuilder());

        model.get(1).setCellRenderer(new SelectCellRenderer());

        addTableActionListener(this);
    }

    private class OrganizationalUnitChooseAssistentDirectorTableModelBuilder extends LockableImpl implements TableModelBuilder {

        public TableModel makeModel(Table t, PageState s) {
            t.getRowSelectionModel().clearSelection(s);
            DataCollection m_persons = SessionManager.getSession().retrieve(Person.BASE_DATA_OBJECT_TYPE);
            m_persons.addEqualsFilter(ContentItem.VERSION, ContentItem.DRAFT);
            return new OrganizationalUnitChooseAssistentDirectorTableModel(t, m_persons);
        }
    }

    private class OrganizationalUnitChooseAssistentDirectorTableModel implements TableModel {

        private Table m_table;
        private DataCollection m_persons;
        private Person m_person;

        private OrganizationalUnitChooseAssistentDirectorTableModel(Table t, DataCollection persons) {
            m_table = t;
            m_persons = persons;
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        public boolean nextRow() {
            if (m_persons.next()) {
                m_person = (Person) DomainObjectFactory.newInstance(m_persons.getDataObject());
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    StringBuilder personNameBuilder = new StringBuilder();
                    if ((m_person.getTitlePre() != null) &&
                            m_person.getTitlePre().length() > 0) {
                        personNameBuilder.append(m_person.getTitlePre());
                        personNameBuilder.append(" ");
                    }
                    if ((m_person.getGivenName() != null) &&
                            m_person.getGivenName().length() > 0) {
                        personNameBuilder.append(m_person.getGivenName());
                        personNameBuilder.append(" ");
                    }
                    if ((m_person.getSurname() != null) &&
                            m_person.getSurname().length() > 0) {
                        personNameBuilder.append(m_person.getSurname());
                        personNameBuilder.append(" ");
                    }
                    if ((m_person.getTitlePost() != null) &&
                            m_person.getTitlePost().length() > 0) {
                        personNameBuilder.append(m_person.getTitlePost());
                    }
                    return personNameBuilder.toString();
                case 1:
                    return COL_CHOOSE_PERSON;
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_person.getID();
        }
    }

    private class SelectCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            ControlLink link = new ControlLink(value.toString());
            link.setConfirmation(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.assistentdirector.selectThisPerson").localize().toString());
            return link;
        }

    }

    public void cellSelected(TableActionEvent e) {
        PageState state = e.getPageState();
        TableColumn col = getColumnModel().get(e.getColumn().intValue());
        String colName = (String) col.getHeaderValue();

        if (COL_CHOOSE_PERSON.equals(colName)) {
            m_parent.showDisplayPane(state);
            BigDecimal personID = new BigDecimal (e.getRowKey().toString());
            OrganizationalUnit orgaunit = (OrganizationalUnit) this.m_selOrgaUnit.getSelectedObject(state);
            Person person = (Person) DomainObjectFactory.newInstance(new OID(Person.BASE_DATA_OBJECT_TYPE, personID));
            orgaunit.setAssistentDirection(person);
        }
    }

    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}