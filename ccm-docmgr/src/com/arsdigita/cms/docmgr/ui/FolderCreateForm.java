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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormValidationException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.ResourceExistsException;
import com.arsdigita.cms.docmgr.Util;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.permissions.PermissionService;

/**
 * This form serves to attach a child node folder to the selected folder
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FolderCreateForm extends Form   
        implements FormInitListener, FormValidationListener, FormProcessListener, DMConstants {
    public static final String versionId = 
        "$Id: //apps/docmgr-cms/dev/src/com/arsdigita/cms/docmgr/ui/FolderCreateForm.java#2 $" +
        "$Author: cwolfe $" +
        "$DateTime: 2003/08/05 10:40:23 $";

    private StringParameter m_FolderName;
    private StringParameter m_FolderDesc;
    private Tree m_tree;
    private BrowsePane m_parent;
    private Submit m_submit;

    public FolderCreateForm(BrowsePane parent, Tree tree) {
        super("CreateFolderForm", new ColumnPanel(2));

        m_parent = parent;
        m_tree = tree;

        add(FOLDER_NAME_LABEL);
        m_FolderName = new StringParameter(FOLDER_NAME);
        //m_FolderName.addParameterListener(new StringLengthValidationListener(200));
        TextField fnameEntry = new TextField(m_FolderName);
        //fnameEntry.addValidationListener(new NotEmptyValidationListener());
        add(fnameEntry);

        add(FOLDER_DESCRIPTION_LABEL);
        m_FolderDesc = new StringParameter(FOLDER_DESCRIPTION);
        //m_FolderDesc.addParameterListener(new StringLengthValidationListener(4000));
        TextArea textArea = new TextArea(m_FolderDesc);
        textArea.setRows(10);
        textArea.setCols(40);
        add(textArea);

        SimpleContainer sc = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel(FOLDER_SAVE);
        sc.add(m_submit);
        Submit cancel = new Submit(CANCEL);
        sc.add(cancel);

        add(new Label()); // spacer
        add(sc, ColumnPanel.LEFT); 
        
        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        BigDecimal createdFolderID = null;
        if (m_submit.isSelected(state)) {
        FormData data = e.getFormData();

        String fname = (String) data.get(FOLDER_NAME);
        String fdesc = (String) data.get(FOLDER_DESCRIPTION);

        String selKey = (String) m_tree.getSelectedKey(state);
        DocFolder p = null;
        if (selKey == null) {
            p = DMUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);

            try {
                p = new DocFolder(folderID);
            } catch (DataObjectNotFoundException nf) {
                // TODO show error lable instead
                throw new RuntimeException("Could not find folder");
            }
        }
        final DocFolder parent = p;

        // already validated fname
        final DocFolder folder = new DocFolder(fname, fdesc, parent);

        try {
            folder.save();
        } catch (ResourceExistsException ree) {
            throw new FormValidationException
                ("A folder with this name already exists.");
        }

        new KernelExcursion() {
            protected void excurse() {
                    //Party currentParty = Kernel.getContext().getParty();
                setParty(Kernel.getSystemParty());
                //PermissionService.grantPermission
                //    (new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                //                              folder,
                //                              currentParty));
                PermissionService.setContext(folder, parent);
            }
        }.run();
            createdFolderID = folder.getID();
        }

        if (m_parent != null) {
            m_parent.setCreatedFolderID(state, createdFolderID);
            m_parent.displayFolderContentPanel(state);
        }
    }

    /**
     * Validate that the folder we want to attach does not already
     * exist in the current parent folder.
     */
    public void validate(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        if (m_submit.isSelected(state)) {
        FormData data = event.getFormData();
        HttpServletRequest req = state.getRequest();

        String fname = (String) data.get(FOLDER_NAME);
            if (fname == null || fname.trim().length() == 0) {
                data.addError(FOLDER_NAME, "This parameter is required");
            }
            if (fname != null && fname.length() > 200) {
            	data.addError(FOLDER_NAME, "This parameter is too long. It must be fewer than 200 characters.");
            }

            String fDesc = (String) data.get(FOLDER_DESCRIPTION);
            if (fDesc != null && fDesc.length() > 4000) {
            	data.addError(FOLDER_DESCRIPTION, "This parameter is too long. It must be fewer than 4000 characters.");
            }

        DocFolder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DMUtils.getRootFolder(state);

            if (parent == null) {
                data.addError(FOLDER_NAME,
                              (String) FOLDER_PARENTNOTFOUND_ERROR
                              .localize(req));
            }
        } else {
            BigDecimal folderID = new BigDecimal(selKey);

            try {
                parent = new DocFolder(folderID);
            } catch(DataObjectNotFoundException nf) {
                data.addError(FOLDER_NAME,
                              (String) FOLDER_PARENTNOTFOUND_ERROR
                              .localize(req));
            }
        }

        try {
            if (parent != null) {
                parent.retrieveSubFolder(fname);
                data.addError
                    (FOLDER_NAME, (String) RESOURCE_EXISTS_ERROR.localize(req));
            }
        } catch(DataObjectNotFoundException e) {
            // ok if here
        } // catch(InvalidNameException ex) {
          //   data.addError(FOLDER_NAME, ex.getMessage());
          // }
    }
    }
}
