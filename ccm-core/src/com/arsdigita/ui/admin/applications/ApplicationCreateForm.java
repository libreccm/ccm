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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.BoxPanel;
//import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

//import java.util.ArrayList;
//import java.util.List;
import java.util.TooManyListenersException;

/**
 * Basic form for creating new Application instances. Should be suitable for 
 * most applications types. If you have special needs... $todo
 *
 * This form does not support parent/child application structures. If your app 
 * needs this, add a widget for selecting the parent application and extend 
 * the process method.
 *
 * @param <T> Type of application
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ApplicationCreateForm<T extends Application> extends Form implements FormSubmissionListener,
                                                                                  FormValidationListener,
                                                                                  FormProcessListener {

    public static final String FORM_NAME = "ApplicationCreateForm";
    private static final String PARENT_APP = "parentAll";
    private static final String APPLICATION_URL = "applicationUrl";
    private static final String APPLICATION_TITLE = "applicationTitle";
    private static final String APPLICATION_DESC = "applicationDesc";
    private final String appClassName;
    private final ApplicationType applicationType;
    private final SingleSelect parentApp;
    private final TextField applicationUrl;
    private final TextField applicationTitle;
    private final TextArea applicationDesc;
    private final Container widgetSection;   
    private final SaveCancelSection saveCancelSection;

    public ApplicationCreateForm(final Class<T> appClass, final boolean allowRoot) {

        super(FORM_NAME);

        appClassName = appClass.getName();

        final Session session = SessionManager.getSession();
        final DataCollection appTypes = session.retrieve(ApplicationType.BASE_DATA_OBJECT_TYPE);
        appTypes.addEqualsFilter("objectType", appClass.getName());

        if (appTypes.isEmpty()) {
            throw new IllegalArgumentException(String.format("Not application found for object type '%s'.",
                                                             appClass.getName()));
        }

        appTypes.next();
        applicationType = (ApplicationType) DomainObjectFactory.newInstance(appTypes.getDataObject());
        appTypes.close();

        widgetSection = new BoxPanel(BoxPanel.VERTICAL);
        add(widgetSection);

        parentApp = new SingleSelect(PARENT_APP);
        try {
            parentApp.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.addOption(new Option("", ""));

                    final ApplicationCollection applications = Application.retrieveAllApplications();
                    applications.addOrder(Application.PRIMARY_URL);
                    while (applications.next()) {
                        target.addOption(new Option(applications.getApplication().getPath(),
                                                    applications.getApplication().getPath()));
                    }
                }
            });

            if (!allowRoot) {
                parentApp.addValidationListener(new NotNullValidationListener());
                parentApp.addValidationListener(new NotEmptyValidationListener());
            }

        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        applicationUrl = new TextField(new StringParameter(APPLICATION_URL));
        applicationUrl.setSize(42);
        applicationUrl.addValidationListener(new NotNullValidationListener(
                GlobalizationUtil.globalize("ui.admin.applications.url.validation.not_blank")));
        applicationUrl.addValidationListener(new StringInRangeValidationListener(1, 100, GlobalizationUtil.globalize(
                "ui.admin.applications.url.valiation.minmaxlength")));

        applicationTitle = new TextField(new StringParameter(APPLICATION_TITLE));
        applicationTitle.setSize(42);
        applicationTitle.addValidationListener(new NotNullValidationListener(
                GlobalizationUtil.globalize("ui.admin.applications.title.validation.not_blank")));
        applicationTitle.addValidationListener(new StringInRangeValidationListener(1, 200, GlobalizationUtil.globalize(
                "ui.admin.applications.title.valiation.minmaxlength")));

        applicationDesc = new TextArea(new StringParameter(APPLICATION_DESC));
        applicationDesc.setRows(5);
        applicationDesc.setCols(42);
        applicationDesc.addValidationListener(new StringInRangeValidationListener(0, 4000, GlobalizationUtil.globalize(
                "ui.admin.applications.desc.valiation.minmaxlength")));

        widgetSection.add(new Label(GlobalizationUtil.globalize("ui.admin.applications.parent.label")));
        widgetSection.add(parentApp);
        widgetSection.add(new Label(GlobalizationUtil.globalize("ui.admin.applications.url.label")));
        widgetSection.add(applicationUrl);
        widgetSection.add(new Label(GlobalizationUtil.globalize("ui.admin.applications.title.label")));
        widgetSection.add(applicationTitle);
        widgetSection.add(new Label(GlobalizationUtil.globalize("ui.admin.applications.desc.label")));
        widgetSection.add(applicationDesc);
        
        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addValidationListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    protected Container getWidgetSection() {
        return widgetSection;
    }

    public String getAppClassName() {
        return appClassName;
    }

    protected SingleSelect getParentApp() {
        return parentApp;
    }

    protected TextField getApplicationUrl() {
        return applicationUrl;
    }

    protected TextField getApplicationTitle() {
        return applicationTitle;
    }

    protected TextArea getApplicationDesc() {
        return applicationDesc;
    }

    protected SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {
            Application parent;
            boolean createContainerGroup;

            final String parentPath = (String) parentApp.getValue(state);
            if ((parentPath == null) || parentPath.isEmpty()) {
                parent = null;
                createContainerGroup = false;
            } else {
                final ApplicationCollection applications = Application.retrieveAllApplications();
                applications.addEqualsFilter(Application.PRIMARY_URL, parentPath + "/");
                if (applications.next()) {
                    parent = applications.getApplication();
                    createContainerGroup = true;
                } else {
                    parent = null;
                    createContainerGroup = false;
                }
                applications.close();
            }

            final Application application = Application.createApplication(applicationType,
                                                                          (String) applicationUrl.getValue(state),
                                                                          (String) applicationTitle.getValue(state),
                                                                          parent,
                                                                          createContainerGroup);
            application.setDescription((String) applicationDesc.getValue(state));
            application.save();
        }
    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (saveCancelSection.getCancelButton().isSelected(state)) {
            parentApp.setValue(state, "");
            applicationTitle.setValue(state, "");
            applicationUrl.setValue(state, "");
            applicationDesc.setValue(state, "");

            throw new FormProcessException("Cancelled",
                                           GlobalizationUtil.globalize(
                                                   "ui.admin.cancel_msg"));
        }
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {

        /** */
        final PageState state = event.getPageState();
        /** */
        final String url = (String) applicationUrl.getValue(state);

        if (url.contains("/")) {
            throw new FormProcessException(
                    "The URL fragement may not contain slashes",
                    GlobalizationUtil.globalize(
                    "ui.admin.applications.url.validation.no_slash_allowed"));
        }

        if (Application.isInstalled(Application.BASE_DATA_OBJECT_TYPE, url)) {
            throw new FormProcessException(
                    "The provided URL is already in use",
                    GlobalizationUtil.globalize(
                    "ui.admin.applications.url.validation.url_already_in_use"));
        }
    }
}
