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
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
import com.arsdigita.cms.contenttypes.Member;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table used to select the Director of
 * an organizational unit.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationalUnitChooseDirectorTable extends Table implements TableActionListener {

    private final static Logger logger = Logger.getLogger(OrganizationalUnitChooseDirectorTable.class);
    private static final String COL_PERSON = "Person";
    private static final String COL_CHOOSE_PERSON = "Select";
    private ItemSelectionModel m_selOrgaUnit;
    private OrganizationalUnitChooseDirectorStep m_parent;

    public OrganizationalUnitChooseDirectorTable(ItemSelectionModel selOrgaUnit, OrganizationalUnitChooseDirectorStep parent) {

        super();

        this.m_parent = parent;
        this.m_selOrgaUnit = selOrgaUnit;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(0, COL_PERSON));
        model.add(new TableColumn(1, COL_CHOOSE_PERSON));

        setEmptyView(new Label("New Persons available."));
        setModelBuilder(new OrganizationalUnitChooseDirectorTableModelBuilder());

        model.get(1).setCellRenderer(new SelectCellRenderer());

        addTableActionListener(this);
    }

    private class OrganizationalUnitChooseDirectorTableModelBuilder extends LockableImpl implements TableModelBuilder {

        public TableModel makeModel(Table t, PageState s) {
            t.getRowSelectionModel().clearSelection(s);
            DataCollection m_persons = SessionManager.getSession().retrieve(Member.BASE_DATA_OBJECT_TYPE);
            m_persons.addEqualsFilter(ContentItem.VERSION, ContentItem.DRAFT);
            return new OrganizationalUnitChooseDirectorTableModel(t, m_persons);
        }
    }

    private class OrganizationalUnitChooseDirectorTableModel implements TableModel {

        private Table m_table;
        private DataCollection m_persons;
        private Member m_person;

        private OrganizationalUnitChooseDirectorTableModel(Table t, DataCollection persons) {
            m_table = t;
            m_persons = persons;
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        public boolean nextRow() {
            if (m_persons.next()) {
                m_person = (Member) DomainObjectFactory.newInstance(m_persons.getDataObject());
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
            link.setConfirmation("Select this person?");
            return link;
        }

    }

    public void cellSelected(TableActionEvent e) {
        PageState state = e.getPageState();
        TableColumn col = getColumnModel().get(e.getColumn().intValue());
        String colName = (String) col.getHeaderValue();

        logger.debug("Cell selected invoked...");

        if (COL_CHOOSE_PERSON.equals(colName)) {
            logger.debug ("Col is choose person col...");
            m_parent.showDisplayPane(state);
            BigDecimal personID = new BigDecimal (e.getRowKey().toString());
            OrganizationalUnit orgaunit = (OrganizationalUnit) this.m_selOrgaUnit.getSelectedObject(state);
            Member person = (Member) DomainObjectFactory.newInstance(new OID(Member.BASE_DATA_OBJECT_TYPE, personID));
            orgaunit.setDirection(person);
        }
    }

    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
