/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui;

import com.arsdigita.london.portal.StatefulPersistentPortal;
import com.arsdigita.london.portal.StatefulPortlet;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;

import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.jsp.DefinePage;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;

import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.portal.PortletTypeCollection;

import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ResourceConfigComponent;

import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.xml.Element;

import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;

import java.math.BigDecimal;

import com.arsdigita.formbuilder.util.FormBuilderUtil;
import java.util.HashMap;
import java.util.Iterator;

import java.io.IOException;

import org.apache.log4j.Logger;

// XXX this class is disgusting
/**
 * PersistentPortals are able to have more than one column, and the constructor
 * for PersitentPortal takes in an integer argument for number of columns.
 * 
 * HomepagePortals defined on the jsp page each construct instances of this
 * class, one for the portal in view mode, one in edit mode. 
 * 
 */
public class PersistentPortal extends SimpleContainer {

	public static final String ACTION_CUSTOMIZE = "customize";
	public static final String ACTION_MOVE_UP = "moveUp";
	public static final String ACTION_MOVE_DOWN = "moveDown";
	public static final String ACTION_MOVE_LEFT = "moveLeft";
	public static final String ACTION_MOVE_RIGHT = "moveRight";


	public static final String ACTION_DELETE = "delete";

    private static final Logger s_log = Logger.getLogger(PersistentPortal.class);

	private WorkspaceSelectionModel m_workspace;
	private PortalSelectionModel m_portal;
	private PortletTypeSelectionModel m_portletType;
	private PortletSelectionModel m_portlet;

	private String m_mode;
	private int m_columns;

	private PortletTypeForm m_adders[];
	private HashMap m_create;
	private HashMap m_modify;
	private HashMap m_createApp;

	private RequestLocal m_parentResource;
	private RequestLocal m_currentResource;
	private RequestLocal m_currentApp;
	private PortalModelBuilder m_portalModelBuilder;
	private DomainObjectParameter m_parentApp;

	private SingleSelectionModel m_column;

	// Ought to be enough until browser window size is a few thousand
	// pixels wide...
	public static final int MAX_COLUMNS = 10;

    public PersistentPortal(PortalSelectionModel portal,
                            String mode) {
		this(portal, "portal", mode);
	}

