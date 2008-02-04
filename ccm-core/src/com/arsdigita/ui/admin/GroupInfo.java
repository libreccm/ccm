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

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.EmailAddress;

final class GroupInfo extends SimpleComponent
    implements AdminConstants
{

    private GroupAdministrationTab m_parent;

    public GroupInfo(GroupAdministrationTab parent) {
        m_parent = parent;
    }

    public void generateXML(PageState state, Element parent) {

        Element elmt = new Element("admin:groupInfo", ADMIN_XML_NS);

        BigDecimal id = (BigDecimal) state.getValue(GROUP_ID_PARAM);

        if (id == null) {
            return;
        }

        Group group = m_parent.getGroup(state);

        if (group == null) {
            elmt.addAttribute("name", "Group #" + id + " not found");
            return;
        }

        elmt.addAttribute("id", group.getID().toString());
        elmt.addAttribute("name", group.getName());

        // Primary email is optional

        EmailAddress email = group.getPrimaryEmail();
        elmt.addAttribute
            ("email", email != null ? email.toString() : "");

        parent.addContent(elmt);
    }
}
