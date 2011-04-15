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


import com.arsdigita.portalserver.util.GlobalizationUtil; 

import java.math.BigDecimal;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ComponentSelectionModel;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SplitWizard;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;
import com.arsdigita.portalserver.PortalSite;
import com.arsdigita.portalserver.PortalPage;
import com.arsdigita.portal.Portlet;
import com.arsdigita.xml.Element;
import javax.servlet.ServletException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * <b><strong>Experimental</strong></b>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/ApplicationsPane.java#7 $
 */
public final class ApplicationsPane {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/ui/admin/ApplicationsPane.java#7 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/17 23:19:25 $";

    private static final Logger s_log =
        Logger.getLogger(ApplicationsPane.class.getName());

    private ApplicationsPane() { } // To prevent construction.

    public static Component create(final RequestLocal portalsiteRL) {
        final BigDecimalParameter appTypeParam = new BigDecimalParameter("at");
        final SplitWizard sw = new SplitWizard
            (new Label("Please select an application type from the " +
                       "list on the left.")) {
                public void respond(PageState ps)
                    throws javax.servlet.ServletException {
                    String name = ps.getControlEventName();
                    String value = ps.getControlEventValue();

                    if ("apptype".equals(name)) {
                        ps.setValue(appTypeParam, new BigDecimal(value));
                    } else {
                        super.respond(ps);
                    }
                }

                public void register(Page p) {
                    super.register(p);
                    p.addComponentStateParam(this, appTypeParam);
                }
            };

        GridPanel appTypePanel = new GridPanel(1);
        ApplicationTypeCollection atc =
            ApplicationType.retrieveAllApplicationTypes();
        atc.filterToFullPageViewable();
        atc.filterToWorkspaceApplication();
        atc.orderByTitle();

        Map map = new HashMap();
        while (atc.next()) {
            final BigDecimal id = atc.getID();

            appTypePanel.add(new ControlLink(atc.getTitle()) {
                    public void setControlEvent(PageState ps) {
                        ps.setControlEvent(sw, "apptype", id.toString());
                    }
                });

            ApplicationType appType = atc.getApplicationType();
            appType.disconnect();
            Component c = editView(portalsiteRL, appType, true);
            sw.add(c);
            map.put(atc.getID(), c);
        }

        SimpleContainer leftPanel = new SimpleContainer();
        Label header = new Label(GlobalizationUtil.globalize("cw.workspace.ui.admin.tool_type"));
        header.setFontWeight(Label.BOLD);
        leftPanel.add(header);
        leftPanel.add(appTypePanel);

        sw.setSelector(leftPanel);
        sw.setSelectionModel(new MapComponentSelector(map, appTypeParam));

        return sw;
    }

    private static class MapComponentSelector
        extends AbstractSingleSelectionModel
        implements ComponentSelectionModel {

        private Map m_componentMap;
        private ParameterModel m_model;

        MapComponentSelector(Map componentMap, ParameterModel model) {
            m_componentMap = componentMap;
            m_model = model;
        }

        public Object getSelectedKey(PageState ps) {
            return ps.getValue(m_model);
        }

        public void setSelectedKey(PageState ps, Object key) {
            ps.setValue(m_model, key);
        }

        public Component getComponent(PageState ps) {
            return (Component) m_componentMap.get(getSelectedKey(ps));
        }

        public ParameterModel getStateParameter() { return m_model; }
    }

