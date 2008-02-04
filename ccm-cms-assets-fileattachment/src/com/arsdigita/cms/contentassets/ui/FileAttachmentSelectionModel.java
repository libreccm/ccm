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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

/**
 * SelectionModel to track the current Link object for view/edit purposes.
 */
public class FileAttachmentSelectionModel extends ACSObjectSelectionModel {

    public FileAttachmentSelectionModel(BigDecimalParameter param) {
        super(FileAttachment.class.getName(),
              FileAttachment.BASE_DATA_OBJECT_TYPE,
              param);
    }

    /**
     * Construct a new <code>FileAttachmentSelectionModel</code>
     *
     * @param itemClass The name of the Java class which represents
     *    the content item. Must be a subclass of Link. In
     *    addition, the class must have a constructor with a single
     *    OID parameter.
     * @param objectType The name of the persistence metadata object type
     *    which represents the content item. In practice, will often be
     *    the same as the itemClass.
     * @param parameter The state parameter which should be used by this item
     */
    public FileAttachmentSelectionModel(String itemClass, String objectType,
                              BigDecimalParameter parameter) {
        super(itemClass, objectType, parameter);
    }

    /**
     * Returns the currently-selected Link
     *
     * @param state the PageState for the current request.
     * @return The current Link
     */
    public FileAttachment getSelectedFileAttachment(PageState state) {
        return (FileAttachment)getSelectedObject(state);
    }
}
