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

import com.arsdigita.docmgr.*;
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

        final String mimeType = Util.guessContentType(elem.getFileName(), null);
        final File f1 = new File(parent);
        try {
            f1.setContent(diskFile,
                    elem.getFileName(),
                    elem.getDescription(),
                    mimeType);
            f1.save();
        } catch (TypeChangeException ex) {
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
