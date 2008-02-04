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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.Asset;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;

import org.apache.log4j.Logger;
import com.arsdigita.cms.search.AssetMetadataProvider;

/**
 * This class is an implementation of the Search metadata provider for any
 * subclass of {@link com.arsdigita.cms.Asset}.
 *
 * @author <a href="mailto:berrange@redhat.com">Daniel Berrange</a>
 * @version $Revision: 1.2 $ $Date: 2005/09/07 15:18:36 $
 */
public class FileAttachmentMetadataProvider extends AssetMetadataProvider {

	private static final Logger s_log =
		Logger.getLogger(FileAttachmentMetadataProvider.class);

	public final static String versionId =
		"$Id: FileAttachmentMetadataProvider.java,v 1.2 2005/09/07 15:18:36 cgyg9330 Exp $"
			+ " by $Author: cgyg9330 $, $DateTime: 2004/08/17 23:15:09 $";

	public String getSummary(DomainObject dobj) {

		// add config parameter to allow link to owner
		FileAttachment file = (FileAttachment) dobj;
		String description = file.getDescription();
		ContentPage owner = (ContentPage) file.getFileOwner();
		StringBuffer summary = new StringBuffer();
		if (description != null) {
			summary.append(description + " - ");
		}
	if (owner != null) {
	    String title = owner.getTitle();

		if (owner.isLiveVersion()) {
			ParameterMap map = new ParameterMap();
			map.setParameter( "oid", owner.getOID().toString() );
			
			String url = new URL(Web.getConfig().getDefaultScheme(),
					Web.getConfig().getServer().getName(),
					Web.getConfig().getServer().getPort(),
					"",
					"",
					"/redirect/", map ).getURL();	  
			
		summary.append("A file attached to <a href=\"" + url + "\">" + title + "</a>");

		} else {
			// draft - don't give a live link because stylesheets 
            // escape <a> tags. If this is changed in the bebop stylesheets
            // then just add parameter context=draft in map created above
            // instead of this
			summary.append("A file attached to " + title);

		}
	}

		return summary.toString();

	}

	
	
	public boolean isIndexable (DomainObject dobj) {
		FileAttachment file = (FileAttachment) dobj;
		ContentPage owner = (ContentPage) file.getFileOwner();
	boolean index = false;
	if (owner != null) {
		s_log.debug("index this file attachment? " + !owner.indexAssetsWithPage());
	    index =  !owner.indexAssetsWithPage();
	}
	return index;
    }
	
    public String getContentSection(DomainObject dobj) {
	String sectionName = "";
       	FileAttachment file = (FileAttachment) dobj;
       	ContentItem owner = file.getFileOwner();
       	if (owner != null) {
    		
	    ContentSection section = owner.getContentSection();
       	    if (section != null) {
       		sectionName = section.getName();
       	    }
       	}
	return sectionName;
	}

}
