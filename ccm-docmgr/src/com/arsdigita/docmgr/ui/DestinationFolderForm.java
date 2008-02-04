/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormValidationException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.docmgr.ResourceImpl;
import com.arsdigita.docmgr.Util;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * Intermediate form of the "Move-to" and "Copy-to" process.
 * It shows the folder tree of the repositories expanded
 * with checkboxes next to it.
 *
 * @author <a href="mailto:ddao@arsdigita.com">David Dao</a>
 * @author <a href="mailto:stefan@arsdigita.com">Stefan Deusch</a>
 *
 */
class DestinationFolderForm extends Form
    implements FormInitListener, FormProcessListener, DMConstants {

    private Hidden m_resourceList;

    private ExpandedFolderTree m_radioGroup;

    private Submit m_copySubmit;
    private Submit m_moveSubmit;
    private BrowsePane m_parent;

    public DestinationFolderForm(BrowsePane parent) {
        super("Destination-Folder", new ColumnPanel(1));
        m_parent = parent;
        m_resourceList = new Hidden(new ArrayParameter("resourceList"));
        add(m_resourceList);

        m_radioGroup = new ExpandedFolderTree();

        add(m_radioGroup);

        m_copySubmit = new Submit("Copy");
        add(m_copySubmit);

        m_moveSubmit = new Submit("Move");
        add(m_moveSubmit);
        addInitListener(this);
        addProcessListener(this);
    }

    public void generateXML(PageState ps, Element elt) {
        doSubmit(ps);
        super.generateXML(ps, elt);
    }

    private void doSubmit(PageState ps) {
        Object[] list = (Object[]) m_resourceList.getValue(ps);
        ArrayList l = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            l.add(list[i]);
        }
        m_radioGroup.setSources(ps, l);
    }

    public void setResourceList(PageState state, Object[] list) {
        m_resourceList.setValue(state, list);
    }

    public void setCopy(PageState state) {
        state.setVisible(m_moveSubmit, false);
        state.setVisible(m_copySubmit, true);
    }

    public void setMove(PageState state) {
        state.setVisible(m_moveSubmit, true);
        state.setVisible(m_copySubmit, false);
    }

    public void init(FormSectionEvent e) {
        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(e.getPageState());
        }
    }

    public void process(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();

        boolean isCopy = true;
        boolean isError = false;

        ArrayList msgList = new ArrayList();
        if (m_moveSubmit.isSelected(state)) {
            isCopy = false;
        }

        try {
            String parent = (String) m_radioGroup.getValue(state);
            if (parent == null) {
                throw new FormValidationException(
                                                  "Please choose a destination."
                                                  );
            }
            OID parentOID =
                new OID(ResourceImpl.BASE_DATA_OBJECT_TYPE,
                        new BigDecimal(parent));
            ResourceImpl  parentResource =
                (Folder) DomainObjectFactory.newInstance(parentOID);

            String[] resourceIDs = (String[]) m_resourceList.getValue(state);
            for (int i = 0; i < resourceIDs.length; i++) {

                OID oid = new OID(ResourceImpl.BASE_DATA_OBJECT_TYPE,
                                  new BigDecimal(resourceIDs[i]));

                ResourceImpl resource = null;
                try {
                    resource =
                        (ResourceImpl) DomainObjectFactory.newInstance(oid);
                    if (resource.isFile()) {
                        resource = (File) DomainObjectFactory.newInstance(
                                                                          new OID(
                                                                                  File.BASE_DATA_OBJECT_TYPE,
                                                                                  new BigDecimal(resourceIDs[i])));
                    }
                    if (isCopy) {
                        resource.copyTo(parentResource);
                    } else {
                        resource.setParent(parentResource);
                        resource.save();
                    }
                } catch (PersistenceException exc) {
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getName());
                    }
                    // Can't delete file and folder.
                } catch (DataObjectNotFoundException exc) {
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getName());
                    }
                    // Move on to the next object.
                }
            }
        } catch (DataObjectNotFoundException exc) {
            isError = true;
            // Unable to find parent object.
        }

        if (isError) {
            String action = null;
            if (isCopy) {
                action = "copy";
            } else {
                action = "move";
            }

            m_parent.displayErrorMsgPanel(state, action, msgList);
        } else {
            m_parent.displayFolderContentPanel(state);
        }
    }


    /**
     * Create an expanded tree of all repositories and folder for given user.
     * Each folder has a checkbox to be selected as destination folder.
     * The parent folder is not selectable. This class should not be use
     * outside of document manager.
     */

    private class ExpandedFolderTree extends RadioGroup {

        private RequestLocal m_srcResources; // Exclusion list of folders.

        public ExpandedFolderTree() {
            super("resourceID");
            m_srcResources = new RequestLocal();
        }

        public void setSources(PageState state, ArrayList list) {
            m_srcResources.set(state, list);
        }

        public void generateXML(PageState state, Element parent) {
            Element treeElement = parent.newChildElement("bebop:tree", BEBOP_XML_NS);

            BigDecimal sourceFolderID = m_parent.getFolderID(state);

            HashMap map = new HashMap();
            map.put(new BigDecimal("-1"), treeElement);

            Session session = SessionManager.getSession();
            DataQuery query =
                session
                .retrieveQuery("com.arsdigita.docs.listDestinationFolders");

            Repository repository = (Repository) Web.getContext().getApplication();
            BigDecimal rootID = repository.getRoot().getID();
            String path = repository.getRoot().getPath();

            query.setParameter("rootID", rootID);
            query.setParameter("rootPath", path);
            //query.setParameter("srcResources",
            //                   (ArrayList) m_srcResources.get(state));

            // it may be possible to do this with one query but for right
            // now this works since this is not a page that is loaded very
            // often
            ArrayList resources = (ArrayList) m_srcResources.get(state);
            if (resources != null) {
                DataCollection collection =
                    session.retrieve(ResourceImpl.BASE_DATA_OBJECT_TYPE);
                Iterator iterator = resources.iterator();
                while (iterator.hasNext()) {
                    collection.addEqualsFilter(ResourceImpl.ID,
                                               iterator.next());
                }
                if (resources.size() > 0) {
                    // we add each path as part of the filter
                    int item = 0;
                    while (collection.next()) {
                        Filter filter = query.addFilter
                            (" not " + ResourceImpl.PATH + " like " +
                             ":item" + item);
                        filter.set("item" + item,
                                   collection.get(ResourceImpl.PATH));
                    }
                }
            }

            query.addOrder(ResourceImpl.PATH);
            query.addOrder(ResourceImpl.NAME);

            while (query.next()) {
                BigDecimal parentID = (BigDecimal) query.get("parentID");
                BigDecimal resourceID = (BigDecimal) query.get("resourceID");

                String name = null;

                if (resourceID.equals(rootID)) {
                    name = "Documents";
                } else {
                    name = (String) query.get("name");
                }
                Element p = (Element) map.get(parentID);

                boolean isSelectable = !resourceID.equals(sourceFolderID);

                if (p != null) {
                    map.put(resourceID, createNode(state, p, isSelectable,
                                                   resourceID, name));
                }
            }

        }

        private Element createNode(PageState state,
                                   Element parent,
                                   boolean makeSelectable,
                                   BigDecimal id,
                                   String name) {

            Element element = parent.newChildElement("bebop:t_node", BEBOP_XML_NS);

            element.addAttribute("indentStart", "t");
            element.addAttribute("indentClose", "t");
            if(makeSelectable) {
                element.addAttribute("resourceID", id.toString());
                element.addAttribute("radioGroup", "t");
                element.addAttribute("radioGroupName", getName());
            } else {
                element.addAttribute("radioGroup", "f");
            }

            Label label = new Label(name);
            label.generateXML(state, element);

            return element;
        }
    }

}
