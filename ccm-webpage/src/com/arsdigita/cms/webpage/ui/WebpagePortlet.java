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

package com.arsdigita.cms.webpage.ui;

import org.apache.log4j.Category;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.installer.Initializer;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.london.util.UrlUtil;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portal;
import com.arsdigita.portal.Portlet;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * Portlet showing the n most recently updated documents of a portal.
 * 
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch </a>
 */
public class WebpagePortlet extends Portlet {

	private static Category s_log = Category.getInstance(WebpagePortlet.class.getName());

	public static final String WEBPAGE = "webpage";

	public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.webpage.ui.WebpagePortlet";

	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public WebpagePortlet(DataObject dataObject) {
		super(dataObject);
	}

	public Webpage getWebpage() {
		DataObject obj = (DataObject) get(WEBPAGE);
		return obj == null ? null : (Webpage) DomainObjectFactory.newInstance(obj);
	}

	public void setWebpage(Webpage webpage) {
		setAssociation(WEBPAGE, webpage);
	}

	protected AbstractPortletRenderer doGetPortletRenderer() {
		return new WebpagePortletRenderer(this);
	}
	
	public void setPortal(final Portal portal) {
		super.setPortal(portal);
		
        // set the parent application to being the workspace
        if (portal instanceof WorkspacePage) {
            WorkspacePage wPage = (WorkspacePage) portal;
            setParentResource(wPage.getWorkspace());
        }

		// try to publish created webpage
		Webpage webpage = getWebpage();
		if (webpage != null && !webpage.isPublished()) {
			try {
				// get lifecycleDefinition
				LifecycleDefinitionCollection lfColl = null;
				try {
					lfColl = webpage.getContentSection().getLifecycleDefinitions();
					if (lfColl.next()) {
						webpage.publish(lfColl.getLifecycleDefinition(), new java.util.Date());
						
						// Force the lifecycle scheduler to run to avoid any
						// scheduler delay for items that should be published
						// immediately.
						webpage.getLifecycle().start();
						
						webpage.save();
					}
				}
				finally {
					if (lfColl != null) {
						lfColl.close();
					}
				}
			}
			catch (Exception e) {
				s_log.error("try to publish created webpage", e);
			}
		}
	}
}

class WebpagePortletRenderer extends AbstractPortletRenderer {

	private WebpagePortlet m_portlet;

	public WebpagePortletRenderer(WebpagePortlet portlet) {
		m_portlet = portlet;
	}

	protected void generateBodyXML(PageState pageState, Element parent) {

		Webpage webpage = m_portlet.getWebpage();
//		User user = (User) Kernel.getContext().getParty();
//		SecurityManager sm = new SecurityManager(webpage.getContentSection());
//		if (sm.canAccess(user, SecurityConstants.EDIT_ITEM, webpage)) {

		Label label = new Label(webpage.getBody(), false);
		label.generateXML(pageState, parent);

        // have to add the application link to a new element
        // attributes set on the parent are lost, somehow
		PermissionDescriptor perm = new PermissionDescriptor(PrivilegeDescriptor.EDIT,
                                                             Web.getContext().getApplication(),
                                                             Web.getContext().getUser());
		if (PermissionService.checkPermission(perm)) {
			StringBuffer appLinkURL = new StringBuffer(Web.getConfig().getDispatcherServletPath());
			appLinkURL.append('/');
			appLinkURL.append(Initializer.getConfig().getContentSection());
			appLinkURL.append("/edit.jsp?portletID=");
			appLinkURL.append(m_portlet.getID());

			//parent.addAttribute("applicationlink", UrlUtil.prepareURL(pageState, appLinkURL.toString(), null, false));
            Element applink = parent.newChildElement("bebop:applicationlink", com.arsdigita.bebop.Label.BEBOP_XML_NS);
            applink.addAttribute("value", UrlUtil.prepareURL(pageState, appLinkURL.toString(), null, false));
		}
	}
}
