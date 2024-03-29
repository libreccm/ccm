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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.Role;
import org.apache.log4j.Logger;

/**
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RoleEditForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
final class RoleEditForm extends BaseRoleForm {

    private static final Logger s_log = Logger.getLogger(RoleEditForm.class);

    private final RoleRequestLocal m_role;
    private final boolean m_useViewersGroup;

    public RoleEditForm(RoleRequestLocal role, boolean useViewersGroup) {
        super("EditStaffRole", gz("cms.ui.role.edit"), useViewersGroup);

        m_role = role;
        m_useViewersGroup = useViewersGroup;

        m_name.addValidationListener(new NameUniqueListener(m_role));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();
            final Role role = m_role.getRole(state);

            m_name.setValue(state, role.getName());
            m_description.setValue(state, role.getDescription());

            final String[] privileges = RoleFactory.getRolePrivileges
                (CMS.getContext().getContentSection(), role);

            m_privileges.setValue(state, privileges);
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();

            final Role role = m_role.getRole(state);
            role.setName((String) m_name.getValue(state));
            role.setDescription((String) m_description.getValue(state));
            role.save();

            RoleFactory.updatePrivileges
                (role,
                 (String[]) m_privileges.getValue(state),
                 CMS.getContext().getContentSection());

            role.save();
        }
    }
}
