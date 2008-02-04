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

import com.arsdigita.docmgr.ContentTypeException;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.TestRepository;
import com.arsdigita.docmgr.ui.DMConstants;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.xmlutil.PermissionsAction;
import org.apache.log4j.Logger;

import java.util.Locale;
public class NewFile extends PermissionsAction {
    private static Logger s_log = Logger.getLogger(NewFile.class);

    public NewFile() {
        super("new_file", Namespaces.DOCS);
        s_log.warn("Creating NewFile Element!!!!");
    }

    public void doPermissionTest() throws Exception {
        s_log.warn("Creating file!");
        File file = makeFile();
        s_log.warn("created file: " + file.getName() + " with mime-type: " + file.getContentType());
    }

    private File makeFile() throws Exception {
        FileElement elem = (FileElement) getChild("file", Namespaces.DOCS);
        Folder parent = elem.getParentFolder();
        java.io.File diskFile = new java.io.File(elem.getFullPath());

        final File f1 = new File(parent);
        try {
            f1.setContent(diskFile, elem.getFileName(), elem.getDescription());
            f1.initializeContentType(null);
            f1.save();
        } catch (ContentTypeException ex) {
            throw new UncheckedWrapperException(ex.getMessage(), ex);
        }

        new KernelExcursion() {
                protected void excurse() {
                    Party currentParty = Kernel.getContext().getParty();
                    setParty(Kernel.getSystemParty());
                    PermissionService.grantPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                                               f1,
                                                                               currentParty));
                    PermissionService.setContext(f1, TestRepository.get());
                }}.run();

        // annotate first file upload as initial version

        TransactionCollection tc = f1.getTransactions(false);
        if (tc.next()) {
            Transaction t1 = tc.getTransaction();
            //            t1.setDescription(DMConstants.FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION.localize(Locale.ENGLISH)
            //                              .toString());
            //            t1.save();
        }
        tc.close();
        DocMap.instance().addFile(elem.getFileID(), f1);

        return f1;
    }


}