    static Component editView(final RequestLocal portalsiteRL,
                              final ApplicationType type,
                              boolean fullPagePortal) {

        // app or portlet
        final BigDecimalParameter selectedParam =
            new BigDecimalParameter("sp");

        final ModalContainer container = new ModalContainer() {
                public void register(Page p) {
                    super.register(p);
                    p.addComponentStateParam(this, selectedParam);
                }
            };

        final ActionListener reset = new ActionListener() {
                public void actionPerformed(ActionEvent ev) {
                    PageState ps = ev.getPageState();
                    ps.reset(container);
                    ((PortalPage) ps.getPage()).goUnmodal(ps);
                    container.setVisible(ps, true);
                }
            };

        final RequestLocal selectedPortletRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    BigDecimal portletID =
                        (BigDecimal) ps.getValue(selectedParam);
                    if (portletID == null) {
                        return null;
                    }
                    return Portlet.retrievePortlet(portletID);
                }
            };

        final Component portletModifyComponent =
            new ApplicationModifyComponent(selectedPortletRL, true,
                                           reset, reset);

        final RequestLocal selectedAppRL = new RequestLocal() {
                protected Object initialValue(PageState ps) {
                    BigDecimal appID =
                        (BigDecimal) ps.getValue(selectedParam);
                    if (appID == null) {
                        return null;
                    }
                    return Application.retrieveApplication(appID);
                }
            };

        ResourceConfigFormSection appModify =
            type.getModifyFormSection(selectedAppRL);

        final Component appModifyForm =
            new ApplicationModifyComponent.DefaultBuilder
            (selectedAppRL, reset, reset).build(appModify);

        final ResourceConfigFormSection appCreate =
            type.getCreateFormSection(portalsiteRL);

        final Component appCreateForm =
            new ApplicationCreateComponent.DefaultBuilder
            (reset, reset).build(appCreate);

        final Component appsDisplay = 
            new ApplicationsDisplay(portalsiteRL, type, fullPagePortal) {
              public void respond(PageState ps) throws ServletException {
                String name = ps.getControlEventName();
                String value = ps.getControlEventValue();
                if (ApplicationsDisplay.CREATE.equals(name)) {
                    ((PortalPage) ps.getPage()).goModal(ps, appCreateForm);
                } else if (ApplicationsDisplay.CONFIG_PORTLET.equals(name)) {
                    ((PortalPage)ps.getPage()).goModal(ps, portletModifyComponent);
                    ps.setValue(selectedParam, new BigDecimal(value));
                } else if (ApplicationsDisplay.CONFIG_APP.equals(name)) {
                    ((PortalPage) ps.getPage()).goModal(ps, appModifyForm);
                     ps.setValue(selectedParam, new BigDecimal(value));
                    } else {
                        super.respond(ps);
                    }
                }
            };

        container.add(appsDisplay);
        container.add(appCreateForm);
        container.add(portletModifyComponent);
        container.add(appModifyForm);
        container.setDefaultComponent(appsDisplay);

        return container;
    }

    private static class ApplicationsDisplay extends SimpleComponent {
      static final String CREATE = "c";
      static final String CONFIG_PORTLET = "cp";
      static final String CONFIG_APP = "ca";

      private RequestLocal m_portalsiteRL;

      private ControlLink m_confLink;
      private Label m_linkLabel;
      private ControlLink m_link;
      private ApplicationType m_appType;
      private boolean m_fullPagePortal;

      ApplicationsDisplay(RequestLocal portalsiteRL, ApplicationType type,
                          boolean fullPagePortal) {
          m_portalsiteRL = portalsiteRL;
          m_linkLabel = new Label("");
          m_link = new ControlLink(m_linkLabel);
          Image i = new Image("/assets/general/Edit16.gif");
          i.setBorder("0");
          m_confLink = new ControlLink(i);
          m_appType = type;
          m_fullPagePortal = fullPagePortal;
      }

      public void generateXML(PageState ps, Element parent) {
          PortalSite psite = (PortalSite) m_portalsiteRL.get(ps);
          parent = parent.newChildElement("portalserver:appsDisplay",
                                          PortalPage.PORTAL_XML_NS);

          parent.addAttribute("name", m_appType.getTitle());

          ApplicationCollection apps;

          if (m_fullPagePortal) {
              apps = psite.getFullPagePortalSiteApplications();
          } else {
              apps = psite.getChildApplications();
          }

          apps.filterToApplicationType(m_appType.getApplicationObjectType());
          apps.orderByTitle();

          String currentObjectType = "";

          if (!m_appType.isSingleton() || apps.size() == 0) {
              m_linkLabel.setLabel( (String) GlobalizationUtil.globalize("cw.workspace.ui.admin.create_new").localize() + " " + m_appType.getTitle(), ps);
              ps.setControlEvent(this, ApplicationsDisplay.CREATE, "");
              m_link.setClassAttr("actionLink");
              m_link.generateXML(ps, parent);
          }

          while (apps.next()) {
              Element appElt = 
                parent.newChildElement("portalserver:appsDisplayApp",
                                       PortalPage.PORTAL_XML_NS);

              m_linkLabel.setLabel(apps.getTitle(), ps);
              new Link(m_linkLabel, apps.getPrimaryURL()).generateXML( ps, appElt);

              Element descr = 
               appElt.newChildElement("portalserver:appsDisplayAppDescription",
                                              PortalPage.PORTAL_XML_NS);
              descr.setText(apps.getDescription());

              ps.setControlEvent(this, ApplicationsDisplay.CONFIG_APP,
                                 apps.getID().toString());
              m_confLink.generateXML(ps, appElt);

              ApplicationCollection portlets =
                  apps.getApplication().getChildApplications();
              while (portlets.next()) {
                  Element port = 
                    appElt.newChildElement("portalserver:appsDisplayAppPortlet",
                                            PortalPage.PORTAL_XML_NS);
                  port.addAttribute("name", portlets.getTitle());

                  ps.setControlEvent(this,
                                     ApplicationsDisplay.CONFIG_PORTLET,
                                     portlets.getID().toString());
                  m_confLink.generateXML(ps, port);
              }
            }
        }
    }
}
