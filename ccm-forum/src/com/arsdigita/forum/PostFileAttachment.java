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
import com.arsdigita.cms.FileAsset;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Domain object for a file attached to a post. Modelled as domain object
 * rather than simple association with link attribute because cloning 
 * seems to struggle with link attributes and also this allows files
 * to be indexed separately for searching
 */
public class PostFileAttachment extends FileAsset {
	
    private static final Logger s_log = Logger.getLogger(PostFileAttachment.class);

    public static final String BASE_DATA_OBJECT_TYPE =
		"com.arsdigita.forum.PostFileAttachment";
    public static final String FILE_OWNER = "fileMessage";
    public static final String FILE_ORDER = "fileOrder";

    public PostFileAttachment() {
	super(BASE_DATA_OBJECT_TYPE);
	setVersion(DRAFT);
		
    }

    public PostFileAttachment(OID oid) {
	super(oid);
    }

    public PostFileAttachment(BigDecimal id) {
	this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public PostFileAttachment(DataObject obj) {
	super(obj);
    }

    public PostFileAttachment(String type) {
	super(type);
    }

    public String getBaseDataObjectType() {
	return BASE_DATA_OBJECT_TYPE;
    }
		
    public void setFileOrder(Integer order) {
	set(FILE_ORDER, order);
    }

    public void setFileOrder(int order) {
	set(FILE_ORDER, new Integer(order));
    }
	
    /*
     * forums don't have a full publishing cycle,
     * so we won't apply a heavyweight publishing to attached files
     * just switch version tag
     */
    public void setLive () {
	setVersion(LIVE);	
    }
	
    /*
     * prevent files from being returned in search results - 
     * used after save of post, so that for example if post is unapproved, 
     * files are removed from search results
     */
    public void setDraft () {
	setVersion(DRAFT);
    }
	
	
    public Post getOwner() {
	return (Post)DomainObjectFactory.newInstance((DataObject)get(FILE_OWNER));
    }

    protected static void removeUnattachedFiles() {
	s_log.debug("removing orphaned files created more than a day ago");
	
	DataCollection files = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
	Calendar yesterday = Calendar.getInstance();
	yesterday.add(Calendar.DATE, -1);
	files.addFilter(files.getFilterFactory().lessThan(AUDITING + "." + BasicAuditTrail.CREATION_DATE,
				yesterday.getTime(),
				false));
				
	files.addEqualsFilter(FILE_OWNER, null);
	while (files.next()) {
	    s_log.debug("deleting one");
				
	    DomainObjectFactory.newInstance(files.getDataObject()).delete();
	}
		
    }
	



}
