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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ui.ScriptPrinter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Selects a component based on content type. Helper class for {@link
 * com.arsdigita.cms.ui.authoring.WizardSelector}.
 *
 * @version $Id: AuthoringKitSelector.java 2090 2010-04-17 08:04:14Z pboy $
 */
public abstract class AuthoringKitSelector extends SimpleContainer {

    private static Logger s_log =
                          Logger.getLogger(AuthoringKitSelector.class);
    private Map m_comps;
    private MapComponentSelectionModel m_sel;
    ContentTypeCollection m_types;
    private ScriptPrinter scriptPrinter;

    /**
     * Construct a new AuthoringKitSelector. Load all the possible authoring kits from the database and construct
     * components for them.
     *
     * @param model the {@link ItemSelectionModel} which will supply the selector with the id of a content type
     *
     * @pre itemModel != null
     */
    public AuthoringKitSelector(SingleSelectionModel model) {
        super();

        m_comps = new HashMap();
        m_sel = new MapComponentSelectionModel(model, m_comps);

        m_types = ContentType.getAllContentTypes();
        if (m_types.isEmpty()) {
            m_types.close();
            throw new IllegalStateException((String) GlobalizationUtil.globalize(
                    "cms.ui.authoring.no_content_types_were_found").localize());
        }
    }

    // Overloaded add methods
    @Override
    public void add(Component c) {
        throw new UnsupportedOperationException();
    }

    // Overloaded add methods
    @Override
    public void add(Component c, int constraints) {
        throw new UnsupportedOperationException();
    }

    /**
     * Instantiate all the authoring kit wizards. The child class should call this method after it is done with
     * initialization
     */
    protected void processKit() {
        while (m_types.next()) {
            ContentType type = m_types.getContentType();
            AuthoringKit kit = type.getAuthoringKit();
            if (kit != null) {
                Component c = instantiateKitComponent(kit, type);
                if (c != null) {
                    super.add(c);
                    m_comps.put(type.getID(), c);
                    s_log.info("Added component " + c + " for "
                               + type.getAssociatedObjectType());
                }

                if (c instanceof LayoutPanel) {
                    Label label = new Label("", false);
                    label.addPrintListener(new PrintListener() {

                        public void prepare(final PrintEvent event) {
                            final Label label = (Label) event.getTarget();
                            if (scriptPrinter != null) {
                                label.setLabel(scriptPrinter.printScript(event.getPageState()));
                            }
                        }

                    });

                    ((LayoutPanel) c).setBottom(label);
                }
            }
        }
    }

    /**
     * Instantiate an authoring kit component. Child classes should override this to do the right thing. It is
     * permissible for this method to return null.
     *
     * @param kit  for this kit
     * @param type for this type
     */
    protected abstract Component instantiateKitComponent(
            AuthoringKit kit, ContentType type);

    /**
     * @param id The content type id
     *
     * @return The component the given type id
     */
    public Component getComponent(BigDecimal id) {
        return (Component) m_comps.get(id);
    }

    /**
     * @return The selection model used by this wizard
     */
    public MapComponentSelectionModel getComponentSelectionModel() {
        return m_sel;
    }

    // Choose the right component and run it
    public void generateXML(PageState state, Element parent) {
        if (isVisible(state)) {
            Component c = m_sel.getComponent(state);
            if (c == null) {
                throw new IllegalStateException("No component for "
                                                + m_sel.getSelectedKey(state));
            }
            c.generateXML(state, parent);
        }
    }    
}
