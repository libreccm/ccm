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


import com.arsdigita.portalserver.permissions.util.GlobalizationUtil; 


import java.util.LinkedList;
import java.util.Collection;
import java.util.HashMap;

import com.arsdigita.xml.Element;

import com.arsdigita.util.Assert;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionService;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.table.*;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;

import com.arsdigita.bebop.util.Color;

import com.arsdigita.portalserver.*;

import com.arsdigita.util.Assert;
import org.apache.log4j.Category;


public class ObjectPermissionEdit extends CompoundComponent {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/permissions/ObjectPermissionEdit.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (ObjectPermissionEdit.class.getName());

    // Heavily-reused per-request label for renderer getComponent calls
    private final static RequestLocal s_dynamicLabel = new RequestLocal() {
            public Object initialValue(PageState ps) {
                return new Label();
            }
        };


    private static class ObjectGrantsTable extends GrantsTable {
        static void getGrantsHelper(ACSObject object,
                                    Collection types,
                                    LinkedList ordering) {
            HashMap canonicalMap = new HashMap();

            ObjectPermissionCollection opc =
                PermissionService.getDirectGrantedPermissions(object.getOID());

            try {
                while (opc.next()) {
                    s_log.debug
                        ("Current grant in loop is " + opc.getPrivilege());

                    if (opc.isInherited()
                        // Skip create privileges.  They are created
                        // and destroyed implicitly when the other
                        // privileges are used.
                        || opc.getPrivilege().equals
                        (PrivilegeDescriptor.CREATE)) {
                        continue;
                    }

                    Grant grant = new Grant();

                    grant.populatePrivilege(opc.getPrivilege());

                    if (grant.level < 0) {
                        continue;
                    }

                    if (grant.objectType != null
                        && types != null
                        && !types.contains(grant.objectType)) {
                        continue;
                    }

                    grant.granteeID = opc.getGranteeID();
                    grant.granteeName = opc.getGranteeName();
                    grant.granteeIsUser = opc.granteeIsUser();
                    grant.objectID = object.getID();
                    grant.objectName = object.getDisplayName();

                    if (!canonicalMap.containsKey(grant)) {
                        ordering.add(grant);
                        canonicalMap.put(grant, grant);
                        continue;
                    }

                    Grant canonical = (Grant) canonicalMap.get(grant);

                    if (grant.level > canonical.level) {
                        canonical.level = grant.level;
                        canonical.basePrivilege = grant.basePrivilege;
                    }
                }
            } finally {
                opc.close();
            }
        }


        public ObjectGrantsTable(RequestLocal grantsRL,
                                 RequestLocal typesRL,
                                 boolean isEditable) {
            super(grantsRL, typesRL, isEditable);

            // COLUMN 0: The party with the grant
            TableColumn partyColumn = new TableColumn(0, "User or Role");
            partyColumn.setCellRenderer(new TableCellRenderer() {
                    final RequestLocal m_userNameDisplay = new RequestLocal() {
                            public Object initialValue(PageState ps) {
                                SimpleContainer result = new SimpleContainer();
                                result.add(Icons.USER_16);
                                result.add((Component) s_dynamicLabel.get(ps));
                                return result;
                            }
                        };
                    final RequestLocal m_groupNameDisplay = new RequestLocal() {
                            public Object initialValue(PageState ps) {
                                SimpleContainer result = new SimpleContainer();
                                result.add(Icons.GROUP_16);
                                result.add((Component)s_dynamicLabel.get(ps));
                                return result;
                            }
                        };
                    public Component getComponent(Table table,
                                                  PageState ps,
                                                  Object value,
                                                  boolean isSelected,
                                                  Object key,
                                                  int row,
                                                  int column) {
                        Grant grant = (Grant)value;
                        Label nameLabel = (Label)s_dynamicLabel.get(ps);
                        nameLabel.setLabel( (String) GlobalizationUtil.globalize("cw.cw.permissions.").localize() + grant.granteeName);
                        if (grant.granteeIsUser) {
                            return (Component) m_userNameDisplay.get(ps);
                        } else {
                            return (Component) m_groupNameDisplay.get(ps);
                        }
                    }
                });
            getColumnModel().add(0, partyColumn);

        }
    }



