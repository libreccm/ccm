/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.*;
// import com.arsdigita.bebop.event.*;
// import com.arsdigita.bebop.form.*;
// import com.arsdigita.bebop.list.ListModel;
// import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.*;
// import com.arsdigita.kernel.permissions.*;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.log4j.Category;

/**
 * ParticipantModel.
 *
 * Reimplementation using original arsdigita portalserver code. 
 * @author dennis (2003/08/15)
 * @version $Id: com/arsdigita/portalserver/ui/PortalParticipants.java $
 */
public class ParticipantModel implements PropertySheetModel {

    private static Category s_log = Category.getInstance
        (ParticipantModel.class.getName());

    int m_counter = -1;
    Party m_p;
    Workspace m_workspace;
    boolean m_includeRoles;

    ParticipantModel(Party p, Workspace workspace, boolean includeRoles) {
        m_p = p;
        m_workspace = workspace;
        m_includeRoles = includeRoles;
    }

    public boolean nextRow() {
        if ((m_includeRoles && (m_counter == 2)) ||
            (!m_includeRoles && (m_counter == 1))) {
            return false;
        }
        m_counter++;
        return true;
    }

    public String getLabel() {
        if (m_counter == 0) {
            return "Email";
        } else if (m_counter == 1) {
            return "Name";
        } else if (m_counter == 2) {
            return "Roles";
        } else {
            throw new IllegalStateException("invalid counter");
        }
    }

    public GlobalizedMessage getGlobalizedLabel() {
        return GlobalizationUtil.globalize(getLabel());
    }

    public String getValue() {
        if (m_counter == 0) {
            if (m_p.getPrimaryEmail() != null) {
                return m_p.getPrimaryEmail().getEmailAddress();
            } else {
                return "None";
            }
        } else if (m_counter == 1) {
            return m_p.getDisplayName();
        } else if (m_counter == 2) {
            StringBuffer result = new StringBuffer();
            boolean foundFirst = false;
            RoleCollection rc = m_workspace.getRoles();
            while (rc.next()) {
                Role r = rc.getRole();
                // XXX: determining role membership
                if (r.getGroup().hasDirectMemberOrSubgroup(m_p)) {
                    if (foundFirst) {
                        result.append(", ");
                    }
                    //result.append(r.getAssigneeTitle());
                    result.append(r.getName());
                    foundFirst = true;
                }
            }
            return result.toString();
        } else {
            throw new IllegalStateException("invalid counter");
        }
    }
}
