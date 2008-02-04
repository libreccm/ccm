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
package com.arsdigita.simplesurvey;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;

/**
 * A placeholder app just in existance because you
 * can't work with Application / ApplicationType classes
 * directly.
 */
public class SimpleSurvey extends Application {
    
    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.simplesurvey.SimpleSurvey";
    
    public SimpleSurvey(DataObject obj) {
        super(obj);
    }

    public SimpleSurvey(OID oid) 
        throws DataObjectNotFoundException {

        super(oid);
    }
    
    public String getContextPath() {
        return "/ccm-simplesurvey";
    }
    
    public String getServletPath() {
        return "/files";
    }

}
