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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.forum.Post;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.SystemLocaleProvider;
import com.arsdigita.kernel.Party;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.MetadataProvider;
import com.arsdigita.toolbox.util.GlobalizationUtil;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 */
public class PostMetadataProvider implements MetadataProvider {

	private static Logger s_log = Logger.getLogger(PostMetadataProvider.class);
	/**
	 * No specific information is provided for a forum post 
	 */
	public String getTypeSpecificInfo(DomainObject dobj) {
		return null;
	}

	/**
	 * returns the default system Locale
	 */
	public Locale getLocale(DomainObject dobj) {
		return new SystemLocaleProvider().getLocale();
	}

	/**
	 * returns the subject of the post
	 * @see com.arsdigita.search.MetadataProvider#getTitle(com.arsdigita.domain.DomainObject)
	 */
	public String getTitle(DomainObject dobj) {
		Post post = (Post)dobj;
		return post.getSubject();
	}

	/**
	 * returns the date of posting and the author 
	 */
	public String getSummary(DomainObject dobj) {
		Post post = (Post)dobj;
		return "Posted to " + post.getForum().getTitle() + " by " + 
		post.getFrom().getName() + " on " + 
		DateFormat.getDateInstance(DateFormat.MEDIUM).format(post.getSentDate());
	}

	public Date getCreationDate(DomainObject dobj) {
		Post post = (Post)dobj;
		return post.getSentDate();
	}

	public Party getCreationParty(DomainObject dobj) {
		Post post = (Post)dobj;
		return post.getFrom();
	}

	public Date getLastModifiedDate(DomainObject dobj) {		
		//we are not storing the date if the post is edited
		return null;
	}

	public Party getLastModifiedParty(DomainObject dobj) {		
		//we are not storing the person that edits a post 
		return null;
	}

	public ContentProvider[] getContent(DomainObject dobj, ContentType type) {
		List content = new ArrayList();
        
	  if (type.equals(ContentType.XML)) {		  
		  content.add(new XMLContentProvider("xml",	(Post)dobj));
	  } else if (type.equals(ContentType.TEXT)) {		 
		  content.add(new TextContentProvider("text", (Post)dobj));
	  } 
	  
	  // don't deal with raw content - attached files are indexed separately
	 
	  return (ContentProvider[])content.toArray(
		  new ContentProvider[content.size()]);
  }
	public String getContentSection(DomainObject dobj) {
		return null;
	}

	public boolean isIndexable(DomainObject dobj) {
		Post post = (Post)dobj;
		s_log.debug("Post saved - status is " + post.getStatus() + ". Index Object? " + post.getStatus().equals(Post.APPROVED));
		
		return post.getStatus().equals(Post.APPROVED);
	}
	
	

}
