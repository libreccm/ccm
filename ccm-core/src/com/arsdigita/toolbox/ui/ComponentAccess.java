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
import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.toolbox.Security;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * <p>Wrapper class that registers access checks (actions) to a
 * Bebop component.</p>
 *
 * @author Michael Pih 
 * @version $Revision: #11 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class ComponentAccess {

    private Component m_component;
    private ArrayList m_list;

    private static final Logger s_log =
        Logger.getLogger(ComponentAccess.class);


    /**
     * @param c The component
     */
    public ComponentAccess(Component c) {
        m_list = new ArrayList();
        m_component = c;
    }

    /**
     * @param c The component
     * @param check An access check
     */
    public ComponentAccess(Component c, String check) {
        this(c);
        addAccessCheck(check);
    }

    /**
     * Add an access check to this component.
     *
     * @param check The access check
     */
    public void addAccessCheck(String check) {
        m_list.add(check);
    }

    /**
     * Get the access checks.
     *
     * @return The list of access checks
     */
    public ArrayList getAccessCheckList() {
        return m_list;
    }

    /**
     * Get the component.
     *
     * @return The component
     */
    public Component getComponent() {
        return m_component;
    }

    /**
     * Do all the access checks registered to the component pass?
     *
     * @param state The page state
     * @param security The Security implementation that will perform
     *    the access checks
     * @return true if all the access checks pass, false otherwise
     */
    public boolean canAccess(PageState state, Security security) {
        boolean canAccess = true;

        Party party = Kernel.getContext().getParty();

        for ( Iterator i = getAccessCheckList().iterator(); i.hasNext(); ) {
            String check = (String) i.next();
            if ( !security.canAccess(party, check) ) {
                canAccess = false;
                break;
            }
        }

        return canAccess;
    }

}
