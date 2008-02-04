/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this fileare subject to the CCM Public
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

package com.arsdigita.cms.docmgr.ui.tree;

import java.util.Iterator;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.docmgr.DocFolder;

public class DocFolderIterator implements Iterator {
    
    private Folder.ItemCollection m_collection;

    public DocFolderIterator(Folder.ItemCollection collection ) {
            m_collection  = collection ;
    }
    
    public boolean hasNext() {
        if(!m_collection.isEmpty()) {
            
            if(m_collection.next()){
                return true;
            }
            m_collection.close();
        }
        return false;
    }

    public Object next() {
        DocFolder df = (DocFolder) m_collection.getContentItem();
        return new DocFolderTreeNode(df);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}


