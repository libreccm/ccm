/*
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
 */

package com.arsdigita.portalworkspace;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.PortalModel;
import com.arsdigita.bebop.portal.PortalModelBuilder;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.portalworkspace.ui.PortalSelectionModel;
import com.arsdigita.portal.Portal;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.util.LockableImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Portal Model Builder that should only be applied to portal in
 * browse mode, as it does not allow for editing.
 * Portal in edit mode should have a
 * com.arsdigita.london.portal.ui.PersistentPortalModelBuilder.
 * 
 * For stateless portlets, this builder creates a renderer with a reference to 
 * its portlet data object. For stateful portlets the builder retrieves one
 * of the empty renderers registered to the page and puts the portlet data 
 * object in a requestlocal.
 *
 * @author cgyg9330
 */
public class StatefulPersistentPortalModelBuilder
	extends LockableImpl
		implements PortalModelBuilder {

	/** Logging instace for debugging purpose.                               */
    private Logger s_log =
                   Logger.getLogger(StatefulPersistentPortalModelBuilder.class);

	/** Hashmap that maps portlet types to a list of empty renderers that
       have been registered on the page                                      */
	private Map statefulCollections;
	private PortalSelectionModel portalModel;

	/**
     * Constructor
     * @param portal
     * @param stateful
     */
    public StatefulPersistentPortalModelBuilder(
		PortalSelectionModel portal,
		Map stateful) {
		this.portalModel = portal;
		this.statefulCollections = stateful;

	}

	public PortalModel buildModel(PageState state) {
		// aim of this method is to get hold of a list of renderers for
        // this particular portal and use them to make a PortalModel
		s_log.debug("START - buildModel");
		List portletRenderers = new ArrayList();

		HashMap statefulIterators = new HashMap();

		// get iterators for all the renderer lists (this means that each
        // instance of the portlet has it's own renderer)
		// getting iterators in the buildmodel method means that each time
        // model is built, we start at the beginning of the list
		Iterator it = statefulCollections.entrySet().iterator();
		while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String resourceType = (String)entry.getKey();
				Iterator renderersIterator = ((List)entry.getValue()).iterator();
			statefulIterators.put(resourceType, renderersIterator);
		}
		Portal portal = portalModel.getSelectedPortal(state);
		PortletCollection portlets = portal.getPortlets();
		while (portlets.next()) {
			com.arsdigita.portal.Portlet portlet = portlets.getPortlet();
			s_log.debug("portlet " + portlet.getPortletType().getDescription() +
                        " " + portlet.getTitle());
			if (portlet instanceof StatefulPortlet) {
				StatefulPortletRenderer statefulRenderer =
                        (StatefulPortletRenderer)((Iterator) statefulIterators
                            .get(portlet.getPortletType()
                                        .getResourceObjectType())).next();

				statefulRenderer.setPortlet(portlet, state);
				portletRenderers.add(statefulRenderer);
				s_log.debug("stateful renderer added to model");
			} else {
				PortletRenderer renderer = portlet.getPortletRenderer();
				portletRenderers.add(renderer);
				s_log.debug("stateless renderer added to model");
			}

			
		
		}
		s_log.debug("FINISH - buildModel " + portal.getTitle());
		return new StatefulPersistentPortalModel(
			portletRenderers.iterator(),
				portal.getTitle());

	}
	private class StatefulPersistentPortalModel implements PortalModel {

		private Iterator portlets;
		private String title;

		public StatefulPersistentPortalModel(Iterator portlets, String title) {
			this.portlets = portlets;
			this.title = title;
		}

		public Iterator getPortletRenderers() {
			return portlets;
		}

		public String getTitle() {
			return title;
		}
	}
}
