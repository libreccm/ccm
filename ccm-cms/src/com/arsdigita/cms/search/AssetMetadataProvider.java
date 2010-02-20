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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * This class is an implementation of the Search metadata provider for any
 * subclass of {@link com.arsdigita.cms.Asset}.
 *
 * @author <a href="mailto:berrange@redhat.com">Daniel Berrange</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 * @version $Id: AssetMetadataProvider.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class AssetMetadataProvider extends ContentItemMetadataProvider {

    public static final String ADAPTER_CONTEXT =
        AssetMetadataProvider.class.getName();
    
    private static final Logger s_log = 
        Logger.getLogger(AssetMetadataProvider.class);

    public String getTitle(DomainObject dobj) {
        Asset item = (Asset)dobj;
        String title = item.getName();
        if (StringUtils.emptyString(title)) {
            throw new IllegalArgumentException(
                "Asset must have non-blank name!"
            );
        }
        return title;
    }

    public String getSummary(DomainObject dobj) {
        Asset item = (Asset)dobj;
        return item.getDescription();
    }

    /* Assets are not assigned to a particular content section. */
    public String getContentSection(DomainObject dobj) {
	return "";
    }
}
