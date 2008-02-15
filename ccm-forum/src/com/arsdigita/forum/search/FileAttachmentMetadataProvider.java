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
package com.arsdigita.forum.search;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.arsdigita.cms.search.AssetExtractor;
import com.arsdigita.cms.search.AssetMetadataProvider;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.PostFileAttachment;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.SystemLocaleProvider;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.User;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.toolbox.util.GlobalizationUtil;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * 
 */
public class FileAttachmentMetadataProvider extends  AssetMetadataProvider {
	
    private static Logger s_log = Logger.getLogger(FileAttachmentMetadataProvider.class);
	
    public String getSummary(DomainObject dobj) {
	PostFileAttachment file = (PostFileAttachment)dobj;
	s_log.debug("Getting Summary File " + file.getID());	
	Post owner = file.getOwner();
	//s_log.debug("Owner " + (owner == null ? null : owner.getID()));
	if (owner == null){
	    //until post is saved, the file doesn't have an owner 
	    //file is saved as draft so result will not be returned at front end
	    return "File attached to forum post";
	} 
	String url =  URLService.locate(owner.getOID());				
	String fileDescription = file.getDescription();
	StringBuffer summary = new StringBuffer();
	if (fileDescription != null) {
	    summary.append(fileDescription + " - ");
	}
	summary.append("A file attached to <a href=\"" + url + "\">"  + 
		owner.getSubject()  + "</a> - a posting by " + 
		((User)owner.getFrom()).getName() + " to forum " + owner.getForum().getTitle());
	return summary.toString();
		
    }
	
    public boolean isIndexable (DomainObject dobj) {
	PostFileAttachment file = (PostFileAttachment)dobj;
	Post owner = file.getOwner();
	Post root = null;
	s_log.debug("start:isIndexable");	
	boolean indexable = false;
	if (owner != null) {
	    // post has been saved
	    s_log.debug("owner = " + owner);
	    BigDecimal rootId = owner.getRoot();
	    if (rootId != null) {
		root = new Post(owner.getRoot());
		// else this is a root post
	    }
	    indexable = owner.getStatus().equals(Post.APPROVED) && (root == null ? true : root.getStatus().equals(Post.APPROVED));
	}
	return indexable;	
		
    }
	

}