    private static class ObjectDirectGrantsTable extends ObjectGrantsTable {
        public ObjectDirectGrantsTable(final RequestLocal objectRL,
                                       final RequestLocal typesRL) {
            super(new RequestLocal() {
                    public Object initialValue(PageState ps) {
                        LinkedList ordering = new LinkedList();
                        getGrantsHelper((ACSObject) objectRL.get(ps),
                                        (Collection) typesRL.get(ps),
                                        ordering);
                        return ordering.iterator();
                    }
                }, typesRL, true);
        }

    }



    private static class ObjectIndirectGrantsTable extends ObjectGrantsTable {
        public ObjectIndirectGrantsTable(final RequestLocal objectRL,
                                         final RequestLocal typesRL) {
            super(new RequestLocal() {
                    public Object initialValue(PageState ps) {
                        LinkedList ordering = new LinkedList();

                        ACSObject object = (ACSObject) objectRL.get(ps);
                        Collection types = (Collection) typesRL.get(ps);

                        // FIXME: May need to do this by getting the OID
                        // and doing a verifySubtype check
                        while (!(object instanceof PortalSite)) {
                            DataObject ctx = PermissionService.getContext(object);
                            if (ctx == null) {
                                break;
                            }
                            object = (ACSObject) DomainObjectFactory.newInstance
                                (ctx);
                            getGrantsHelper(object, types, ordering);
                        }

                        return ordering.iterator();
                    }
                }, typesRL, false);

            // 'Target' column
            TableColumn targetColumn = new TableColumn(1, "On");
            targetColumn.setCellRenderer(new TableCellRenderer() {
                    public Component getComponent(Table table,
                                                  PageState ps,
                                                  Object value,
                                                  boolean isSelected,
                                                  Object key,
                                                  int row,
                                                  int column) {
                        Grant grant = (Grant) value;
                        Label l = (Label) s_dynamicLabel.get(ps);
                        l.setLabel(grant.objectName);
                        return l;
                    }
                });
            getColumnModel().add(1, targetColumn);
        }
    }

    private class MainDisplay extends CompoundComponent {
        MainDisplay(final RequestLocal objectRL,
                    final RequestLocal directTypesRL,
                    final RequestLocal indirectTypesRL,
                    final ActionListener onAddClick) {
            super(new BoxPanel(BoxPanel.VERTICAL));


            // Header for direct permission list
            final GridPanel directHeader = new GridPanel(2);

            Label directLabel =
                new Label(GlobalizationUtil.globalize("cw.cw.permissions.view_and_manage_specific_permissions"));
            directLabel.setFontWeight(Label.BOLD);
            directHeader.add(directLabel, GridPanel.LEFT);
            ActionLink newGrantLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.cw.permissions.add_user_or_role").localize());
            newGrantLink.setClassAttr("actionLink");
            newGrantLink.addActionListener(onAddClick);
            directHeader.add(newGrantLink, GridPanel.RIGHT);
            add(directHeader);

            add(new Label("The following users and roles have specific " +
                          "privileges on this knowledge item:"));

            // Direct permission list
            Table directTable = new ObjectDirectGrantsTable
                (objectRL, directTypesRL);
            directTable.setCellPadding("5");
            Label directEmptyView = new Label("No specific privileges are " +
                                              "defined on this item.");
            directEmptyView.setFontWeight(Label.ITALIC);
            directTable.setEmptyView(directEmptyView);
            add(directTable);

            add(new Label(" "));

            // Header for indirect permission list
            Label indirectLabel = new Label(GlobalizationUtil.globalize("cw.cw.permissions.view_general_permissions"));
            indirectLabel.setFontWeight(Label.BOLD);
            add(indirectLabel);

            add(new Label("The following users and roles have broader " +
                          "privileges applying to this and other knowledge " +
                          "items.  Note that these are inherited privileges " +
                          "and can only be changed from the location given " +
                          "in the \"On\" column."));


            // Indirect permission list
            Table indirectTable = new ObjectIndirectGrantsTable
                (objectRL, indirectTypesRL);

            indirectTable.setCellPadding("5");
            Label indirectEmpty = new Label("No general permissions apply " +
                                            "to this item.");
            indirectEmpty.setFontWeight(Label.ITALIC);
            indirectTable.setEmptyView(indirectEmpty);
            add(indirectTable);

        }

    }

