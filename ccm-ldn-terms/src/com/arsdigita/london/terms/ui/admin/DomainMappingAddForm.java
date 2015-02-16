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

import com.arsdigita.bebop.BoxPanel;
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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.util.TermsGlobalizationUtil;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import java.util.TooManyListenersException;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class DomainMappingAddForm extends Form {

    private final DomainObjectParameter selected;
    private final SingleSelect application;
    private final TextField context;
    private final SaveCancelSection saveCancel;

    public DomainMappingAddForm(final DomainObjectParameter selected) {
        super("domainMappingAddForm", new BoxPanel(BoxPanel.HORIZONTAL));

        this.selected = selected;

        application = new SingleSelect(new DomainObjectParameter("domainMappingApp"));
        application.addValidationListener(new NotNullValidationListener());

        try {
            application.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    if (event.getPageState().getValue(selected) == null) {
                        target.setReadOnly();
                    } else {
                        final ApplicationCollection applications = Application.
                                retrieveAllApplications();
                        applications.addOrder(Application.PRIMARY_URL);
                        target.addOption(new Option(null, new Label(TermsGlobalizationUtil.globalize(
                                "terms.domain.mapping.ui.app.select_one"))));
                        while (applications.next()) {
                            final Application app = applications.getApplication();
                            target.addOption(new Option(app.getOID().toString(),
                                                        app.getPath()));
                        }
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.application")));
        add(application);

        context = new TextField("domainMappingContext");
        try {
            context.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {

                    if (event.getPageState().getValue(selected) == null) {
                        final TextField target = (TextField) event.getTarget();
                        target.setReadOnly();
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        context.setSize(20);
        //For some purposes it is neccessary to map a domain with a null context
        //context.addValidationListener(new NotNullValidationListener());
        //context.addValidationListener(new StringInRangeValidationListener(1, 100));
        add(new Label(TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.context")));
        add(context);

        saveCancel = new SaveCancelSection();
        try {
            saveCancel.getCancelButton().addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    if (event.getPageState().getValue(selected) == null) {
                        final Submit target = (Submit) event.getTarget();
                        target.setDisabled();
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        try {
            saveCancel.getSaveButton().addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    if (event.getPageState().getValue(selected) == null) {
                        final Submit target = (Submit) event.getTarget();
                        target.setDisabled();
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        add(saveCancel);

        addInitListener(new InitListener());
        addSubmissionListener(new SubmissionListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        @Override
        public void init(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            application.setValue(state, null);
            context.setValue(state, null);
        }

    }

    private class SubmissionListener implements FormSubmissionListener {

        @Override
        public void submitted(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            if (saveCancel.getCancelButton().isSelected(state)) {
                application.setValue(state, null);
                context.setValue(state, null);

                fireCompletionEvent(state);
                throw new FormProcessException(TermsGlobalizationUtil.globalize("terms.cancelled"));
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();

            final Domain domain = (Domain) state.getValue(selected);

            final Application app = (Application) application.getValue(state);
            final String domainContext = (String) context.getValue(state);

            domain.setAsRootForObject(app, domainContext);

            application.setValue(state, null);
            context.setValue(state, null);

            fireCompletionEvent(state);
        }

    }
}
