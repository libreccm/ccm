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
import java.util.ArrayList;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.docmgr.ResourceImpl;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;

/**
 * This class has dual functionality as the name implies.
 * Firstly, it contains a table that lists the contents of
 * a given directory.
 * Secondly, it contains following buttons to apply the
 * corrsponding operation to the selected files and folders:
 * CUT, COPY, DELETE
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

class FolderContentsTableForm extends Form
    implements FormProcessListener,
               DMConstants {

    private Tree m_tree;
    private FolderTable m_folder;
    private SingleSelect m_actionSelect ;
    private StringParameter m_action = new StringParameter("folder-action");

    private Submit m_deleteSubmit;
    private Submit m_copySubmit;
    private Submit m_moveSubmit;
    private SimpleContainer m_actionBar;
    private Label m_emptyFolderLabel;
    private BrowsePane m_parent;

    /**
     * Constructor
     */
    public FolderContentsTableForm(BrowsePane parent, Tree tree) {
        super("FolderContentAction", new ColumnPanel(1));

        m_tree = tree;
        m_parent = parent;

        m_folder = new FolderTable(m_tree, this);
        add(m_folder);

        m_actionBar = new SimpleContainer();

        m_copySubmit = new Submit(ACTION_COPY_SUBMIT);
        m_actionBar.add(m_copySubmit);

        m_moveSubmit = new Submit(ACTION_MOVE_SUBMIT);
        m_actionBar.add(m_moveSubmit);

        m_deleteSubmit = new DeleteSubmit(ACTION_DELETE_SUBMIT);
        m_actionBar.add(m_deleteSubmit);

        add(m_actionBar);

        m_emptyFolderLabel = FOLDER_EMPTY_LABEL;
        add( m_emptyFolderLabel);

        addProcessListener(this);
    }

    /**
     * Register the defa
     */
    public void register(Page p) {

        p.setVisibleDefault( m_actionBar, true);
        p.setVisibleDefault( m_emptyFolderLabel, false);
        super.register(p);
    }


    public void process(FormSectionEvent e)
        throws FormProcessException {
        PageState state = e.getPageState();

        ArrayList msgList = new ArrayList();
        boolean isError = false;

        String[] selectedItems =
            (String[]) m_folder.getCheckboxGroup().getValue(state);

        if (selectedItems == null) {
            // Nothing selected, can stop process right here.
            return;
        }

        ArrayList list = new ArrayList();
        for (int i = 0; i < selectedItems.length; i++) {
            list.add(selectedItems[i]);
        }

        if (m_deleteSubmit.isSelected(state)) {

            for (int i = 0; i < selectedItems.length; i++) {
                OID oid = new OID(ResourceImpl.BASE_DATA_OBJECT_TYPE,
                                  new BigDecimal(selectedItems[i]));

                ResourceImpl resource = null;
                try {

                    resource =
                        (ResourceImpl) DomainObjectFactory.newInstance(oid);
                    resource.delete();
                } catch (PersistenceException exc) {
                    // Can't delete file and folder.
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getName());
                    }


                } catch (DataObjectNotFoundException exc) {
                    // Move on to the next object.
                    isError = true;
                    if (resource != null) {
                        msgList.add(resource.getName());
                    }

                }
            }
        } else if (m_copySubmit.isSelected(state)) {
            m_parent.displayDestinationFolderPanel(state,
                                                   list.toArray(),
                                                   false);

        } else if (m_moveSubmit.isSelected(state)) {
            m_parent.displayDestinationFolderPanel(state,
                                                   list.toArray(),
                                                   true);
        }

        // Reset selected item in checkbox.
        m_folder.getCheckboxGroup().setValue(state, null);

        if (isError) {
            m_parent.displayErrorMsgPanel(state, "delete", msgList);
        }
    }

    public void hideActionLinks(PageState state) {
        m_actionBar.setVisible(state, false);
        m_emptyFolderLabel.setVisible(state, true);
    }

    public void hideEmptyLabel(PageState state) {
        m_emptyFolderLabel.setVisible(state, false);
        m_actionBar.setVisible(state, true);
    }

}


/**
 * Display a confirmation dialog box when clicked.
 */
class DeleteSubmit extends Submit implements DMConstants {
    public DeleteSubmit(GlobalizedMessage label) {
        super(label);
        avoidDoubleClick(false);
        setAttribute(ON_CLICK,
                     "return confirm("+
                     ACTION_DELETE_CONFIRM.localize()
                     +");");
    }
}
