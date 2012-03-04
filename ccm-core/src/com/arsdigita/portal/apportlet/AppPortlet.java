/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portal.apportlet;

import com.arsdigita.portal.Portlet;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.portal.PortletRenderer;
import com.arsdigita.web.Application;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import com.arsdigita.web.URL;

/**
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 */

public class AppPortlet extends Portlet {

    protected AppPortlet(DataObject dataObject) {
        super(dataObject);
    }

    public Application getParentApplication() {
     
        Application app = (Application)getParentResource(); 
        return app;
    }

    public void setParentApplication(Application app) {
        
        setParentResource(app);
    }

    public AppPortletType getAppPortletType() {
        DataObject dataObject = (DataObject) get("resourceType");

        dataObject.specialize(AppPortletType.BASE_DATA_OBJECT_TYPE);

        AppPortletType portletType = AppPortletType.retrieveAppPortletType(dataObject);

        Assert.exists(portletType);

        return portletType;
    }

    /**
     * <p>Get the zoom URL associated with a portlet.  Portlet
     * implementors may override this to specify a Portlet's zoom URL.
     * The default implmementation simply finds the
     */

    public String getZoomURL() {

	if (getAppPortletType().isPortalApplication()) {
	    // The parent Application of a portlet is its zoomable counterpart.
	    Application application = getParentApplication();
	    
	    if (application != null) {
		return (URL.getDispatcherPath() + application.getPrimaryURL());
	    } else {
		return null;
	    }
	}  else {
	    return null;
	}
    }

    @Override
    public PortletRenderer getPortletRenderer() {
        AbstractPortletRenderer portletRenderer = doGetPortletRenderer();

        portletRenderer.setTitle(getTitle());
        portletRenderer.setCellNumber(getCellNumber());
        portletRenderer.setSortKey(getSortKey());
        portletRenderer.setProfile(getProfile());

        String zoomURL = getZoomURL();

        if (zoomURL != null) {
            portletRenderer.setPortletAttribute("applicationLink", zoomURL);
        }

        return portletRenderer;
    }



}
