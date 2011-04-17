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

import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.docmgr.TestRepository;
import com.arsdigita.persistence.OID;
import com.arsdigita.xmlutil.TestResource;
import com.arsdigita.xmlutil.ResourceRegistry;

import java.util.HashMap;
import java.util.Map;

public class DocMap implements TestResource {

    public static DocMap instance() {
        return s_instance;
    }

    public void cleanUp() {
        m_folderMap.clear();
    }

    public void addFolder(String folderID, Folder folder) throws Exception {
        m_folderMap.put(folderID, folder.getOID());
    }

    public  void removeFolder(String folderID) {
        m_folderMap.remove(folderID);
    }
    public  Folder getFolder(String folderID) throws Exception {
        Folder folder;
        if ( folderID.equals("root") ) {
            Repository docManager = TestRepository.get();
            folder = docManager.getRoot();

        } else {
            OID id = (OID) m_folderMap.get(folderID);
            folder = new Folder(id);
        }
        return folder;
    }

    public void addFile(String fileID, File file) throws Exception {
        m_fileMap.put(fileID, file.getOID());
    }

    public  void removeFile(String fileID) {
        m_fileMap.remove(fileID);
    }
    public  File getFile(String fileID) throws Exception {

        OID id = (OID) m_fileMap.get(fileID);
        File file = new File(id);
        return file;
    }

    private DocMap() {
        ResourceRegistry.instance().addResource(this);
    }
    private Map m_folderMap = new HashMap();
    private Map m_fileMap = new HashMap();

    private static DocMap s_instance = new DocMap();
}
