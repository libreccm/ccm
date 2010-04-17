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

/**
 * An {@link ImageBrowserModel} which is always empty
 *
 * @author Stanislav Freidin
 * @version $Id: EmptyImageBrowserModel.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class EmptyImageBrowserModel implements ImageBrowserModel {

    /**
     * Construct a new EmptyImageBrowserModel
     */
    public EmptyImageBrowserModel() {
    }

    public boolean nextRow() {
        return false;
    }

    public ImageAsset getImageAsset() {
        throw new IllegalStateException(
                                        "Model is empty but getImageAsset() was called"
                                        );
    }

    public String getActionLabel() {
        throw new IllegalStateException(
                                        "Model is empty but getImageAsset() was called"
                                        );
    }
}
