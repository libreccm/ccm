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

package com.arsdigita.cms.docmgr.ui;

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
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.Util;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.util.Assert;
import com.arsdigita.versioning.Versions;

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
     * @param parent
     * @param tree
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
        s_log.debug("Inserting a file");

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

        DocFolder p = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            p = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                p = new DocFolder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException
                    ((String) FOLDER_PARENTNOTFOUND_ERROR.localize(req));
            }
        }
        final DocFolder parent = p;

        // insert the file in the data base below parent

        final Document f1 = new Document();
        try {
            f1.setName(fname);
            f1.setTitle(fname);
            f1.setDescription(fdesc);
            f1.setRepository(DocFolder.getRepository(p));
            f1.setLanguage("en");
            FileAsset fa = new FileAsset();
            fa.loadFromFile(fname,src,"txt");
            f1.setFile(fa);

            // annotate first file upload as initial version
            Versions.tag(f1.getOID(),(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION 
                .localize(req) 
                .toString()));

            f1.setLastModifiedLocal(f1.getLastModifiedDate());

            final ContentBundle bundle = new ContentBundle(f1);
            bundle.setParent(parent);
            bundle.setContentSection(parent.getContentSection());
            bundle.save();

            //f1.save();
            new KernelExcursion() {
                protected void excurse() {
                    Party currentParty = Kernel.getContext().getParty();
                    setParty(Kernel.getSystemParty());
                    //PermissionService.grantPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                    //                                                           f1,
                    //                                                           currentParty));
                    //Application app = Web.getWebContext().getApplication();
                    //Assert.exists(app, Application.class);
                    //PermissionService.setContext(f1, app);
                    PermissionService.setContext(bundle,parent);
                }}.run();
            
            return f1.getID();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            throw new FormProcessException(ex);
        }

            //} catch (ContentTypeException ex) {
         //   throw new RuntimeException(ex.getMessage());
         //}
    }

    @Override
    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

    }

    /**
     * Post the file to a temporary file on the server and
     * insert it into the database
     * @param e
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
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
     * @param e
     * @return 
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

        DocFolder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                parent = new DocFolder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException(FOLDER_PARENTNOTFOUND_ERROR
                                                  .localize(req).toString());
            }
        }

        try {
            parent.retrieveSubResource(fname);
            data.addError(FILE_UPLOAD,
                          RESOURCE_EXISTS_ERROR
                          .localize(req).toString());
        } catch(DataObjectNotFoundException nf) {
            // ok here
        }// catch(InvalidNameException ex) {
         //   data.addError(FILE_UPLOAD,
         //                 ex.getMessage());
         //}
    }
}
