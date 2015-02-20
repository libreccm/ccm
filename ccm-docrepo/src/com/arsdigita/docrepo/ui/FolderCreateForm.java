/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docrepo.ui;


//import com.arsdigita.docrepo.util.GlobalizationUtil;

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
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
//import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.docrepo.Util;

import com.arsdigita.docrepo.Folder;
import com.arsdigita.docrepo.ResourceExistsException;
import com.arsdigita.docrepo.InvalidNameException;
import com.arsdigita.docrepo.util.GlobalizationUtil;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

/**
 * This form serves to attach a child node folder to the selected folder
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @version $Id: FolderCreateForm.java  pboy $
 */
class FolderCreateForm extends Form   
                       implements FormInitListener, FormValidationListener,
                                  FormProcessListener, DRConstants {

    private StringParameter m_FolderName;
    private StringParameter m_FolderDesc;
    private Tree m_tree;
    private BrowsePane m_parent;

    public FolderCreateForm(BrowsePane parent, Tree tree) {
        super("CreateFolderForm", new ColumnPanel(2));

        m_parent = parent;
        m_tree = tree;

        add(FOLDER_NAME_LABEL);
        m_FolderName = new StringParameter(FOLDER_NAME);
        m_FolderName.addParameterListener
            (new StringLengthValidationListener(200));
        TextField fnameEntry = new TextField(m_FolderName);
        fnameEntry.addValidationListener(new NotEmptyValidationListener());
        add(fnameEntry);

        add(FOLDER_DESCRIPTION_LABEL);
        m_FolderDesc = new StringParameter(FOLDER_DESCRIPTION);
        m_FolderDesc.addParameterListener
            (new StringLengthValidationListener(4000));
        TextArea textArea = new TextArea(m_FolderDesc);
        textArea.setRows(10);
        textArea.setCols(40);
        add(textArea);

        SimpleContainer sc = new SimpleContainer();
        Submit submit = new Submit("submit");
        submit.setButtonLabel(FOLDER_SAVE);
        sc.add(submit);
        CancelButton cancel = new CancelButton(CANCEL);
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
        FormData data = e.getFormData();

        String fname = (String) data.get(FOLDER_NAME);
        String fdesc = (String) data.get(FOLDER_DESCRIPTION);

        String selKey = (String) m_tree.getSelectedKey(state);
        Folder parent = null;
        if (selKey == null) {
            parent = DRUtils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);

            try {
                parent = new Folder(folderID);
            } catch (DataObjectNotFoundException nf) {
                // TODO show error lable instead
                throw new RuntimeException("Could not find folder");
            }
        }

        // already validated fname
        final Folder folder = new Folder(fname, fdesc, parent);

        try {
            folder.save();
        } catch (ResourceExistsException ree) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "ui.folder.name_not_unique"));
        }

        new KernelExcursion() {
            protected void excurse() {
                Party currentParty = Kernel.getContext().getParty();
                setParty(Kernel.getSystemParty());
                PermissionService.grantPermission
                    (new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                              folder,
                                              currentParty));
            }
        }.run();

        if (m_parent != null) {
            m_parent.displayFolderContentPanel(state);
        }
    }

    /**
     * Validate that the folder we want to attach does not already
     * exist in the current parent folder.
     */
    public void validate(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        HttpServletRequest req = state.getRequest();

        String fname = (String) data.get(FOLDER_NAME);

        Folder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = DRUtils.getRootFolder(state);

            if (parent == null) {
                data.addError(FOLDER_NAME,
                              (String) FOLDER_PARENTNOTFOUND_ERROR
                              .localize(req));
            }
        } else {
            BigDecimal folderID = new BigDecimal(selKey);

            try {
                parent = new Folder(folderID);
            } catch(DataObjectNotFoundException nf) {
                data.addError(FOLDER_NAME,
                              (String) FOLDER_PARENTNOTFOUND_ERROR
                              .localize(req));
            }
        }

        try {
            if (parent != null) {
                parent.retrieveFolder(fname);
                data.addError
                    (FOLDER_NAME, (String) RESOURCE_EXISTS_ERROR.localize(req));
            }
        } catch(DataObjectNotFoundException e) {
            // ok if here
        } catch(InvalidNameException ex) {
            data.addError(FOLDER_NAME, ex.getMessage());
        }
    }

}
