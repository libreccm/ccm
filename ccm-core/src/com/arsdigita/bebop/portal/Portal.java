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
 *
 */
package com.arsdigita.bebop.portal;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;
import java.util.Iterator;

/**
 *  <p>A Bebop widget to
 * display a set of portlets in a layout.  A Portal is customarily a
 * very dynamic and configurable component.
 * <p>
 * There are two different ways to change what the Portal shows
 * on each request.  One way is to treat the Portal as a container, rebuild
 * it on each request, and add the desired portlets to it as children.
 *
 * <p>The other way is to use a {@link PortalModelBuilder} and {@link
 * PortalModel}, whose implementations determine what the Portal shows
 * on each request.</p>
 *
 * <p>The first approach allows you to use stateful components in the Portal,
 * but forces you to rebuild the Portal on each request.  The second
 * approach allows the Portal to be cached across requests, but prevents
 * the use of stateful components in the portal.
 *
 * <p>Here is an example use of a Portal and its support classes:</p>
 *
 * <blockquote><pre>
 * private Page buildSomePage() {
 *     Page page = new Page("Some Page");
 *
 *     PortalModelBuilder portalModelBuilder = new PortalModelBuilder() {
 *              // A callback for per-request data.
 *              public PortalModel buildModel(PageState pageState) {
 *                  // SomePortalModel is a subclass of PortalModel that
 *                  // I define for my own particular purposes.
 *                  return new SomePortalModel(with args it needs);
 *              }
 *         };
 *
 *     Portal portal = new Portal(portalModelBuilder);
 *
 *     page.add(portal);
 *
 *     return page;
 * }
 * </pre></blockquote>
 *
 * <p>One critical section of note, in recipe steps:</p>
 *
 * <blockquote><pre>
 * public void generateXML(PageState pageState, Element parent) {
 *     // 1. Get a fresh PortalModel for this request.
 *     // 2. Use the PortalModel to get a set of Portlets.
 *     // 3. Call the generateXML method of each PortletRenderer and attach
 *     //    the new XML created to the parent.
 * }
 * </blockquote></pre>
 *
 * <p>This is the work that gets done for each request.  Note that
 * Portal is not a Bebop {@link com.arsdigita.bebop.Container}, though
 * from the standpoint of the XML it generates, it "contains" {@link
 * Portlet}s.  Instead, the Portal is opaque, and the state of
 * individual Portlets is <em>not</em> managed through {@link
 * Component}-generic services.  This was done to permit the {@link
 * com.arsdigita.bebop.Page}-registered Component hierarchy to remain
 * static across requests.</p>
 *
 * @see PortalModel
 * @see Portlet
 * @see PortalModelBuilder
 * @see PortletRenderer
 * @see AbstractPortletRenderer
 * @author Justin Ross
 * @author James Parsons
 * @version $Id: Portal.java 287 2005-02-22 00:29:02Z sskracic $ */
public class Portal extends SimpleContainer implements BebopConstants {
    public static final String versionId =
        "$Id: Portal.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Portal.class.getName());

    private PortalModelBuilder m_portalModelBuilder;

    private String m_defaultTitle;

    /**
     * Construct a new Portal.  This will ordinarily be done when a
     * Page is built.
     *
     * @param portalModelBuilder the PortalModelBuilder whose method
     * {@link PortalModelBuilder#buildModel(PageState)} gets called
     * when serving a page request.
     *
     * @pre portalModelBuilder != null
     */
    public Portal(PortalModelBuilder portalModelBuilder) {
        super();

        m_portalModelBuilder = portalModelBuilder;
    }

    /**
     * Construct a new Portal.  This will ordinarily be done when a
     * Page is built.  Since the resulting Portal will not be
     * model-backed, Portlets must be added manually as children, and
     * the Portal must be rebuilt on each request.
     *
     * @param title The portal title
     */
    public Portal(String title) {
        super();
        m_defaultTitle = title;
    }

    /**
     * <p>Build an XML fragment and attach it to this component's
     * parent.  It will look like this:</p>
     *
     * <blockquote><pre>
     * &lt;bebop:portal&gt;
     *   &lt;bebop:portlet title="A One Portlet" cellNumber="1"
     *       profile="narrow"&gt;
     *     &lt;!-- Some Bebop XML --&gt;
     *   &lt;/bebop:portlet&gt;
     *   &lt;bebop:portlet title="A Two Portlet" cellNumber="1"
     *       profile="narrow"&gt;
     *     &lt;!-- Some Bebop XML --&gt;
     *   &lt;/bebop:portlet&gt;
     * &lt;/bebop:portal&gt;
     * </pre></blockquote>
     *
     * @param pageState the PageState for the current request.
     * @param parent the element to which to attach the XML
     * this method creates.
     * @pre pageState != null
     * @pre parent != null
     */
    public void generateXML(PageState pageState, Element parent) {
        if (isVisible(pageState)) {
            Element portal = parent.newChildElement
                (BEBOP_PORTAL, Component.BEBOP_XML_NS);

            exportAttributes(portal);

            Iterator iter = null;
            String title = null;

            if (m_portalModelBuilder != null) {
                PortalModel pm = m_portalModelBuilder.buildModel(pageState);
                iter = pm.getPortletRenderers();
                title = pm.getTitle();
            } else {
                // no model builder, so treat as container
                title = m_defaultTitle;
                iter = children();
            }

            portal.addAttribute("title", title);

            PortletRenderer renderer = null;

            while (iter.hasNext()) {
                renderer = (PortletRenderer) iter.next();

                try {
                    renderer.generateXML(pageState, portal);
                } catch (Throwable t) {
                    // We catch any and all exceptions here, since we
                    // want to prevent a single buggy portlet from
                    // ruining the entire portal.

                    s_log.error
                        ("Tried to render a portlet and encountered an " +
                         "error.  ", t);
                }
            }
        }
    }
}
