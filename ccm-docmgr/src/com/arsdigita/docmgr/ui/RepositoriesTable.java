/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;

/**
 * This class has dual functionality as the name implies.
 * Firstly, it lists all the repositories a user has
 * acces to. These are currently all repositories of fellow
 * group members.
 * Secondly, it contains a checkbox group to select/unselect
 * a repository to be mounted. (One's own repository is always
 * mounted).
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class RepositoriesTable implements DMConstants {

    private static String REPOSITORIES_IDS
        = "repositories-ids";
    private static String REPOSITORIES_SUBSCRIBED_IDS =
        "repositories-subscribed-ids";

    static String[] s_tableHeaders = {
        "",
        "Portal",
        "Files"
    };

    private CheckboxGroup m_checkboxGroup;
    private Hidden m_subscribed;
    private ArrayParameter m_sourcesSubscribed;

    private ArrayParameter m_sources;
    private Table m_table;
    private TableModelBuilder m_tableBuilder;

    // this query only once per page request
    private RequestLocal m_query;


    /**
     * Default constructor
     */

    public RepositoriesTable() {

        // store query result once
        m_query = new RequestLocal() {
                protected Object initialValue(PageState s) {
                    User viewer = DMUtils.getUser(s);
                    Session session = SessionManager.getSession();
                    DataQuery query  = session.retrieveQuery(GET_REPOSITORIES);
                    query.setParameter("userID", viewer.getID());
                    Vector result = new Vector();
                    while(query.next()) {

                        Object[] row = new Object[4];
                        row[0] = query.get(REPOSITORY_ID);
                        row[1] = query.get(IS_MOUNTED);
                        row[2] = query.get(NAME);
                        row[3] = query.get(NUM_FILES);
                        result.add(row);
                    }
                    query.close();
                    return result;
                }
            };

        m_tableBuilder = new RepositoriesTableModelBuilder(this);
        m_table = new Table(m_tableBuilder,
                            s_tableHeaders);

        m_sources = new ArrayParameter(new BigDecimalParameter
                                       (REPOSITORIES_IDS));
        m_checkboxGroup = new CheckboxGroup(m_sources);

        m_sourcesSubscribed = new ArrayParameter(new BigDecimalParameter
                                                 (REPOSITORIES_SUBSCRIBED_IDS));
        m_subscribed = new Hidden(m_sourcesSubscribed);

        setCellRenderers();
        m_table.setClassAttr("AlternateTable");
    }

    /**
     * Get the sotred query results
     */
    public Iterator getQuery(PageState state) {
        return ((Vector) m_query.get(state)).iterator();
    }

    public void setQuery(PageState state, ArrayList list) {
        m_query.set(state, list);
    }

    Table getTable() {
        return m_table;
    }

    CheckboxGroup getCheckboxGroup() {
        return m_checkboxGroup;
    }

    Hidden getSubscribedHidden() {
        return m_subscribed;
    }

    Object[] getSelectedIDs(PageState s) {
        Iterator it = getQuery(s);
        ArrayList a = new ArrayList();
        BigDecimal id = null;
        while(it.hasNext()) {
            Object[] field = (Object [])it.next();
            if( 1 == ((BigDecimal) field[1]).intValue()) {
                id = (BigDecimal) field[0];
                a.add( id.toString());
            }
        }
        return a.toArray();
    }

    public void register(Page p) {
        m_table.register(p);
        p.addComponentStateParam(m_table, m_sources);
        p.addComponentStateParam(m_table, m_sourcesSubscribed);
    }

    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException();
    }

    private void setCellRenderers() {
        m_table.getColumn(0).setCellRenderer(new CheckBoxRenderer());
    }

    private final class CheckBoxRenderer implements TableCellRenderer {
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            BigDecimal id = (BigDecimal) key;
            String optionName = m_sources.marshalElement(id.abs());


            Option option = new Option(optionName, "");
            option.setGroup(m_checkboxGroup);
            return option;
        }
    }


}


class  RepositoriesTableModelBuilder
    extends LockableImpl implements TableModelBuilder {

    RepositoriesTable m_parent;

    RepositoriesTableModelBuilder(RepositoriesTable parent) {
        m_parent = parent;
    }


    public TableModel makeModel(Table t, PageState state) {

        return new RepositoriesTableModel(state);
    }


    class RepositoriesTableModel implements TableModel, DMConstants {

        private Iterator m_it;
        private Object[] m_field = new Object[4];

        RepositoriesTableModel(PageState state) {
            m_it = m_parent.getQuery(state);
        }

        public int getColumnCount() {
            return 3; // same length as header String[]
        }

        public Object getElementAt(int columnIndex) {
            switch(columnIndex) {
            case 0 :
                int isMounted = ((BigDecimal) m_field[1]).intValue();
                if(isMounted == 1) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            case 1:
                // return name
                return m_field[2];
            case 2:
                // return num of files
                return m_field[3];
            default:
                break;
            }
            return null;
        }

        // always return the ID as key
        public Object getKeyAt(int columnIndex) {
            return m_field[0];
        }

        public boolean nextRow() {
            if(m_it.hasNext()) {
                m_field = (Object [])m_it.next();
                return true;
            } else {
                return false;
            }
        }
    }
}
