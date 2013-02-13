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

import com.arsdigita.web.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.table.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.xml.Element;

import java.util.*;
import java.util.List;

import org.apache.log4j.Category;

/**
 * PartyPermissionEdit.
 *
 * @author dennis (2003/08/15)
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/permissions/PartyPermissionEdit.java#2 $
 */ 
public class PartyPermissionEdit extends CompoundComponent {

    private static Category s_log = Category.getInstance
        (PartyPermissionEdit.class.getName());

    // Heavily-reused per-request label for renderer getComponent calls
    private final static RequestLocal s_dynamicLabel = new RequestLocal() {
            public Object initialValue(PageState ps) {
                return new Label();
            }
        };

    private static class PartyGrantsTable extends GrantsTable {

        static void getGrantsHelper(Party party,
                                    Application application,
                                    LinkedList ordering) {
            //Collection types,
            HashMap canonicalMap = new HashMap();

            Iterator privIter = PermissionService.getDirectPrivileges
                (application.getOID(), party.getOID());

            while (privIter.hasNext()) {
                Grant grant = new Grant();

                PrivilegeDescriptor priv =
                    (PrivilegeDescriptor) privIter.next();

                s_log.debug("Current grant in loop is " + priv);

                if (priv.equals(PrivilegeDescriptor.CREATE)) {
                    // Skip create privs.  They are created and
                    // destroyed implicitly through the other
                    // privileges, read, edit, and manage.
                    continue;
                }

                grant.populatePrivilege(priv);

                if (grant.level < 0) {
                    continue;
                }

//                 if (grant.objectType != null &&
//                     types != null &&
//                     !types.contains(grant.objectType)) {
//                     continue;
//                 }

                grant.granteeID = party.getID();
                grant.granteeName = party.getName();
                grant.granteeIsUser = (party instanceof User);
                grant.objectID = application.getID();
                grant.objectName = application.getDisplayName();

                if (!canonicalMap.containsKey(grant)) {
                    ordering.add(grant);
                    canonicalMap.put(grant, grant);
                    continue;
                }

                Grant canonical = (Grant)canonicalMap.get(grant);

                if (grant.level > canonical.level) {
                    canonical.level = grant.level;
                    canonical.basePrivilege = grant.basePrivilege;
                }
            }
        }

        public PartyGrantsTable(final RequestLocal workspaceRL,
                                final RequestLocal partyRL,
                                final RequestLocal typesRL) {
            super(new RequestLocal() {
                    public Object initialValue(PageState ps) {
                        LinkedList ordering = new LinkedList();

                        Workspace workspace = 
                           (Workspace) workspaceRL.get(ps);
                        Party party = getParty(partyRL, ps);
                        //Collection types = (Collection) typesRL.get(ps);

                        //getGrantsHelper(party, workspace, types, ordering);
                        getGrantsHelper(party, workspace, ordering);

                        ApplicationCollection ac =
                            workspace.getFullPageWorkspaceApplications();

                        while (ac.next()) {
                            getGrantsHelper(party,
                                            ac.getApplication(),
                                            ordering);
                            //types,
                        }

                        return ordering.iterator();
                    }
                }, typesRL, true);

            TableColumn appColumn = new TableColumn(0, "On:");
            appColumn.setCellRenderer(new TableCellRenderer() {
                    public Component getComponent(Table table, PageState ps,
                                                  Object value, boolean isSelected,
                                                  Object key, int row, int col) {
                        Grant grant = (Grant)value;
                        Label l = (Label)s_dynamicLabel.get(ps);
                        l.setLabel(grant.objectName);
                        return l;
                    }
                });
            appColumn.setWidth("150");
            getColumnModel().add(0, appColumn);
        }
    }

    private static class BoldLabel extends Label {
        public BoldLabel(String text) {
            super(text);
            setFontWeight(Label.BOLD);
        }
    }

    private static class MyAddGrantForm extends AddGrantForm {

        private RequestLocal m_workspaceRL;
        private RequestLocal m_partyRL;
        private RequestLocal m_typesRL;
        private RequestLocal m_errorMessageRL;
        private ActionListener m_onComplete;

        private RequestLocal m_targetsRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    List result = new ArrayList();

                    Party party = getParty(m_partyRL, ps);
                    Workspace workspace = 
                        (Workspace) m_workspaceRL.get(ps);
                    PermissionDescriptor perm =
                        new PermissionDescriptor(PrivilegeDescriptor.READ,
                                                 workspace,
                                                 party);
                    if (!PermissionService.checkDirectPermission(perm)) {
                        result.add(new Option(workspace.getID().toString(),
                                              workspace.getDisplayName()));
                    }

                    ApplicationCollection ac =
                        workspace.getFullPageWorkspaceApplications();
                    while (ac.next()) {
                        Application app = ac.getApplication();
                        perm =
                            new PermissionDescriptor(PrivilegeDescriptor.READ,
                                                     app,
                                                     party);
                        if (!PermissionService.checkDirectPermission(perm)) {
                            result.add(new Option(app.getID().toString(),
                                                  app.getDisplayName()));
                        }
                    }

