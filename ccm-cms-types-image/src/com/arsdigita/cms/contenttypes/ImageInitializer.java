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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import org.apache.log4j.Logger;

/**
 * Initializes the image content type.
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map).
 *
 * @author Sören Bernstein (sbernstein@zes.uni-bremen.de);
 */
public class ImageInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(ImageInitializer.class);

    public ImageInitializer() {
        super("ccm-cms-types-image.pdl.mf", ContentPage.BASE_DATA_OBJECT_TYPE);
    }
}
