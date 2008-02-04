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
 * The <code>ImageBrowserModel</code> is an abstraction used by the
 * {@link ImageBrowser} class in order to display a table of images.
 */

public interface ImageBrowserModel {

    public static final String versionId = "$Id: ImageBrowserModel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    /**
     * Advance to the next row, if possible
     *
     * @return true if the current row is valid; false if there
     *  are no more rows
     */
    boolean nextRow();

    /**
     * @return the current image asset
     */
    ImageAsset getImageAsset();

    /**
     * @return the label for the action link
     */
    public String getActionLabel();

}
