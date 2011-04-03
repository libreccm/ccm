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

package com.arsdigita.portalworkspace.ui.admin;

//import com.arsdigita.bebop.*;
//import com.arsdigita.bebop.list.*;
//import com.arsdigita.bebop.form.*;
//import com.arsdigita.bebop.event.*;
//import com.arsdigita.bebop.parameters.*;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
//import com.arsdigita.kernel.permissions.*;
//import com.arsdigita.portalserver.*;
//import com.arsdigita.portalserver.ui.*;
//import com.arsdigita.portalserver.PortalSite;
//import com.arsdigita.portalserver.permissions.PartyPermissionEdit;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.ui.ParticipantBrowsePane;
import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.london.util.ui.DomainObjectSelectionModel;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.ACSObjectCollectionListModel;
import com.arsdigita.toolbox.ui.IteratorListModel;

import java.util.ArrayList;

import org.apache.log4j.Category;

/**
 * PeoplePane.
 *
 * @author ashah  (2003/08/15)
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PeoplePane.java $
 */ 
public class PeoplePane extends SimpleContainer {

    private static final Category s_log = Category.getInstance
        (PeoplePane.class.getName());

    private RequestLocal m_workspaceRL;
    private StringParameter m_action;

    public PeoplePane() {
		setTag("portal:admin");
		setNamespace(PortalConstants.PORTAL_XML_NS);
    }

    public void init(StringParameter sp) {

        m_workspaceRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return Kernel.getContext().getResource();
                }
            };

        m_action = sp;

        Workspace workspace = (Workspace) Kernel.getContext().getResource();

        final DynamicListWizard roles = 
                   (DynamicListWizard) buildRoles(m_workspaceRL);

        final DynamicListWizard browse = (DynamicListWizard)
            ParticipantBrowsePane.createForAdmin
            (m_workspaceRL, m_action, new ActionListener() {
                 public void actionPerformed(ActionEvent ev) {
                     // Nothing
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

        add(browse);

        add(roles);
    }

    private static Component buildRoles(final RequestLocal workspaceRL) {

//         final ACSObjectSelectionModel selectionModel =
//             new ACSObjectSelectionModel("selectedRole");

//         List rList = new List(new ListModelBuilder() {
//                 public ListModel makeModel(List l, PageState ps) {
//                     Workspace workspace = (Workspace) workspaceRL.get(ps);
//                     return new ACSObjectCollectionListModel(workspace.getRoles()) {
//                             public Object getElement() {
//                                 RoleCollection rc = (RoleCollection) getCollection();

//                                 if (isSystem(rc)) {
//                                     return rc.getRole().getName() + "*";
//                                 } else {
//                                     return rc.getRole().getName();
//                                 }
//                             }
//                         };
//                 }

//                 public void lock() {
//                     // Do nothing.
//                 }

//                 public boolean isLocked() {
//                     return true;
//                 }
//             });
//         rList.setSelectionModel(selectionModel);

        final DomainObjectSelectionModel selectionModel =
            new DomainObjectSelectionModel("selectedRole") {
                public void setSelectedKey(PageState state,
                                           Object key) {
                    if (key != null) {
                        s_log.debug("Key: "+key.toString()+" "+key.getClass().getName());
                    }
                    if (key instanceof String) {
                        super.setSelectedKey(state, OID.valueOf((String) key));
                    } else {
                        super.setSelectedKey(state, key);
                    }
                }};

        List rList = new List(new ListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    RoleCollection rc = workspace.getRoles();
                    ArrayList al = new ArrayList();
                    while (rc.next()) {
                        Role r = rc.getRole();
                        al.add(r.getOID());
                    }
                    return new IteratorListModel(al.iterator()) {
                            public Object getElement() {
                                OID oid = (OID) super.getElement();
                                Role r = (Role) new Role(oid);

                                if (isSystem(r)) {
                                    return r.getName() + "*";
                                } else {
                                    return r.getName();
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
            }) {
                public Object getSelectedKey(PageState state) {
                    Object key = super.getSelectedKey(state);
                    if (key != null) {
                        s_log.debug("Key: "+key.toString()+" "+key.getClass().getName());
                    }
                    if (key instanceof String) {
                        String oid = (String) super.getSelectedKey(state);
                        return OID.valueOf(oid);
                    } else {
                        return key;
                    }
                }
            };
        rList.setSelectionModel(selectionModel);

        Label emptyView = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_roles_defined"));
        emptyView.setFontWeight(Label.ITALIC);
        rList.setEmptyView(emptyView);

        final DynamicListWizard dlw = new DynamicListWizard
            ("Workspace Roles", rList, selectionModel, "Add a role", new Label(""));

        final RequestLocal role = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return (Role) selectionModel.getSelectedObject(ps);
                }
            };

        dlw.setAddPane(buildRolesAdd(role, workspaceRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, (Role) role.get(ps));
                }
            }));

        dlw.setEditPane(buildRolesEdit(role, workspaceRL, new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    selectionModel.setSelectedObject(ps, null);
                }
            }));

        return dlw;
    }

    private static Component buildRolesAdd(final RequestLocal roleRL,
                                           final RequestLocal workspaceRL,
                                           final ActionListener onAdd) {
        final Form result = new Form("roleAdd");

        result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.role_name")));

        final TextField roleNameField = new TextField("roleName");
        roleNameField
            .getParameterModel()
            .addParameterListener(new NotEmptyValidationListener());

        result.add(roleNameField);

//         result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.assignee_title")));

//         final TextField assigneeTitleField = new TextField("assigneeTitle");
//         assigneeTitleField
//             .getParameterModel()
//             .addParameterListener(new NotEmptyValidationListener());

//         result.add(assigneeTitleField);

        result.add(new Label());
        result.add(new Submit("Add Role"));

        result.addProcessListener(new FormProcessListener() {
          public void process(FormSectionEvent ev) {
            PageState ps = ev.getPageState();
            String roleName = (String) roleNameField.getValue(ps);
            //String assigneeTitle = (String)assigneeTitleField.getValue(ps);
            // XXX: creating roles with no descriptions
            Workspace workspace = (Workspace) workspaceRL.get(ps);
            //Role newRole =
            //    Role.createRole(workspace, roleName, assigneeTitle, "");
            Role newRole =
                ((Group) workspace.getParty()).createRole(roleName);
            newRole.save();
            PermissionService.grantPermission(
              new PermissionDescriptor(PrivilegeDescriptor.READ,
                                       workspace,
                                       newRole.getGroup()));
               roleRL.set(ps, newRole);
               onAdd.actionPerformed(new ActionEvent(result, ps));
          }
            });

        return result;
    }

    private static Component buildRolesEdit(final RequestLocal roleRL,
                                            final RequestLocal workspaceRL,
                                            final ActionListener onDelete) {
        final BoxPanel result = new BoxPanel();
        result.setWidth("100%");

        GridPanel formPanel = new GridPanel(2);
        final Form properties = new Form("roleEdit", formPanel);

        properties.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.role_name")));

        final TextField roleNameEntry = new TextField("roleName") {
                public boolean isVisible(PageState ps) {
                    Role r = (Role)roleRL.get(ps);
                    return ((r != null) && !isSystem(r));
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
                    tgt.setLabel(r.getName());
                }
            });


        final SimpleContainer roleNameComposite = new SimpleContainer();
        roleNameComposite.add(roleNameEntry);
        roleNameComposite.add(roleNameDisplay);
        properties.add(roleNameComposite);

        properties.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.assignee_title")));

