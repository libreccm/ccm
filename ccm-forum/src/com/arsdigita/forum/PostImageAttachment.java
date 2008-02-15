/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
package com.arsdigita.forum;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Domain object for an image attached to a post. Modelled as domain object
 * rather than simple association with link attribute because cloning 
 * seems to struggle with link attributes 
 */
public class PostImageAttachment extends ImageAsset {
	
    private static final Logger s_log = Logger.getLogger(PostImageAttachment.class);

    public static final String BASE_DATA_OBJECT_TYPE =
			"com.arsdigita.forum.PostImageAttachment";

    public static final String IMAGE_OWNER = "imageMessage";
    public static final String IMAGE_ORDER = "imageOrder";

    /**
     * Default constructor. ;
     **/
    public PostImageAttachment() {
	super(BASE_DATA_OBJECT_TYPE);
	setVersion(DRAFT);
			
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public PostImageAttachment(OID oid) {
	super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>ContentPage.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public PostImageAttachment(BigDecimal id) {
	this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PostImageAttachment(DataObject obj) {
	super(obj);
    }

    public PostImageAttachment(String type) {
	super(type);
    }

    public String getBaseDataObjectType() {
	return BASE_DATA_OBJECT_TYPE;
    }
		
    public void setImageOrder(Integer order) {
	set(IMAGE_ORDER, order);
    }

    public void setImageOrder(int order) {
	set(IMAGE_ORDER, new Integer(order));
    }
	
	
	
    public Post getOwner() {
	return (Post)DomainObjectFactory.newInstance((DataObject)get(IMAGE_OWNER));
    }
	
	
    protected static void removeUnattachedImages() {
	s_log.debug("removing orphaned images created more than a day ago");
	DataCollection images = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
	Calendar yesterday = Calendar.getInstance();
	yesterday.add(Calendar.DATE, -1);
	images.addFilter(images.getFilterFactory().lessThan(AUDITING + "." + BasicAuditTrail.CREATION_DATE,
			yesterday.getTime(),
			false));
				
	images.addEqualsFilter(IMAGE_OWNER, null);
	while (images.next()) {
	    s_log.debug("deleting one");
	    DomainObjectFactory.newInstance(images.getDataObject()).delete();
	}
		
    }
	



}
