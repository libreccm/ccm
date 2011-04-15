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
package com.arsdigita.portalserver.ui;


import com.arsdigita.portalserver.util.GlobalizationUtil; 


import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.Role;
import com.arsdigita.portalserver.RoleCollection;
import com.arsdigita.portalserver.personal.PersonalPortal;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.StringParameter;



import org.apache.log4j.Category;

public class PortalParticipants {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/PortalParticipants.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (PortalParticipants.class.getName());

    private PortalParticipants() {
        // No construction.
    }

    public static PortalPage createPage() {
        final RequestLocal portalsiteRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return PortalSite.getCurrentPortalSite(ps.getRequest());
                }
            };

        PortalPage p = new ParticipantPortalPage();

        final StringParameter actionParam = new StringParameter("action");
        p.addGlobalStateParam(actionParam);

        p.setTitle(new Label(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    Label tgt = (Label) ev.getTarget();
                    PortalSite psite = 
                           (PortalSite) portalsiteRL.get(ev.getPageState());
                    tgt.setLabel(psite.getTitle() + " Participants");
                }
            }));

        final TabbedPane mainDisplay = new TabbedPane();

        Component browsePane = ParticipantBrowsePane.createForUser
            (portalsiteRL, actionParam, new ActionListener() {
                 public void actionPerformed(ActionEvent ev) {
                     mainDisplay.setSelectedIndex(ev.getPageState(), 0);
                 }
             });
        mainDisplay.addTab("Browse", browsePane);

        Component searchPane = ParticipantSearchPane.create
            (portalsiteRL, actionParam, new ActionListener() {
                 public void actionPerformed(ActionEvent ev) {
                     mainDisplay.setSelectedIndex(ev.getPageState(), 1);
                 }
             });

        mainDisplay.addTab("Search", searchPane);

        p.add(mainDisplay);

        p.addRequestListener(new RequestListener() {
                public void pageRequested(RequestEvent ev) {
                    PageState ps = ev.getPageState();
                    String actionName = (String) ps.getValue(actionParam);

                    if (actionName != null) {
                        if (actionName.equals("search")) {
                            mainDisplay.setSelectedIndex(ps, 1);
                            ps.setValue(actionParam, null);
                        }
                    }
                }
            });

        p.lock();

        return p;
    }
}

class ParticipantPortalPage extends PortalPage {
    protected void buildContextBar() {
                    DimensionalNavbar navbar = new DimensionalNavbar();

                    navbar.setClassAttr("portalNavbar");

                    navbar.add(new Link(new PersonalPortalLinkPrinter()));

                    // This link will not show up if the current portal is a
                    // personal portal.
                    Link current = new Link(new CurrentPortalLinkPrinter()) {
                       public boolean isVisible(PageState state) {
                          PortalSite psite = PortalSite.getCurrentPortalSite
                                    (state.getRequest());

                                return !(psite instanceof PersonalPortal);
                            }
                        };
                    navbar.add(current);

                    navbar.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.participants")));

                    getHeader().add(navbar);
                }
}

class ParticipantListModel implements ListModel {
    protected PartyCollection m_p;

    public ParticipantListModel(PortalSite p, String initial) {
        m_p = p.getParticipantsWithInitial(initial);
    }

    public ParticipantListModel(PortalSite p) {
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

class ParticipantDisplay extends CompoundComponent {
    public ParticipantDisplay(Container container,
                              final RequestLocal portalsiteRL,
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
                                       (PortalSite) portalsiteRL.get(ps), true);
                }
            }));
    }

    public ParticipantDisplay(final RequestLocal portalsiteRL,
                              final RequestLocal participantRL) {
        this(new BoxPanel(BoxPanel.VERTICAL), portalsiteRL, participantRL);
    }
}

class ParticipantModel implements PropertySheetModel {
    int m_counter = -1;
    Party m_p;
    PortalSite m_psite;
    boolean m_includeRoles;

    ParticipantModel(Party p, PortalSite psite, boolean includeRoles) {
        m_p = p;
        m_psite = psite;
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

    /**
     *  @deprecated use getGlobalizedLabel instead
     */
    public String getLabel() {
        return getGlobalizedLabel().getKey();
    }

    public GlobalizedMessage getGlobalizedLabel() {
        if (m_counter == 0) {
            return GlobalizationUtil.globalize("cw.workspace.ui.email");
        } else if (m_counter == 1) {
            return GlobalizationUtil.globalize("cw.workspace.ui.name");
        } else if (m_counter == 2) {
            return GlobalizationUtil.globalize("cw.workspace.ui.roles");
        } else {
            throw new IllegalStateException("invalid counter");
        }
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
            RoleCollection rc = m_psite.getRoles();
            while (rc.next()) {
                Role r = rc.getRole();
                // XXX: determining role membership
                if (r.hasDirectMemberOrSubgroup(m_p)) {
                    if (foundFirst) {
                        result.append(", ");
                    }
                    result.append(r.getAssigneeTitle());
                    foundFirst = true;
                }
            }
            return result.toString();
        } else {
            throw new IllegalStateException("invalid counter");
        }
    }
}
