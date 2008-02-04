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
package com.arsdigita.toolbox.ui;



import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.CompoundComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;


import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;

import com.arsdigita.xml.Element;


/**
 * <p>This component wraps two other components and chooses one of
 * them based on whether or not a given party (specified in a
 * RequestLocal) has a given privilege on a given object (also
 * specified in a RequestLocal).</p>
 **/
public class PrivilegedComponentSelector extends CompoundComponent {

    private PrivilegeDescriptor m_requiredPrivilege;
    private RequestLocal m_objectRL;

    private Component m_privilegedComponent;
    private Component m_unprivilegedComponent;


    /**
     * <p>Construct a new PrivilegedComponentSelector.</p>
     *
     * @param requiredPrivilege Privilege that will be checked
     * @param objectRL RequestLocal in which the object on which
     * permission will be checked is stored
     * @param privilegedComponent Component to be displayed when the
     * current user has the required privilege on the specified object
     * @param unprivilegedComponent Component to be displayed when the
     * current user lacks the required privilege on the specified
     * object
     **/
    public PrivilegedComponentSelector(PrivilegeDescriptor requiredPrivilege,
                                       RequestLocal objectRL,
                                       Component priviledgedComponent,
                                       Component unprivilegedComponent) {
        super(new SimpleContainer());
        getContainer().add(priviledgedComponent);
        getContainer().add(unprivilegedComponent);

        m_requiredPrivilege = requiredPrivilege;
        m_objectRL = objectRL;

        m_privilegedComponent = priviledgedComponent;
        m_unprivilegedComponent = unprivilegedComponent;
    }

    public void generateXML(PageState ps, Element parent) {
        ACSObject object = (ACSObject)m_objectRL.get(ps);
        Party party  = Kernel.getContext().getParty();

        if ((object == null) || (party == null)) {
            m_privilegedComponent.setVisible(ps, false);
            m_unprivilegedComponent.setVisible(ps, true);
        } else {
            PermissionDescriptor perm =
                new PermissionDescriptor(m_requiredPrivilege, object, party);
            if (PermissionService.checkPermission(perm)) {
                m_privilegedComponent.setVisible(ps, true);
                m_unprivilegedComponent.setVisible(ps, false);
            } else {
                m_privilegedComponent.setVisible(ps, false);
                m_unprivilegedComponent.setVisible(ps, true);
            }
        }

        super.generateXML(ps, parent);
    }
}
