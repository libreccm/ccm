/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.webdevsupport;

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.caching.CacheTable.TimestampedEntry;
import com.arsdigita.util.LockableImpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-01-13
 * @version $Revision: #8 $ $DateTime: 2004/08/16 18:10:38 $
 */
final class CacheTableBrowser extends SimpleContainer {

    private final Table m_listOfTables;
    private final CacheTableContents m_tableContents;

    CacheTableBrowser() {
        m_listOfTables = new ListOfCacheTables();
        m_tableContents = new CacheTableContents();
        m_listOfTables.addTableActionListener(new TableActionListener() {
                public void cellSelected(TableActionEvent ev) {
                    int column = ev.getColumn().intValue();
                    switch (column) {
                      case 0:
                    PageState state = ev.getPageState();
                    m_tableContents.setTableID(state,(String) ev.getRowKey());
                        break;
                      case 5:
                	CacheTable.BROWSER.purge((String) ev.getRowKey());
  	              	break;
                    default:
	              break;
                    }
                }

                public void headSelected(TableActionEvent e) { }
            });

        add(m_listOfTables);
        add(m_tableContents);
    }

    /**
     * Shows all the CacheTable instances that have been instantiated.
     **/
    private static class ListOfCacheTables extends Table {
        public ListOfCacheTables() {
            super(new ModelBuilder(),
                  new String[] {"Cache table", "Max size", "Current size",
                                "Max age, sec", "Shared", "Purge"});
        }

        private static class ModelBuilder extends LockableImpl
            implements TableModelBuilder {

            public TableModel makeModel(Table table,
                                        PageState state) {

                List keys = new ArrayList(CacheTable.BROWSER.getTableIDs());
                Collections.sort(keys);
                return new Model(keys.iterator());
            }
        }

        private static class Model implements TableModel {
            private final Iterator m_keys;
            private String m_key;

            Model(Iterator keys) {
                m_keys = keys;
            }

            public boolean nextRow() {
                if (m_keys.hasNext()) {
                    m_key = (String) m_keys.next();
                    return true;
                }
                m_key = null;
                return false;
            }
            public int getColumnCount() {
                return 6;
            }

            public Object getElementAt(int columnIndex) {
                switch (columnIndex) {
                case 0:
                    if (CacheTable.BROWSER.getCurrentSize(m_key) == 0) {
                        return new Label(m_key);
                    } else {
                        return new ControlLink(new Label(m_key));
                    }
                case 1:
                    return String.valueOf(CacheTable.BROWSER.getMaxSize(m_key));
                case 2:
                    return String.valueOf(CacheTable.BROWSER.getCurrentSize(m_key));
                case 3:
                    return String.valueOf(CacheTable.BROWSER.getMaxAge(m_key));
                case 4:
                    return String.valueOf(CacheTable.BROWSER.isShared(m_key));
                case 5:
                    if (CacheTable.BROWSER.isPurgeAllowed(m_key)) {
                      return new ControlLink(new Label("purge"));
                    } else {
                      return new Label("can't be purged");
                    }
                default:
                    throw new IllegalArgumentException
                        ("columnIndex: " + columnIndex);
                }

            }

            public Object getKeyAt(int columnIndex) {
                return m_key;
            }
        }
    }

    /**
     * Shows the contents of a single CacheTable
     **/
    private static class CacheTableContents extends Table {
        private final static String TABLE_ID_ATTR =
            CacheTableContents.class.getName();

        private String m_tableID;

        public CacheTableContents() {
            super(new ModelBuilder(),
                  new String[] { "Key", "Class", "Value", "Hits", "Hash code", "Timestamp" });
        }

        public void setTableID(PageState state, String tableID) {
            state.getRequest().setAttribute(TABLE_ID_ATTR, tableID);
        }

        private static class ModelBuilder extends LockableImpl
            implements TableModelBuilder {

            public TableModel makeModel(Table table,
                                        PageState state) {

                String tableID =
                    (String) state.getRequest().getAttribute(TABLE_ID_ATTR);
                return new Model(tableID);
            }
        }

        private static class Model implements TableModel {
            private final static DateFormat FORMATTER =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            private final Iterator m_entries;
            private TimestampedEntry m_entry;
            private int m_max_age;

            Model(String tableID) {
                if ( tableID==null ) {
                    m_entries = Collections.EMPTY_LIST.iterator();
                    return;
                }

                Set set = CacheTable.BROWSER.getEntrySet(tableID);
                if ( set==null ) {
                    m_entries = Collections.EMPTY_LIST.iterator();
                    return;
                }

                m_max_age = CacheTable.BROWSER.getMaxAge(tableID);
                
                List list = new ArrayList(set);
                Collections.sort(list, new Comparator() {
                        public int compare(Object o1, Object o2) {
                            if ( o1==null || o2==null ) {
                                throw new NullPointerException();
                            }
                            TimestampedEntry t1 = (TimestampedEntry) o1;
                            TimestampedEntry t2 = (TimestampedEntry) o2;
                            return t1.getKey().compareTo(t2.getKey());
                        }
                    });
                m_entries = list.iterator();

            }

            public boolean nextRow() {
                if (m_entries.hasNext()) {
                    m_entry = (TimestampedEntry) m_entries.next();
                    return true;
                }
                m_entry = null;
                return false;
            }
            public int getColumnCount() {
                return 6;
            }

            private static String classname(Object obj) {
                if ( obj == null ) { return null; }
                final String cName = obj.getClass().getName();
                final int dotIdx = cName.lastIndexOf('.');
                return dotIdx<0 ? cName : cName.substring(dotIdx+1);
            }

            public Object getElementAt(int columnIdx) {
                switch (columnIdx) {
                case 0:
                    return m_entry.getKey();
                case 1:
                    return classname(m_entry.getValue());
                case 2:
                    return m_entry.getValue();
                case 3:
                    return String.valueOf(m_entry.getHits());
                case 4:
                    return m_entry.getValue()==null ?
                        "null" : toHex(m_entry.getValue().hashCode());
                case 5:
                	String status = m_entry.isExpired(m_max_age) ? "[EXP]" : "[LIVE]";
                    return FORMATTER.format(m_entry.getTimestamp()) + " " + status;
                default:
                    throw new IllegalArgumentException("idx=" + columnIdx);
                }
            }


            private static String toHex(int value) {
                String hex = Integer.toHexString(value).toUpperCase();
                return "00000000".substring(0, 8-hex.length()) + hex;
            }

            public Object getKeyAt(int columnIndex) {
                return m_entry.getKey();
            }
        }
    }
}
