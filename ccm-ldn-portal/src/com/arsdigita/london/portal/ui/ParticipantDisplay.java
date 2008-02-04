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

package com.arsdigita.london.portal.ui;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.london.portal.util.GlobalizationUtil; 
import com.arsdigita.london.portal.Workspace;

import org.apache.log4j.Category;

public class ParticipantDisplay extends CompoundComponent {

    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/PortalParticipants.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/15 13:46:34 $";

    private static Category s_log = Category.getInstance
        (ParticipantDisplay.class.getName());

    public ParticipantDisplay(Container container,
                              final RequestLocal workspaceRL,
                              final RequestLocal participantRL) {
        super(container);

        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.participant_info"));
        header.setFontWeight(Label.BOLD);
        add(header);

        add(new PropertySheet(new PropertySheetModelBuilder() {
                public void lock() {
                    // Do nothing.
                }

                public boolean isLocked() {
                    return ParticipantDisplay.this.isLocked();
                }

                public PropertySheetModel makeModel(PropertySheet sheet,
                                                    final PageState ps) {
                    return new ParticipantModel((Party) participantRL.get(ps),
                                                (Workspace) workspaceRL.get(ps),
                                                true);
                }
            }));
    }

    public ParticipantDisplay(final RequestLocal workspaceRL,
                              final RequestLocal participantRL) {
        this(new BoxPanel(BoxPanel.VERTICAL), workspaceRL, participantRL);
    }
}
