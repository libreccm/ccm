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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.persistence.OID;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * An ItemSearch parameter, used in conjunction with the ItemSearch
 * widget which uses the search UI to find  a content item and fill
 * the widget with a string in the form itemID (ItemName). When
 * unmarshalled, a content item is returned
 *
 * @author <a href="mailto:sseago@redhat.com">Scott Seago</a>
 */

public class ItemSearchParameter extends StringParameter {

    private ContentType m_contentType;

    /**
     * Create a new item search parameter corresponding to a request parameter
     * with the given name.
     *
     * @param name the name of the request parameter from which item
     */
    public ItemSearchParameter(String name) {
        this(name,null);
    }


    /**
     * Create a new item search parameter corresponding to a request parameter
     * with the given name.
     *
     * @param name the name of the request parameter from which item
     * @param contentType If not null, search will be limited to the
     * specified content type 
     */
    public ItemSearchParameter(String name, 
			       ContentType contentType) {
        super(name);
	m_contentType = contentType;
    }

    /**
     * Retrieve the content item from the request. Returns
     * <code>null</code> if the request parameter does not 
     * correspond to a content item
     *
     * @param request represents the current request
     * @return the content item
     * @throws IllegalArgumentException if the request parameter does not
     * look like a valid email address.
     */
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {

        String itemStr = Globalization.decodeParameter(request, getName());
	
        return unmarshal(itemStr);
    }

    public Object unmarshal(String encoded)
        throws IllegalArgumentException {

        // As stated above, if we get an invalid address just return null.
        if (encoded == null || encoded.length() < 1) {
            return null;
	}
        String idStr = encoded.substring(0,encoded.indexOf(' '));
        if (idStr == null || idStr.length() < 1) {
            return null;
        }
        BigDecimal itemID = new BigDecimal(idStr);
        if (itemID == null) {
            return null;
        }
        ContentItem contentItem;
        try {
            contentItem = (ContentItem) DomainObjectFactory.newInstance
                (new OID(ContentItem.BASE_DATA_OBJECT_TYPE, itemID));
        } catch (DataObjectNotFoundException e) {
            throw new IllegalArgumentException
                (encoded + 
                 " is not a valid contentItem." + 
                 e.getMessage());
        }
    
	if (m_contentType != null && 
	    !contentItem.getContentType().equals(m_contentType)) {
	    return null;
	    /*
	    throw new IllegalArgumentException
		(encoded + " is not a valid " + m_contentType.getLabel());
	    */
	}
        return contentItem;
    }

    public String marshal(Object value) {
        if (value == null) {
            return null;
        } else {
	    ContentPage theItem = (ContentPage) value;
	    return (theItem.getID().toString() + " (" + theItem.getTitle() + ")");
        }
    }
        
    public Class getValueClass() {
        return ContentPage.class;
    }

}
