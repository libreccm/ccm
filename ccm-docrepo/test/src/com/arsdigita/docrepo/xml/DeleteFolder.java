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
import com.arsdigita.kernel.Party;
import org.apache.log4j.Logger;

public class DeleteFolder extends PermissionsAction {
    private static Logger s_log = Logger.getLogger(DeleteFolder.class);

    public DeleteFolder() {
        super("delete_folder", Namespaces.DOCS);
    }

    public void doPermissionTest() throws Exception {
        String id = getFolderID();
        Folder folder = DocMap.instance().getFolder(id);
        Party user = getUser();
        s_log.warn("User: " + user.getPrimaryEmail() + " is deleting folder: " + folder.getName() + " with id: " + folder.getOID());

        folder.delete();
        s_log.warn("Deleted");
        DocMap.instance().removeFolder(id);
    }

    public String getFolderID() {
        String id = getAttributeValue("folder_id");
        return id;
    }
}
