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
package com.arsdigita.navigation.ui.admin;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.NavigationAppManager;
import com.arsdigita.navigation.NavigationGlobalizationUtil;
import com.arsdigita.navigation.tools.NavigationCreator;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;

/**
 * This form is used by the {@link NavigationAppManager} for creating a new navigation instance. The part which is 
 * creating the instance ({@link ProcessListener#process(com.arsdigita.bebop.event.FormSectionEvent)} is taken from
 * the {@link NavigationCreator} CLI application.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class NavigationCreateForm extends Form {

    public static final String FORM_NAME = "NavigationCreateForm";
    private static final String NAV_URL = "navUrl";
    private static final String NAV_TITLE = "navTitle";
    private static final String NAV_DOMAIN = "navDomain";
    private static final String NAV_DESC = "navDesc";
    private final SaveCancelSection saveCancelSection;

    public NavigationCreateForm() {
        super(FORM_NAME, new ColumnPanel(2));

        add(new Label(NavigationGlobalizationUtil.globalize("ui.create.nav_url")));
        add(new TextField(NAV_URL));

        add(new Label(NavigationGlobalizationUtil.globalize("ui.create.nav_title")));
        add(new TextField(NAV_TITLE));

        add(new Label(NavigationGlobalizationUtil.globalize("ui.create.nav_default_domain")));
        final SingleSelect termDomainSelect = new SingleSelect(NAV_DOMAIN);
        try {
            termDomainSelect.addPrintListener(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    final DataCollection termDomains = SessionManager.getSession().
                            retrieve(Domain.BASE_DATA_OBJECT_TYPE);
                    while (termDomains.next()) {
                        target.addOption(new Option((String) termDomains.getDataObject().get(Domain.KEY),
                                                    (String) termDomains.getDataObject().get(Domain.TITLE)));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        add(termDomainSelect);

        add(new Label(NavigationGlobalizationUtil.globalize("ui.create.nave_desc")));
        add(new TextArea(NAV_DESC, 10, 60, TextArea.OFF));

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addSubmissionListener(new SubmissionListener());
        addProcessListener(new ProcessListener());
    }

    private class SubmissionListener implements FormSubmissionListener {

        public SubmissionListener() {
            //Nothing
        }

        public void submitted(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            final FormData data = event.getFormData();

            if (saveCancelSection.getCancelButton().isSelected(state)) {
                data.put(NAV_URL, "");
                data.put(NAV_TITLE, "");
                data.put(NAV_DOMAIN, "");
                data.put(NAV_DESC, "");

                throw new FormProcessException("Canceled");
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        public ProcessListener() {
            //Nothing
        }

        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            final FormData data = event.getFormData();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                Navigation.createNavigation(data.getString(NAV_URL),
                                            data.getString(NAV_TITLE),
                                            data.getString(NAV_DOMAIN),
                                            data.getString(NAV_DESC));

                data.put(NAV_URL, "");
                data.put(NAV_TITLE, "");
                data.put(NAV_DOMAIN, "");
                data.put(NAV_DESC, "");
            }
        }

    }
}
