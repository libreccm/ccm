/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
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
