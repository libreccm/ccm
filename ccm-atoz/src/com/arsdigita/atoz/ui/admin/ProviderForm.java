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

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;

import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;

import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.util.Classes;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

public abstract class ProviderForm extends Form {

    private static final Logger s_log = Logger.getLogger(ProviderForm.class);

    private ACSObjectSelectionModel m_provider;
    private Class m_providerType;

    private TextField m_title;
    private TextArea m_description;
    private SaveCancelSection m_buttons;

    public ProviderForm(String name,
                        Class providerType,
                        ACSObjectSelectionModel provider) {
        super(name, new SimpleContainer());
        setRedirecting(true);

        m_provider = provider;
        m_providerType = providerType;

        m_buttons = new SaveCancelSection(new SimpleContainer());

        addWidgets();
        add(m_buttons);

        addProcessListener(new ProviderProcessListener());
        addSubmissionListener(new ProviderSubmissionListener());
        addInitListener(new ProviderInitListener());
    }


    protected void addWidgets() {
        m_title = new TextField("title");
        m_title.setMetaDataAttribute("label", "Title");
        m_title.addValidationListener(new StringInRangeValidationListener(1, 200));
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setSize(80);

        m_description = new TextArea("description");
        m_description.setMetaDataAttribute("label", "Description");
        m_description.addValidationListener(
            new StringInRangeValidationListener(1, 4000));
        m_description.addValidationListener(new NotNullValidationListener());
        m_description.setCols(80);
        m_description.setRows(5);

        add(m_title);
        add(m_description);
    }

    protected void processWidgets(PageState state,
                                  AtoZProvider provider) {
        provider.setTitle((String)m_title.getValue(state));
        provider.setDescription((String)m_description.getValue(state));
    }

    protected void initWidgets(PageState state,
                               AtoZProvider provider) {
        if (provider != null) {
            m_title.setValue(state, provider.getTitle());
            m_description.setValue(state, provider.getDescription());
        }
    }

    private class ProviderSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancel hit");
            }
        }
    }

    private class ProviderProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            AtoZProvider provider = (AtoZProvider)m_provider
                .getSelectedObject(state);

            if (provider == null) {
                AtoZ atoz = (AtoZ)Kernel.getContext().getResource();
                Assert.exists(atoz, AtoZ.class);
                provider = (AtoZProvider)Classes.newInstance(m_providerType);
                atoz.addProvider(provider);
                //provider.setAtoZ(atoz);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Creating provider " + provider.getOID());
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Loading provider " + provider.getOID());
                }
            }

            processWidgets(state,
                           provider);

            fireCompletionEvent(state);
        }
    }

    private class ProviderInitListener implements FormInitListener {
        public void init(FormSectionEvent e)
            throws FormProcessException {
            PageState state = e.getPageState();

            AtoZProvider provider = (AtoZProvider)m_provider
                .getSelectedObject(state);
            if (provider != null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Loading provider " + provider.getOID());
                }
            } else {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No provider available");
                }
            }

            initWidgets(state,provider);
        }
    }
}
