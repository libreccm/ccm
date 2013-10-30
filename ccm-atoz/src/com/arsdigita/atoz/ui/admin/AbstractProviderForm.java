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
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import org.apache.log4j.Logger;

public abstract class AbstractProviderForm extends Form {

    private static final Logger LOGGER = Logger.getLogger(AbstractProviderForm.class);
    private final ACSObjectSelectionModel providerModel;
    private final Class providerType;
    private TextField title;
    private TextArea description;
    private final SaveCancelSection buttons;
    private final ApplicationInstanceAwareContainer parent;

    public AbstractProviderForm(final String name,
                                final Class providerType,
                                final ACSObjectSelectionModel provider) {
        this(name, providerType, provider, null);
    }

    public AbstractProviderForm(final String name,
                                final Class providerType,
                                final ACSObjectSelectionModel provider,
                                final ApplicationInstanceAwareContainer parent) {
        super(name, new ColumnPanel(2));
        setRedirecting(true);

        this.providerModel = provider;
        this.providerType = providerType;
        this.parent = parent;

        buttons = new SaveCancelSection(new SimpleContainer());

        addWidgets();
        add(buttons);

        addProcessListener(new ProviderProcessListener());
        addSubmissionListener(new ProviderSubmissionListener());
        addInitListener(new ProviderInitListener());
    }

    protected void addWidgets() {

        title = new TextField("title");
        //m_title.setMetaDataAttribute("label", "Title");
        title.addValidationListener(new StringInRangeValidationListener(1, 200));
        title.addValidationListener(new NotNullValidationListener());
        title.setSize(80);

        description = new TextArea("description");
        //m_description.setMetaDataAttribute("label", "Description");
        description.addValidationListener(
                new StringInRangeValidationListener(1, 4000));
        description.addValidationListener(new NotNullValidationListener());
        description.setCols(80);
        description.setRows(5);

        add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.provider_title")));
        add(title);
        add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.provider_description")));
        add(description);
    }

    protected void processWidgets(final PageState state, final AtoZProvider provider) {
        provider.setTitle((String) title.getValue(state));
        provider.setDescription((String) description.getValue(state));
    }

    protected void initWidgets(final PageState state,
                               final AtoZProvider provider) {
        if (provider != null) {
            title.setValue(state, provider.getTitle());
            description.setValue(state, provider.getDescription());
        }
    }

    private class ProviderSubmissionListener implements FormSubmissionListener {

        public ProviderSubmissionListener() {
            super();
        }

        @Override
        public void submitted(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            if (buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancel hit");
            }
        }

    }

    private class ProviderProcessListener implements FormProcessListener {

        public ProviderProcessListener() {
            super();
        }

        @Override
        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            AtoZProvider provider = (AtoZProvider) providerModel.getSelectedObject(state);

            if (provider == null) {
                final AtoZ atoz;
                if (parent == null) {
                    atoz = (AtoZ) Kernel.getContext().getResource();
                } else {
                    atoz = (AtoZ) parent.getAppInstance();
                }
                Assert.exists(atoz, AtoZ.class);
                provider = (AtoZProvider) Classes.newInstance(providerType);
                atoz.addProvider(provider);
                //provider.setAtoZ(atoz);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Creating provider " + provider.getOID());
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Loading provider " + provider.getOID());
                }
            }

            processWidgets(state, provider);

            fireCompletionEvent(state);
        }

    }

    private class ProviderInitListener implements FormInitListener {

        public ProviderInitListener() {
            super();
        }

        public void init(final FormSectionEvent event)
                throws FormProcessException {
            final PageState state = event.getPageState();

            final AtoZProvider provider = (AtoZProvider) providerModel.getSelectedObject(state);
            if (provider == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No provider available");
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Loading provider " + provider.getOID());
                }
            }

            initWidgets(state, provider);
        }

    }
}
