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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
//import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.HashSet;

/**
 * This form shows all accessible repositories
 * and allows to check/uncheck those one whishes to subscribe to.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class RepositoriesSelectionForm extends Form
    implements FormInitListener,
               FormProcessListener,
               DRConstants {

    private OptionGroup m_selection;
    private Hidden m_subscribed;
    private ArrayParameter m_origIDs;

    private RepositoriesTable m_table;
    private SingleSelect m_actionSelect ;
    private StringParameter m_action = new StringParameter("folder-action");


    /**
     * Constructor
     *
     * @param the table into which this form is embedded.
     */

    public RepositoriesSelectionForm(RepositoriesTable table) {
        super("FolderContentAction", new ColumnPanel(1));

        m_table =  table;

        add(m_table.getTable());

        m_selection = m_table.getCheckboxGroup();
        m_subscribed = m_table.getSubscribedHidden();

        add(m_selection);
        add(m_subscribed);
        add(new Submit( REPOSITORIES_MOUNTED_SAVE));

        addInitListener(this);
        addProcessListener(this);
    }

    @Override
    public void register(Page p) {
        super.register(p);
    }

    public void reset(PageState state) {
        m_selection.setValue(state, m_table.getSelectedIDs(state));
        m_subscribed.setValue(state, m_table.getSelectedIDs(state));
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        reset(e.getPageState());
    }

    public void process (FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();
        User subscriber = DRUtils.getUser(state);

        /*
          Construct sets for previously subscribed
          and currently selected repository IDs
        */
        HashSet subscribed = new HashSet();
        BigDecimal[] subID = (BigDecimal[]) m_subscribed.getValue(state);
        for(int i=0; subID != null && i<subID.length; i++) {
            subscribed.add(subID[i]);
        }

        HashSet selected = new HashSet();
        HashSet selected_bk = new HashSet(); // need a 3rd Set somewhere
        BigDecimal[] selID = (BigDecimal[]) m_selection.getValue(state);
        for(int i=0; selID !=null && i<selID.length; i++) {
            selected.add(selID[i]);
            selected_bk.add(selID[i]);
        }

        // Insert into repository mapping table newly selected
        selected.removeAll(subscribed);
        if(selected.size() > 0 ) {
            DataOperation operation = SessionManager.getSession()
                .retrieveDataOperation(
                 "com.arsdigita.docs.addUserRepositoriesMapping");
            operation.setParameter("userID", subscriber.getID());
            operation.setParameter("repositoryIDs", selected);
            operation.execute();
            operation.close();
        }

        // Delete from repository mapping newly unselected repositories
        subscribed.removeAll(selected_bk);
        if(subscribed.size() > 0 ) {
            DataOperation operation = SessionManager.getSession()
                .retrieveDataOperation(
                 "com.arsdigita.docrepo.removeUserRepositoriesMapping");
            operation.setParameter("userID", subscriber.getID());
            operation.setParameter("repositoryIDs", subscribed);
            operation.execute();
            operation.close();
        }
        reset(state);
    }

}
