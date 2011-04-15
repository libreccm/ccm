/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.ui.admin;


import java.math.BigDecimal;

import java.util.HashMap;

import com.arsdigita.xml.Element;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.CompoundComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;

import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.bebop.form.Submit;


import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.web.ApplicationTypeCollection;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.PortletTypeCollection;

import org.apache.log4j.Logger;

/**
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/ApplicationCreateComponent.java#5 $
 */
public class ApplicationCreateComponent extends CompoundComponent {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/ApplicationCreateComponent.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log = Logger.getLogger
        (ApplicationCreateComponent.class);

    private RequestLocal m_appTypeRL;

    private HashMap m_configureComponents;

    public interface Builder {
        public Component build(ResourceConfigFormSection acfs);
    }

    public static class DefaultBuilder implements Builder {
        private final ActionListener m_onSuccess;
        private final ActionListener m_onCancel;

        public DefaultBuilder(ActionListener onSuccess,
                              ActionListener onCancel) {
            m_onSuccess = onSuccess;
            m_onCancel = onCancel;
        }

        public Component build(final ResourceConfigFormSection acfs) {
            final Form f = new Form("ac", new GridPanel(1));
            f.add(acfs);
            BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
            final Submit create = new Submit("Create");
            final Submit cancel = new Submit("Cancel");
            buttons.add(create);
            buttons.add(cancel);
            f.add(buttons);
            // XXX: label on create button
            f.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent ev)
                      throws FormProcessException {
                      PageState ps = ev.getPageState();
                      if (create.isSelected(ps)) {
                              acfs.createResource(ps).save();
                            m_onSuccess.actionPerformed(new ActionEvent(f, ps));
                        } else if (cancel.isSelected(ps)) {
                            m_onCancel.actionPerformed(new ActionEvent(f, ps));
                        }
                    }
                });

            return f;
        }
    }

    public ApplicationCreateComponent(RequestLocal appTypeRL,
                                      RequestLocal parentApplicationRL,
                                      boolean forPortlets,
                                      ActionListener onSuccess,
                                      ActionListener onCancel) {
        this(appTypeRL, parentApplicationRL, forPortlets,
             new DefaultBuilder(onSuccess, onCancel));
    }

    public ApplicationCreateComponent(RequestLocal appTypeRL,
                                      RequestLocal parentApplicationRL,
                                      boolean forPortlets,
                                      Builder builder) {
        m_appTypeRL = appTypeRL;

        m_configureComponents = new HashMap();

        if (forPortlets) {
            s_log.debug("Building a portlet create component");

            PortletTypeCollection types =
                PortletType.retrieveAllPortletTypes();

            while (types.next()) {
                PortletType type = types.getPortletType();

                ResourceConfigFormSection config = type.getCreateFormSection
                    (parentApplicationRL);

                // XXX LOOKY - This incorrectly returns a BasicResourceConfigFormSection:
                //
                // DEBUG admin.ApplicationCreateComponent - Fetched
                // config
                // com.arsdigita.kernel.ui.BasicResourceConfigFormSection@78a2af6a
                // for type 'Knowledge Items' with ID 155

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Fetched config " + config + " for type '" +
                                type.getTitle() + "' with ID " + type.getID());
                }

                if (config != null) {
                    Component c = builder.build(config);
                    m_configureComponents.put(type.getID(), c);
                    add(c);
                }
            }
        } else {
            s_log.debug("Building an application create component");

            ApplicationTypeCollection types =
                ApplicationType.retrieveAllApplicationTypes();

            while (types.next()) {
                ApplicationType type = types.getApplicationType();

                ResourceConfigFormSection config =
                    type.getCreateFormSection(parentApplicationRL);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Fetched config " + config + " for type '" +
                                type.getTitle() + "'");
                }

                if (config != null) {
                    Component c = builder.build(config);
                    m_configureComponents.put(type.getID(), c);
                    add(c);
                }
            }
        }
    }

    public Component get(PageState ps) {
        ResourceType rt = (ResourceType) m_appTypeRL.get(ps);

        if (!canCreate(rt)) {
            throw new IllegalStateException("can not display form for a: "
                                            + rt.getTitle());
        }

        return (Component) m_configureComponents.get(rt.getID());
    }

    public void generateXML(PageState ps, Element parentElt) {
        Component c = get(ps);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating XML of component " + c + " fetched " +
                        "using ID " + ((ResourceType) m_appTypeRL.get(ps)).getID());
        }

        c.generateXML(ps, parentElt);
    }

    public boolean canCreate(BigDecimal appTypeID) {
        return m_configureComponents.containsKey(appTypeID);
    }

    public boolean canCreate(ResourceType rt) {
        return m_configureComponents.containsKey(rt.getID());
    }
}
