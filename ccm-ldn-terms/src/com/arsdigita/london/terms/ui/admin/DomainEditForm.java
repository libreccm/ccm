/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.london.terms.ui.admin;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.util.TermsGlobalizationUtil;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.london.util.ui.parameters.URLParameter;
import com.arsdigita.util.UncheckedWrapperException;
import java.net.URL;
import java.util.TooManyListenersException;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class DomainEditForm extends Form {

    private final DomainObjectParameter selected;
    private final TextField key;
    private final TextField title;
    private final TextField url;
    private final TextArea description;
    private final TextField version;
    private Date released;
    private SaveCancelSection saveCancel;

    /**
     * 
     * @param name
     * @param selected 
     */
    public DomainEditForm(final String name, 
                          final DomainObjectParameter selected) {
        super(name, new ColumnPanel(2));
        setClassAttr("domainEdit");

        this.selected = selected;

        key = new TextField("domainKey");
        key.setSize(20);
        key.addValidationListener(new NotNullValidationListener());
        key.addValidationListener(new StringInRangeValidationListener(1, 20));
        key.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.key_hint"));
        try {
            key.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    final TextField target = (TextField) event.getTarget();
                    if (event.getPageState().getValue(selected) != null) {
                        target.setReadOnly();
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.key_label")));
        add(key);

        title = new TextField("domainTitle");
        title.setSize(50);
        title.addValidationListener(new NotNullValidationListener());
        title.addValidationListener(new StringInRangeValidationListener(1, 300));
        title.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.title_hint"));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.title_label")));
        add(title);

        url = new TextField(new URLParameter("domainUrl"));
        url.setSize(50);
        url.addValidationListener(new NotNullValidationListener());
        url.addValidationListener(new StringInRangeValidationListener(1, 255));
        url.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.url_hint"));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.url_label")));
        add(url);

        description = new TextArea("domainDesc");
        description.setCols(50);
        description.setRows(5);
        description.addValidationListener(new StringInRangeValidationListener(0, 4000));
        description.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.description_hint"));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.description_label")));
        add(description);

        version = new TextField("domainVersion");
        version.setSize(20);
        version.addValidationListener(new NotNullValidationListener());
        version.addValidationListener(new StringInRangeValidationListener(1, 20));
        version.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.version_hint"));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.version_label")));
        add(version);

        released = new Date("domainReleased");
        released.addValidationListener(new NotNullValidationListener());
        released.setHint(TermsGlobalizationUtil.globalize("terms.domain.ui.released_hint"));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.ui.released_label")));
        add(released);

        saveCancel = new SaveCancelSection();
        add(saveCancel);

        addInitListener(new InitListener());
        addSubmissionListener(new SubmissionListener());
        addProcessListener(new Processlistener());
    }

    /**
     * 
     */
    private class InitListener implements FormInitListener {

        /**
         * 
         * @param event
         * @throws FormProcessException 
         */
        @Override
        public void init(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            final Domain domain = (Domain) state.getValue(selected);

            if (domain == null) {
                key.setValue(state, null);
                title.setValue(state, null);
                url.setValue(state, null);
                description.setValue(state, null);
                version.setValue(state, null);
                released.setValue(state, null);
            } else {
                key.setValue(state, domain.getKey());
                title.setValue(state, domain.getTitle());
                url.setValue(state, domain.getURL());
                description.setValue(state, domain.getDescription());
                version.setValue(state, domain.getVersion());
                released.setValue(state, domain.getReleased());
            }
        }

    }

    
    /**
     *
     */ 
    private class SubmissionListener implements FormSubmissionListener {

        /**
         * 
         * @param event
         * @throws FormProcessException 
         */
        @Override
        public void submitted(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            if (saveCancel.getCancelButton().isSelected(state)) {
                state.setValue(selected, null);
                //fireCompletionEvent(state);   
                key.setValue(state, null);
                title.setValue(state, null);
                url.setValue(state, null);
                description.setValue(state, null);
                version.setValue(state, null);
                released.setValue(state, null);
                throw new FormProcessException("canceled");
            }
        }

    }

    /**
     * 
     */
    private class Processlistener implements FormProcessListener {

        /**
         * 
         * @param event
         * @throws FormProcessException 
         */
        @Override
        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            final Domain domain = (Domain) state.getValue(selected);

            if (domain == null) {
                Domain.create((String) key.getValue(state),
                              (URL) url.getValue(state),
                              (String) title.getValue(state),
                              (String) description.getValue(state),
                              (String) version.getValue(state),
                              (java.util.Date) released.getValue(state));
            } else {
                domain.setURL((URL) url.getValue(state));
                domain.setTitle((String) title.getValue(state));
                domain.setDescription((String) description.getValue(state));
                domain.setVersion((String) version.getValue(state));
                domain.setReleased((java.util.Date) released.getValue(state));
            }

            state.setValue(selected, null);
            key.setValue(state, null);
            title.setValue(state, null);
            url.setValue(state, null);
            description.setValue(state, null);
            version.setValue(state, null);
            released.setValue(state, null);

            fireCompletionEvent(state);
        }

    }
}
