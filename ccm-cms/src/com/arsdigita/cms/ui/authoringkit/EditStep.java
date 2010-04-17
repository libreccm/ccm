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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;


/**
 * This class contains a form component to edit an authoring step
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #10 $ $Date: 2004/08/17 $
 * @version $Id: EditStep.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class EditStep extends AddStep {

    protected final SingleSelectionModel m_steps;


    /**
     * @param types The content type selection model. This is to tell the form
     *   which content type is selected.
     * @param steps The authoring step selection model. This is to tell the form
     *   which step is selected for editing.
     */
    public EditStep(ContentTypeRequestLocal type, SingleSelectionModel steps) {
        super(type);

        m_steps = steps;
    }

    /**
     * Form init listener which initializes form values.
     */
    public void init(FormSectionEvent e) {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        AuthoringKit kit = getKit(state);
        AuthoringStep step = getStep(state);

        BigDecimal id = step.getID();
        String label =  step.getLabel();
        String labelKey =  step.getLabelKey();
        String labelBundle =  step.getLabelBundle();
        String description =  step.getDescription();
        String descriptionKey =  step.getDescriptionKey();
        String descriptionBundle =  step.getDescriptionBundle();
        String component =  step.getComponent();
        BigDecimal ordering = kit.getOrdering(step);

        data.put(m_id.getName(), id);
        data.put(m_labelKey.getName(), (labelKey == null) ? label : labelKey);
        data.put(m_labelBundle.getName(), labelBundle);
        data.put(m_descriptionKey.getName(), (descriptionKey == null) ? description : descriptionKey);
        data.put(m_descriptionBundle.getName(), descriptionBundle);
        data.put(m_component.getName(), component);
        data.put(m_ordering.getName(), ordering);
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
}
