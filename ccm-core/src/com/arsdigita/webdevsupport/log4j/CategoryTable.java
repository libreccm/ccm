/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.webdevsupport.log4j;

import com.arsdigita.util.LockableImpl;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import java.util.Enumeration;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Log4j category level list
 *
 * Created: Mon Jul 29 14:01:52 2002
 *
 * @author Daniel Berrange
 */

public class CategoryTable extends Table {


    public CategoryTable() {
        super(new CategoryTableModelBuilder(), new String[] { "Logger", "Level" });
    }

    private static class CategoryTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        public TableModel makeModel(Table table,
                                    PageState state) {
            Enumeration loggers = LogManager.getCurrentLoggers();
            TreeSet set = new TreeSet(new LoggerComparator());
            while (loggers.hasMoreElements()) {
                set.add(loggers.nextElement());
            }

            return new CategoryTableModel(set.iterator());
        }
    }

    private static class CategoryTableModel implements TableModel {

        private Iterator m_cats;
        private Logger m_cat;

        public CategoryTableModel(Iterator cats) {
            m_cats = cats;
        }

        public boolean nextRow() {
            if (m_cats.hasNext()) {
                m_cat = (Logger)m_cats.next();
                return true;
            }
            m_cat = null;
            return false;
        }
        public int getColumnCount() {
            return 2;
        }

        public Object getElementAt(int columnIndex) {
            if (columnIndex == 0) {
                return new ControlLink(new Label(m_cat.getName()));
            } else {
                Level level = m_cat.getLevel();
                return new Label(level != null ? level.toString() : "none");
            }
        }
        public Object getKeyAt(int columnIndex) {
            return m_cat.getName();
        }
    }

    private static class LoggerComparator implements Comparator {
        public int compare(Object o1,
                           Object o2) {
            Logger c1 = (Logger)o1;
            Logger c2 = (Logger)o2;

            return c1.getName().compareTo(c2.getName());
        }

    }

}
