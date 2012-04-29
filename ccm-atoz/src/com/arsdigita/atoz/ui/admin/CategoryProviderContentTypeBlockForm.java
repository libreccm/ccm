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
 */

package com.arsdigita.atoz.ui.admin;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.CategoryProvider;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

public class CategoryProviderContentTypeBlockForm extends Form {

    private static final Logger s_log = Logger
            .getLogger(CategoryProviderContentTypeBlockForm.class);

    private static final String TYPE_ID = "tid";

    private ACSObjectSelectionModel m_provider;

    private SingleSelect m_type_picker;

    private SaveCancelSection m_buttons;

    public CategoryProviderContentTypeBlockForm(ACSObjectSelectionModel provider) {
        super("contentTypeBlockForm", new SimpleContainer());
        setRedirecting(true);

        m_provider = provider;

        m_type_picker = getContentTypeSelect();
        add(m_type_picker);

        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addProcessListener(new ProviderProcessListener());
        addSubmissionListener(new ProviderSubmissionListener());
    }

    private SingleSelect getContentTypeSelect() {
        SingleSelect select = new SingleSelect(new BigDecimalParameter(TYPE_ID));
        try {
            select.addPrintListener(new PrintListener() {

                public void prepare(PrintEvent e) {
                    OptionGroup optionGroup = (OptionGroup) e.getTarget();
                    ContentTypeCollection ctc = ContentType
                            .getRegisteredContentTypes();
                    ctc.addOrder(ContentType.LABEL);
                    while (ctc.next()) {
                        ContentType contentType = ctc.getContentType();
                        optionGroup.addOption(new Option(contentType.getID()
                                .toString(), contentType.getLabel()));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            throw new UncheckedWrapperException(e.getClass().getName()
                    + e.getMessage(), e);
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException(e.getClass().getName()
                    + e.getMessage(), e);
        }
        return select;
    }

    public class ProviderSubmissionListener implements FormSubmissionListener {

        public void submitted(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancel hit");
            }
        }

    }

    public class ProviderProcessListener implements FormProcessListener {

        public void process(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();

            CategoryProvider provider = (CategoryProvider) m_provider
                    .getSelectedObject(state);

            BigDecimal contentTypeID = (BigDecimal) m_type_picker
                    .getValue(state);
            if (contentTypeID == null)
                return;

            ContentType contentType = new ContentType(contentTypeID);
            if (contentType == null)
                return;

            provider.addContentTypeBlock(contentType);

            fireCompletionEvent(state);
        }
    }

}
