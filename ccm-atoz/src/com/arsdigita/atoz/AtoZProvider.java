/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.atoz;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


import com.arsdigita.kernel.ACSObject;

import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;


public abstract class AtoZProvider extends ACSObject {

    /** Private logger instace to assist debugging                           */
    private static final Logger s_log = Logger.getLogger(AtoZProvider.class);
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.atoz.AtoZProvider";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    protected AtoZProvider(String type) {
        super(type);
    }
    
    public AtoZProvider(DataObject obj) {
        super(obj);
    }

    public AtoZProvider(OID oid) {
        super(oid);
    }
    
    protected void setup(String title,
                         String description) {
        setTitle(title);
        setDescription(description);
    }

    public String getTitle() {
        return (String)get(TITLE);
    }
    
    public void setTitle(String title) {
        Assert.exists(title, String.class);
        set(TITLE, title);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }
    
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public abstract AtoZGenerator getGenerator();
}
