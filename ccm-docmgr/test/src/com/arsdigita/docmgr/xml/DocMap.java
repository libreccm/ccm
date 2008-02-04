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
