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
import com.arsdigita.portalworkspace.ui.portlet.LoginPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;

/**
 * 
 * @version $Id: LoginPortlet.java 2075 2010-01-28 20:03:59Z pboy $
 */
public class LoginPortlet extends Portlet {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.portalworkspace.portlet.LoginPortlet";


    /**
     * Constructor
     *
     * @param dataObject
     */
    public LoginPortlet(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Retrieve Base Data Object Type
     *
     * @return String BaseDataObjectType
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * 
     * @return
     */
    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new LoginPortletRenderer(this);
    }
}
