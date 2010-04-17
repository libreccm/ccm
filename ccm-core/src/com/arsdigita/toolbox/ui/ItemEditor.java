/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/** 
 * 
 * @version $Id: ItemEditor.java 287 2005-02-22 00:29:02Z sskracic $
 */ 
public class ItemEditor extends ModalContainer {

    private static final Logger s_log = Logger.getLogger(ItemEditor.class);

    private Label m_heading = null;
    private Component m_summary = null;
    private Component m_details = null;
    private ActionLink m_editLink = null;
    private Form m_edit = null;
    private ActionLink m_deleteLink = null;
    private Form m_delete = null;
    private ActionLink m_returnLink = null;

    public final void setSummary(final Label heading,
                                  final Component summary) {
        Assert.exists(heading, "Label heading");
        Assert.exists(summary, "Component summary");
        Assert.isUnlocked(this);

        m_heading = heading;
        m_summary = summary;
    }

    public final void setDetails(final Component details) {
        Assert.exists(details, "Component details");
        Assert.isUnlocked(this);

        m_details = details;
    }

    public final void setEdit(final ActionLink editLink,
                              final Form edit) {
        Assert.exists(editLink, "ActionLink editLink");
        Assert.exists(edit, "Form edit");
        Assert.isUnlocked(this);

        m_editLink = editLink;
        m_edit = edit;
    }

    public final void setDelete(final ActionLink deleteLink,
                                final Form delete) {
        Assert.exists(deleteLink, "ActionLink deleteLink");
        Assert.exists(delete, "Form delete");
        Assert.isUnlocked(this);

        m_deleteLink = deleteLink;
        m_delete = delete;
    }

    public final void setReturn(final ActionLink returnLink) {
        Assert.exists(returnLink, "ActionLink returnLink");
        Assert.isUnlocked(this);

        m_returnLink = returnLink;
    }

    public void register(final Page page) {
        Assert.exists(m_heading, "Label m_heading");
        Assert.exists(m_summary, "Component m_summary");

        final SimpleContainer info = new SimpleContainer();
        super.setDefaultComponent(info);

        info.add(new Summary());

        if (m_details != null) {
            info.add(m_details);
        }

        if (m_edit != null) {
            add(m_edit);

            // XXX if we use FormPrimes, do cancel logic here too.

            m_edit.addProcessListener(new FormProcessListener() {
                    public final void process(final FormSectionEvent e) {
                        ItemEditor.this.reset(e.getPageState());
                    }
                });
        }

        if (m_delete != null) {
            add(m_delete);

            // XXX if we use FormPrimes, do cancel logic here too.

            m_delete.addProcessListener(new FormProcessListener() {
                    public final void process(final FormSectionEvent e) {
                        ItemEditor.this.reset(e.getPageState());
                    }
                });
        }

        super.register(page);
    }

    public final void setDefaultComponent(final Component component) {
        throw new UnsupportedOperationException();
    }

    private class Summary extends Section {
        Summary() {
            setHeading(m_heading);

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_summary);

            if (m_editLink != null) {
                prepareEditLink();

                group.addAction(m_editLink);
            }

            if (m_deleteLink != null) {
                prepareDeleteLink();

                group.addAction(m_deleteLink);
            }

            if (m_returnLink != null) {
                group.addAction(m_returnLink);
            }
        }

        private void prepareEditLink() {
            m_editLink.addActionListener(new ActionListener() {
                    public final void actionPerformed(final ActionEvent e) {
                        ItemEditor.this.setVisibleComponent
                            (e.getPageState(), m_edit);
                    }
                });
        }

        private void prepareDeleteLink() {
            m_deleteLink.addActionListener(new ActionListener() {
                    public final void actionPerformed(final ActionEvent e) {
                        ItemEditor.this.setVisibleComponent
                            (e.getPageState(), m_delete);
                    }
                });
        }
    }
}