    // Maximum party search results
    private static final long MAX_RESULTS = 20;

    private class NewGrantDisplay extends CompoundComponent {
        // RequestLocal storing the object
        private final RequestLocal m_objectRL;

        // Collection of 'relevant types'
        private final RequestLocal m_typesRL;

        // Parameter containing user's search string
        private final StringParameter m_queryParam =
            new StringParameter("queryParam");

        // Flag indicating whether or not to limit search to current
        // workspace's participants
        private final BooleanParameter m_limitParam =
            new BooleanParameter("limitParam");

        private final RequestLocal m_partiesRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    PortalSite psite =
                        PortalSite.getCurrentPortalSite(ps.getRequest());

                    String queryString = (String) ps.getValue(m_queryParam);

                    PartyCollection parties;
                    Boolean limit = (Boolean) ps.getValue(m_limitParam);

                    if (limit != null && limit.booleanValue()) {
                        parties = psite.getParticipants();
                    } else {
                        parties = Party.retrieveAllParties();
                    }

                    parties.filter(queryString);

                    return parties;
                }
            };


        private void clearQuery(PageState ps) {
            ps.setValue(m_queryParam, null);
            ps.setValue(m_limitParam, null);
        }

        private void setQuery(PageState ps, String query, Boolean limit) {
            ps.setValue(m_queryParam, query);
            ps.setValue(m_limitParam, limit);
        }

        private boolean haveQuery(PageState ps) {
            return (ps.getValue(m_queryParam) != null);
        }

        private PartyCollection getQueryResults(PageState ps) {
            if (!haveQuery(ps)) {
                throw new IllegalStateException("No query specified.");
            }
            return (PartyCollection) m_partiesRL.get(ps);
        }

        private class BoldLabel extends Label {
            public BoldLabel(String text) {
                super(text);
                setFontWeight(Label.BOLD);
            }
        }

        private class MyAddGrantForm extends AddGrantForm {
            public MyAddGrantForm(String name,
                                  Widget partyWidget,
                                  final RequestLocal errorMessageRL) {
                super(name, new GridPanel(4), errorMessageRL);
                setMethod(Form.POST);

                // Row 1: Labels
                add(new BoldLabel("Select Grantee:"));

                add(new BoldLabel("On:") {
                        public void generateXML(PageState ps, Element parent) {
                            Collection types = (Collection)m_typesRL.get(ps);

                            if ((types == null) || (types.size() <= 1)) {
                                parent.newChildElement("bebop:label", BEBOP_XML_NS);
                                return;
                            } else {
                                super.generateXML(ps, parent);
                            }
                        }
                    });

                add(new BoldLabel("Privilege:"));

                add(new Label(""));

                // Row 2: Widgets

                // Create a hidden widget for the objectID
                add(new Hidden(getObjectParameter()));

                // Use provided widget for partyID
                partyWidget.setParameterModel(getPartyParameter());
                add(partyWidget);

                // Selection widget for the type.
                add(new TypeSingleSelect(getTypeParameter(), m_typesRL));

                // Selection widget for the privilege
                add(new PrivilegeSingleSelect(getPrivilegeParameter()));

                // Submission button
                add(new Submit("Grant"));

                // Init listener to initialize the objectID parameter
                addInitListener(new FormInitListener() {
                        public void init(FormSectionEvent ev) {
                            PageState ps = ev.getPageState();
                            FormData fd = ev.getFormData();
                            fd.put(getObjectParameter().getName(),
                                   ((ACSObject)m_objectRL.get(ps)).getID());
                        }
                    });

                addProcessListener(new FormProcessListener() {
                        public void process(FormSectionEvent ev) {
                            PageState ps = ev.getPageState();
                            NewGrantDisplay.this.fireCompletionEvent(ps);
                        }
                    });
            }
        }

        NewGrantDisplay(RequestLocal objectRL,
                        RequestLocal typesRL) {
            m_objectRL = objectRL;
            m_typesRL = typesRL;

            // This RL makes available a PartyCollection containing
            // the result of the specified query

            final BoxPanel screen1 = new BoxPanel(BoxPanel.VERTICAL) {
                    public boolean isVisible(PageState ps) {
                        if (!haveQuery(ps)) {
                            return true;
                        }
                        PartyCollection pc =
                            NewGrantDisplay.this.getQueryResults(ps);
                        long n = pc.size();
                        return (n == 0 || n > MAX_RESULTS);
                    }
                };
            add(screen1);


            Label roleFormTitle = new BoldLabel("");
            roleFormTitle.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        PageState ps = ev.getPageState();
                        Label tgt = (Label)ev.getTarget();
                        PortalSite psite =
                            PortalSite.getCurrentPortalSite(ps.getRequest());
                        tgt.setLabel("Grant privilege to a role from the \"" +
                                     psite.getTitle() + "\" portal:");
                    }
                });
            screen1.add(roleFormTitle);

            // Form for granting to a portal role
            final RequestLocal rfErrorMessageRL = new RequestLocal();

            SingleSelect roleField = new SingleSelect("roleField");
            roleField.setPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        SingleSelect tgt = (SingleSelect)ev.getTarget();
                        PageState ps = ev.getPageState();
                        PortalSite psite =
                            PortalSite.getCurrentPortalSite(ps.getRequest());
                        RoleCollection rc = psite.getRoles();
                        while (rc.next()) {
                            Option o = new Option
                                (rc.getID().toString(), rc.getRoleName());
                            tgt.addOption(o);
                        }
                    }
                });
            Form roleForm = new MyAddGrantForm("roleGrant",
                                               roleField,
                                               rfErrorMessageRL);
            screen1.add(roleForm);

            // Error message display
            screen1.add(new ErrorMessageDisplay(rfErrorMessageRL));



            // Toplevel search label
            screen1.add(new BoldLabel("Search for a user or role to recieve " +
                                      "a privilege:"));

            Form otherSearch = new Form("otherSearch", new GridPanel(2));
            otherSearch.setRedirecting(true);

            final TextField queryField = new TextField("query");
            otherSearch.add(queryField);

            otherSearch.add(new Submit("Search"));

            Label limitLabel = new Label("");
            limitLabel.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        Label tgt = (Label)ev.getTarget();
                        PageState ps = ev.getPageState();
                        PortalSite psite =
                            PortalSite.getCurrentPortalSite(ps.getRequest());
                        tgt.setLabel("Limit search to \"" + psite.getTitle() +
                                     "\" participants");
                    }
                });

            final CheckboxGroup limitToParticipants =
                new CheckboxGroup("limitToParticipants");
            String[] limitDefault = { "yes" };
            limitToParticipants.setDefaultValue(limitDefault);
            limitToParticipants.addOption(new Option("yes", limitLabel));
            otherSearch.add(limitToParticipants);

            otherSearch.add(new Label(""));

            // Label to display when no matches are found
            Label noMatchLabel = new Label(GlobalizationUtil.globalize("cw.cw.permissions.no_matches_found")) {
                    public boolean isVisible(PageState ps) {
                        if (!haveQuery(ps)) {
                            return false;
                        }

                        PartyCollection pc = getQueryResults(ps);

                        if (pc == null || pc.size() > 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                };
            noMatchLabel.setColor(Color.red);
            otherSearch.add(noMatchLabel);

            // Label to display when too many matches are found.
            Label tooManyLabel =
                new Label(GlobalizationUtil.globalize("cw.cw.permissions.too_many_matches_refine_your_search")) {
                    public boolean isVisible(PageState ps) {
                        if (!haveQuery(ps)) {
                            return false;
                        }

                        PartyCollection pc = getQueryResults(ps);

                        if (pc == null || pc.size() <= MAX_RESULTS) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                };
            tooManyLabel.setColor(Color.red);
            otherSearch.add(tooManyLabel);



            // SECOND Add screen: Search results
            final BoxPanel screen2 = new BoxPanel(BoxPanel.VERTICAL) {
                    public boolean isVisible(PageState ps) {
                        if (!haveQuery(ps)) {
                            return false;
                        }
                        PartyCollection pc = getQueryResults(ps);
                        long n = pc.size();
                        return (n > 0 && n <= MAX_RESULTS);
                    }
                };
            add(screen2);

            otherSearch.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent ev) {
                        PageState ps = ev.getPageState();
                        String queryString = (String)queryField.getValue(ps);
                        String[] limit = (String[])limitToParticipants.getValue(ps);
                        if (limit != null && limit.length > 0) {
                            setQuery(ps, queryString, Boolean.TRUE);
                        } else {
                            setQuery(ps, queryString, Boolean.FALSE);
                        }
                    }
                });

            screen1.add(otherSearch);

            screen1.add(new Label(""));

            ActionLink returnLink =
                new ActionLink( (String) GlobalizationUtil.globalize("cw.cw.permissions.return_to_current_permissions_view").localize());
            returnLink.setClassAttr("actionLink");
            returnLink.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        fireCompletionEvent(ev.getPageState());
                    }
                });
            screen1.add(returnLink);


            // Build Search results form

            RequestLocal pfErrorMessageRL = new RequestLocal();

            RadioGroup partyField = new RadioGroup("partyField");
            partyField.setLayout(RadioGroup.VERTICAL);
            partyField.setPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        PageState ps = ev.getPageState();
                        RadioGroup target = (RadioGroup)ev.getTarget();
                        PartyCollection parties = getQueryResults(ps);
                        while (parties.next()) {
                            target.addOption(new Option(parties.getID().toString(),
                                                        parties.getDisplayName()));
                        }
                    }
                });


            Form partyForm = new MyAddGrantForm("partyGrant",
                                                partyField,
                                                pfErrorMessageRL);

            screen2.add(partyForm);

            screen2.add(new ErrorMessageDisplay(pfErrorMessageRL));

            ActionLink newSearchLink = new ActionLink( (String) GlobalizationUtil.globalize("cw.cw.permissions.try_a_new_search").localize());
            newSearchLink.setClassAttr("actionLink");
            newSearchLink.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        clearQuery(ev.getPageState());
                    }
                });
            screen2.add(newSearchLink);

            ActionLink returnToMainLink =
                new ActionLink( (String) GlobalizationUtil.globalize("cw.cw.permissions.return_to_current_permissions_view").localize());
            returnToMainLink.setClassAttr("actionLink");
            returnToMainLink.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        NewGrantDisplay.this.fireCompletionEvent(ev.getPageState());
                    }
                });
            screen2.add(returnToMainLink);
        }


        public void fireCompletionEvent(PageState ps) {
            clearQuery(ps);
            super.fireCompletionEvent(ps);
        }

        public void register(Page p) {
            p.addComponentStateParam(this, m_queryParam);
            p.addComponentStateParam(this, m_limitParam);
            super.register(p);
        }

    }


    public void register(Page p) {
     // Assert.assertTrue((p instanceof CWPage),
        Assert.isTrue((p instanceof CWPage),
                          "ObjectPermissionEdit may only be used on " +
                          "instances of CWPage.");
        super.register(p);
    }


    private void initialize(RequestLocal objectRL,
                            RequestLocal directTypesRL,
                            RequestLocal indirectTypesRL) {

        final SimpleContainer c = (SimpleContainer)getContainer();

        final Completable newGrantDisplay =
            new NewGrantDisplay(objectRL, directTypesRL) {
                public void register(Page p) {
                    super.register(p);
                    p.setVisibleDefault(this, false);
                }
            };

        ActionListener onAddClick = new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    CWPage page = (CWPage)ps.getPage();
                    page.goModal(ps, newGrantDisplay);
                }
            };

        final Component mainDisplay =
            new MainDisplay(objectRL,
                            directTypesRL,
                            indirectTypesRL,
                            onAddClick);

        newGrantDisplay.addCompletionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    CWPage page = (CWPage)ps.getPage();
                    page.goUnmodal(ps);
                }
            });

        add(mainDisplay);
        add(newGrantDisplay);
    }



    public ObjectPermissionEdit(final RequestLocal objectRL) {
        super(new SimpleContainer());
        initialize(objectRL, new RequestLocal(), new RequestLocal());
        /*
          {
          public Object initialValue(PageState ps) {
          ACSObject object = (ACSObject)objectRL.get(ps);
          HashSet result = new HashSet(1);
          result.add(object.getObjectType());
          return result;
          }
          });
        */

    }

}
