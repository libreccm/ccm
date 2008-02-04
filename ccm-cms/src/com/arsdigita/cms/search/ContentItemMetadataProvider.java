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
package com.arsdigita.cms.search;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.MetadataProvider;

import java.util.List;
import java.util.ArrayList;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class is an implementation of the Search metadata provider that uses the
 * <code>DomainObjectTextRenderer</code> to extract search content for any
 * subclass of {@link com.arsdigita.cms.ContentItem}.
 *
 * @author <a href="mailto:berrange@redhat.com">Daniel Berrange</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2005/10/04 12:09:55 $
 */
public abstract class ContentItemMetadataProvider implements MetadataProvider {
    private static final Logger s_log = 
        Logger.getLogger(ContentItemMetadataProvider.class);

    public final static String versionId =
        "$Id: ContentItemMetadataProvider.java 1263 2006-07-17 08:18:47Z cgyg9330 $"
        + " by $Author: cgyg9330 $, $DateTime: 2004/08/17 23:15:09 $";

    public String getTypeSpecificInfo(DomainObject dobj) {
        ContentItem item = (ContentItem) dobj;
        return ContentItem.LIVE.equals(item.getVersion()) ?
            ContentItem.LIVE : ContentItem.DRAFT;
    }

    public java.util.Locale getLocale(DomainObject dobj) {
        ContentItem item = (ContentItem)dobj;
        String lang = item.getLanguage();
        return lang == null ? null : new java.util.Locale(lang,"");
    }

    public Date getCreationDate(DomainObject dobj) {
        ContentItem item = (ContentItem)dobj;
        return item.getCreationDate();
    }

    public Party getCreationParty(DomainObject dobj) {
        ContentItem item = (ContentItem)dobj;
        return item.getCreationUser();
    }

    public Date getLastModifiedDate(DomainObject dobj) {
        ContentItem item = (ContentItem)dobj;
        return item.getLastModifiedDate();
    }

    public Party getLastModifiedParty(DomainObject dobj) {
        ContentItem item = (ContentItem)dobj;
        return item.getLastModifiedUser();
    }
    
    public boolean isIndexable (DomainObject dobj) {
    	return true;
    }
    public ContentProvider[] getContent(DomainObject dobj,
                                        ContentType type) {
        List content = new ArrayList();
        
        if (type == ContentType.XML) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding XML Content for " + dobj.getOID());
            }
            content.add(new XMLContentProvider("xml", dobj,
                                               getClass().getName()));
        } else if (type == ContentType.TEXT) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding Text Content for " + dobj.getOID());
            }
            content.add(new TextContentProvider("text", dobj,
                                                getClass().getName()));
			if (dobj instanceof ContentPage) {
				if (((ContentPage)dobj).indexAssetsWithPage()) {
        	
            		content.add(new TextContentProvider("text", dobj,
                                                AssetExtractor.class.getName()));
				}
			} else {
            content.add(new TextContentProvider("text", dobj,
                                                AssetExtractor.class.getName()));
            }
        } else if (type == ContentType.RAW) { 
        	// if assets are indexed separately for this type, don't index them here. If that is the case, 
        	// then the asset itself must have a separate metadataprovider registered to ensure that 
        	// the asset will be indexed
        	if (dobj instanceof ContentPage) {
        		if (((ContentPage)dobj).indexAssetsWithPage()) {
        			AssetExtractor ex = new AssetExtractor();
					ex.walk(dobj, AssetExtractor.class.getName());
					content.addAll(ex.getContent());
        		} // else do nothing
        	} else {
        		// by default, add content for these items - asset applications should register their own 
        		// metadataproviders to ensure they react correctly to the value of ContentPage.indexAssetsWithPage
        		//
        		// only the asset istelf knows how to refer to it's owning article in order to find out if it 
        		// should be indexed or not
            AssetExtractor ex = new AssetExtractor();
            ex.walk(dobj, AssetExtractor.class.getName());
            content.addAll(ex.getContent());
        }

		   
        }

        return (ContentProvider[])content.toArray(
            new ContentProvider[content.size()]);
    }
}
