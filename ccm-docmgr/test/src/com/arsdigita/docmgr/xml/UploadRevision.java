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
import com.arsdigita.docmgr.File;

public class UploadRevision extends PermissionsAction {
    public UploadRevision() {
        super("upload_revision", Namespaces.DOCS);
    }

    public void doPermissionTest() throws Exception {
        FileElement elem = (FileElement) getChild("file", Namespaces.DOCS);
        java.io.File diskFile = new java.io.File(elem.getFullPath());

        File file = DocMap.instance().getFile(elem.getFileID());
        file.saveNewRevision(diskFile, 
                             diskFile.getName(), 
                             elem.getDescription(), 
                             null);
    }
}
