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
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.Resource;
import com.arsdigita.web.ApplicationTypeCollection;
import com.arsdigita.kernel.ResourceTypeCollection;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.PortletTypeCollection;

public class ApplicationModifyComponent extends CompoundComponent {
    private RequestLocal m_appRL;

    private HashMap m_configureComponents;

    public interface Builder {
        public Component build(ResourceConfigFormSection acfs);
    }

    public static class DefaultBuilder implements Builder {
        private final RequestLocal m_appRL;
        private final ActionListener m_onSuccess;
        private final ActionListener m_onCancel;

        public DefaultBuilder(RequestLocal appRL,
                              ActionListener onSuccess,
                              ActionListener onCancel) {
            m_appRL = appRL;
            m_onSuccess = onSuccess;
            m_onCancel = onCancel;
        }

        public Component build(final ResourceConfigFormSection acfs) {
            final Form f = new Form("ac", new GridPanel(1));
            f.add(acfs);
            BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
            final Submit update = new Submit("Update");
            final Submit cancel = new Submit("Cancel");
            buttons.add(update);
            buttons.add(cancel);
            f.add(buttons);
            // XXX: label on update button
            f.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent ev) {
                        PageState ps = ev.getPageState();
                        if (update.isSelected(ps)) {
                            acfs.modifyResource(ps);
                            ((Resource) m_appRL.get(ps)).save();
                            m_onSuccess.actionPerformed(new ActionEvent(f, ps));
                        } else if (cancel.isSelected(ps)) {
                            m_onCancel.actionPerformed(new ActionEvent(f, ps));
                        }
                    }
                });

            return f;
        }
    }

    public ApplicationModifyComponent(final RequestLocal appRL,
                                      boolean forPortlets,
                                      final ActionListener onSuccess,
                                      final ActionListener onCancel) {
        this(appRL, forPortlets, new DefaultBuilder(appRL,
                                                    onSuccess,
                                                    onCancel));
    }

    public ApplicationModifyComponent(RequestLocal appRL,
                                      boolean forPortlets,
                                      Builder builder) {
        m_appRL = appRL;

        m_configureComponents = new HashMap();
        ResourceTypeCollection atc;
        if (forPortlets) {
            atc = PortletType.retrieveAllPortletTypes();
        } else {
            atc = ApplicationType.retrieveAllApplicationTypes();
        }
        while (atc.next()) {
            ResourceType at;
            if (forPortlets) {
                at = (PortletType)((PortletTypeCollection)atc).getPortletType();
            } else {
                at = (ApplicationType)((ApplicationTypeCollection)atc).getApplicationType();
            }
            ResourceConfigFormSection fs = at.getModifyFormSection(m_appRL);

            if (fs != null) {
                Component c = builder.build(fs);
                m_configureComponents.put(at.getID(), c);
                add(c);
            }
        }
    }

    public Component get(PageState ps) {
        ResourceType type = ((Resource) m_appRL.get(ps)).getResourceType();

        if (!canModify(type.getID())) {
            throw new IllegalStateException
                ("Cannot display form for a " + type.getTitle());
        }

        return (Component) m_configureComponents.get(type.getID());
    }

    public void generateXML(PageState ps, Element parentElt) {
        get(ps).generateXML(ps, parentElt);
    }

    boolean canModify(BigDecimal resTypeID) {
        return m_configureComponents.containsKey(resTypeID);
    }

    boolean canModify(ResourceType rt) {
        return m_configureComponents.containsKey(rt.getID());
    }
}
