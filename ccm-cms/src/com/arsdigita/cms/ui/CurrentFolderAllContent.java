/*
 * Copyright (C) 2006 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.cms.ui;

import org.apache.log4j.Logger;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.Folder;


/**
 * Implementation of FolderContent that automatically sets the
 * content section and folder to the current ones as obtained
 * by <code>CMS.getContext()</code>, and which pulls out all
 * items, even unpublished ones.
 * 
 */
public class CurrentFolderAllContent extends CurrentFolderContent {

    private static Logger log = Logger.getLogger(CurrentFolderAllContent.class);

    public CurrentFolderAllContent() {
        super();
        setLiveOnly(false);
        setFolder((Folder) CMS.getContext().getContentItem().getDraftVersion());
    }
}
