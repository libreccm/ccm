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

package com.arsdigita.portalworkspace.portlet;

import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.portalworkspace.ui.portlet.ApplicationDirectoryPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;

/**
 * ApplicationDirectoryPortlet
 *
 * @version $Id: ApplicationDirectoryPortlet.java 1174 2006-06-14 14:14:15Z fabrice $ 
 */
public class ApplicationDirectoryPortlet extends Portlet {

	public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.portalworkspace.portlet.ApplicationDirectoryPortlet";

    public static final String CONTENT = "content";

    public ApplicationDirectoryPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new ApplicationDirectoryPortletRenderer(this);
    }
}
