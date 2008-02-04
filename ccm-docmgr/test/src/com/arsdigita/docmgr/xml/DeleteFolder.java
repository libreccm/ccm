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
