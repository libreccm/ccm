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
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

import com.arsdigita.cms.search.ContentPageMetadataProvider;
import com.arsdigita.domain.DomainObject;

/**
 * This class is an implementation of the Search metadata provider that 
 * provides a more meaningful title for a multipart article section
 *
 * @author Chris Gilbert
 * @version $Id: ArticleSectionMetadataProvider.java,v 1.2 2006/05/16 15:23:21 cgyg9330 Exp $
 */
public class ArticleSectionMetadataProvider 
extends ContentPageMetadataProvider {

    private static final Logger s_log =
        Logger.getLogger(ArticleSectionMetadataProvider.class);

    @Override
    public String getTitle(DomainObject dobj) {
        ArticleSection section = (ArticleSection)dobj;
        StringBuffer title = new StringBuffer();
        String pageTitle = section.getPageTitle();
        if (MultiPartArticle.getConfig().includeParentTitle()) {
        	title.append(section.getMPArticle().getTitle());
        	if (pageTitle != null) {
            	title.append(": ");
            }
        }
        if (pageTitle != null) {
        title.append(pageTitle);
        }
        
        return title.toString();
    }
}
