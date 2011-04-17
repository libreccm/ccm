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

import org.jdom.Element;

public class FolderElement extends Element {
    public FolderElement() {
        super("folder", Namespaces.DOCS);
    }

    public String getFolderName() {
        Element nameElem = getChild("name", Namespaces.DOCS);
        String name = nameElem.getTextTrim();
        return name;
    }
    public String getDescription() {
        Element descElem = getChild("description", Namespaces.DOCS);
        String description = descElem.getTextTrim();
        return description;
    }

    public String getID() {
        String id = getAttributeValue("folder_id");
        return id;
    }

    public String getParentID() {
        String id = getAttributeValue("parent_id");
        return id;
    }
}
