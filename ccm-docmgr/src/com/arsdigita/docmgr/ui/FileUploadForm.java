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

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.docmgr.ContentTypeException;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.InvalidNameException;
import com.arsdigita.docmgr.Util;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * Form to upload and submit a file to the document repository.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
public class FileUploadForm extends Form
        implements FormInitListener, FormValidationListener,
                   FormProcessListener, DMConstants {

    private static final Logger s_log = Logger.getLogger(FileUploadForm.class);

    // Form constants
    private final static String FILE_UPLOAD = "file-upload";
    private final static String FILE_UPLOAD_FORM = "file-upload-form";
    private final static String FILE_UPLOAD_INPUT_DESCRIPTION = "file-description";

    private FileUpload m_fileUpload;

    private StringParameter m_FileDesc;
    private Tree m_tree;
    private BrowsePane m_parent;

    /**
     * Constructor
     */
    public FileUploadForm(BrowsePane parent, Tree tree) {
        this(parent, tree, true);
    }

    public FileUploadForm(BrowsePane parent, Tree tree, boolean initListeners) {
        super(FILE_UPLOAD_FORM, new ColumnPanel(2));

        m_parent = parent;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        m_tree = tree;

        m_fileUpload = new FileUpload(FILE_UPLOAD);

        m_FileDesc = new StringParameter(FILE_UPLOAD_INPUT_DESCRIPTION);
        m_FileDesc.addParameterListener
            (new StringLengthValidationListener(4000));

        add(new Label(FILE_UPLOAD_ADD_FILE));
        add(m_fileUpload);

        add(new Label(FILE_DESCRIPTION));
        TextArea textArea = new TextArea(m_FileDesc);
        textArea.setRows(10);
        textArea.setCols(40);
        add(textArea);

        SimpleContainer sc = new SimpleContainer();
        Submit submit = new Submit("submit");
        submit.setButtonLabel(FILE_SUBMIT);
        sc.add(submit);
        CancelButton cancel = new CancelButton(CANCEL);
        sc.add(cancel);

        add(new Label()); // spacer
        add(sc, ColumnPanel.LEFT);

        if (initListeners) {
            addInitListener(this);
            addProcessListener(this);
            addValidationListener(this);
        }
    }

    /**
     * Post the file to a temporary file on the server and
     * insert it into the database
     */
    protected BigDecimal insertFile(FormSectionEvent e)
            throws FormProcessException {
        s_log.debug("Inserting a file into the database");

        PageState state = e.getPageState();
        FormData data = e.getFormData();
        final HttpServletRequest req = state.getRequest();

        String fname = getFileName(e);
        String fdesc = (String) data.get(FILE_UPLOAD_INPUT_DESCRIPTION);
        String fpath = (String) data.get(FILE_UPLOAD);

        if (s_log.isDebugEnabled()) {
            s_log.debug("getFileName() -> '" + fname + "'");
            s_log.debug("description == '" + fdesc + "'");
            s_log.debug("path == '" + fpath + "'");
        }

        java.io.File src = null;

        if (fpath != null && fpath.length() > 0) {
            HttpServletRequest mreq = e.getPageState().getRequest();

            Assert.isTrue(mreq instanceof MultipartHttpServletRequest,
                              "I got a " + mreq + " when I was " +
                              "expecting a MultipartHttpServletRequest");

            src = ((MultipartHttpServletRequest) mreq).getFile(FILE_UPLOAD);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("file == '" + src + "'");
        }

        Folder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                parent = new Folder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException
                    ((String) FOLDER_PARENTNOTFOUND_ERROR.localize(req));
            }
        }

        // insert the file in the data base below parent

        final File f1 = new File(parent);
        try {
            f1.setContent(src, fname, fdesc);
            f1.initializeContentType(req);

            // annotate first file upload as initial version
            f1.setDescription(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION 
                .localize(req) 
                .toString());

            f1.applyTag(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION 
                .localize(req) 
                .toString());

            f1.save();
        } catch (ContentTypeException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        new KernelExcursion() {
            protected void excurse() {
                Party currentParty = Kernel.getContext().getParty();
                setParty(Kernel.getSystemParty());
                PermissionService.grantPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                                           f1,
                                                                           currentParty));
                Application app = Web.getContext().getApplication();
                Assert.exists(app, Application.class);
                PermissionService.setContext(f1, app);
            }}.run();

        return f1.getID();
    }

    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

    }

    /**
     * Post the file to a temporary file on the server and
     * insert it into the database
     */
    public void process(FormSectionEvent e)
            throws FormProcessException {
        s_log.debug("Processing form submission");

        insertFile(e);

        if (m_parent != null) {
            m_parent.displayFolderContentPanel(e.getPageState());
        }

    }

    /**
     * Gets either the file name from the widget
     * or takes the filename from the upload
     * widget in this order.
     */
    protected String getFileName(FormSectionEvent e) {
        FormData data = e.getFormData();
        String filename = (String) data.get(FILE_UPLOAD);
        return DMUtils.extractFileName(filename, e.getPageState());
    }


    /**
     * Verify that the parent folder exists and does not contain any
     * other files or sub folders with the same name as the file being
     * uploaded.
     */

    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        HttpServletRequest req = state.getRequest();

        String fname = DMUtils.extractFileName(getFileName(e), state);

        // XXX Not localized as the other errors are.
        if (fname.length() > 200) {
            data.addError
                (FILE_UPLOAD,
                 "This filename is too long.  It must be fewer than 200 " +
                 "characters.");
        }

        Folder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                parent = new Folder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException(FOLDER_PARENTNOTFOUND_ERROR
                                                  .localize(req).toString());
            }
        }

        try {
            parent.getResourceID(fname);
            data.addError(FILE_UPLOAD,
                          RESOURCE_EXISTS_ERROR
                          .localize(req).toString());
        } catch(DataObjectNotFoundException nf) {
            // ok here
        } catch(InvalidNameException ex) {
            data.addError(FILE_UPLOAD,
                          ex.getMessage());
        }
    }
}
