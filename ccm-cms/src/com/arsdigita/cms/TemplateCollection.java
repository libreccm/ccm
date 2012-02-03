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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.mimetypes.MimeType;


/**
 * Represents a collection of template mappings along with their
 * link attributes.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 * @see com.arsdigita.cms.ItemTemplateCollection
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TemplateCollection.java 2090 2010-04-17 08:04:14Z pboy $
 */
public abstract class TemplateCollection extends DomainCollection {

    public static final String USE_CONTEXT  = "useContext";
    public static final String IS_DEFAULT   = "isDefault";
    public static final String TEMPLATE     = "template";

    /**
     * Constructor.
     *
     **/
    public TemplateCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Set the order of this Collection. This method needs to be called
     * before <code>next()</code> is called on this collection.
     *
     */
    @Override
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }

    /**
     * Return the current template
     */
    public abstract Template getTemplate();

    /**
     * Return the current use context
     */
    public abstract String getUseContext();

    /**
     * Return true if the current template is the default for its context
     */
    public abstract Boolean isDefault();

    /**
     *  this is the mime type for this context but does not necessarily
     *  have to be the same mime type that is returned by calling
     *  getTemplate().getMimeType().  This can return null
     */
    public MimeType getMimeType() {
        Template template = getTemplate();
        if (template != null) {
            return template.getMimeType();
        }
        return null;
    }
    
    /**
     * Filter this collection for equality by the specified attribute
     */
    @Override
    public Filter addEqualsFilter(String attr, Object value)
        throws PersistenceException {
        return m_dataCollection.addEqualsFilter(attr, value);
    }

    /**
     * Return the internal DataCollection
     */
    public DataCollection getDataCollection() {
        return m_dataCollection;
    }

}
