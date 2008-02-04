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

import com.arsdigita.bebop.PageState;
import com.arsdigita.util.Lockable;

/**
 * Constructs a new {@link ImageBrowserModel}
 * This class will be supplied to the {@link ImageBrowser}
 * class in order to provide it with the model.
 */
public interface ImageBrowserModelBuilder extends Lockable  {

    public static final String versionId = "$Id: ImageBrowserModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    /**
     * Construct a new ImageBrowserModel
     *
     * @param browser The {@link ImageBrowser}
     * @param state The page state
     * @return An {@link ImageBrowserModel}
     */
    ImageBrowserModel makeModel(ImageBrowser browser, PageState state);

}
