/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.portalworkspace.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.persistence.Filter;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.PortletTypeCollection;
import com.arsdigita.web.Application;

public class PortletTypeForm extends Form {

    private Label m_portletTypeLabel;

    private SingleSelect m_portletType;

    private Submit m_submit;

    private static Logger s_log = Logger.getLogger(PortletTypeForm.class);

    private PrintListener m_portletTypePrintListener = new PrintListener() {
        public void prepare(PrintEvent e) {
            PageState pageState = e.getPageState();

            OptionGroup optionGroup = (OptionGroup) e.getTarget();
            optionGroup.clearOptions();

            PortletTypeCollection portletTypes = PortletType
                    .retrieveAllPortletTypes();
            List excludedTypes = Workspace.getConfig()
                    .getExcludedPortletTypes();
            if (!excludedTypes.isEmpty()) {
                Filter excludedTypesFilter = portletTypes
                        .addFilter(Application.OBJECT_TYPE
                                           + " not in :nonDisplayTypes");
                excludedTypesFilter.set("nonDisplayTypes", excludedTypes);
            }
            User thisUser = (User) Kernel.getContext().getParty();
            if (thisUser == null) {
				// can't actually happen, as user must be logged in at the point
                // when the page with this portal in edit mode was requested
                thisUser = Kernel.getPublicUser();
            }
            Workspace mainWorkspace;
            if (Subsite.getContext().hasSite()) {
                mainWorkspace = (Workspace) Subsite.getContext().getSite()
                        .getFrontPage();
            } else {
                mainWorkspace = Workspace.getDefaultHomepageWorkspace();
            }

            PermissionDescriptor admin = new PermissionDescriptor(
                    PrivilegeDescriptor.ADMIN, mainWorkspace, thisUser);
            if (PermissionService.checkPermission(admin)) {
                s_log.debug(thisUser.getName()
                                    + " has admin rights on the current workspace");
            } else {
                s_log.debug(thisUser.getName()
                                    + " cannot administer the current main workspace");
                List adminTypes = Workspace.getConfig().getAdminPortletTypes();
                if (!adminTypes.isEmpty()) {
                    Filter adminTypesFilter = portletTypes
                            .addFilter(Application.OBJECT_TYPE
                                               + " not in :adminTypes");
                    adminTypesFilter.set("adminTypes", adminTypes);
                }
            }
            portletTypes.addOrder("title");
            while (portletTypes.next()) {
                PortletType portletType = portletTypes.getPortletType();
                Option option = new Option(portletType.getID().toString(),
                                           portletType.getTitle());

                optionGroup.addOption(option);
            }
        }
    };

    /**
     * Default constructor
     */
    public PortletTypeForm() {
        this("portletTypeForm");
    }

    public PortletTypeForm(String name) {
        super(name, new SimpleContainer());
        setRedirecting(true);

        m_portletType = new SingleSelect("portletTypeID");
        try {
            m_portletType.addPrintListener(m_portletTypePrintListener);
        } catch (TooManyListenersException e) {
            /* Nothing here yet. */
        }
        m_portletType.addValidationListener(new NotNullValidationListener(
                "You must select a Portlet Type"));
        add(m_portletType);

        m_submit = new Submit("Add");
        add(m_submit);
    }

    public BigDecimal getPortletType(PageState state) {
        return new BigDecimal((String) m_portletType.getValue(state));
    }
}