                    return result;
                }
            };

        private Submit m_cancel = new Submit("Return");

        private MyAddGrantForm(RequestLocal workspaceRL,
                               RequestLocal partyRL,
                               RequestLocal typesRL,
                               RequestLocal errorMessageRL,
                               ActionListener onComplete) {
            super("addGrant", new BoxPanel(), errorMessageRL);
            setMethod(Form.POST);

            m_workspaceRL = workspaceRL;
            m_partyRL = partyRL;
            m_typesRL = typesRL;
            m_errorMessageRL = errorMessageRL;
            m_onComplete = onComplete;

            final GridPanel widgets = new GridPanel(4);

            // Row 1: Labels
            widgets.add(new BoldLabel("Select Target:"));

            widgets.add(new BoldLabel("On:"));

//             widgets.add(new BoldLabel("On:") {
//                     public void generateXML(PageState ps, Element parent) {
//                         Collection types = (Collection) m_typesRL.get(ps);
//                         if ((types == null) || (types.size() <= 1)) {
//                             parent.newChildElement("bebop:label", BEBOP_XML_NS);
//                             return;
//                         } else {
//                             super.generateXML(ps, parent);
//                         }
//                     }
//                 });

            widgets.add(new BoldLabel("Privilege:"));

            widgets.add(new Label(""));

            // Row 2: Widgets

            // Create a hidden widget for the partyID
            add(new Hidden(getPartyParameter()));

            SingleSelect objectSelect =
                new SingleSelect(getObjectParameter());
            objectSelect.setPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        SingleSelect target = (SingleSelect)ev.getTarget();
                        PageState ps = ev.getPageState();
                        List options = (List) m_targetsRL.get(ps);
                        for (Iterator it = options.iterator(); it.hasNext(); ) {
                            target.addOption((Option) it.next());
                        }
                    }
                });
            widgets.add(objectSelect);

            // Selection widget for the type.
            //widgets.add(new TypeSingleSelect(getTypeParameter(), typesRL));

            // Selection widget for the privilege
            widgets.add(new PrivilegeSingleSelect(getPrivilegeParameter()));

            // Submission button
            final Submit grant = new Submit("Grant");
            widgets.add(grant);
            add(widgets);

            add(new ErrorMessageDisplay(errorMessageRL));

            add(m_cancel);

            addSubmissionListener(new FormSubmissionListener() {
                    public void submitted(FormSectionEvent evt)
                        throws FormProcessException {
                        PageState ps = evt.getPageState();
                        if (m_cancel.isSelected(ps)) {
                            ps.reset(MyAddGrantForm.this);
                            m_onComplete.actionPerformed(
                                                         new ActionEvent(MyAddGrantForm.this, ps)
                                                         );
                            throw new FormProcessException("cancel");
                        }
                    }
                });

            // Init listener to initialize the objectID parameter
            addInitListener(new FormInitListener() {
                    public void init(FormSectionEvent ev)
                        throws FormProcessException {
                        PageState ps = ev.getPageState();
                        FormData fd = ev.getFormData();
                        fd.put(getPartyParameter().getName(),
                               getParty(m_partyRL, ps).getID());
                        List targets = (List) m_targetsRL.get(ps);
                        if (targets.size() == 0) {
                            widgets.setVisible(ps, false);
                            m_cancel.setVisible(ps, true);
                            throw new FormValidationException(
                                                              "There are no objects that this " +
                                                              "party can be granted permission on."
                                                              );
                        }
                    }
                });
        }

        public MyAddGrantForm(RequestLocal workspaceRL, RequestLocal partyRL,
                              RequestLocal typesRL,
                              ActionListener onComplete) {
            this(workspaceRL, partyRL, typesRL, new RequestLocal(),
                 onComplete);
        }

        public void register(Page p) {
            super.register(p);
            p.setVisibleDefault(m_cancel, false);
        }

        protected void fireProcess(FormSectionEvent evt)
            throws FormProcessException {
            super.fireProcess(evt);

            PageState ps = evt.getPageState();
            m_onComplete.actionPerformed(
                                         new ActionEvent(MyAddGrantForm.this, ps)
                                         );
        }

    }


    public PartyPermissionEdit(String title,
                               RequestLocal workspaceRL,
                               RequestLocal partyRL) {
        super(new ModalContainer());

        final ModalContainer container = (ModalContainer)getContainer();

        final BoxPanel mainDisplay = new BoxPanel();
        mainDisplay.setWidth("100%");
        container.add(mainDisplay);
        container.setDefaultComponent(mainDisplay);

        ActionListener returnToMain = new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    container.setVisibleComponent(ev.getPageState(), mainDisplay);
                }
            };
        final MyAddGrantForm addForm = new MyAddGrantForm(workspaceRL,
                                                          partyRL,
                                                          new RequestLocal(),
                                                          returnToMain);
        container.add(addForm);


        GridPanel header = new GridPanel(2);
        //        header.setWidth("100%");
        Label titleLabel = new Label(title);
        titleLabel.setFontWeight(Label.BOLD);
        header.add(titleLabel, BoxPanel.LEFT);

        ActionLink grantLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.permissions.add_a_privilege").localize());
        grantLink.setClassAttr("actionLink");
        grantLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    container.setVisibleComponent(ev.getPageState(), addForm);
                }
            });
        header.add(grantLink, BoxPanel.RIGHT);
        mainDisplay.add(header);

        mainDisplay.add(new PartyGrantsTable(workspaceRL,
                                             partyRL,
                                             new RequestLocal()));
    }

    public static Party getParty(RequestLocal partyRL, PageState ps) {
        Object obj = partyRL.get(ps);
        if (obj instanceof Role) {
            return ((Role) obj).getGroup();
        } else if (obj instanceof Party) {
            return (Party) obj;
        } else {
            return null;
        }
    }
}
