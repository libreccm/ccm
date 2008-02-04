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

package com.arsdigita.london.portal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.portal.Portal;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.london.portal.ui.PortalSelectionModel;
import com.arsdigita.london.portal.ui.PortletSelectionModel;
import com.arsdigita.london.portal.ui.PortletTypeSelectionModel;
import com.arsdigita.london.portal.ui.WorkspaceSelectionModel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.PortletTypeCollection;
import com.arsdigita.xml.Element;
/**
 * 
 * @author cgyg9330 &lt;chris.gilbert@westsussex.gov.uk&gt
 * @version $Id: StatefulPersistentPortal.java 1271 2006-07-18 13:36:43Z cgyg9330 $
 *
 * Dynamically render the portlets for the current portal. If any 
 * portlets are stateful, retrieve a renderer from cache so that the page can 
 * manage the state of the portlet. 
 * 
 * ONLY VALID FOR BROWSE MODE, use com.arsdigita.portal.ui.PersistentPortal for edit mode
 * 
 * 
 */
public class StatefulPersistentPortal extends SimpleContainer {

	private static final Logger s_log =
		Logger.getLogger(StatefulPersistentPortal.class);

	private WorkspaceSelectionModel m_workspace;
	private PortletSelectionModel m_portlet;
private PortalModelBuilder m_portalModelBuilder;
private PortalSelectionModel m_portal;


	/**
	 * maps portlet type to list of empty renderers
	 */
private static Map  m_stateful;



	public StatefulPersistentPortal(PortalSelectionModel portal) {
	this(portal, "portal");
	}
		/**
	 * Constructor is invoked when the page component hierarchy is recreated. This occurs 
	 * a) when the first person goes to the homepage after a server restart 
	 * b) when someone adds a stateful portlet to one of the homepage portals, which increases the 
	 *    maximum number of that portlet type that appears on any portal 
	 * @param portal the portalSelectionModel used by the Dynamic PortalModelBuilder
	 * @param name
	 */
	public StatefulPersistentPortal(
		PortalSelectionModel portal,
		String name) {
		s_log.debug("IN constructor" + name );
		m_portal = portal;
		setTag("portal:portal");
		setNamespace(PortalConstants.PORTAL_XML_NS);

		// retrieve empty renderers for any stateful portlets and add them to the component hierarchy
		PortletTypeCollection types = PortletType.retrieveAllPortletTypes();
		m_stateful = new HashMap();

		while (types.next()) {

			PortletType type = types.getPortletType();
			s_log.debug("checking portlet type " + type.getDescription());
			String portletObjectType = type.getResourceObjectType();
			StatefulPortletRendererFactory factory = StatefulPortlet.getRendererFactory(portletObjectType);	
			if (null != factory ) {
				DataQuery findMaxInstances = SessionManager.getSession().retrieveQuery("com.arsdigita.london.portal.MaxPortletInstances");
				findMaxInstances.setParameter("portletType", type.getID());
			
				int requiredRenderers = 0;
				while (findMaxInstances.next()) {
					requiredRenderers = ((Integer)findMaxInstances.get("maxCount")).intValue();
				}
				s_log.debug("stateful portlet - I am going to instantiate " + requiredRenderers + " renderers");
				List renderers = new ArrayList();
				for (int i = 0; i < requiredRenderers; i++) {
					StatefulPortletRenderer renderer = factory.getRenderer();
					renderers.add(renderer);

					// and add it to the page
					add(renderer);
					s_log.debug("renderer added to page");
				}
				m_stateful.put(type.getResourceObjectType(), renderers);

			}
		}
		m_portalModelBuilder =
			new StatefulPersistentPortalModelBuilder(portal, m_stateful);

	}
		// copied almost directly from PersistentPortal
		public void generateXML(PageState state,
								Element parent) {
			Element content = generateParent(parent);

			WorkspacePage page = m_portal.getSelectedPortal(state);
			content.addAttribute("layout", page.getLayout().getFormat());
			content.addAttribute("title", page.getTitle());
			content.addAttribute("description", page.getDescription());
	
			PortalModel pm = m_portalModelBuilder.buildModel(state);
			Iterator portlets = pm.getPortletRenderers();
            
			while (portlets.hasNext()) {            
				Object entry = portlets.next();
				if (entry instanceof Object[]) {
					PortletRenderer renderer = (PortletRenderer)((Object[])entry)[0];
					BigDecimal portlet = (BigDecimal)((Object[])entry)[1];

					// We want the root element created by the portlet
					// but the crap generateXML signature doesn't let
					// us get at it :-( And the bebop portlet isn't 
					// any more helpful either :-(
					Element hack = new Element("hack");
                
					renderer.generateXML(state, hack);
                
					Iterator elements = hack.getChildren().iterator();
					while (elements.hasNext()) {
						Element child = (Element)elements.next();
                    
						content.addContent(child);
	}
				} else {

					PortletRenderer renderer = (PortletRenderer)entry;
					renderer.generateXML(state, content);
				}
	}
	}

	/**
	 * return the number of renderers for the given portlet type currently registered on the page
	 * @param portletType
	 * @return
	 */
	public static int getCurrentPortletRendererInstances (String portletType) {
		int count = 0;
		List renderers = (List)m_stateful.get(portletType);
		if (renderers != null) {
			count = renderers.size();
		}
		return count;
	}

}
