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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.AssetURLFinder;

import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * A URLFinder for PostFileAttachments. 
 */
public class PostFileAttachmentURLFinder implements URLFinder {

	private static final AssetURLFinder s_assetFinder = new AssetURLFinder();

	/**
	  * 
	  * find URL for a file attachment by finding its post
	  * 
	  * @param oid the OID of the file attachment
	  * @param content the context of the search (ie draft/live)
	  */
	public String find(OID oid, String context) throws NoValidURLException {
		// a draft attachment is one where the post hasn't been saved yet
		// the behaviour is the same as far as finding the url goes
		return find(oid);
		
		
	}

	/**
	  * 
	  * find URL for the context of a file attachment. Delegates to
	  * AssetURLFinder.
	  * 
	  * @param oid the OID of the file attachment
	  * 
	  */
	public String find(OID oid) throws NoValidURLException {
		return s_assetFinder.find(oid);
	}
}
