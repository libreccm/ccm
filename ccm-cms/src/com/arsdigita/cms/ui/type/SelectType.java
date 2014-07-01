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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * This class contains a form component to that allows adding
 * already-existing content type to a content section.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: SelectType.java 287 2005-02-22 00:29:02Z sskracic $ 
 */
public class SelectType extends CMSForm
        implements PrintListener, FormSubmissionListener, FormProcessListener {

    private final static String TYPES = "types";
    private CheckboxGroup m_typesCheckbox;
    private Submit m_submit;
    private Submit m_cancel;

    public SelectType() {
        super("ContentTypeSelect");

        m_typesCheckbox = new CheckboxGroup(TYPES);
        try {
            m_typesCheckbox.addPrintListener(this);
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e.getMessage());
        }

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.available_types")));
        add(m_typesCheckbox);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Add Selected Content Types");
        s.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        addProcessListener(this);
        addSubmissionListener(new TypeSecurityListener());
        addSubmissionListener(this);
    }

    /**
     * Generate a checkbox list of all content type not associated
     * with the current content section
     */
    public void prepare(PrintEvent event) {

        CheckboxGroup t = (CheckboxGroup) event.getTarget();

        // Get the current content section
        ContentSection section = CMS.getContext().getContentSection();

        ContentTypeCollection contentTypes =
                section.getNotAssociatedContentTypes();
        contentTypes.addOrder(ContentType.LABEL);
        while (contentTypes.next()) {
            ContentType contentType = contentTypes.getContentType();

            Label label = new Label(contentType.getName());
            if (contentType.isHidden()) {
                label.setFontWeight(Label.ITALIC);
            }
            t.addOption(new Option(contentType.getID().toString(), label));
        }
    }

    /**
     * Form submission listener.
     * If the cancel button was pressed, do not process the form.
     *
     * @param event The submit event
     */
    public void submitted(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        if (isCancelled(state)) {
            throw new FormProcessException("cancelled");
        }
    }

    /**
     * Returns true if this form was cancelled.
     *
     * @return true if the form was cancelled, false otherwise
     */
    public boolean isCancelled(PageState state) {
        return m_cancel.isSelected(state);
    }

    /**
     * Processes form listener which updates a life cycle
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        ContentSection section = CMS.getContext().getContentSection();

        FormData data = e.getFormData();
        String[] types = (String[]) data.get(TYPES);
        ContentType type;

        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                try {
                    type = new ContentType(new BigDecimal(types[i]));
                    section.addContentType(type);
                } catch (DataObjectNotFoundException ex) {
                    throw new UncheckedWrapperException("Content Type ID#" + types[i]
                            + " not found", ex);
                }
            }

            if (types.length > 0) {
                section.save();
            }
        }
    }
}
