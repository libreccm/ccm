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

import java.math.BigDecimal;
import java.util.Iterator;

import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.Role;
import com.arsdigita.portalserver.RoleCollection;

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

import com.arsdigita.portalserver.permissions.PartyPermissionEdit;

import org.apache.log4j.Category;

/**
 *
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 */
public class ParticipantEdit extends CompoundComponent {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/ParticipantEdit.java#6 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (ParticipantEdit.class.getName());

    private static final IteratorListModel.KeyFunction s_keyFunc =
        new IteratorListModel.KeyFunction() {
            public String getKey(Object obj) {
                return ((Role)obj).getID().toString();
            }
        };

    public ParticipantEdit(final Container container,
                           final RequestLocal portalsiteRL,
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
                                              (PortalSite) portalsiteRL.get(ps),
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
                        if (!role.hasDirectMemberOrSubgroup(participant)) {
                            role.addMemberOrSubgroup(participant);
                            role.save();
                        }
                    } else {
                        if (role.hasDirectMemberOrSubgroup(participant)) {
                            role.removeMemberOrSubgroup(participant);
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
                    PortalSite psite = (PortalSite)portalsiteRL.get(ps);
                    RoleCollection rc = psite.getRoles();
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
                        l.setLabel(role.getRoleName());
                        if (role.hasDirectMemberOrSubgroup(participant)) {
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
                                    portalsiteRL,
                                    participantRL));

        final ActionLink removeParticipant = new ActionLink(
                                "Remove this participant from this portal");
        removeParticipant.setClassAttr("actionLink");
        removeParticipant.setConfirmation(
                                      "Really remove participant from Portal?");
        removeParticipant.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final PageState ps = e.getPageState();
                    final PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    final Party p = (Party) participantRL.get(ps);
                    psite.assertPrivilege(PrivilegeDescriptor.ADMIN);
                    KernelExcursion ex = new KernelExcursion() {
                        protected void excurse() {
                            setEffectiveParty(Kernel.getSystemParty());
                            psite.removeParticipant(p);
                            psite.save();
                        }
                    };
                    ex.run();
                    onDelete.actionPerformed
                        (new ActionEvent(ParticipantEdit.this, ps));
                }
            });
        add(removeParticipant);
    }

    public ParticipantEdit(final RequestLocal portalsiteRL,
                           final RequestLocal participantRL,
                           final ActionListener onDelete) {
        this(new BoxPanel(BoxPanel.VERTICAL),
             portalsiteRL,
             participantRL,
             onDelete);
    }
}
