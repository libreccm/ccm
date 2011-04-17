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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.docrepo.File;
import com.arsdigita.docrepo.Folder;
import com.arsdigita.docrepo.InvalidNameException;
import com.arsdigita.docrepo.Util;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
//import com.arsdigita.versioning.Transaction;
//import com.arsdigita.versioning.TransactionCollection;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * This component allows to change the file name and the
 * description of a file. It also serves to associate
 * keywords to a file (knowledge object).
 *
 *  @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FileEditForm extends Form
    implements FormValidationListener,
               FormProcessListener,
               FormInitListener,
               DRConstants
{

    private final static String FILE_EDIT = "file-edit";
    private final static String FILE_EDIT_FNAME = "file-edit-name";
    private final static String FILE_EDIT_DESCRIPTION = "file-edit-description";

    private StringParameter m_FileName;
    private StringParameter m_FileDesc;
    private FileInfoPropertiesPane m_parent;

    /**
     * Constructor
     */

    public FileEditForm(FileInfoPropertiesPane parent) {
        super(FILE_EDIT, new ColumnPanel(2));

        m_parent = parent;

        m_FileName = new StringParameter(FILE_EDIT_FNAME);
        m_FileDesc = new StringParameter(FILE_EDIT_DESCRIPTION);

        add(new Label(FILE_NAME_REQUIRED));
        TextField fnameEntry = new TextField(m_FileName);
        fnameEntry.addValidationListener(new NotEmptyValidationListener());
        add(fnameEntry);

        add(new Label(FILE_DESCRIPTION));
        TextArea descArea = new TextArea(m_FileDesc);
        descArea.setRows(10);
        descArea.setCols(40);
        add(descArea);

        Submit submit = new Submit("file-edit-save");
        submit.setButtonLabel(FILE_SAVE);
        add(new Label()); // spacer

        SimpleContainer sc = new SimpleContainer();
        sc.add(submit);
        sc.add(new CancelButton(CANCEL));

        add(sc);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Initializer to pre-fill name and description
     */
    public void init(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

        FormData data = e.getFormData();

        BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
        File file = DRUtils.getFile(id);

        data.put(FILE_EDIT_FNAME, file.getName());
        data.put(FILE_EDIT_DESCRIPTION, file.getDescription());
    }

    /**
     * read form and update
     */
    public void process(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();
        HttpServletRequest req = state.getRequest();
        FormData data = e.getFormData();

        String fname = (String) data.get(FILE_EDIT_FNAME);
        String fdesc = (String) data.get(FILE_EDIT_DESCRIPTION);

        File file = DRUtils.getFile
            ((BigDecimal) state.getValue(FILE_ID_PARAM));
        file.setName(file.appendExtension(fname));
        file.setDescription(fdesc);
        file.applyTag(FILE_EDIT_ACTION_DESCRIPTION.localize(req).toString());
        file.save(); // creates a new revision


        m_parent.displayPropertiesAndActions(state);
    }

    /**
     * Test if the new name already exists in the current folder
     */

    public void validate(FormSectionEvent event)
        throws FormProcessException {

        PageState state = event.getPageState();
        FormData data = event.getFormData();
        HttpServletRequest req = state.getRequest();

        File file = DRUtils.getFile
            ((BigDecimal) state.getValue(FILE_ID_PARAM));

        // Construct a name with the optional extension

        String name = file.appendExtension
            ((String) data.get(FILE_EDIT_FNAME));

        if (!file.isValidNewName(name)) {
            data.addError(FILE_EDIT_FNAME,
                          "Not a valid new name for this file");
        }

        // Verify that the new name does not correspond to an existing
        // resource (file or folder)

        if (!name.equals(file.getName())) {
            try {
                Folder parent = (Folder) file.getParent();
                parent.getResourceID(name);
                data.addError(FILE_EDIT_FNAME,
                              (String)RESOURCE_EXISTS_ERROR.localize(req));
            } catch(DataObjectNotFoundException nfe) {
                // good, so we can rename it
            } catch (InvalidNameException ex) {
                data.addError(FILE_EDIT_FNAME,
                              ex.getMessage());
            }
        }
    }

}