    public PersistentPortal(PortalSelectionModel portal,
                            String name,
			String mode) {
		setTag("portal:portal");
		setNamespace(PortalConstants.PORTAL_XML_NS);

		s_log.debug("IN constructor" + name + " " + mode);
		m_adders = new PortletTypeForm[MAX_COLUMNS];
		m_mode = mode;
		m_portal = portal;

        m_column = new ParameterSingleSelectionModel(new IntegerParameter("column"));
        

		if (m_mode.equals(PortalConstants.MODE_EDITOR)) {
			for (int i = 0; i < m_adders.length; i++) {
				m_adders[i] = new PortletTypeForm("add" + name + i);
				m_adders[i].setRedirecting(true);
				add(m_adders[i]);
				m_adders[i].addProcessListener(new PortletAddListener(m_portal,
						i + 1));
			}

            m_portlet = new PortletSelectionModel(
                new BigDecimalParameter("edit")
            );
			m_portletType = new PortletTypeSelectionModel(
                new BigDecimalParameter("create")
            );

			m_portal.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent event) {
					PageState state = event.getPageState();
					if (m_portal.isSelected(state)) {
                            WorkspacePage portal = 
                                m_portal.getSelectedPortal(state);
						s_log.debug("Setting portal" + portal);
						m_parentResource.set(state, portal);
					} else {
						s_log.debug("Clearing portal");
						m_parentResource.set(state, null);
					}
				}
			});
			m_portlet.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent event) {
					PageState state = event.getPageState();
					if (m_portal.isSelected(state)) {
                            com.arsdigita.portal.Portlet portlet = 
                                m_portlet.getSelectedPortlet(state);
						s_log.debug("Setting portlet" + portlet);
						m_currentResource.set(state, portlet);
					} else {
						s_log.debug("Clearing portlet");
						m_currentResource.set(state, null);
					}
				}
			});

			m_parentApp = new DomainObjectParameter("parentApp");
			m_parentResource = new RequestLocal() {
				public Object initialValue(PageState state) {
					return state.getValue(m_parentApp);
				}
			};
			m_currentResource = new RequestLocal() {
				public Object initialValue(PageState state) {
					return m_portlet.getSelectedPortlet(state);
				}
			};
			m_currentApp = new RequestLocal() {
				public Object initialValue(PageState state) {
					return Kernel.getContext().getResource();
				}
			};

			PortletTypeCollection types = PortletType.retrieveAllPortletTypes();
			m_create = new HashMap();
			m_modify = new HashMap();
			m_createApp = new HashMap();
			s_log.debug("Do add types");
			while (types.next()) {
				PortletType type = types.getPortletType();
				s_log.debug("Add type " + type.getResourceObjectType());

                final ResourceConfigComponent create = 
                    type.getCreateComponent(m_parentResource);
                final ResourceConfigComponent modify = 
                    type.getModifyComponent(m_currentResource);


				ApplicationType appType = type.getProviderApplicationType();
				SimpleContainer createApp = null;
				if (appType != null) {
                    final ResourceConfigComponent appCreate = 
                        appType.getCreateComponent(m_currentApp);
					ApplicationSelector sel = new ApplicationSelector(appType,
                                                                      m_parentApp,
                                                                      appType.getConfig() == null ? null : appType.getConfig().getViewPrivilege());
					appCreate.addCompletionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							PageState state = e.getPageState();
							s_log.debug("Do create of portlet");
							Resource resource = appCreate.createResource(state);
							if (resource == null) {
								s_log.debug("No resource, reset");
								m_portletType.clearSelection(e.getPageState());
							} else {
								s_log.debug("Seting res to " + resource);
								state.setValue(m_parentApp, resource);
							}
						}
					});
					sel.addCompletionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							PageState state = e.getPageState();
							if (state.getValue(m_parentApp) == null) {
								s_log.debug("Sel no resource, reset");
								m_portletType.clearSelection(e.getPageState());
							} else {
                                    s_log.debug("Got res " + 
                                                state.getValue(m_parentApp));
							}
						}
					});

					createApp = new SimpleContainer();
					createApp.add(appCreate);
					createApp.add(sel);
				}

				s_log.debug("Create component is " + create);
				s_log.debug("Modify component is " + modify);

				create.addCompletionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PageState state = e.getPageState();
						s_log.debug("Do create of portlet");
						Resource resource = create.createResource(state);

						if (resource != null) {
                                Integer column = (Integer)
                                    m_column.getSelectedKey(state);
							Assert.exists(column, Integer.class);

                                WorkspacePage portal = 
                                    m_portal.getSelectedPortal(state);
                                portal.addPortlet((Portlet)resource, 
                                                  column.intValue());
							portal.save();
						}
							            
                            
                            // added cg - remove cached page if max existing
						// stateful portlet count is exceeded so page is
						// rebuilt with correct number of renderers
						if (resource instanceof StatefulPortlet) {
							s_log.debug("Stateful portlet added");
							// check if the maximum number of stateful
							// portlets has increased
                                PortletType portletType =
                                    ((Portlet) resource).getPortletType();
                                DataQuery findMaxInstances =
                                    SessionManager.getSession().retrieveQuery(
                                        "com.arsdigita.london.portal.MaxPortletInstances");
                                findMaxInstances.setParameter(
                                    "portletType",
									portletType.getID());
							int maxCount = 0;
							while (findMaxInstances.next()) {
                                    maxCount =
                                        ((Integer) findMaxInstances
                                            .get("maxCount"))
                                            .intValue();
							}
							String key = portletType.getResourceObjectType();

                                int previousMax = StatefulPersistentPortal.getCurrentPortletRendererInstances(key);

                                s_log.debug(
                                    portletType
                                        + ": previous count = "
                                        + previousMax
                                        + " | new max = "
                                        + maxCount);
							if (maxCount > previousMax) {
									DefinePage.invalidatePage(DispatcherHelper.getCurrentResourcePath(state.getRequest())); }
						}
						m_portletType.clearSelection(e.getPageState());
						state.setValue(m_parentApp, null);
						m_parentResource.set(state, null);
					}
				});
				modify.addCompletionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						s_log.debug("Do modify of portlet");
						PageState state = e.getPageState();
						modify.modifyResource(state);
						Portlet portlet = m_portlet.getSelectedPortlet(state);
						portlet.save();
						m_portlet.clearSelection(state);
					}
				});
				m_create.put(type.getResourceObjectType(), create);
				m_modify.put(type.getResourceObjectType(), modify);
				if (createApp != null) {
					m_createApp.put(type.getResourceObjectType(), createApp);
				}
				add(create);
				add(modify);
				if (createApp != null) {
					add(createApp);
				}
			}
            s_log.debug("Done with add types" + m_create.size() 
                      + " " + m_modify.size());

			Assert.exists(m_parentResource, RequestLocal.class);
            m_portalModelBuilder = new PortalEditModelBuilder(
                portal, m_adders, 
                m_portletType, m_column, m_portlet,
                m_create, m_modify, m_createApp, m_parentResource);
		} else {
			m_portalModelBuilder = new PortalViewModelBuilder(portal);
		}
	}

	public void register(Page page) {
		super.register(page);

		if (m_portlet != null) {
			page.addComponentStateParam(this, m_portlet.getStateParameter());
		}
		if (m_portletType != null) {
            page.addComponentStateParam(this, m_portletType.getStateParameter());
		}

		page.addComponentStateParam(this, m_column.getStateParameter());
		if (m_parentApp != null) {
			page.addComponentStateParam(this, m_parentApp);
		}
	}

    public void generateXML(PageState state,
                            Element parent) {
		Element content = generateParent(parent);

		WorkspacePage page = m_portal.getSelectedPortal(state);
		content.addAttribute("layout", page.getLayout().getFormat());
		content.addAttribute("style", page.getLayout().getTitle());
		content.addAttribute("title", page.getTitle());
		content.addAttribute("description", page.getDescription());

		PortalModel pm = m_portalModelBuilder.buildModel(state);
		Iterator portlets = pm.getPortletRenderers();

		while (portlets.hasNext()) {
			Object entry = portlets.next();
			if (entry instanceof Object[]) {
				PortletRenderer renderer = (PortletRenderer) ((Object[]) entry)[0];
				BigDecimal portlet = (BigDecimal) ((Object[]) entry)[1];

				// We want the root element created by the portlet
				// but the crap generateXML signature doesn't let
				// us get at it :-( And the bebop portlet isn't
				// any more helpful either :-(
				Element hack = new Element("hack");

				renderer.generateXML(state, hack);

				Iterator elements = hack.getChildren().iterator();
				while (elements.hasNext()) {
					Element child = (Element) elements.next();

					generateActionXML(state, child, portlet);
					content.addContent(child);
				}
			} else {
				PortletRenderer renderer = (PortletRenderer) entry;
				renderer.generateXML(state, content);
			}
		}
	}

    public void generateActionXML(PageState state,
                                  Element parent,
			BigDecimal portlet) {
		generateActionXML(state, parent, portlet, ACTION_CUSTOMIZE);
		generateActionXML(state, parent, portlet, ACTION_MOVE_UP);
		generateActionXML(state, parent, portlet, ACTION_MOVE_DOWN);
		generateActionXML(state, parent, portlet, ACTION_MOVE_LEFT);
		generateActionXML(state, parent, portlet, ACTION_MOVE_RIGHT);
		generateActionXML(state, parent, portlet, ACTION_DELETE);
	}

    public void generateActionXML(PageState state,
                                  Element parent,
                                  BigDecimal portlet,
                                  String name) {
		Element action = parent.newChildElement("portlet:action",
				PortalConstants.PORTLET_XML_NS);
		try {
            state.setControlEvent(this,
                                  name, 
                                  portlet.toString());            
			action.addAttribute("name", name);
			action.addAttribute("url", state.stateAsURL());

			state.clearControlEvent();
		} catch (IOException ex) {
			throw new UncheckedWrapperException("cannot get state url", ex);
		}
	}

	public void respond(PageState state) {
		WorkspacePage portal = m_portal.getSelectedPortal(state);

		if (m_mode.equals(PortalConstants.MODE_EDITOR)) {

			// check permission on Workspace, not WorkspacePage,
			// as this is where the permissiones/groups are set
			Workspace workspace = portal.getWorkspace();
			Party party = Kernel.getContext().getParty();
            if (!PortalHelper.canCustomize(party,
                                           workspace)) {
				throw new AccessDeniedException(
						"no permissions to customize workspace");
			}

			String key = state.getControlEventName();
			String value = state.getControlEventValue();
			Portlet portlet = Portlet.retrievePortlet(new BigDecimal(value)); 

			if (ACTION_MOVE_UP.equals(key)) {
				portal.swapPortletWithPrevious(portlet);
				portal.save();
			} else if (ACTION_MOVE_DOWN.equals(key)) {
				portal.swapPortletWithNext(portlet);
				portal.save();
                
			} else if (ACTION_MOVE_LEFT.equals(key)) {
                int cell = portlet.getCellNumber();
                cell = cell - 1;
                if (cell < 1) { cell = 1; }
                portlet.setCellNumber(cell);
                portlet.save();
			} else if (ACTION_MOVE_RIGHT.equals(key)) {
                int cello = portlet.getCellNumber();
                cello = cello + 1;
                if (cello > 3) { cello = 3; }
                portlet.setCellNumber(cello);
                portlet.save();
			} else if (ACTION_DELETE.equals(key)) {
                if (portlet != null) {
				// null if double click on link - in which case do nothing 
				// note - may not have js double click protection on this as it is 
				// a link cg 
				portlet.delete();
                } else {
                	s_log.debug("doubleclick detected");
                }
                
			} else if (ACTION_CUSTOMIZE.equals(key)) {
				m_portlet.setSelectedKey(state, new BigDecimal(value));
			}
		}
		state.clearControlEvent();
	}

	private class PortletAddListener implements FormProcessListener {

		private PortalSelectionModel m_portal;
		private int m_col;

        public PortletAddListener(PortalSelectionModel portal,
                                  int column) {
			m_portal = portal;
			m_col = column;
		}

        public void process(FormSectionEvent e) 
            throws FormProcessException {
			PageState state = e.getPageState();

			PortletTypeForm form = (PortletTypeForm) e.getSource();

			WorkspacePage portal = m_portal.getSelectedPortal(state);

			// check permission on Workspace, not WorkspacePage,
			// as this is where the permissiones/groups are set
			Workspace workspace = portal.getWorkspace();
			Party party = Kernel.getContext().getParty();
            if (!PortalHelper.canCustomize(party,
                                           workspace)) {
				throw new AccessDeniedException(
                    "no permissions to customize workspace"
                );
			}

            PortletType type = PortletType.retrievePortletType(
                form.getPortletType(state));
			if (s_log.isDebugEnabled()) {
                s_log.debug("Got create event for type " + 
                            type.getID() + " column " + m_col);
			}
			m_portletType.setSelectedKey(state, type.getID());
			m_column.setSelectedKey(state, new Integer(m_col));
		}
	}
}
