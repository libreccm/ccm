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
 *
 */
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Represents a mapping between an arbitrary parent and a template.
 * Known implementations are {@link SectionTemplateMapping} and
 * {@link ItemTemplateMapping}
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 *
 * @version $Id: TemplateMapping.java 2090 2010-04-17 08:04:14Z pboy $
 */
public abstract class TemplateMapping extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.TemplateMapping";

    public static final String TEMPLATE     = TemplateCollection.TEMPLATE;
    public static final String USE_CONTEXT  = TemplateCollection.USE_CONTEXT;
    public static final String IS_DEFAULT   = TemplateCollection.IS_DEFAULT;

    // Default constructor
    public TemplateMapping() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    // OID constructor
    public TemplateMapping(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    // ID constructor
    public TemplateMapping(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    // DataObject constructor
    public TemplateMapping(DataObject obj) {
        super(obj);
    }

    // Subtype constructor
    public TemplateMapping(String type) {
        super(type);

        setDefault(Boolean.FALSE);
        setUseContext(TemplateManager.PUBLIC_CONTEXT);
    }

    /**
     * Return the context section where the template is associated.
     * Child classes should override this method to do the right thing.
     */
    public abstract ContentSection getContentSection();

    /**
     * Return the parent of the template within the section.
     * This will most likely be a content type or a content item.
     */
    public abstract ACSObject getParent();

    /**
     * Return the template for this mapping
     */
    public Template getTemplate() {
        return (Template) DomainObjectFactory.newInstance
            ((DataObject) get(TEMPLATE));
    }

    /**
     * Set the new template for this mapping
     */
    public void setTemplate(Template t) {
        Assert.exists(t);
        setAssociation(TEMPLATE, t);
    }

    /**
     * Get the use context in which this template is associated
     */
    public String getUseContext() {
        return (String)get(USE_CONTEXT);
    }

    /**
     * Set the use context for this mapping
     */
    public void setUseContext(String context) {
        Assert.exists(context);
        set(USE_CONTEXT, context);
    }

    /**
     * Determine if the template will be the default within its
     * context. Child classes should override this method to do the
     * right thing.
     */
    public abstract Boolean isDefault();

    /**
     * Set whether the template will be the default within its
     * context. Child classes should override this method to do the
     * right thing.
     */
    public abstract void setDefault(Boolean b);

}
