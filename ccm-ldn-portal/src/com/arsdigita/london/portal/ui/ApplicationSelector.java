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

package com.arsdigita.london.portal.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

public class ApplicationSelector extends Form {

    private static final Logger s_log = Logger.getLogger(ApplicationSelector.class);

	private ApplicationType m_type;
	private SingleSelect m_apps;
	private DomainObjectParameter m_app;
	private SaveCancelSection m_buttons;

    private PrivilegeDescriptor m_privilege;
	
	public ApplicationSelector(ApplicationType type,
							DomainObjectParameter app) {
								this(type, app, null);	
							}

    public ApplicationSelector(ApplicationType type,
                               DomainObjectParameter app,
                               PrivilegeDescriptor privilege) {
		super("applicationSelector");

		m_type = type;
		m_app = app;
        m_privilege = privilege;
        
        s_log.debug("displayed applications will be filtered by privilege " + m_privilege);

		m_apps = new SingleSelect(new DomainObjectParameter("apps"));
		m_apps.addValidationListener(new NotNullValidationListener());
		try {
			m_apps.addPrintListener(new AppPrintListener());
		} catch (TooManyListenersException ex) {
			throw new UncheckedWrapperException("this cannot happen", ex);
		}
		add(m_apps);

		m_buttons = new SaveCancelSection(new SimpleContainer());
		add(m_buttons);

		addProcessListener(new AppProcessListener());
		addSubmissionListener(new AppSubmissionListener());
	}

	private class AppSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent e) 
            throws FormProcessException {
			if (m_buttons.getCancelButton().isSelected(e.getPageState())) {
				s_log.debug("Firing event for cancel");
				fireCompletionEvent(e.getPageState());
				throw new FormProcessException("canncelled");
			}
			s_log.debug("Falling through for process");
		}
	}
	private class AppProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e) 
            throws FormProcessException {
			s_log.debug("Firing event for process");

			PageState state = e.getPageState();

			state.setValue(m_app, m_apps.getValue(state));
			fireCompletionEvent(e.getPageState());
		}
	}
	private class AppPrintListener implements PrintListener {
		public void prepare(PrintEvent e) {
			ApplicationCollection apps = Application.retrieveAllApplications();
            apps.addEqualsFilter("resourceType.id",
                                 m_type.getID());
            if (m_privilege != null) {
            	Party user = Kernel.getContext().getParty();
            	if (user == null) {
            		user = Kernel.getPublicUser();
            	}
            	PermissionService.filterObjects(apps,m_privilege, user.getOID());
            }
			apps.addOrder("primaryURL");

			SingleSelect t = (SingleSelect) e.getTarget();
            t.addOption(new Option(null,
                                   "--select one--"));
			while (apps.next()) {
				Application app = apps.getApplication();
                t.addOption(new Option(app.getOID().toString(),
                                       app.getTitle() + " (" + app.getPath() + ")"));
			}
		}
	}
}
