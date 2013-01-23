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

import java.util.Iterator;

import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalSiteCollection;

import com.arsdigita.util.LockableImpl;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeNode;

import com.arsdigita.domain.DomainCollectionIterator;
import org.apache.log4j.Logger;

/**
 * <p>TreeModelBuilder for Portal Site hierarchies.
 * This Class will work in two different modes by setting
 * the internal boolean class var. This can be done during
 * construction or via a setter method. A true value
 * filters PortalSites to include only those that
 * are online. This mode is helpful for typical user UI.
 * If the value is set to false, then all Portal Sites,
 * including those that are archived and in Draft status, are
 * included in the tree model. This mode is useful for admin UI.
 *  </p>
 **/
public class PortalTreeModelBuilder
    extends LockableImpl
    implements TreeModelBuilder {

    private static final Logger s_log = Logger.getLogger(PortalTreeModelBuilder.class);


    private BigDecimal m_rootId;
    private boolean m_filterPSites;


    /**
     * <p>Construct a new PortalTreeModelBuilder whose root is the
     * toplevel portal.</p>
     **/
    public PortalTreeModelBuilder() {
        this(null, true);
    }

    public PortalTreeModelBuilder(boolean filterPSites) {
        this(null, filterPSites);
    }

    /**
     * <p>Construct a new PortalTreeModelBuilder whose root is the
     * portal with the specified ID.</p>
     **/
    public PortalTreeModelBuilder(BigDecimal rootprtlId) {
        m_rootId = rootprtlId;
        m_filterPSites = true;
    }

    public PortalTreeModelBuilder(BigDecimal rootprtlId, boolean filterPSites) {
        m_rootId = rootprtlId;
        m_filterPSites = filterPSites;
    }


    /**
     * <p>Get the ID of the portal at which the tree built by this
     * ModelBuilder will be rooted.</p>
     **/
    public BigDecimal getRootID() {
        return m_rootId;
    }

    /**
     * <p>Get the filter mode value for 
     * this ModelBuilder.</p>
     **/
    public boolean getFilterPSites() {
        return m_filterPSites;
    }

    /**
     * <p>Set the filter mode value for 
     * this ModelBuilder. Setting this value
     * to false results in the TreeModel
     * including ALL PortalSites in the system: Archived
     * and Draft versions as well as online Portal Sites.
     * A true value for this parameter filters PortalSite
     * collections to include only online Portal Sites.</p>
     **/
    public void setFilterPSites(boolean filterPSites) {
        m_filterPSites = filterPSites;
    }

    public TreeModel makeModel(Tree t, PageState s) {

        return new TreeModel() {

          public Iterator getChildren(TreeNode n, PageState ps) {
            PortalSite psite = (PortalSite)n.getElement();
              final PortalSiteCollection psc;

                if (psite == null) {
                    if(m_filterPSites)
                      psc = PortalSite.getRootPortalSites();
                    else
                      psc = PortalSite.getAllRootPortalSites();
                } else {
                    if(m_filterPSites)
                      psc = psite.getChildPortalSites();
                    else
                      psc = psite.getAllChildPortalSites();
                }

                final DomainCollectionIterator dci =
                    new DomainCollectionIterator(psc);

                return new Iterator() {
                        public boolean hasNext() {
                            return dci.hasNext();
                        }

                        public Object next() {
                            final PortalSite psite = (PortalSite) dci.next();
                            return new TreeNode() {
                                    public Object getElement() {
                                        return psite;
                                    }

                                    public Object getKey() {
                                        return psite.getID().toString();
                                    }
                                };
                        }

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
            }

            public TreeNode getRoot(PageState ps) {
                if (getRootID() == null) {
                    return new TreeNode() {
                            public Object getElement() {
                                return null;
                            }
                            public Object getKey() {
                                return "root";
                            }
                        };
                } else {
                    final PortalSite psite =
                        PortalSite.retrievePortalSite(getRootID());
                    return new TreeNode() {
                            public Object getElement() {
                                return psite;
                            }
                            public Object getKey() {
                                return psite.getID();
                            }
                        };
                }
            }

            public boolean hasChildren(TreeNode n, PageState ps) {
                PortalSiteCollection psc;
                PortalSite psite = (PortalSite)n.getElement();
                if (psite == null) {
                    if(m_filterPSites)
                      psc = PortalSite.getRootPortalSites();
                    else
                      psc = PortalSite.getAllRootPortalSites();
                } else {
                    if(m_filterPSites)
                      psc = psite.getChildPortalSites();
                    else
                      psc = psite.getAllChildPortalSites();
                }
                boolean isEmpty = psc.isEmpty();
                psc.close();
                return !isEmpty;
            }
        };
}

    public static class DefaultRenderer implements TreeCellRenderer {
        public Component getComponent(Tree tree, PageState ps,
                                      Object value, boolean isSelected,
                                      boolean isExpanded, boolean isLeaf,
                                      Object key) {
            PortalSite psite = (PortalSite)value;
            if (psite == null) { return new Label(""); }
            String title = psite.getTitle();

            SimpleContainer result = new SimpleContainer();
            Label l = new Label(title);
            if (isSelected) {
                l.setFontWeight(Label.BOLD);
                result.add(l);
            } else {
                result.add(new ControlLink(l));
            }

//          String psURL = psite.getSiteNode().getURL();
            // TODO: Does replacement work??
            //       Compare with version 2.0.x release
            String psURL = psite.getPath();
            result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.")));
            Link viewLink = new Link( new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.view")),  psURL);
            viewLink.setTargetFrame(Link.NEW_FRAME);
            result.add(viewLink);
            result.add(new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.")));

            return result;
        }
    }
}
