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

//import com.arsdigita.portalserver.Role;
//import com.arsdigita.portalserver.RoleCollection;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.util.GlobalizationUtil;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.PrivilegedComponentSelector;
import com.arsdigita.toolbox.ui.IteratorListModel;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Iterator;

import org.apache.log4j.Category;

/**
 * ParticipantBrowsePane.
 *
 * @author ashah (2003/08/15)
 * @version $Id: //portalserver/src/com/arsdigita/portalserver/ui/ParticipantBrowsePane.java#7 $
 +/
public class ParticipantBrowsePane {

    private static Category s_log = Category.getInstance
        (ParticipantBrowsePane.class.getName());

    private ParticipantBrowsePane() {
        // No construction.
    }

    public static Component createForUser
        (final RequestLocal workspace,
         final StringParameter actionParam,
         final ActionListener selectSearch) {
        return buildBrowsePane(workspace, actionParam, selectSearch, false);
    }

    public static Component createForAdmin
        (final RequestLocal workspace,
         final StringParameter actionParam,
         final ActionListener selectSearch) {
        return buildBrowsePane(workspace, actionParam, selectSearch, true);
    }

    private static Component buildBrowsePane
        (final RequestLocal workspaceRL,
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
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    return (workspace.getParticipantCount() > 20) && super.isVisible(ps);
                }
            };
        initialsList.setLayout(List.HORIZONTAL);
        initialsList.setEmptyView(new Label(GlobalizationUtil.globalize("cw.workspace.ui.no_participants")));
        initialsList.setModelBuilder(new AbstractListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    return new IteratorListModel(workspace.getParticipantInitials());
                }
            });
        final RequestLocal firstInitialRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    Iterator initials = workspace.getParticipantInitials();
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
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    String initial = (String) initialsList.getSelectedKey(ps);

                    if (initial == null) {
                        return new ParticipantListModel(workspace);
                    } else {
                        return new ParticipantListModel(workspace, initial);
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
            (workspaceRL, participantRL, onDelete);

        Component participantDisplay = new ParticipantDisplay
            (workspaceRL, participantRL);

        final Component participantEditOrDisplay =
            new PrivilegedComponentSelector(PrivilegeDescriptor.ADMIN,
                                            workspaceRL,
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
            dlw.setAddPane(buildAddParticipant(workspaceRL));
        }

        return dlw;
    }

    private static FormSection buildRoleFormSection
        (final RequestLocal workspaceRL, final RequestLocal participantRL) {
        FormSection form = new FormSection(new ColumnPanel(1));

        Label rHeader = new Label(GlobalizationUtil.globalize("cw.workspace.ui.roles"));
        rHeader.setFontWeight(Label.BOLD);
        form.add(rHeader);
        final CheckboxGroup roles = new CheckboxGroup("roles");
        roles.setPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    CheckboxGroup tgt = (CheckboxGroup) ev.getTarget();
                    PageState ps = ev.getPageState();
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    RoleCollection roleColl = workspace.getRoles();
                    while (roleColl.next()) {
                        tgt.addOption(new Option(roleColl.getRole().getID().toString(),
                                                 roleColl.getRole().getName()));
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

                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    RoleCollection rc = ((Group) workspace.getParty()).getRoles();
                    LinkedList roleIDs = new LinkedList();
                    while (rc.next()) {
                        Role r = rc.getRole();
                        if (r.getGroup().hasDirectMemberOrSubgroup(party)) {
                            roleIDs.add(rc.getRole().getID().toString());
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
//                             Role role = (Role) DomainObjectFactory.newInstance
//                                 (new OID(Role.BASE_DATA_OBJECT_TYPE, id));
//                             role.getPortalSite().assertPrivilege
//                                 (PrivilegeDescriptor.ADMIN);
                            Workspace workspace = (Workspace) workspaceRL.get(ps);
                            workspace.assertPrivilege(PrivilegeDescriptor.ADMIN);
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
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    RoleCollection rc = workspace.getRoles();

                    while (rc.next()) {
                        Role role = rc.getRole();
                        if (roleIDs.contains(role.getID())) {
                            if (!role.getGroup().hasDirectMemberOrSubgroup(party)) {
                                role.getGroup().addMemberOrSubgroup(party);
                                role.save();
                            }
                        } else {
                            if (role.getGroup().hasDirectMemberOrSubgroup(party)) {
                                role.getGroup().removeMemberOrSubgroup(party);
                                role.save();
                            }
                        }
                    }
                }
            });

        return form;
    }

    private static Component buildAddParticipant
        (final RequestLocal workspaceRL) {
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

                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    PartyCollection parties = workspace.getNonParticipants();
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

                    final Party party = (Party) addedParticipantRL.get(ps);
                    final Workspace workspace = (Workspace) workspaceRL.get(ps);

                    workspace.assertPrivilege(PrivilegeDescriptor.ADMIN);
                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            workspace.addParticipant(party);
                            workspace.save();
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
