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
package com.arsdigita.portalserver.ui.admin;


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalserver.PortalSite;
import org.apache.log4j.Category;

import com.arsdigita.portalserver.permissions.PartyPermissionEdit;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.ACSObjectCollectionListModel;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.ui.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.list.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.parameters.*;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListModel;

public class PeoplePane {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PeoplePane.java#8 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Category s_log = Category.getInstance
        (PeoplePane.class.getName());

    private PeoplePane() {
        // No construction allowed.
    }

    public static Component create(Page page, final RequestLocal portalsite) {
        final SimpleContainer container = new SimpleContainer();

        final DynamicListWizard roles = 
                   (DynamicListWizard) buildRoles(portalsite);

        final StringParameter action = new StringParameter("action");
        page.addGlobalStateParam(action);

        final DynamicListWizard browse = (DynamicListWizard)
            ParticipantBrowsePane.createForAdmin
            (portalsite, action, new ActionListener() {
                 public void actionPerformed(ActionEvent ev) {
                     // No thing
                 }
             });

        roles.getAddLink().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    browse.reset(e.getPageState());
                }
            });

        roles.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                PageState ps = e.getPageState();
                if (roles.getSelectionModel().isSelected(ps)) {
                    browse.reset(e.getPageState());
                }
            }
        });

        browse.getAddLink().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    roles.reset(e.getPageState());
                }
            });

        browse.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                PageState ps = e.getPageState();
                if (browse.getSelectionModel().isSelected(ps)) {
                    roles.reset(e.getPageState());
                }
            }
        });

        container.add(browse);

        container.add(roles);

        return container;
    }

    private static Component buildRoles(final RequestLocal portalsiteRL) {
        final ACSObjectSelectionModel selectionModel =
            new ACSObjectSelectionModel("selectedRole");

        List rList = new List(new ListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    return new ACSObjectCollectionListModel(psite.getRoles()) {
                            public Object getElement() {
                                RoleCollection rc = (RoleCollection) getCollection();

                                if (rc.isSystem()) {
                                    return rc.getRoleName() + "*";
                                } else {
                                    return rc.getRoleName();
                                }
                            }
                        };
                }

                public void lock() {
                    // Do nothing.
                }

                public boolean isLocked() {
                    return true;
                }
            });
        rList.setSelectionModel(selectionModel);
        Label emptyView = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_roles_defined"));
        emptyView.setFontWeight(Label.ITALIC);
        rList.setEmptyView(emptyView);

        final DynamicListWizard dlw = new DynamicListWizard
            ("Portal Roles", rList, selectionModel, "Add a role",
             new Label(""));

        final RequestLocal role = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return (Role) selectionModel.getSelectedObject(ps);
                }
            };

        dlw.setAddPane(buildRolesAdd(role, portalsiteRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, (Role) role.get(ps));
                }
            }));

        dlw.setEditPane(buildRolesEdit(role, portalsiteRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, null);
                }
            }));

        return dlw;
    }

    private static Component buildRolesAdd(final RequestLocal roleRL,
                                           final RequestLocal portalsiteRL,
                                           final ActionListener onAdd) {
        final Form result = new Form("roleAdd");

        result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.role_name")));

        final TextField roleNameField = new TextField("roleName");
        roleNameField
            .getParameterModel()
            .addParameterListener(new NotEmptyValidationListener());

        result.add(roleNameField);

        result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.assignee_title")));

        final TextField assigneeTitleField = new TextField("assigneeTitle");
        assigneeTitleField
            .getParameterModel()
            .addParameterListener(new NotEmptyValidationListener());

        result.add(assigneeTitleField);

        result.add(new Label());
        result.add(new Submit("Add Role"));

        result.addProcessListener(new FormProcessListener() {
          public void process(FormSectionEvent ev) {
            PageState ps = ev.getPageState();
            String roleName = (String)roleNameField.getValue(ps);
            String assigneeTitle = (String)assigneeTitleField.getValue(ps);
            // XXX: creating roles with no descriptions
            PortalSite psite = (PortalSite) portalsiteRL.get(ps);
            Role newRole =
                Role.createRole((PortalSite) portalsiteRL.get(ps),
                                roleName, assigneeTitle, "");
            newRole.save();
            PermissionService.grantPermission(
              new PermissionDescriptor(PrivilegeDescriptor.READ,psite, newRole));
               roleRL.set(ps, newRole);
               onAdd.actionPerformed(new ActionEvent(result, ps));
           }
       });

        return result;
    }

    private static Component buildRolesEdit(final RequestLocal roleRL,
                                            final RequestLocal portalsiteRL,
                                            final ActionListener onDelete) {
        final BoxPanel result = new BoxPanel();
        result.setWidth("100%");

        GridPanel formPanel = new GridPanel(2);
        //        formPanel.setWidth("100%");
        final Form properties = new Form("roleEdit", formPanel);

        properties.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.role_name")));

        final TextField roleNameEntry = new TextField("roleName") {
                public boolean isVisible(PageState ps) {
                    Role r = (Role)roleRL.get(ps);
                    return ((r != null) && !r.isSystem());
                }
            };
        roleNameEntry.addValidationListener(new NotEmptyValidationListener());

        final Label roleNameDisplay = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.null_role")) {
                public boolean isVisible(PageState ps) {
                    Role r = (Role)roleRL.get(ps);
                    return !roleNameEntry.isVisible(ps);
                }
            };
        roleNameDisplay.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    Role r = (Role)roleRL.get(ev.getPageState());
                    if (r == null) {
                        return;
                    }
                    Label tgt = (Label)ev.getTarget();
                    tgt.setLabel(r.getRoleName());
                }
            });


        final SimpleContainer roleNameComposite = new SimpleContainer();
        roleNameComposite.add(roleNameEntry);
        roleNameComposite.add(roleNameDisplay);
        properties.add(roleNameComposite);

        properties.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.assignee_title")));

        final TextField titleEntry = new TextField("assigneeTitle") {
                public boolean isVisible(PageState ps) {
                    return roleNameEntry.isVisible(ps);
                }
            };
        titleEntry.addValidationListener(
                                         new NotEmptyValidationListener());

        final Label titleDisplay = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.null_assignee_title")) {
                public boolean isVisible(PageState ps) {
                    return roleNameDisplay.isVisible(ps);
                }
            };
        titleDisplay.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    Role r = (Role)roleRL.get(ev.getPageState());
                    if (r == null) {
                        return;
                    }
                    Label tgt = (Label) ev.getTarget();
                    tgt.setLabel(r.getAssigneeTitle());
                }
            });

        final SimpleContainer titleComposite = new SimpleContainer();
        titleComposite.add(titleEntry);
        titleComposite.add(titleDisplay);
        properties.add(titleComposite);


        properties.add(new Label());
        properties.add(new Submit("Update Role") {
                public boolean isVisible(PageState ps) {
                    return roleNameEntry.isVisible(ps);
                }
            });

        properties.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    Role role = (Role) roleRL.get(ps);
                    roleNameEntry.setValue(ps, role.getRoleName());
                }
            });

        properties.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev)
                    throws FormProcessException {
                    PageState ps = ev.getPageState();
                    Role role = (Role) roleRL.get(ps);
                    if (!role.isSystem()) {
                        String newRoleName = (String) roleNameEntry.getValue(ps);
                        String newTitle = (String) titleEntry.getValue(ps);
                        role.setRoleName(newRoleName);
                        role.setAssigneeTitle(newTitle);
                        role.save();
                    }
                }
            });
        result.add(properties);

        final ActionLink deleteRole = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.delete_role").localize()) {
                public boolean isVisible(PageState ps) {
                    Role r = (Role) roleRL.get(ps);
                    return !r.isSystem();
                }
            };
        deleteRole.setConfirmation(
                                   "Really delete portal role?");
        deleteRole.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    final Role r = (Role) roleRL.get(ps);
                    r.getPortalSite().assertPrivilege(PrivilegeDescriptor.ADMIN);
                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            r.delete();
                        }
                    };
                    ex.run();
                    onDelete.actionPerformed(new ActionEvent(result, ps));
                }
            });
        result.add(deleteRole);

        final PartyPermissionEdit permWidget =
            new PartyPermissionEdit("Privileges", portalsiteRL, roleRL);
        result.add(permWidget);

        return result;
    }

    private static class ParticipantsModel implements ListModel {
        private PartyCollection m_parties;

        public ParticipantsModel(PortalSite psite) {
            m_parties = psite.getParticipants();
        }

        public boolean next() {
            return m_parties.next();
        }

        public String getKey() {
            return m_parties.getID().toString();
        }

        public Object getElement() {
            return m_parties.getDisplayName();
        }
    }
}
