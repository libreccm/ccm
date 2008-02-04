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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Log4j logger level adjuster
 *
 * Created: Mon Jul 29 14:01:52 2002
 *
 * @author <a href="mailto:dan@camden.london.redhat.com">Daniel Berrange</a>
 * @version $Date: 2004/08/16 $
 */

public class CategoryPanel extends SimpleContainer {

    private CategoryTable m_categories;
    private CategoryForm m_form;

    public CategoryPanel() {

        m_categories = new CategoryTable();
        m_form = new CategoryForm();

        m_form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {

                    PageState state = e.getPageState();

                    String category = m_form.getLogger(state);
                    String level = m_form.getLevel(state);

                    m_form.setLogger(state, "");
                    m_form.setLevel(state, "DEBUG");

                    Logger.getLogger(category).setLevel(Level.toLevel(level));
                    m_categories.getRowSelectionModel().clearSelection(state);
                }
            });

        m_categories.addTableActionListener(new TableActionListener() {
                public void cellSelected(TableActionEvent e) {
                    PageState state = e.getPageState();
                    Logger cat = Logger.getLogger((String)e.getRowKey());
                    Level level = cat.getLevel();

                    m_form.setLogger(state, cat.getName());
                    m_form.setLevel(state,
                                    level != null ? level.toString() : "DEBUG");
                }
                public void headSelected(TableActionEvent e) {
                }
            });

        add(m_form);
        add(m_categories);
    }

}// CategoryPanel
