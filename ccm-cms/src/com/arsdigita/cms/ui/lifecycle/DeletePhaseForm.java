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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.lifecycle.PhaseDefinition;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.domain.DataObjectNotFoundException;

import java.math.BigDecimal;

/**
 * This class handles the deleting of a phase definition.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 */
class DeletePhaseForm extends CMSForm
        implements FormProcessListener, FormInitListener {
    public static final String versionId =
        "$Id: DeletePhaseForm.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private final PhaseRequestLocal m_phase;

    private Hidden m_id;
    private Submit m_deleteWidget;
    private Submit m_cancelWidget;

    /**
     * @param m The phase selection model. This tells the form which
     * phase definition is selected.
     */
    public DeletePhaseForm(final PhaseRequestLocal phase) {
        super("PhaseDefinitionDelete");

        m_phase = phase;

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        m_deleteWidget = new Submit("delete");
        m_deleteWidget.setButtonLabel("Delete");
        m_deleteWidget.setClassAttr("deletePhase");
        buttons.add(m_deleteWidget);

        m_cancelWidget = new Submit("cancel");
        m_cancelWidget.setButtonLabel("Cancel");
        m_cancelWidget.setClassAttr("canceldeletePhase");
        buttons.add(m_cancelWidget);

        add(buttons, ColumnPanel.CENTER|ColumnPanel.FULL_WIDTH);

        addInitListener(this);

        addSubmissionListener
            (new FormSecurityListener(SecurityManager.LIFECYCLE_ADMIN));

        addProcessListener(this);
    }

    /**
     * Returns true if this form was cancelled.
     *
     * @param s The page state
     * @return true if the form was cancelled, false otherwise
     */
    public boolean isCancelled(PageState s) {
        return m_cancelWidget.isSelected(s);
    }

    /**
     * Form process listener.
     * Deletes a phase definition
     *
     * @param e The form process event
     * @exception FormProcessException
     */
    public final void process(final FormSectionEvent e) 
            throws FormProcessException {
        final FormData data = e.getFormData();
        final BigDecimal key = (BigDecimal) data.get(m_id.getName());

        // Check if the object is already deleted for double click
        // protection.
        try {
            PhaseDefinition phaseDefinition = new PhaseDefinition(key);
            phaseDefinition.delete();
        } catch (DataObjectNotFoundException ex) {
            // Just ignore it since it is already deleted.
        }

    }

    /**
     * Init listener.
     * gets the id of the selected phase definition
     *
     * @param e The form init event
     */
    public final void init(final FormSectionEvent e) {
        final FormData data = e.getFormData();
        final PageState state = e.getPageState();

        final BigDecimal id =  m_phase.getPhase(state).getID();

        data.put(m_id.getName(), id);
    }
}
