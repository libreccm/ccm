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
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.TypeChangeException;
import com.arsdigita.docmgr.Util;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;

/**
 * This component uploads a new version of a file.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class VersionUploadForm extends Form
    implements FormInitListener,
               FormProcessListener,
               FormValidationListener,
               DMConstants
{
    private static final String VERSION_UPLOAD_FORM =
        "file-version";
    private static final String VERSION_TRANSACTION_DESCRIPTION =
        "file-transaction-description";
    private static final String VERSION_FILE_UPLOAD =
        "file-version-upload";

    private static Logger s_log = Logger.getLogger(VersionUploadForm.class);
    private FileUpload m_fileUpload;
    private StringParameter m_versionDesc;
    private RequestLocal m_fileData;
    private FileInfoPropertiesPane m_parent;

    //This  const allows for less than 4k bytes of 2byte unicode chars, plus
    // a little wiggle room...
    private static int FOUR_K_CHAR_LIMIT = 1994;

    /**
     * Constructor with parent component.
     */

    public VersionUploadForm(FileInfoPropertiesPane parent) {
        super(VERSION_UPLOAD_FORM, new ColumnPanel(2));
        setMethod(Form.POST);
        setEncType("multipart/form-data");

        m_parent = parent;

        // initialize the file
        m_fileData = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
                    File file = null;
                    try {
                        file = new File(id);
                    } catch(DataObjectNotFoundException nfe) {
                        // ...
                    }
                    return file;
                }
            };

        m_fileUpload = new FileUpload(VERSION_FILE_UPLOAD);
        m_fileUpload.addValidationListener(new NotEmptyValidationListener());

        m_versionDesc = new StringParameter(VERSION_TRANSACTION_DESCRIPTION);

        add(new Label(FILE_NAME));
        add(makeFileLabel());

        add(new Label(FILE_SOURCE));
        add(m_fileUpload);

        add(new Label(FILE_VERSION_DESCRIPTION));
        TextArea fversionDesc = new TextArea(m_versionDesc);
        fversionDesc.setRows(10);
        fversionDesc.setCols(40);
        fversionDesc.addValidationListener(new NotEmptyValidationListener());
        add( fversionDesc);

        Submit submit = new Submit("file-version-upload");
        submit.setButtonLabel(FILE_SAVE);
        add(new Label()); // spacer

        SimpleContainer sc = new SimpleContainer();
        sc.add(submit);
        CancelButton cancel = new CancelButton(CANCEL);
        sc.add(cancel);

        add(sc, ColumnPanel.LEFT);

        addInitListener(this);
        addValidationListener(this);
        addProcessListener(this);
    }

    private File getFile(PageState s) {
        return (File)m_fileData.get(s);
    }

    private Label makeFileLabel() {
        Label label = new Label();
        label.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Label t= (Label) e.getTarget();
                    t.setLabel(getFile(state).getName());
                }
            });
        return label;
    }

    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

    }

    /**
     * Receive uploaded file and reset file content, mime type, and
     * description. Return to File properties screen.
     */
    public void process(FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();
        FormData data = e.getFormData();
        HttpServletRequest req = state.getRequest();

        String fpath = (String)data.get(VERSION_FILE_UPLOAD);
        fpath = DMUtils.extractFileName(fpath, state);

        java.io.File src = null;
        if (fpath != null && fpath.length() > 0) {
            src = ((MultipartHttpServletRequest)e.getPageState().getRequest())
                .getFile(VERSION_FILE_UPLOAD);
        }

        // Try to update the file in the database

        File file = getFile(state);
        // Annotate transaction description
        String vdesc = (String)data.get(VERSION_TRANSACTION_DESCRIPTION);
        //If version description string is over 4K in size, truncate...
        if(vdesc.length() > FOUR_K_CHAR_LIMIT)
            vdesc = vdesc.substring(0, FOUR_K_CHAR_LIMIT);

        try {
            file.saveNewRevision(src, fpath, vdesc, req);
        } catch (TypeChangeException ex) {
            throw new FormProcessException(ex.getMessage(), ex);
        }


        m_parent.displayPropertiesAndActions(state);
    }


    /**
     * Validate if user tries to upload a file with a different Mime type than
     * the original. This is not supported.
     */

    public void validate(FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();
        FormData data = e.getFormData();
        HttpServletRequest req = state.getRequest();

        String uploadedFileName = (String) data.get(VERSION_FILE_UPLOAD);
        String newType = File.guessContentType(uploadedFileName,req);
        String oldType = getFile(state).getContentType();

        if (!newType.equalsIgnoreCase(oldType)) {
            data.addError(VERSION_FILE_UPLOAD,
                          DIFFERENT_MIMETYPE_ERROR.localize(req).toString());
        }
    }
}
