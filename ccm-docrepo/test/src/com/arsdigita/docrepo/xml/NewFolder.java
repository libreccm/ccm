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
