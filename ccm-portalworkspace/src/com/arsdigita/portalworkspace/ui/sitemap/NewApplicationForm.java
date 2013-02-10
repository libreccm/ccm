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

package com.arsdigita.portalworkspace.ui.sitemap;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;

/**
 * 
 */
public class NewApplicationForm extends Form {

    private SingleSelect m_app;

    /**
     * Constructor.
     */
    public NewApplicationForm() {

        super("newApp", new SimpleContainer("portal:newApplication",
                                            WorkspacePage.PORTAL_XML_NS));

        m_app = new SingleSelect(new OIDParameter("app"));
        ApplicationTypeCollection types = ApplicationType
                                          .retrieveAllApplicationTypes();
        types.addFilter("not(lower(title) like 'admin')");
        types.addOrder("title");
        m_app.addOption(new Option(null, "-- select application --"));
        while (types.next()) {
            ApplicationType type = types.getApplicationType();
            m_app.addOption(new Option(type.getOID().toString(), type
                                                                 .getTitle()));
        }
        m_app.addValidationListener(new NotNullValidationListener());
        add(m_app);

        add(new Submit("Create"));

        addProcessListener(new FormProcessListener() {
            public void process(FormSectionEvent ev)
                        throws FormProcessException {

                fireCompletionEvent(ev.getPageState());
            }
        });
    }

    /**
     * 
     * @param state
     * @return 
     */
    public ApplicationType getApplicationType(PageState state) {
        OID app = (OID) m_app.getValue(state);
        return (ApplicationType) DomainObjectFactory.newInstance(app);
    }
}