//         final TextField titleEntry = new TextField("assigneeTitle") {
//                 public boolean isVisible(PageState ps) {
//                     return roleNameEntry.isVisible(ps);
//                 }
//             };
//         titleEntry.addValidationListener(new NotEmptyValidationListener());

//         final Label titleDisplay = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.null_assignee_title")) {
//                 public boolean isVisible(PageState ps) {
//                     return roleNameDisplay.isVisible(ps);
//                 }
//             };
//         titleDisplay.addPrintListener(new PrintListener() {
//                 public void prepare(PrintEvent ev) {
//                     Role r = (Role)roleRL.get(ev.getPageState());
//                     if (r == null) {
//                         return;
//                     }
//                     Label tgt = (Label) ev.getTarget();
//                     tgt.setLabel(r.getAssigneeTitle());
//                 }
//             });

//         final SimpleContainer titleComposite = new SimpleContainer();
//         titleComposite.add(titleEntry);
//         titleComposite.add(titleDisplay);
//         properties.add(titleComposite);

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
                    roleNameEntry.setValue(ps, role.getName());
                }
            });

        properties.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev)
                    throws FormProcessException {
                    PageState ps = ev.getPageState();
                    Role role = (Role) roleRL.get(ps);
                    if (!isSystem(role)) {
                        String newRoleName = (String) roleNameEntry.getValue(ps);
                        //String newTitle = (String) titleEntry.getValue(ps);
                        role.setName(newRoleName);
                        //role.setAssigneeTitle(newTitle);
                        role.save();
                    }
                }
            });
        result.add(properties);

        final ActionLink deleteRole = new ActionLink( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.delete_role").localize()) {
                public boolean isVisible(PageState ps) {
                    Role r = (Role) roleRL.get(ps);
                    return !isSystem(r);
                }
            };
        deleteRole.setConfirmation("Really delete portal role?");
        deleteRole.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    final Role r = (Role) roleRL.get(ps);
                    //r.getPortalSite().assertPrivilege(PrivilegeDescriptor.ADMIN);
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    workspace.assertPrivilege(PrivilegeDescriptor.ADMIN);
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
            new PartyPermissionEdit("Privileges", workspaceRL, roleRL);
        result.add(permWidget);

        return result;
    }

    private static class ParticipantsModel implements ListModel {
        private PartyCollection m_parties;

        public ParticipantsModel(Workspace workspace) {
            m_parties = workspace.getParticipants();
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

    public static boolean isSystem(RoleCollection rc) {
        return "Administrators".equals(rc.getRole().getName());
    }

    public static boolean isSystem(Role r) {
        return "Administrators".equals(r.getName());
    }
}
