/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.modules.assets;

import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.portation.convertion.NgCmsCollection;
import com.arsdigita.portation.Portable;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/6/18
 */
public class FileAsset extends BinaryAsset implements Portable {

    /**
     * Constructor for the ng-object.
     *
     * @param trunkFileAsset the trunk object
     */
    public FileAsset(final FileAttachment trunkFileAsset) {
        super(trunkFileAsset);

        NgCmsCollection.fileAssets.put(this.getObjectId(), this);
    }
}
