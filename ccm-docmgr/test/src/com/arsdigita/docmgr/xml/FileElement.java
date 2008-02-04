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
import org.jdom.Element;
import com.arsdigita.docmgr.Folder;

public class FileElement extends Element {
    public FileElement() {
        super("file", Namespaces.DOCS);
    }

    public Folder getParentFolder() throws Exception {
        String parentID = getAttributeValue("folder_id");

        if (null == parentID) {
            parentID = "root";
        }

        Folder parent = DocMap.instance().getFolder(parentID);
        return parent;
    }

    public String getFileID() {
        String id = getAttributeValue("file_id");
        return id;

    }
    public String getFileName() {
        String name = getChild("file_name", Namespaces.DOCS).getTextTrim();
        return name;
    }

    public String getFilePath() {
        String path = getChild("file_path", Namespaces.DOCS).getTextTrim();
        return path;
    }

    public String getFullPath() {
        return getFilePath() + "/" + getFileName();
    }
    public String getDescription() {
        String description = getChild("description",Namespaces.DOCS).getTextTrim();
        return description;
    }

}
