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
package com.arsdigita.docmgr.xml;

import com.arsdigita.xmlutil.PermissionsAction;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.util.Assert;

public class MoveFolder extends PermissionsAction {
    public MoveFolder() {
        super("move_folder", Namespaces.DOCS);
    }

    public void doPermissionTest() throws Exception {
        Folder destination = getDestinationFolder();
        Folder movingFolder = getFolderToMove();

        movingFolder.setParent(destination);
        movingFolder.save();
        Assert.equal(destination, movingFolder.getParent());
    }

    private Folder getDestinationFolder() throws Exception {
        String id = getAttributeValue("dest_folder_id");
        Folder folder = DocMap.instance().getFolder(id);
        return folder;
    }

    private Folder getFolderToMove() throws Exception {
        String id = getAttributeValue("folder_id");
        Folder folder = DocMap.instance().getFolder(id);
        return folder;
    }
}
