/* 
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications.tree;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.ui.admin.ApplicationsAdministrationTab;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;
import java.util.Iterator;

/**
 * A {@link TreeModel} for the tree of applications in {@link ApplicationsAdministrationTab}. The tree consists of two
 * different types of nodes: Nodes for {@link ApplicationTypes} and nodes for {@link Application} instances.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ApplicationTreeModel implements TreeModel {
        
    public ApplicationTreeModel() {        
        //Nothing        
    }
    
    public TreeNode getRoot(final PageState state) {        
        return new RootTreeNode();
    }

    public boolean hasChildren(final TreeNode node, final PageState state) {
        if (node instanceof RootTreeNode) {
            return true;
        } else if (node instanceof ApplicationTypeTreeNode) {
            final ApplicationTypeTreeNode typeTreeNode = (ApplicationTypeTreeNode) node;
            
            if (typeTreeNode.getApplicationType().isSingleton()) {
                return false;
            } else {
                return !retrieveApplicationInstances(typeTreeNode.getApplicationType()).isEmpty();
            }            
        } else if (node instanceof ApplicationInstanceTreeNode) {
            return false;
        } else {
            throw new IllegalArgumentException(
                    "The ApplicationTreeModel can only work with ApplicationTypeTreeNodes and"
                    + "ApplicationInstanceTreeNodes.");
        }
    }

    public Iterator getChildren(final TreeNode node, final PageState state) {
        if (node instanceof RootTreeNode) {
            final ApplicationTypeCollection appTypes = ApplicationType.retrieveAllApplicationTypes();                        
            appTypes.addOrder("title");                    
                                    
            return new AppTypesIterator(appTypes);            
        } else if (node instanceof ApplicationTypeTreeNode) {
            final ApplicationTypeTreeNode typeTreeNode = (ApplicationTypeTreeNode) node;
            final ApplicationType appType = typeTreeNode.getApplicationType();
            
            final ApplicationCollection applications = Application.retrieveAllApplications(
                    appType.getApplicationObjectType());
            applications.addOrder("title");
            
            return new AppIterator(applications);            
        } else if (node instanceof ApplicationInstanceTreeNode) {
            return null;
        } else {
            throw new IllegalArgumentException(
                    "The ApplicationTreeModel can only work with ApplicationTypeTreeNodes and"
                    + "ApplicationInstanceTreeNodes.");
        }
    }

    private ApplicationCollection retrieveApplicationInstances(final ApplicationType applicationType) {
        final ApplicationCollection applications = Application.retrieveAllApplications();
        applications.addEqualsFilter("objectType", applicationType.getApplicationObjectType());
        
        return applications;
    }
    
    private class RootTreeNode implements TreeNode {

        public RootTreeNode() {
            //Nothing
        }
        
        public Object getKey() {
            return "-1";
        }

        public Object getElement() {
            return "/";
        }

    }
    
    private class AppTypesIterator implements Iterator<ApplicationTypeTreeNode> {

        private final ApplicationTypeCollection appTypes;
        
        public AppTypesIterator(final ApplicationTypeCollection appTypes) {
            this.appTypes = appTypes;
        }
        
        public boolean hasNext() {
            return appTypes.next();
        }

        public ApplicationTypeTreeNode next() {
            return new ApplicationTypeTreeNode(appTypes.getApplicationType());
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }        
    }
    
    private class AppIterator implements Iterator<ApplicationInstanceTreeNode> {
        
        private final ApplicationCollection applications;
        
        public AppIterator(final ApplicationCollection applications) {
            this.applications = applications;
        }
        
        public boolean hasNext() {
            return applications.next();
        }
        
        public ApplicationInstanceTreeNode next() {
            return new ApplicationInstanceTreeNode(applications.getApplication());
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }        
    }
}
