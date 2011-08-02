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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * This class is an implementation of the Search metadata provider that uses the
 * <code>DomainObjectTextRenderer</code> to extract search content for any
 * subclass of {@link com.arsdigita.cms.ContentPage}.
 *
 * @author <a href="mailto:berrange@redhat.com">Daniel Berrange</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 * @version $Id: ContentPageMetadataProvider.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class ContentPageMetadataProvider extends ContentItemMetadataProvider {

    public static final String ADAPTER_CONTEXT =
                               ContentPageMetadataProvider.class.getName();
    private static final Logger s_log =
                                Logger.getLogger(
            ContentPageMetadataProvider.class);

    public String getTitle(DomainObject dobj) {
        if (dobj instanceof ContentPage) {
            ContentPage item = (ContentPage) dobj;
            s_log.debug(String.format("getting title of item with oid '%s'",
                    item.getOID().toString()));
            s_log.debug(String.format("and name '%s'", item.getName()));
            s_log.debug(String.format("and title '%s'", item.getTitle()));
            String title = item.getTitle();
            if (StringUtils.emptyString(title)) {
                throw new IllegalArgumentException(
                        "ContentPage must have non-blank title!");
            }
            return title;
        } else {
            // this is not pretty,
            // but fails more gracefully for items which are not contentpages
            s_log.warn("Item is not a ContentPage.");
            return "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        }
    }

    public String getSummary(DomainObject dobj) {
        if (dobj instanceof ContentPage) {
            ContentPage item = (ContentPage) dobj;
            return item.getSearchSummary();
        } else {
            return "";
        }
    }

    public String getContentSection(DomainObject dobj) {
        String sectionName = "";
        if (dobj instanceof ContentPage) {
            ContentPage item = (ContentPage) dobj;
            ContentSection section = item.getContentSection();
            if (section != null) {
                sectionName = section.getName();
            }
        }
        return sectionName;
    }
}
