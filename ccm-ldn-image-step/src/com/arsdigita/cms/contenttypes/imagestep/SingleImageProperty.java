/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.cms.contenttypes.imagestep;

import com.arsdigita.london.navigation.DataCollectionProperty;

import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.FilterFactory;

import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contenttypes.ItemImageAttachment;

/**
 * Add a single image with a specific context.
 */
public class SingleImageProperty implements DataCollectionProperty {
    private String m_context;

    private static final String IMAGE_ID =
        ItemImageAttachment.IMAGE_ATTACHMENTS + "." +
        ItemImageAttachment.IMAGE + "." +
        ReusableImageAsset.ID;

    private static final String WIDTH =
        ItemImageAttachment.IMAGE_ATTACHMENTS + "." +
        ItemImageAttachment.IMAGE + "." +
        ReusableImageAsset.WIDTH;

    private static final String HEIGHT =
        ItemImageAttachment.IMAGE_ATTACHMENTS + "." +
        ItemImageAttachment.IMAGE + "." +
        ReusableImageAsset.HEIGHT;

    private static final String CAPTION =
        ItemImageAttachment.IMAGE_ATTACHMENTS + "." +
        ItemImageAttachment.CAPTION;

    private static final String CONTEXT =
        ItemImageAttachment.IMAGE_ATTACHMENTS + "." +
        ItemImageAttachment.USE_CONTEXT;

    /**
     * Create a SingleImageProperty which will pull out a single
     * ItemImageAttachment with the given use context.
     */
    public SingleImageProperty( String context ) {
        m_context = context;
    }

    public void addProperty( DataCollection dc ) {
        dc.addPath( IMAGE_ID );
        dc.addPath( WIDTH );
        dc.addPath( HEIGHT );
        dc.addPath( CAPTION );
        dc.addPath( CONTEXT );

        FilterFactory ff = dc.getFilterFactory();
        CompoundFilter or = ff.or();

        or.addFilter( ff.equals( CONTEXT, m_context ) );
        or.addFilter( ff.equals( IMAGE_ID, null ) );

        dc.addFilter( or );
    }
}
