/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;


/**
 * A domain object for maintaining template use
 * context information
 * @see com.arsdigita.cms.TemplateContextCollection
 * @author Daniel Berrange
 */
public class TemplateContext extends DomainObject {
    
    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.cms.TemplateContext";

    public static final String CONTEXT = "context";
    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    
    public TemplateContext() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public TemplateContext(DataObject obj) {
        super(obj);
    }
    
    public static TemplateContext create(String context,
                                         String label,
                                         String description) {
        TemplateContext tc = new TemplateContext();
        tc.setContext(context);
        tc.setLabel(label);
        tc.setDescription(description);
        return tc;
    }

    public static TemplateContext retrieve(String context)
        throws DataObjectNotFoundException {
        DataCollection da = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        da.setParameter(CONTEXT, context);

        try {
            if (da.next()) {
                return (TemplateContext)
                    DomainObjectFactory.newInstance(da.getDataObject());
            }
        } finally {
            da.close();
        }

        throw new DataObjectNotFoundException(
            "cannot find template context " + context
        );
    }

    public static TemplateContextCollection retrieveAll() {
        DataCollection da = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        TemplateContextCollection c = new TemplateContextCollection(da);
        return c;
    }

    public String getContext() {
        return (String)get(CONTEXT);
    }

    public void setContext(String context) {
        set(CONTEXT, context);
    }

    public String getLabel() {
        return (String)get(LABEL);
    }

    public void setLabel(String label) {
        set(LABEL, label);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }
}
