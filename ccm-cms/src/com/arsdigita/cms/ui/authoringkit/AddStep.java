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


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * This class contains a form component to add an authoring step.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #13 $ $Date: 2004/08/17 $
 * @version $Id: AddStep.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class AddStep extends Form
    implements FormProcessListener, FormInitListener {

    private static Logger s_log =
        Logger.getLogger(AddStep.class.getName());

    protected ContentTypeRequestLocal m_type;

    protected Hidden m_id;
    protected TextField m_ordering, m_labelKey, m_labelBundle, m_component;
    protected TextField m_descriptionKey, m_descriptionBundle;
    protected SaveCancelSection  m_saveCancelSection;

    /**
     * @param types The content type selection model. This is to tell the form
     *   which content type is selected, so it can add a step to the content
     *   type's authoring kit.
     */
    public AddStep(ContentTypeRequestLocal type) {
        super("AuthoringStep");

        m_type = type;

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.ordering")));
        m_ordering = new TextField(new BigDecimalParameter("ordering"));
        m_ordering.addValidationListener(new NotNullValidationListener());
        m_ordering.setSize(5);
        add(m_ordering);

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.label_key")));
        m_labelKey = new TextField(new StringParameter("labelKey"));
        m_labelKey.addValidationListener(new NotNullValidationListener());
        m_labelKey.setSize(40);
        m_labelKey.setMaxLength(1000);
        add(m_labelKey);

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.label_bundle")));
        m_labelBundle = new TextField(new StringParameter("labelBundle"));
        m_labelBundle.setSize(40);
        m_labelBundle.setMaxLength(1000);
        add(m_labelBundle);

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.description_key")));
        m_descriptionKey = new TextField(new StringParameter("descriptionKey"));
        m_descriptionKey.addValidationListener(new NotNullValidationListener());
        m_descriptionKey.setSize(40);
        m_descriptionKey.setMaxLength(1000);
        add(m_descriptionKey);

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.description_bundle")));
        m_descriptionBundle = new TextField(new StringParameter("descriptionBundle"));
        m_descriptionBundle.setSize(40);
        m_descriptionBundle.setMaxLength(1000);
        add(m_descriptionBundle);

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.component")));
        m_component = new TextField(new StringParameter("component"));
        m_component.setSize(40);
        m_component.setMaxLength(200);
        add(m_component);

        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        //add the listeners
        addProcessListener(this);
        addInitListener(this);
        addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( isCancelled(state) ) {
                        throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.cancel_hit"));
                    }
                }
            });
    }

    /**
     * Return true if the form is cancelled, false otherwise.
     *
     * @param state The page state
     * @return true if the form is cancelled, false otherwise.
     * @pre ( state != null )
     */
    public boolean isCancelled(PageState state) {
        return m_saveCancelSection.getCancelButton().isSelected(state);
    }

    /**
     * Form process listener which inserts or edits a step
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        FormData data = e.getFormData();
        BigDecimal key = (BigDecimal) data.get(m_id.getName());
        BigDecimal ordering = (BigDecimal) data.get(m_ordering.getName());
        String labelKey = (String) data.get(m_labelKey.getName());
        String labelBundle = (String) data.get(m_labelBundle.getName());
        String descriptionKey = (String) data.get(m_descriptionKey.getName());
        String descriptionBundle = (String) data.get(m_descriptionBundle.getName());
        String component = (String) data.get(m_component.getName());

        AuthoringKit kit = getKit(state);
        AuthoringStep step;

        //check if the object already exists for double click protection
        try {
            step = new AuthoringStep(key);
        } catch (DataObjectNotFoundException ex) {
            step = new AuthoringStep(SessionManager.getSession().create
                                     (new OID(AuthoringStep.BASE_DATA_OBJECT_TYPE, key)));
        }

        step.setLabel(null);
        step.setLabelKey(labelKey);
        step.setLabelBundle(labelBundle);
        step.setDescription(null);
        step.setDescriptionKey(descriptionKey);
        step.setDescriptionBundle(descriptionBundle);
        step.setComponent(component);
        step.save();

        kit.addStep(step, ordering);

        Utilities.refreshItemUI(state);
    };


    public void init(FormSectionEvent e) {
        FormData data = e.getFormData();
        try {
            if (data.get(m_id.getName()) == null) {
                data.put(m_id.getName(), Sequences.getNextValue());
            }
        } catch (SQLException s) {
            s_log.error("Could not generate Sequence ID", s);
            data.addError("Could not generate Sequence ID " + s.getMessage());
        }
    }

    protected AuthoringKit getKit(PageState state) {
        ContentType type = m_type.getContentType(state);
        try {
            return type.getAuthoringKit();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("Authoring kit not found");
        }
    }

}
