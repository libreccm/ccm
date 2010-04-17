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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;

/**
 * This class contains a collection of {@link SectionTemplateMapping}s
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: SectionTemplateCollection.java 1940 2009-05-29 07:15:05Z terry $
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 * @see com.arsdigita.cms.ItemTemplateCollection
 */
class SectionTemplateCollection extends TemplateCollection {

    /**
     * Constructor.
     *
     **/
    public SectionTemplateCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     */
    public DomainObject getDomainObject() {
        return new SectionTemplateMapping(m_dataCollection.getDataObject());
    }

    /**
     * Return the current template
     */
    public Template getTemplate() {
        SectionTemplateMapping m = (SectionTemplateMapping)getDomainObject();
        if(m == null) return null;
        return m.getTemplate();
    }

    /**
     * Return the current use context
     */
    public String getUseContext() {
        SectionTemplateMapping m = (SectionTemplateMapping)getDomainObject();
        if(m == null) return null;
        return m.getUseContext();
    }

    /**
     * Return true if the current template is the default for its context
     */
    public Boolean isDefault() {
        SectionTemplateMapping m = (SectionTemplateMapping)getDomainObject();
        if(m == null) return null;
        return m.isDefault();
    }

}
