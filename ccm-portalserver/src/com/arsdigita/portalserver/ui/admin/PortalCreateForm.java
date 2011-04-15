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

import java.math.BigDecimal;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portalserver.ApplicationDirectoryPortlet;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalNavigatorPortlet;
import com.arsdigita.portalserver.PortalSummaryPortlet;
import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.web.Application;
import com.arsdigita.bebop.form.TextArea;
import org.apache.log4j.Logger;

/**
 * <p>Form for initial Portal construction.</p>
 **/
public class PortalCreateForm {
    public static String versionId = "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/PortalCreateForm.java#6 $ $Author: dennis $ $DateTime: 2004/08/17 23:19:25 $";

    private static Logger s_log = Logger.getLogger(PortalCreateForm.class);

    /**
     * @param parentPortal an input request local holding the parent of the
     * portal to be created
     * @param portalsiteRL the newly created portal will be set in the
     * portalsiteRL RequestLocal
     * @param onFinish will be run when creation of the portal is complete;
     * the source of the action event will be the returned Component
     * @return a component for portalsite creation given a parent portal */
    public static Component create(final RequestLocal parentPortalSite,
                                   final RequestLocal portalsiteRL,
                                   final ActionListener onFinish) {

        final Form portalCreation = new Form("portalcreation");
        portalCreation.setMethod(Form.POST);

        NotEmptyValidationListener notWhiteSpace =
            new NotEmptyValidationListener();

        UniquePortalURLValidationListener uniqueURL =
            new UniquePortalURLValidationListener();

        portalCreation.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.parent_workspace")));
        portalCreation.add(new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState ps = e.getPageState();
                    Label target = (Label) e.getTarget();
                    PortalSite psite = (PortalSite) parentPortalSite.get(ps);
                    if (psite == null) {
                        target.setLabel( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.none").localize());
                        target.setFontWeight(Label.ITALIC);
                    } else {
                        target.setLabel(psite.getDisplayName());
                    }
                }
            }));

        final TextField psName = new TextField(new StringParameter("psName"));
        psName.getParameterModel().addParameterListener(notWhiteSpace);
        portalCreation.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.title")));
        portalCreation.add(psName);

        final TextField psLocation =
            new TextField(new StringParameter("psNode"));
        psLocation.getParameterModel().addParameterListener(notWhiteSpace);
        psLocation.getParameterModel().addParameterListener(uniqueURL);
        psLocation.getParameterModel().addParameterListener(
          new ParameterListener() {
              public void validate(ParameterEvent e) {
                   ParameterData data = e.getParameterData();
                   String value = (String) data.getValue();
                   char[] chars = value.toCharArray();
                   for (int i = 0; i < chars.length; i++) {
                      if (!(Character.isLetterOrDigit(chars[i])
                         || chars[i] == '-' || chars[i] == '_')) {
                         data.addError( "Can only contain letters, numbers, dashes, and underscores");
                         break;
                      }
                   }
              }});
        portalCreation.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.url_fragment")));
        portalCreation.add(psLocation);

        final TextArea psMission =
            new TextArea(new StringParameter("psMission"));
        portalCreation.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.mission")));
        portalCreation.add(psMission);
        portalCreation.add(new Submit("done", "Create Portal"));

        portalCreation.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent ev)
                    throws FormProcessException {
                    PageState ps = ev.getPageState();

                    PortalSite parent = (PortalSite) parentPortalSite.get(ps);

                    String name = (String)psName.getValue(ps);
                    String location = (String)psLocation.getValue(ps);
                    String mission = (String)psMission.getValue(ps);

                    PortalSite psite = PortalSite.createPortalSite
                                              (location, name, parent);
                    psite.setMission(mission);
                    psite.save();

                    // By default, add some workspace portlets.
                    PortalTab mainTab = PortalTab.createTab("Main",psite);

                    Portlet portlet = Portlet.createPortlet
                     (ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE, psite);
                    mainTab.addPortlet(portlet, 1);

                    portlet = Portlet.createPortlet
                     (PortalNavigatorPortlet.BASE_DATA_OBJECT_TYPE, psite);
                    mainTab.addPortlet(portlet, 1);

                    portlet = Portlet.createPortlet
                        (PortalSummaryPortlet.BASE_DATA_OBJECT_TYPE, psite);
                    mainTab.addPortlet(portlet, 1);
/*
                    Application DocRepositoryApp = Application.createApplication
                        (Repository.BASE_DATA_OBJECT_TYPE, "documents",
                         "My Document Manager", ws);
                    DocRepositoryApp.save();

                    portlet = Portlet.createPortlet
                        (RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE,
                         DocRepositoryApp);
                    mainTab.addPortlet(portlet, 2);
*/
                    mainTab.setPortalSite(psite);
                    mainTab.save();
                    psite.addPortalTab(mainTab);
                    psite.save();

                    portalsiteRL.set(ps, psite);

                    Party party = Kernel.getContext().getParty();

                    if (party == null) {
                        throw new IllegalStateException("not logged in");
                    }
                    psite.addMember(party);
                    psite.save();
                    PermissionDescriptor perm = new PermissionDescriptor
                        (PrivilegeDescriptor.ADMIN, psite, party);
                    PermissionService.grantPermission(perm);

                    onFinish.actionPerformed
                        (new ActionEvent(portalCreation, ps));
                }
            });

        return portalCreation;
    }

    /**
     * @param portalsiteRL the newly created portalsite will be set in the
     * portalsiteRL RequestLocal
     * @param onFinish will be run when creation of the portal is complete;
     * the source of the action event will be the returned Component
     * @return a component for portal creation
     **/
    public static Component create(final RequestLocal portalsiteRL,
                                   final ActionListener onFinish) {

        final SimpleContainer retval = new SimpleContainer();

        final Tree psParent = new Tree(new PortalTreeModelBuilder());
        psParent.setSelectionModel( new ParameterSingleSelectionModel(
                                      new BigDecimalParameter("psParent")));

        final Form parentSelection = new Form("parentselection",
                                              new BoxPanel(BoxPanel.VERTICAL));
        parentSelection.setMethod(Form.POST);

        RequestLocal parentPortal = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    BigDecimal id = (BigDecimal) psParent.getSelectedKey(ps);
                    if (id == null) { return null; }
                    return PortalSite.retrievePortalSite(id);
                }
            };

        final Component portalCreation = PortalCreateForm.create(
                      parentPortal, portalsiteRL, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                          PageState ps = e.getPageState();
                          onFinish.actionPerformed(new ActionEvent(retval, ps));
                        }
                     });

        retval.add(parentSelection);
        retval.add(portalCreation);

        parentSelection.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    TreeModel tm = psParent.getTreeModel(ps);
                    portalCreation.setVisible(ps, false);
                    String key = (String) tm.getRoot(ps).getKey();
                    if (psParent.isCollapsed(key, ps)) {
                        psParent.expand((String) tm.getRoot(ps).getKey(), ps);
                    }
                }
            });

        parentSelection.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    portalCreation.setVisible(ps, true);
                    parentSelection.setVisible(ps, false);
                }
            });

        parentSelection.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.select_parent_workspace")));

        psParent.setCellRenderer(
                              new PortalTreeModelBuilder.DefaultRenderer());

        parentSelection.add(psParent);

        parentSelection.add(new Submit("Continue"));

        return retval;
    }
}
