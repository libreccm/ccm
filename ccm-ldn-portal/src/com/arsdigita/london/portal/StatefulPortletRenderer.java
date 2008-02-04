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

package com.arsdigita.london.portal;


import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.portal.Portlet;
import com.arsdigita.xml.Element;

/**
 * @author chris gilbert
 * 
 * Renderer that stores reference to portlet data object in a request local.
 * When the page is built, enough StatefulPortletRenderers are added to the page as might
 * be needed by any one portal. Hence all portals share the same stateful portlets and so 
 * portlet data object cannot be stored as an instane variable.
 * 
 * StatefulPortletModelBuilder sets value of requestlocal when the portlet model is built.
 */
public abstract class StatefulPortletRenderer extends AbstractPortletRenderer {

	private static Logger s_log = Logger.getLogger(StatefulPortletRenderer.class);

	/**
	 * holds the portlet data object for a particular request
	 */
	protected RequestLocal portlet = new RequestLocal();

	
	public void setPortlet(Portlet portlet, PageState state) {
		setTitle(portlet.getTitle());
		setCellNumber(portlet.getCellNumber());
		setSortKey(portlet.getSortKey());
		setProfile(portlet.getProfile());
		this.portlet.set(state, portlet);
	}

	/** 
	 * implementation of abstract method that does nothing. Any components that have been added to the container
	 * will be rendered by AbstractPortletRenderer within the bebop:portlet element
	 * 
	 * if you want to include other information within bebop:portlet, override this method 
	 * otherwise leave it
	 */
	protected void generateBodyXML(
		PageState pageState,
		Element parentElement) {
		// do nothing

	}
	
	
	
	
}
