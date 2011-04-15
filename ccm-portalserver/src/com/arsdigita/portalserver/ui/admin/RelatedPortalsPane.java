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
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DynamicListWizard;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalSiteCollection;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;
import com.arsdigita.globalization.GlobalizedMessage;


public class RelatedPortalsPane {

    private static final Logger s_log = Logger.getLogger(RelatedPortalsPane.class);

    private RelatedPortalsPane() { } // no construction allowed

    public static Component create(final RequestLocal portalsiteRL) {
        // related portals
        final ACSObjectSelectionModel relatedSelection =
            new ACSObjectSelectionModel("relatedws");

        List rList = new List(new ListModelBuilder() {
          public void lock() { }
          public boolean isLocked() { return true; }
          public ListModel makeModel(List l, PageState ps) {
            return new RelatedPortalsModel((PortalSite)portalsiteRL.get(ps));
          }
        });
        rList.setSelectionModel(relatedSelection);
        Label noRelated = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_linked_workspaces"));
        noRelated.setFontWeight(Label.ITALIC);
        rList.setEmptyView(noRelated);

        final DynamicListWizard related = 
          new DynamicListWizard("Linked Portals", rList, relatedSelection,
                      "Link to a portal", new Label(""));

        related.setAddPane(relatedAddPane(portalsiteRL, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    related.reset(ps);
                }
            }));

        related.setEditPane(relatedEditPane(portalsiteRL, new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return relatedSelection.getSelectedObject(ps);
                }
            }, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        PageState ps = e.getPageState();
                        related.reset(ps);
                    }
                }));


        // childportals 
        final ACSObjectSelectionModel childSelection =
            new ACSObjectSelectionModel("childws");

        List cList = new List(new ListModelBuilder() {
          public void lock() { }
          public boolean isLocked() { return true; }
          public ListModel makeModel(List l, PageState ps) {
            return new ChildPortalsModel( (PortalSite) portalsiteRL.get(ps));
                }
            });
        cList.setSelectionModel(childSelection);
        Label noChildren = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.no_child_workspaces"));
        noChildren.setFontWeight(Label.ITALIC);
        cList.setEmptyView(noChildren);

        final DynamicListWizard child = 
          new DynamicListWizard("Child Portals", cList, childSelection,
                               "Add a child portal", new Label(""));

        final RequestLocal newChildPortal = new RequestLocal();
        child.setAddPane( ChildPortalCreateForm.create(portalsiteRL, 
             newChildPortal, new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                 PageState ps = e.getPageState();
                 childSelection.setSelectedKey(ps,((PortalSite) newChildPortal.get(ps)).getID() .toString());
                 }
        }));

        child.setEditPane(childEditPane(portalsiteRL, new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    return childSelection.getSelectedObject(ps);
                }
            }));

        Container retval = new SimpleContainer();
        retval.add(related);
        retval.add(child);

        return retval;
    }

    private static Component childEditPane(final RequestLocal portalsiteRL,
                                           final RequestLocal childPortal) {
        final Container retval = new BoxPanel(BoxPanel.VERTICAL);

        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.child_workspace_info"));
        header.setFontWeight(Label.BOLD);
        retval.add(header);

        retval.add(new PropertySheet(new PropertySheetModelBuilder() {
                public void lock() { }
                public boolean isLocked() { return true; }
                public PropertySheetModel makeModel(PropertySheet sheet,
                                                    final PageState ps) {
                    return new PortalModel((PortalSite) childPortal.get(ps));
                }
            }));

        BoxPanel bp = new BoxPanel(BoxPanel.HORIZONTAL);
        bp.add(new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.go_to_this_workspace")),  new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState ps = e.getPageState();
                    Link target = (Link) e.getTarget();
                    PortalSite cp = (PortalSite) childPortal.get(ps);
                    target.setTarget(cp.getPrimaryURL());
                }
            }));
        bp.add(new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.administer_this_workspace")),  new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState ps = e.getPageState();
                    Link target = (Link) e.getTarget();
                    PortalSite cp = (PortalSite) childPortal.get(ps);
                    target.setTarget(cp.getPrimaryURL() + "admin");
                }
            }));

        retval.add(bp);

        return retval;
    }

    private static Component relatedAddPane(final RequestLocal portalsiteRL,
                                            final ActionListener onAdd) {
        final Form confirmation = new Form("confirm", new SimpleContainer());
        confirmation.setMethod(Form.POST);
        final Container retval = new BoxPanel(BoxPanel.VERTICAL) {
                public void register(Page p) {
                    p.setVisibleDefault(confirmation, false);
                }
            };

        final Tree psTree = new Tree(new PortalTreeModelBuilder()) {
                public void generateXML(PageState ps, Element parent) {
                    TreeModel tm = getTreeModel(ps);
                    String key = (String) tm.getRoot(ps).getKey();
                    expand((String) tm.getRoot(ps).getKey(), ps);
                    super.generateXML(ps, parent);
                }
            };
        psTree.setCellRenderer( new PortalTreeModelBuilder.DefaultRenderer());
        final ACSObjectSelectionModel treeSelection =
            new ACSObjectSelectionModel("wstreeselect");
        psTree.setSelectionModel(treeSelection);

        retval.add(psTree);

        treeSelection.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    PageState ps = e.getPageState();
                    confirmation.setVisible(ps, true);
                }
            });

        confirmation.add(new Submit("Link selected portal"));
        confirmation.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite related =
                        (PortalSite) treeSelection.getSelectedObject(ps);

                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    PortalSiteCollection current = psite.getRelatedPortalSites();
                    current.filterToPortalSite(related.getID());
                    if (current.size() == 0) {
                        psite.addRelatedPortalSite(related);
                        psite.save();
                    }

                    ps.reset(retval);
                    onAdd.actionPerformed(new ActionEvent(retval, ps));
                }
            });

        retval.add(confirmation);

        return retval;
    }

    private static Component relatedEditPane( final RequestLocal portalsiteRL, 
          final RequestLocal relatedPortal, final ActionListener onRemove) {

        final Container retval = new BoxPanel(BoxPanel.VERTICAL);

        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.linked_workspace_info"));
        header.setFontWeight(Label.BOLD);
        retval.add(header);

        retval.add(new PropertySheet(new PropertySheetModelBuilder() {
                public void lock() { }
                public boolean isLocked() { return true; }
                public PropertySheetModel makeModel(PropertySheet sheet,
                                                    final PageState ps) {
                    return new PortalModel(
                       (PortalSite) relatedPortal.get(ps));
                }
            }));

        final ActionLink unLinkPortalPrompt = new ActionLink(
                 "Remove link to portal?");
        unLinkPortalPrompt.setConfirmation("Really remove link to portal?");
        unLinkPortalPrompt.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState ps = e.getPageState();
                    PortalSite psite = (PortalSite) portalsiteRL.get(ps);
                    psite.removeRelatedPortalSite((PortalSite) relatedPortal.get(ps));
                    psite.save();
                    onRemove.actionPerformed(new ActionEvent(retval, ps));
                }
            });
        retval.add(unLinkPortalPrompt);

        return retval;
    }

    static class PortalModel implements PropertySheetModel {
        private PortalSite  m_p;
        private int m_counter = -1;

        PortalModel(PortalSite p) { m_p = p; }

        public boolean nextRow() {
            if (m_counter == 1) { return false; }
            else { m_counter++; return true; }
        }

        /**
         *  @deprecated use getGlobalizedLabel instead
         */
        public String getLabel() {
            return getGlobalizedLabel().getKey();
        }

        public GlobalizedMessage getGlobalizedLabel() {
            if (m_counter == 0) {
                return GlobalizationUtil.globalize("cw.workspace.ui.admin.title");
            } else if (m_counter == 1) {
                return GlobalizationUtil.globalize("cw.workspace.ui.admin.mission");
            } else {
                throw new IllegalStateException("invalid counter");
            }
        }

        public String getValue() {
            if (m_counter == 0) {
                return m_p.getTitle();
            } else if (m_counter == 1) {
                return m_p.getMission();
            } else if (m_counter == 2) {
                PortalSite parent = PortalSite.getPortalSiteForApplication(m_p);
                if (parent == null) { return "None"; }
                return parent.getTitle();
            } else {
                throw new IllegalStateException("invalid counter");
            }
        }
    }

    private static class ChildPortalsModel implements ListModel {
        private PortalSiteCollection m_psc;
        public ChildPortalsModel(PortalSite p) {
            m_psc = p.getChildPortalSites();
        }
        public boolean next() { return m_psc.next(); }
        public String getKey() { return m_psc.getID().toString(); }
        public Object getElement() { return m_psc.getDisplayName(); }
    }

    private static class RelatedPortalsModel implements ListModel {
        private PortalSiteCollection m_psc;
        public RelatedPortalsModel(PortalSite p) {
            m_psc = p.getRelatedPortalSites();
        }
        public boolean next() { return m_psc.next(); }
        public String getKey() { return m_psc.getID().toString(); }
        public Object getElement() { return m_psc.getDisplayName(); }
    }
}
