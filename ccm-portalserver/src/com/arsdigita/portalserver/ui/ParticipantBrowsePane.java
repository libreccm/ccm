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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;

import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.Role;
import com.arsdigita.portalserver.RoleCollection;

import com.arsdigita.web.RedirectSignal;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;

import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.PrivilegedComponentSelector;
import com.arsdigita.toolbox.ui.IteratorListModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;


import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Category;

public class ParticipantBrowsePane {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/ParticipantBrowsePane.java#8 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (ParticipantBrowsePane.class.getName());

    private ParticipantBrowsePane() {
        // No construction.
    }

    public static Component createForUser
        (final RequestLocal portalsite,
         final StringParameter actionParam,
         final ActionListener selectSearch) {
        return buildBrowsePane(portalsite, actionParam, selectSearch,
                               false);
    }

    public static Component createForAdmin
        (final RequestLocal portalsite,
         final StringParameter actionParam,
         final ActionListener selectSearch) {
        return buildBrowsePane(portalsite, actionParam, selectSearch,
                               true);
    }

    private static Component buildBrowsePane
        (final RequestLocal portalsiteRL,
         final StringParameter actionParam,
         final ActionListener selectBrowse,
         final boolean isAdmin) {
        final ACSObjectSelectionModel selectionModel =
            new ACSObjectSelectionModel("selectedparticipant");

        final List participantList = new List();
        participantList.setSelectionModel(selectionModel);
        participantList.setEmptyView(new Label(""));

        final List initialsList = new List() {
                public boolean isVisible(PageState ps) {
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    return (psite.getParticipantCount() > 20) && super.isVisible(ps);
                }
            };
        initialsList.setLayout(List.HORIZONTAL);
        initialsList.setEmptyView(new Label(GlobalizationUtil.globalize("cw.workspace.ui.no_participants")));
        initialsList.setModelBuilder(new AbstractListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    return new IteratorListModel(psite.getParticipantInitials());
                }
            });
        final RequestLocal firstInitialRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    Iterator initials = psite.getParticipantInitials();
                    if (!initials.hasNext()) {
                        return null;
                    }
                    return initials.next();
                }
            };
        initialsList.setSelectionModel(
          new ParameterSingleSelectionModel(new StringParameter("initial")) {
               public Object getSelectedKey(PageState ps) {
                  if (!initialsList.isVisible(ps)) {
                      return null;
                  }
                  Object result = super.getSelectedKey(ps);
                  if (result != null) {
                      return result;
                  }
                  return firstInitialRL.get(ps);
              }
              public boolean isSelected(PageState ps) {
                 if (!initialsList.isVisible(ps)) {
                     return false;
                 }
                 Object result = super.getSelectedKey(ps);
                 if (result != null) {
                     return true;
                 }
                 return (firstInitialRL.get(ps) != null);
              }
         });
        initialsList.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent ev) {
                    PageState ps = ev.getPageState();
                    ps.reset(participantList);
                }
        });

        participantList.setModelBuilder(new AbstractListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    String initial = (String) initialsList.getSelectedKey(ps);

                    if (initial == null) {
                        return new ParticipantListModel(psite);
                    } else {
                        return new ParticipantListModel(psite, initial);
                    }
                }
            });

        final DynamicListWizard dlw =
            new DynamicListWizard
            ("Portal Participants", participantList,
             selectionModel, "Add a participant",
             new Label("")) {
                public void register(Page p) {
                    super.register(p);

                    if (!isAdmin) {
                        p.setVisibleDefault(getListLabel(), false);
                        p.setVisibleDefault(getAddLink(), false);
                    }

                    final DynamicListWizard theDLW = this;

                    p.addRequestListener(new RequestListener() {
                            public void pageRequested(RequestEvent ev) {
                                PageState ps = ev.getPageState();

                                String action = (String) ps.getValue(actionParam);

                                if (action == null
                                    || !action.startsWith("browse")) {
                                    return;
                                }

                                BigDecimal partyID = new BigDecimal
                                    (action.substring(6));

                                ps.setValue(actionParam, null);

                                selectBrowse.actionPerformed
                                    (new ActionEvent(theDLW, ps));

                                OID partyOID = new OID
                                    (ACSObject.BASE_DATA_OBJECT_TYPE, partyID);

                                Party party;

                                try {
                                    party = (Party)
                                        DomainObjectFactory.newInstance(partyOID);
                                } catch (DataObjectNotFoundException ex) {
                                    return;
                                }

                                String initial;

                                if (party instanceof User) {
                                    initial = ((User) party).getPersonName()
                                        .getFamilyName()
                                        .substring(0, 1)
                                        .toUpperCase();
                                } else {
                                    initial = ((Group) party).getName()
                                        .substring(0, 1)
                                        .toUpperCase();
                                }

                                initialsList.setSelectedKey(ps, initial);
                                participantList.setSelectedKey
                                    (ps, partyID.toString());
                            }
                        });
                }
            };
        dlw.setHeader(initialsList);

        RequestLocal participantRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return selectionModel.getSelectedObject(ps);
                }
            };

        ActionListener onDelete = new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    dlw.reset(ev.getPageState());
                }
            };

        Component participantEdit = new ParticipantEdit
            (portalsiteRL, participantRL, onDelete);

        Component participantDisplay = new ParticipantDisplay
            (portalsiteRL, participantRL);

        final Component participantEditOrDisplay =
            new PrivilegedComponentSelector(PrivilegeDescriptor.ADMIN,
                                            portalsiteRL,
                                            participantEdit,
                                            participantDisplay);
        dlw.setEditPane(participantEditOrDisplay);

        // This change listener ensures that we don't get an edit pane
        // or add pane in some weird intermediate state
        selectionModel.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent ev) {
                    ev.getPageState().reset(participantEditOrDisplay);
                }
            });

        if (isAdmin) {
            dlw.setAddPane(buildAddParticipant(portalsiteRL));
        }

        return dlw;
    }

    private static FormSection buildRoleFormSection
        (final RequestLocal portalsiteRL, final RequestLocal participantRL) {
        FormSection form = new FormSection(new ColumnPanel(1));

        Label rHeader = new Label(GlobalizationUtil.globalize("cw.workspace.ui.roles"));
        rHeader.setFontWeight(Label.BOLD);
        form.add(rHeader);
        final CheckboxGroup roles = new CheckboxGroup("roles");
        roles.setPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    CheckboxGroup tgt = (CheckboxGroup)ev.getTarget();
                    PageState ps = ev.getPageState();
                    PortalSite psite = (PortalSite)portalsiteRL.get(ps);
                    RoleCollection roleColl = psite.getRoles();
                    while (roleColl.next()) {
                        tgt.addOption(new Option(roleColl.getID().toString(),
                                                 roleColl.getRoleName()));
                    }
                }
            });

        form.add(roles);

        form.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();

                    Party party = (Party)participantRL.get(ps);
                    if (party == null) {
                        return;
                    }

                    PortalSite psite = (PortalSite)portalsiteRL.get(ps);

                    RoleCollection rc = psite.getRoles();
                    LinkedList roleIDs = new LinkedList();
                    while (rc.next()) {
                        Role r = rc.getRole();
                        if (r.hasDirectMemberOrSubgroup(party)) {
                            roleIDs.add(rc.getID().toString());
                        }
                    }
                    roles.setValue(ps, roleIDs.toArray());
                }
            });

        form.addProcessListener(new FormProcessListener() {
                public void process(final FormSectionEvent ev) {
                    PageState ps = ev.getPageState();

                    Object[] roleIDstrs = (Object[])roles.getValue(ps);
                    final HashSet roleIDs = new HashSet();
                    if (roleIDstrs != null) {
                        int i;
                        for (i = 0; i < roleIDstrs.length; i++) {
                            BigDecimal id
                                = new BigDecimal((String)roleIDstrs[i]);
                            roleIDs.add(id);
                            Role role = (Role) DomainObjectFactory.newInstance
                                (new OID(Role.BASE_DATA_OBJECT_TYPE, id));
                            role.getPortalSite().assertPrivilege
                                (PrivilegeDescriptor.ADMIN);
                        }
                    }

                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            doProcess(roleIDs, ev.getPageState());
                        }
                    };
                    ex.run();
                }

                private void doProcess(HashSet roleIDs, PageState ps) {
                    Party party = (Party)participantRL.get(ps);
                    RoleCollection rc = ((PortalSite)portalsiteRL.get(ps)).getRoles();

                    while (rc.next()) {
                        Role role = rc.getRole();
                        if (roleIDs.contains(role.getID())) {
                            if (!role.hasDirectMemberOrSubgroup(party)) {
                                role.addMemberOrSubgroup(party);
                                role.save();
                            }
                        } else {
                            if (role.hasDirectMemberOrSubgroup(party)) {
                                role.removeMemberOrSubgroup(party);
                                role.save();
                            }
                        }
                    }
                }
            });

        return form;
    }

    private static Component buildAddParticipant
        (final RequestLocal portalsiteRL) {
        final RequestLocal partiesRL = new RequestLocal();

        final Form userSearch = new Form("usersearch");
        userSearch.setMethod(Form.POST);

        final Label searchRedoLabel = new Label("");
        final Label emptyLabel = new Label("");

        final Form userAdd =
            new Form("useradd", new BoxPanel(BoxPanel.VERTICAL));
        userAdd.setMethod(Form.POST);

        final Container result = new SimpleContainer();
        result.add(userSearch);
        final ModalContainer bottomHalf = new ModalContainer();
        bottomHalf.add(emptyLabel);
        bottomHalf.add(searchRedoLabel);
        bottomHalf.add(userAdd);
        bottomHalf.setDefaultComponent(emptyLabel);
        result.add(bottomHalf);

        userSearch.add(
                       new Label(GlobalizationUtil.globalize("cw.workspace.ui.enter_first_name_last_name_andor_email_address")));
        final TextField query = new TextField("query");
        userSearch.add(query);
        userSearch.add(new Label(""));
        userSearch.add(new Submit("Search"));
        userSearch.add(new Label());
        userSearch.add(new FormErrorDisplay(userSearch),
                       ColumnPanel.FULL_WIDTH);

        userSearch.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {
                    PageState ps = e.getPageState();


                    PortalSite psite = (PortalSite)portalsiteRL.get(ps);

                    PartyCollection parties = psite.getNonParticipants();
                    parties.filter((String) query.getValue(ps));

                    long count = parties.size();
                    if (count == 0) {
                        bottomHalf.setVisibleComponent(ps, searchRedoLabel);
                        searchRedoLabel.setLabel( (String) GlobalizationUtil.globalize("cw.workspace.ui.no_matches_found").localize() , ps);
                    } else if (count > 100) {
                        bottomHalf.setVisibleComponent(ps, searchRedoLabel);
                        searchRedoLabel.setLabel(
                                    count + " matches found. Please enter more"
                                    + " specific search criteria.", ps);
                    } else {
                        partiesRL.set(ps, parties);
                        bottomHalf.setVisibleComponent(ps, userAdd);
                    }
                }
            });

        userAdd.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.select_user_or_group_to_add")));
        final RadioGroup users = new RadioGroup("users");
        users.setLayout(RadioGroup.VERTICAL);

        userAdd.add(users);

        final RequestLocal addedParticipantRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    String partIDstr = (String)users.getValue(ps);
                    if (partIDstr == null) {
                        return null;
                    }
                    OID partyOID = new OID(ACSObject.BASE_DATA_OBJECT_TYPE,
                                           new BigDecimal(partIDstr));
                    try {
                        return (Party) DomainObjectFactory.newInstance(partyOID);
                    } catch (DataObjectNotFoundException ex) {
                        return null;
                    }
                }
            };

        //userAdd.add(buildRoleFormSection(portalsiteRL, addedParticipantRL));

        userAdd.add(new Submit("Add Participant"));

        userAdd.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    PartyCollection parties = (PartyCollection) partiesRL.get(ps);
                    if (parties == null) {
                        // This is not a pleasing way to fix this.  I
                        // don't understand the logic requiring an illegal
                        // state exception to be thrown here.
                        bottomHalf.setVisibleComponent(ps, emptyLabel);
                        return;
                        //throw new IllegalStateException("partiesRL not set");
                    }
                    if (parties.next()) {
                        do {
                            EmailAddress email = parties.getPrimaryEmail();
                            users.addOption(
                              new Option(parties.getID().toString(),
                                         parties.getDisplayName() +
                                         (email == null ? "" :
                                         " <" + email.getEmailAddress() + ">")),
                                            ps);
                        } while (parties.next());
                    } else {
                        // XXX deal with no users case
                        users.addOption(new Option("none", "No Users Found"), ps);
                    }
                }
            });

        userAdd.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                        throws FormProcessException {
                    PageState ps = e.getPageState();

                    final PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    final Party party = (Party) addedParticipantRL.get(ps);

                    psite.assertPrivilege(PrivilegeDescriptor.ADMIN);
                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            psite.addParticipant(party);
                            psite.save();
                        }
                    };
                    ex.run();

                    ps.reset(result);
                    String url = null;

                    try {
                        url = ps.stateAsURL();
                    } catch (IOException ioe) {
                        throw new UncheckedWrapperException(ioe);
                    }

                    throw new RedirectSignal(url, true);
                }
            });

        return result;
    }
}
