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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.type.ContentTypeRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;


/**
 * This class contains a form component to edit an authoring kit
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #12 $ $Date: 2004/08/17 $
 * @version $Id: EditKit.java 2090 2010-04-17 08:04:14Z pboy $ 
 */
public class EditKit extends Form
    implements FormProcessListener, FormInitListener {

    protected final ContentTypeRequestLocal m_type;

    protected Hidden m_id;
    protected TextField m_createComponent;
    protected SaveCancelSection  m_saveCancelSection;

    /**
     * @param types The content type selection model. This is to tell the form
     *   which content type is selected.
     */
    public EditKit(ContentTypeRequestLocal type) {
        super("AuthoringKit");

        m_type = type;

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        add(new Label(GlobalizationUtil.globalize("cms.ui.authoringkit.createcomponent")));
        m_createComponent = new TextField(new StringParameter("createComponent"));
        m_createComponent.setSize(40);
        m_createComponent.setMaxLength(200);
        add(m_createComponent);

        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        addProcessListener(this);
        addInitListener(this);
        addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    if ( isCancelled(state) ) {
                        throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.cancel_hit").localize());
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
     * Form process listener which updates a authoring kit
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        FormData data = e.getFormData();
        BigDecimal key = (BigDecimal) data.get(m_id.getName());
        String createComponent = (String) data.get(m_createComponent.getName());

        AuthoringKit kit;
        try {
            kit = new AuthoringKit(key);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.ui.authoringkit.content_type_id").localize() + key.toString() +
                                       " not found", ex);
        }

        kit.setCreateComponent(createComponent);
        kit.save();

        Utilities.refreshItemUI(state);
    }

    /**
     * Form init listener which initializes form values.
     */
    public void init(FormSectionEvent e) {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        AuthoringKit kit = getKit(state);
        BigDecimal id = kit.getID();
        String createComponent = kit.getCreateComponent();

        data.put(m_id.getName(), id);
        data.put(m_createComponent.getName(), createComponent);
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
