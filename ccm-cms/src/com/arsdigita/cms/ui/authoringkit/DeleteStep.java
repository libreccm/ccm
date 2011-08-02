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
package com.arsdigita.cms.ui.authoringkit;


import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

/**
 * This class handles the deleting of an authoring step.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 * @version $Id: DeleteStep.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class DeleteStep extends Form
    implements FormProcessListener, FormSubmissionListener {

    private final SingleSelectionModel m_steps;

    protected final ContentTypeRequestLocal m_type;

    private Submit m_deleteWidget, m_cancelWidget;


    /**
     * @param steps The authoring step selection model. This is to tell the form
     *   which step is selected for deleting.
     */
    public DeleteStep(SingleSelectionModel m, ContentTypeRequestLocal type) {
        super("PhaseDefinitionDelete");

        m_steps = m;
        m_type = type;

        Label confirmation = new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.do_you_really_want_to_delete_this_authoring_step"));
        add(confirmation, ColumnPanel.FULL_WIDTH);

        BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        m_deleteWidget = new Submit("delete");
        m_deleteWidget.setButtonLabel("Delete");
        buttons.add(m_deleteWidget);

        m_cancelWidget = new Submit("cancel");
        m_cancelWidget.setButtonLabel("Cancel");
        buttons.add(m_cancelWidget);

        add(buttons, ColumnPanel.CENTER|ColumnPanel.FULL_WIDTH);

        addProcessListener(this);
        addSubmissionListener(this);
    }

    boolean isCancelled(PageState s) {
        return m_cancelWidget.isSelected(s);
    }

    /**
     * Form process listener which deletes an authoring step
     */
    public void process(FormSectionEvent e) throws FormProcessException {

        PageState state = e.getPageState();
        FormData data = e.getFormData();

        //check if the object is already deleted for double click protection
        try {
            AuthoringKit kit = getKit(state);
            AuthoringStep step = getStep(state);
            kit.removeStep(step);
            step.delete();
        } catch (DataObjectNotFoundException ex) {
            //just ignore it since it is already deleted
        }

    }


    /**
     * Form process listener which for submission
     */
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if(m_cancelWidget.isSelected(e.getPageState())) {
            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.authoringkit.submission_cancelled").localize());
        }
    }

    protected AuthoringStep getStep(PageState state) {
        String step_key = (String) m_steps.getSelectedKey(state);
        BigDecimal stepID = new BigDecimal(step_key);

        try {
            return new AuthoringStep(stepID);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("Authoring Step ID#" + step_key +
                                       " not found", ex);
        }
    }

    protected AuthoringKit getKit(PageState state) {
        ContentType type = m_type.getContentType(state);
        try {
            return type.getAuthoringKit();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("Authoring kit not found", ex);
        }
    }

}
