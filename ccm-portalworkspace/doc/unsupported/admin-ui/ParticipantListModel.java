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

// import com.arsdigita.bebop.*;
// import com.arsdigita.bebop.event.*;
// import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.list.ListModel;
// import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.*;
// import com.arsdigita.kernel.permissions.*;
// import com.arsdigita.london.portal.util.GlobalizationUtil;
import com.arsdigita.portalworkspace.Workspace;

import org.apache.log4j.Category;

/**
 * ParticipantListModel
 *
 * @author dennis (2003/08/15)
 * @version $Id: com/arsdigita/portalserver/ui/PortalParticipants.java $
 */
public class ParticipantListModel implements ListModel {

    private static Category s_log = Category.getInstance
        (ParticipantListModel.class.getName());

    protected PartyCollection m_p;

    public ParticipantListModel(Workspace p, String initial) {
        m_p = p.getParticipantsWithInitial(initial);
    }

    public ParticipantListModel(Workspace p) {
        m_p = p.getParticipants();
    }

    public boolean next() {
        if (m_p.next()) {
            return true;
        } else {
            return false;
        }
    }

    public Object getElement() {
        return m_p.getDisplayName();
    }

    public String getKey() {
        return m_p.getID().toString();
    }
}

