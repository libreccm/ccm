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

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.cms.docmgr.Resource;
import com.arsdigita.cms.docmgr.ResourceExistsException;
import com.arsdigita.cms.docmgr.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

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

    public static final String TYPE_REPOSITORY = "repository";
    public static final String TYPE_FOLDER = "folder";
    
    public static final String VALUE_YES = "yes";
    public static final String VALUE_NO = "no";
    
    private final static Logger s_log =
        Logger.getLogger(DestinationFolderForm.class);

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

    public void generateXML(PageState ps, Element parent) {
        doSubmit(ps);

        if ( isVisible(ps) ) {
            Element form = generateXMLSansState(ps, parent);

            ps.setControlEvent(this);
            ps.generateXML(form, getModel().getParametersToExclude());
            ps.clearControlEvent();
            
            form.addAttribute("isCopy", !m_moveSubmit.isVisible(ps) ? VALUE_YES : VALUE_NO);
            form.addAttribute("isMove", m_moveSubmit.isVisible(ps) ? VALUE_YES : VALUE_NO);
            
            try {
                Object[] resourceIDs = (Object[]) m_resourceList.getValue(ps);
                if (resourceIDs != null) {
                    if (resourceIDs.length == 1) {
                        addResourceAttribute(form, "resourceTitle", (String) resourceIDs[0]);
                    }
                    else {
                        for (int i = 0; i < resourceIDs.length; i++) {
                            addResourceAttribute(form, "resourceTitle" + i, (String) resourceIDs[i]);
                        }
                    }
                }
            }
            catch (Throwable e) {
            	s_log.error("", e);
            }
        }
//        super.generateXML(ps, parent);
    }
    
    private void addResourceAttribute(Element form, String attrName, String resourceIDtxt) throws Exception {
    	BigDecimal id = new BigDecimal(resourceIDtxt);
    	ContentItem ci = new ContentItem(id);
        OID oid = ci.getSpecificOID();
        Resource resource = (Resource) DomainObjectFactory.newInstance(oid);
    	form.addAttribute(attrName, resource.getTitle());
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
            throw new LoginSignal(e.getPageState().getRequest());
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
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "ui.folder.choose_destination"));
            }
            OID parentOID =
                new OID(DocFolder.BASE_DATA_OBJECT_TYPE,
                        new BigDecimal(parent));
            DocFolder  parentResource =
                (DocFolder) DomainObjectFactory.newInstance(parentOID);

            String[] resourceIDs = (String[]) m_resourceList.getValue(state);
            for (int i = 0; i < resourceIDs.length; i++) {

                BigDecimal id = new BigDecimal(resourceIDs[i]);
                ContentItem ci = new ContentItem
                    (new OID(ContentItem.BASE_DATA_OBJECT_TYPE,id));
                OID oid = ci.getSpecificOID();
                Resource resource = null;
                try {
                    resource =
                        (Resource) DomainObjectFactory.newInstance(oid);
                    if (isCopy) {
                        resource.copyTo(parentResource);
                    } else {
                        resource.setParentResource(parentResource);
                    }
                } catch (PersistenceException exc) {
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getTitle());
                    }
                    // Can't delete file and folder.
                } catch (DataObjectNotFoundException exc) {
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getTitle());
                    }
                    // Move on to the next object.
                } catch (ResourceExistsException reex) {
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getTitle()+
                                    " already exists in destination folder");
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
            
            HashMap map = new HashMap();
            //map.put(new BigDecimal("-1"), treeElement);
            map.put("-1", treeElement);

            BigDecimal sourceFolderID = m_parent.getFolderID(state);

            Application parentApplication = (Repository) Web.getWebContext().getApplication();
            do
            {
                s_log.debug("app class: "+parentApplication.getClass().getName());
                parentApplication = parentApplication.getParentApplication();
            }
            while ((parentApplication != null)
                   && !(parentApplication.getClass().getName().equals("com.arsdigita.portalserver.PortalSite"))
                   && !(parentApplication.getClass().getName().equals("com.arsdigita.london.portal.Workspace")));
            // not very clean, but to avoid dependencies on ccm-ldn-portal or ccm-portalserver
            // && !(parentApplication instanceof PortalSite)
            
            Repository repository;
            BigDecimal rootID;
            Session ssn = SessionManager.getSession();
            DataCollection docFolders;
            DomainCollection dc;
            ApplicationCollection repositories = parentApplication.getChildApplicationsForType(Repository.BASE_DATA_OBJECT_TYPE);
            repositories.addOrder("title");
            while (repositories.next()) {
            	repository = (Repository) repositories.getDomainObject();
            	rootID = repository.getRoot().getID();
            	map.put(rootID, createNode(state, treeElement, !rootID.equals(sourceFolderID), rootID, repository.getTitle(), TYPE_REPOSITORY));
            	
            	docFolders = ssn.retrieve(DocFolder.TYPE);
            	dc = new DomainCollection(docFolders);
                dc.addFilter
                    (dc.getFilterFactory()
                     .contains("ancestors",
                                 "/"+rootID.toString()+"/",
                                 false)
                     );
                dc.addOrder("ancestors");
                
                while (dc.next()) {
                    DocFolder folder = (DocFolder) dc.getDomainObject();
                    BigDecimal parentID = folder.getParent().getID();
                    BigDecimal resourceID = folder.getID();

                    s_log.debug("resource id is "+resourceID.toString());
                    s_log.debug("  parent id is "+parentID.toString());

                    String name = null;

                    if (resourceID.equals(rootID)) {
                        name = "Documents";
                    } else {
                        name = folder.getTitle();
                    }
                    Element p = (Element) map.get(parentID);

                    boolean isSelectable = !resourceID.equals(sourceFolderID);

                    if (p != null) {
                        map.put(resourceID, createNode(state, p, isSelectable,
                                                       resourceID, name, TYPE_FOLDER));
                    }
                }
                dc.close();
                docFolders.close();
            }
            
            repositories.close();

        }

        private Element createNode(PageState state,
                                   Element parent,
                                   boolean makeSelectable,
                                   BigDecimal id,
                                   String name,
								   String type) {

            Element element = parent.newChildElement("bebop:t_node", BEBOP_XML_NS);

            element.addAttribute("indentStart", "t");
            element.addAttribute("indentClose", "t");
            element.addAttribute("type", type);
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
