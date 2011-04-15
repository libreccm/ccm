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

import com.arsdigita.portalserver.permissions.PartyPermissionEdit;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.ACSObjectCollectionListModel;

import com.arsdigita.kernel.permissions.*;
import com.arsdigita.portalserver.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.list.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.parameters.*;

import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListModel;

public class RolePane {

    public static Component create(final RequestLocal portalsiteRL) {
        final ACSObjectSelectionModel selectionModel =
            new ACSObjectSelectionModel("selectedRole");

        List rList = new List(new ListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    PortalSite p = (PortalSite)portalsiteRL.get(ps);
                    return new ACSObjectCollectionListModel(p.getRoles()) {
                            public Object getElement() {
                                RoleCollection rc = (RoleCollection)getCollection();
                                if (rc.isSystem()) {
                                    return rc.getRoleName() + "*";
                                } else {
                                    return rc.getRoleName();
                                }
                            }
                        };
                }
                public void lock() {}
                public boolean isLocked() { return true; }
            });
        rList.setSelectionModel(selectionModel);
        Label emptyView = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_roles_defined"));
        emptyView.setFontWeight(Label.ITALIC);
        rList.setEmptyView(emptyView);

        final DynamicListWizard dlw = 
           new DynamicListWizard("Roles", rList, selectionModel, "Add a role", new Label(""));

        final RequestLocal roleRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return (Role)selectionModel.getSelectedObject(ps);
                }
            };

        dlw.setAddPane(addPane(roleRL, portalsiteRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, (Role)roleRL.get(ps));
                }
            }));

        dlw.setEditPane(editPane(roleRL, portalsiteRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, null);
                }
            }));

        return dlw;
    }


    private static Component addPane(final RequestLocal roleRL,
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
            PortalSite psite = (PortalSite)portalsiteRL.get(ps);
            Role newRole =
                Role.createRole((PortalSite)portalsiteRL.get(ps),
                                roleName, assigneeTitle, "");
            newRole.save();
            PermissionService.grantPermission(
              new PermissionDescriptor(PrivilegeDescriptor.READ,psite,newRole));
            roleRL.set(ps, newRole);
            onAdd.actionPerformed(new ActionEvent(result, ps));
        }
    });

        return result;
    }



    private static Component editPane(final RequestLocal roleRL,
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
        titleEntry.addValidationListener( new NotEmptyValidationListener());

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
                    Label tgt = (Label)ev.getTarget();
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
                    Role role = (Role)roleRL.get(ps);
                    roleNameEntry.setValue(ps, role.getRoleName());
                }
            });

        properties.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev)
                    throws FormProcessException {
                    PageState ps = ev.getPageState();
                    Role role = (Role)roleRL.get(ps);
                    if (!role.isSystem()) {
                        String newRoleName = (String)roleNameEntry.getValue(ps);
                        String newTitle = (String)titleEntry.getValue(ps);
                        role.setRoleName(newRoleName);
                        role.setAssigneeTitle(newTitle);
                        role.save();
                    }
                }
            });
        result.add(properties);

        final ActionLink deleteRole = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.delete_role").localize()) {
                public boolean isVisible(PageState ps) {
                    Role r = (Role)roleRL.get(ps);
                    return !r.isSystem();
                }
            };
        deleteRole.setConfirmation(
                                   "Really delete portal role?");
        deleteRole.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    Role r = (Role)roleRL.get(ps);
                    r.delete();
                    onDelete.actionPerformed(new ActionEvent(result, ps));
                }
            });
        result.add(deleteRole);


        final PartyPermissionEdit permWidget =
            new PartyPermissionEdit("Privileges", portalsiteRL, roleRL);
        result.add(permWidget);

        return result;
    }
}
