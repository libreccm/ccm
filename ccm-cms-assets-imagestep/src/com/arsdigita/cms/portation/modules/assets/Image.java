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

import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.portation.conversion.NgCmsImageCollection;
import com.arsdigita.portation.Portable;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 4/6/18
 */
public class Image extends BinaryAsset implements Portable {
    private long width;
    private long height;
    @JsonIdentityReference(alwaysAsId = true)
    private LegalMetadata legalMetadata;

    /**
     * Constructor for the ng-object.
     *
     * @param trunkImage the trunk object
     */
    public Image(final ImageAsset trunkImage) {
        super(trunkImage);

        this.width = trunkImage.getWidth().longValue();
        this.height = trunkImage.getHeight().longValue();

        //this.legalMetaData

        NgCmsImageCollection.images.put(this.getObjectId(), this);
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(final long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(final long height) {
        this.height = height;
    }

    public LegalMetadata getLegalMetadata() {
        return legalMetadata;
    }

    public void setLegalMetadata(final LegalMetadata legalMetadata) {
        this.legalMetadata = legalMetadata;
    }
}
