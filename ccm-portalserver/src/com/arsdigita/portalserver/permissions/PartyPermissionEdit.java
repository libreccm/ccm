/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.permissions;




import java.util.*;
import java.util.List;
import com.arsdigita.web.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;
import com.arsdigita.portalserver.*;
import com.arsdigita.portalserver.permissions.util.GlobalizationUtil;
import com.arsdigita.bebop.*;
import com.arsdigita.bebop.table.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.event.*;

import com.arsdigita.xml.Element;

import org.apache.log4j.Category;

public class PartyPermissionEdit extends CompoundComponent {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/permissions/PartyPermissionEdit.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

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
                                    Collection types,
                                    LinkedList ordering) {
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

                if (grant.objectType != null &&
                    types != null &&
                    !types.contains(grant.objectType)) {
                    continue;
                }

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

        public PartyGrantsTable(final RequestLocal portalsiteRL,
                                final RequestLocal partyRL,
                                final RequestLocal typesRL) {
            super(new RequestLocal() {
                    public Object initialValue(PageState ps) {
                        LinkedList ordering = new LinkedList();

                        PortalSite psite = 
                           (PortalSite)portalsiteRL.get(ps);
                        Party party = (Party)partyRL.get(ps);
                        Collection types = (Collection)typesRL.get(ps);

                        getGrantsHelper(party, psite, types, ordering);

                        ApplicationCollection ac =
                            psite.getFullPagePortalSiteApplications();

                        while (ac.next()) {
                            getGrantsHelper(party,
                                            ac.getApplication(),
                                            types,
                                            ordering);
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

        private RequestLocal m_portalsiteRL;
        private RequestLocal m_partyRL;
        private RequestLocal m_typesRL;
        private RequestLocal m_errorMessageRL;
        private ActionListener m_onComplete;

        private RequestLocal m_targetsRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    List result = new ArrayList();

                    Party party = (Party) m_partyRL.get(ps);
                    PortalSite psite = 
                        (PortalSite) m_portalsiteRL.get(ps);
                    PermissionDescriptor perm =
                        new PermissionDescriptor(PrivilegeDescriptor.READ,
                                                 psite,
                                                 party);
                    if (!PermissionService.checkDirectPermission(perm)) {
                        result.add(new Option(psite.getID().toString(),
                                              psite.getDisplayName()));
                    }

                    ApplicationCollection ac =
                        psite.getFullPagePortalSiteApplications();
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

        private MyAddGrantForm(RequestLocal portalsiteRL,
                               RequestLocal partyRL,
                               RequestLocal typesRL,
                               RequestLocal errorMessageRL,
                               ActionListener onComplete) {
            super("addGrant", new BoxPanel(), errorMessageRL);
            setMethod(Form.POST);

            m_portalsiteRL = portalsiteRL;
            m_partyRL = partyRL;
            m_typesRL = typesRL;
            m_errorMessageRL = errorMessageRL;
            m_onComplete = onComplete;

            final GridPanel widgets = new GridPanel(4);

            // Row 1: Labels
            widgets.add(new BoldLabel("Select Target:"));

            widgets.add(new BoldLabel("On:") {
                    public void generateXML(PageState ps, Element parent) {
                        Collection types = (Collection) m_typesRL.get(ps);
                        if ((types == null) || (types.size() <= 1)) {
                            parent.newChildElement("bebop:label", BEBOP_XML_NS);
                            return;
                        } else {
                            super.generateXML(ps, parent);
                        }
                    }
                });

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
                        target.clearOptions();
                        PageState ps = ev.getPageState();
                        List options = (List) m_targetsRL.get(ps);
                        for (Iterator it = options.iterator(); it.hasNext(); ) {
                            target.addOption((Option) it.next());
                        }
                    }
                });
            widgets.add(objectSelect);

            // Selection widget for the type.
            widgets.add(new TypeSingleSelect(getTypeParameter(), typesRL));

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
                            throw new FormProcessException(GlobalizationUtil.globalize(
                                    "cw.cw.permissions.cancel"));
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
                               ((Party) m_partyRL.get(ps)).getID());
                        List targets = (List) m_targetsRL.get(ps);
                        if (targets.size() == 0) {
                            widgets.setVisible(ps, false);
                            m_cancel.setVisible(ps, true);
                            throw new FormProcessException(GlobalizationUtil.globalize(
                                    "cw.cw.permissions.no_objects"));
                        }
                    }
                });
        }

        public MyAddGrantForm(RequestLocal portalsiteRL, RequestLocal partyRL,
                              RequestLocal typesRL,
                              ActionListener onComplete) {
            this(portalsiteRL, partyRL, typesRL, new RequestLocal(),
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
                               RequestLocal portalsiteRL,
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
        final MyAddGrantForm addForm = new MyAddGrantForm(portalsiteRL,
                                                          partyRL,
                                                          new RequestLocal(),
                                                          returnToMain);
        container.add(addForm);


        GridPanel header = new GridPanel(2);
        //        header.setWidth("100%");
        Label titleLabel = new Label(title);
        titleLabel.setFontWeight(Label.BOLD);
        header.add(titleLabel, BoxPanel.LEFT);

        ActionLink grantLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.cw.permissions.add_a_privilege").localize());
        grantLink.setClassAttr("actionLink");
        grantLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    container.setVisibleComponent(ev.getPageState(), addForm);
                }
            });
        header.add(grantLink, BoxPanel.RIGHT);
        mainDisplay.add(header);

        mainDisplay.add(new PartyGrantsTable(portalsiteRL,
                                             partyRL,
                                             new RequestLocal()));
    }
}
