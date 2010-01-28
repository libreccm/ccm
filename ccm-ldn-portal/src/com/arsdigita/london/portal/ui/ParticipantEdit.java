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
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.toolbox.ui.IteratorListModel;
import com.arsdigita.domain.DomainCollectionIterator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.london.portal.ui.admin.PartyPermissionEdit;
import com.arsdigita.london.portal.util.GlobalizationUtil;  
import com.arsdigita.london.portal.Workspace;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.log4j.Category;

/**
 * ParticipantEdit.
 *
 * @author ashah (2003/08/15)
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: com/arsdigita/portalserver/ui/ParticipantEdit.java $
 */
public class ParticipantEdit extends CompoundComponent {

    private static Category s_log = Category.getInstance
        (ParticipantEdit.class.getName());

    private static final IteratorListModel.KeyFunction s_keyFunc =
        new IteratorListModel.KeyFunction() {
            public String getKey(Object obj) {
                return ((Role)obj).getID().toString();
            }
        };

    public ParticipantEdit(final Container container,
                           final RequestLocal workspaceRL,
                           final RequestLocal participantRL,
                           final ActionListener onDelete) {
        super(container);
        ((BoxPanel) container).setWidth("100%");

        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.participant_info"));
        header.setFontWeight(Label.BOLD);

        add(header);
        add(new PropertySheet(new PropertySheetModelBuilder() {
                public void lock() {
                    /* Do nothing. */
                }

                public boolean isLocked() {
                    return container.isLocked();
                }

                public PropertySheetModel makeModel(PropertySheet sheet,
                                                    final PageState ps) {
                    return new ParticipantModel((Party) participantRL.get(ps),
                                                (Workspace) workspaceRL.get(ps),
                                                false);
                }
            }));

        add(new Label(""));

        Label roleHeader = new Label(GlobalizationUtil.globalize("cw.workspace.ui.participant_roles"));
        roleHeader.setFontWeight(Label.BOLD);
        add(roleHeader);

        final String GRANT = "grant";
        final String REVOKE = "revoke";
        final Table roleEdit = new Table() {
                public void respond(PageState ps) {
                    String eventName = ps.getControlEventName();
                    if (!GRANT.equals(eventName) && !REVOKE.equals(eventName)) {
                        throw new IllegalStateException("Unknown event name");
                    }
                    BigDecimal roleID =
                        new BigDecimal(ps.getControlEventValue());
                    Party participant = (Party)participantRL.get(ps);
                    Role role;
                    try {
                        role = new Role(roleID);
                    } catch (DataObjectNotFoundException ex) {
                        throw new IllegalStateException("Unknown role ID");
                    }
                    if (GRANT.equals(eventName)) {
                        if (!role.getGroup().hasDirectMemberOrSubgroup(participant)) {
                            role.add(participant);
                            role.save();
                        }
                    } else {
                        if (role.getGroup().hasDirectMemberOrSubgroup(participant)) {
                            role.remove(participant);
                            role.save();
                        }
                    }
                }
            };
        roleEdit.setHeader(null);
        roleEdit.getColumnModel().add(new TableColumn(0));
        roleEdit.getColumnModel().add(new TableColumn(1));
        roleEdit.getColumnModel().add(new TableColumn(2));
        roleEdit.setClassAttr("plain");
        roleEdit.setModelBuilder(new AbstractTableModelBuilder() {
                public TableModel makeModel(Table t, PageState ps) {
                    Workspace workspace = (Workspace) workspaceRL.get(ps);
                    RoleCollection rc = ((Group) workspace.getParty()).getRoles();
                    // This chain of adapters is either sick or
                    // beautiful.  I'm not sure which.
                    Iterator roleIter = new DomainCollectionIterator(rc);
                    ListModel roleLM =
                        new IteratorListModel(roleIter, s_keyFunc);
                    return new GridTableModel(roleLM, 3);
                }
            });

        final Label emptyLabel = new Label("");
        final RequestLocal dynamicLabel = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return new Label();
                }
            };
        final RequestLocal eventValueRL = new RequestLocal();
        final RequestLocal grantDisplay = new RequestLocal() {
                public Object initialValue(PageState state) {
                    SimpleContainer result = new SimpleContainer();
                    result.add(new ControlLink(Icons.CHECK_EMPTY_16) {
                            public void setControlEvent(PageState ps) {
                                ps.setControlEvent(roleEdit, GRANT,
                                                 (String)eventValueRL.get(ps));
                            }
                        });

                    result.add((Component)dynamicLabel.get(state));
                    return result;
                }
            };
        final RequestLocal revokeDisplay = new RequestLocal() {
                public Object initialValue(PageState state) {
                    SimpleContainer result = new SimpleContainer();
                    result.add(new ControlLink(Icons.CHECK_FULL_16) {
                            public void setControlEvent(PageState ps) {
                                ps.setControlEvent(roleEdit,
                                                   REVOKE,
                                                   (String)eventValueRL.get(ps));
                            }
                        });

                    result.add((Component)dynamicLabel.get(state));
                    return result;
                }
            };
        roleEdit.setDefaultCellRenderer(new DefaultTableCellRenderer() {
                public Component getComponent(Table t, PageState ps,
                                              Object value, boolean isSelected,
                                              Object key, int row, int col) {
                    if (value == null) {
                        return emptyLabel;
                    }
                    Party participant = (Party)participantRL.get(ps);
                    Role role = (Role)value;

                    if (value != null) {
                        eventValueRL.set(ps, role.getID().toString());
                        Label l = (Label)dynamicLabel.get(ps);
                        l.setLabel(role.getName());
                        if (role.getGroup().hasDirectMemberOrSubgroup(participant)) {
                            return (Component)revokeDisplay.get(ps);
                        } else {
                            return (Component)grantDisplay.get(ps);
                        }
                    } else {
                        return emptyLabel;
                    }
                }
            });
        add(roleEdit);

        add(new PartyPermissionEdit("Additional Privileges",
                                    workspaceRL,
                                    participantRL));

        final ActionLink removeParticipant = new ActionLink(
                                "Remove this participant from this portal");
        removeParticipant.setClassAttr("actionLink");
        removeParticipant.setConfirmation(
                                      "Really remove participant from Portal?");
        removeParticipant.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final PageState ps = e.getPageState();
                    final Party p = (Party) participantRL.get(ps);
                    final Workspace workspace = (Workspace) workspaceRL.get(ps);
                    workspace.assertPrivilege(PrivilegeDescriptor.ADMIN);
                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            workspace.removeParticipant(p);
                            workspace.save();
                        }
                    };
                    ex.run();
                    onDelete.actionPerformed
                        (new ActionEvent(ParticipantEdit.this, ps));
                }
            });
        add(removeParticipant);
    }

    public ParticipantEdit(final RequestLocal workspaceRL,
                           final RequestLocal participantRL,
                           final ActionListener onDelete) {
        this(new BoxPanel(BoxPanel.VERTICAL),
             workspaceRL,
             participantRL,
             onDelete);
    }
}
