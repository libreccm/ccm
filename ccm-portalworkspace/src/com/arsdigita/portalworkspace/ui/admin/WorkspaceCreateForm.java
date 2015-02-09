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
package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.OID;
import com.arsdigita.portalworkspace.PageLayout;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.ui.admin.applications.ApplicationCreateForm;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import java.util.TooManyListenersException;

/**
 * Extended {@link ApplicationCreateForm} for {@link Workspace}.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class WorkspaceCreateForm extends ApplicationCreateForm<Workspace> {

    private static final String LAYOUT = "layout";
    private final SingleSelect layout;

    public WorkspaceCreateForm() {
        super(Workspace.class, false);

        layout = new SingleSelect(new OIDParameter(LAYOUT));
        layout.addValidationListener(new NotNullValidationListener());
        try {
            layout.addPrintListener(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();
                    final DomainCollection layouts = PageLayout.retrieveAll();
                    layouts.addOrder(PageLayout.TITLE);
                    while (layouts.next()) {
                        final PageLayout current = (PageLayout) layouts.getDomainObject();
                        target.addOption(new Option(current.getOID().toString(), current.getTitle()));
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        getWidgetSection().add(new Label(new GlobalizedMessage(
                "cw.workspace.default_layout",
                "com.arsdigita.portalworkspace.WorkspaceResources")));
        getWidgetSection().add(layout);


    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        super.submitted(event);

        final PageState state = event.getPageState();

        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            layout.setValue(state, "");

            throw new FormProcessException(GlobalizationUtil.globalize("portal.ui.cancelled"));
        }
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            Application parent;

            final String parentPath = (String) getParentApp().getValue(state);
            if ((parentPath == null) || parentPath.isEmpty()) {
                parent = null;
            } else {
                final ApplicationCollection applications = Application.retrieveAllApplications();
                applications.addEqualsFilter(Application.PRIMARY_URL, parentPath + "/");
                if (applications.next()) {
                    parent = applications.getApplication();
                } else {
                    parent = null;
                }
                applications.close();
            }

            final String appUrl = (String) getApplicationUrl().getValue(state);
            final String appTitle = (String) getApplicationTitle().getValue(state);
            final String appDesc = (String) getApplicationDesc().getValue(state);

            final OID selectedLayoutOID = (OID) layout.getValue(state);
            final PageLayout pageLayout = (PageLayout) DomainObjectFactory.newInstance(selectedLayoutOID);

            final Workspace workspace = Workspace.createWorkspace(appUrl, appTitle, pageLayout, parent, true);
            workspace.setDescription(appDesc);
            workspace.save();
        }

    }
}
