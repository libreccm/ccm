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
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ImageAssetCollection;

/**
 * An {@link ImageBrowserModel} which displays all images in the
 * given ImageAssetCollection
 *
 * @author Stanislav Freidin
 * @version $Id: DefaultImageBrowserModel.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class DefaultImageBrowserModel implements ImageBrowserModel {

    private ImageAssetCollection m_images;

    /**
     * Construct a new DefaultImageBrowserModel
     *
     * @param images The {@link ImageAssetCollection} whose images will
     *   be displayed
     */
    public DefaultImageBrowserModel(ImageAssetCollection images) {
        m_images = images;
    }

    /**
     * Advance to the next row, if possible
     *
     * @return true if the current row is valid; false if there
     *  are no more rows
     */
    public boolean nextRow() {
        return m_images.next();
    }

    /**
     * @return the current image asset
     */
    public ImageAsset getImageAsset() {
        return m_images.getImage();
    }

    /**
     * @return the label for the action link
     */
    public String getActionLabel() {
        return "select";
    }
}
