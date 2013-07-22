/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.cms.RelationAttributeCollection;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeValuesCollection extends RelationAttributeCollection {
    
    public static final String ATTRIBUTE_NAME = "PublicationTypeAssetTypeValues";
    
    public PublicationTypeValuesCollection() {
        super(ATTRIBUTE_NAME);
    }
    
    public PublicationTypeValuesCollection(final String key) {
        super(ATTRIBUTE_NAME, key);
    }
    
    public PublicationTypeValuesCollection(final DataCollection dataCollection) {
        super(dataCollection);
    }
    
}
