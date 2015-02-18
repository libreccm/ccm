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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.PageState;
import java.math.BigDecimal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.EmailAddress;

import javax.mail.internet.InternetAddress;
import com.arsdigita.bebop.FormProcessException;
/**
 * Edit group form.
 *
 * @author David Dao
 * @version $Id: GroupEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class GroupEditForm extends GroupForm implements AdminConstants,
                                                 FormInitListener,
                                                 FormProcessListener {

    private GroupAdministrationTab m_parent;

    public GroupEditForm() {
        this(null);
    }

    public GroupEditForm(GroupAdministrationTab parent) {
        super(GROUP_FORM_EDIT);
        addInitListener(this);
        addProcessListener(this);

        m_parent = parent;
    }
    /**
     * Initializes form elements by retrieving their values from the
     * database.
     */
    public void init(FormSectionEvent e) {
        PageState ps = e.getPageState();

        BigDecimal id = (BigDecimal) ps.getValue(GROUP_ID_PARAM);

        if (id != null) {
            Group group = new Group(id);

            m_name.setValue(ps, group.getName());
            m_email.setValue(ps, group.getPrimaryEmail());
        }
    }


    /**
     * Processes the form.
     */
    public void process(FormSectionEvent e)
        throws FormProcessException {

        PageState ps = e.getPageState();

        Group group;

        BigDecimal id = (BigDecimal) ps.getValue(GROUP_ID_PARAM);

        if (id == null) {
            throw new FormProcessException(GlobalizationUtil.globalize("ui.admin.groups.ID_is_null"));
        }

        try {
            group = new Group(id);
        } catch (DataObjectNotFoundException exc) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "ui.admin.groups.couldnt_find_specified_group"));
        }

        String name = (String) m_name.getValue(ps);
        group.setName(name);

        // Workaround for bug #189720: there is no way to remove a
        // Party's primary email address, so we set it directly to
        // null if it's value on the form is null.

        InternetAddress email = (InternetAddress) m_email.getValue(ps);
        if (email != null) {
            group.setPrimaryEmail(new EmailAddress(email.getAddress()));
        } else {
            //group.set("primaryEmail", null);
            group.setPrimaryEmail(null);
        }

        group.save();

        if (m_parent != null) {
            m_parent.displayGroupInfoPanel(ps);
        }
    }

}
