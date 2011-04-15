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


import com.arsdigita.portalserver.*;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.*;
import com.arsdigita.bebop.form.*;
import com.arsdigita.kernel.*;
import com.arsdigita.kernel.permissions.*;

import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;

import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.ACSObjectCollectionListModel;
import com.arsdigita.toolbox.ui.PrivilegedComponentSelector;


import org.apache.log4j.Category;

/**
 *
 *
 * @author Justin Ross (<a href="mailto:jross@redhat.com">jross@redhat.com</a>)
 */
public class ParticipantSearchPane {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/ParticipantSearchPane.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static Category s_log = Category.getInstance
        (ParticipantSearchPane.class.getName());

    private ParticipantSearchPane() {
        // No construction.
    }

    public static Component create(final RequestLocal portalsite,
                                   final StringParameter actionParam,
                                   final ActionListener selectSearch) {
        return buildSearchPane(portalsite, actionParam, selectSearch);
    }

    private static
        Component buildSearchPane(final RequestLocal portalsiteRL,
                                  final StringParameter actionParam,
                                  final ActionListener selectSearch) {
        final ACSObjectSelectionModel selectionModel =
            new ACSObjectSelectionModel("selectedparticipant");


        final StringParameter searchString =
            new StringParameter("searchString");

        final List participantList = new List();
        participantList.setSelectionModel(selectionModel);

        final Label emptyView = new Label("");
        participantList.setEmptyView(emptyView);

        participantList.setModelBuilder(new AbstractListModelBuilder() {
                public ListModel makeModel(List l, PageState ps) {
                    String searchStringValue = (String) ps.getValue(searchString);
                    if (searchStringValue == null || searchStringValue.equals("")) {
                        return new ListModel() {
                                public boolean next() { return false; }
                                public Object getElement() { return null; }
                                public String getKey() { return null; }
                            };
                    } else {
                        PortalSite psite = (PortalSite)portalsiteRL.get(ps);
                        PartyCollection pc = psite.getParticipants();
                        pc.filter(searchStringValue);
                        long count = pc.size();
                        if (count < 100 && count > 0) {
                            return new ACSObjectCollectionListModel(pc);
                        } else {
                            if (count == 0) {
                                emptyView.setLabel( (String) GlobalizationUtil.globalize("cw.workspace.ui.no_matches_found").localize() , ps);
                            } else {
                                emptyView.setLabel(
                                   count + " matches found. Please enter more"
                                   + " specific search criteria.", ps);
                            }
                            return new ListModel() {
                                    public boolean next() { return false; }
                                    public Object getElement() { return null; }
                                    public String getKey() { return null; }
                                };
                        }
                    }
                }
            });

        final DynamicListWizard dlw = new DynamicListWizard
            ("Participants", participantList, selectionModel, "",
             new Label("")) {
                public void register(Page p) {
                    super.register(p);
                    p.addComponentStateParam(this, searchString);
                    p.setVisibleDefault(getListLabel(), false);
                    p.setVisibleDefault(getAddLink(), false);
                }
            };

        final Form searchForm = new Form("participantSearch",
                                         new ColumnPanel(1));

        Label searchLabel = new Label(GlobalizationUtil.globalize("cw.workspace.ui.enter_search_criteria"));
        searchLabel.setFontWeight(Label.BOLD);
        searchForm.add(searchLabel);

        final TextField searchStringEntry = new TextField("searchEntry");
        searchStringEntry.addValidationListener(
                                                new NotEmptyValidationListener("No search criteria entered"));
        searchForm.add(searchStringEntry);
        searchForm.add(new Submit("Search"));

        searchForm.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    String searchStringValue = (String)ps.getValue(searchString);
                    if (searchStringValue == null) {
                        return;
                    }
                    searchStringEntry.setValue(ps, searchStringValue);
                }
            });

        searchForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    ps.reset(participantList);
                    ps.setValue(searchString, null);
                }
            });

        searchForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev) {
                    PageState ps = ev.getPageState();
                    ps.setValue(searchString, searchStringEntry.getValue(ps));
                }
            });

        dlw.setHeader(searchForm);

        RequestLocal participantRL = new RequestLocal() {
                public Object initialValue(PageState ps) {
                    return selectionModel.getSelectedObject(ps);
                }
            };

        Component participantEdit = new ParticipantEdit
            (portalsiteRL,
             participantRL,
             new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     dlw.reset(e.getPageState());
                 }
             });

        Component participantDisplay =
            new ParticipantDisplay(portalsiteRL, participantRL);

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

        return dlw;
    }
}
