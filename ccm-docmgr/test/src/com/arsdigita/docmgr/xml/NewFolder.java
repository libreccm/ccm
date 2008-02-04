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

public class NewFolder extends PermissionsAction {
    public NewFolder() {
        super("new_folder", Namespaces.DOCS);
    }

    public void doPermissionTest() throws Exception {
        FolderElement elem = (FolderElement) getChild("folder", Namespaces.DOCS);
        String parentID = elem.getParentID();
        Folder parent = getRootFolder(parentID);

        Folder newFolder = new Folder(elem.getFolderName(), elem.getDescription(), parent);
        newFolder.save();

        DocMap.instance().addFolder(elem.getID(), newFolder);
    }

    private Folder getRootFolder(String parentID) throws Exception {
        Folder parent;
        if (null == parentID) {
            parentID = "root";
        }

        parent = DocMap.instance().getFolder(parentID);
        return parent;
    }
}
