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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;


/**
 * An invisible component which contains all the possible authoring kits. 
 * The kits are loaded from the database at construction time. The selector 
 * chooses which kit to display at page rendering time based on the value 
 * of the content_type state parameter.
 * 
 * Essentially, this component is a hack which is used to get around
 * the fact that we cannot instantiate stateful components dynamically.
 *
 * @version $Id: WizardSelector.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class WizardSelector extends AuthoringKitSelector
    implements Resettable {

    private ItemSelectionModel m_itemSel;

    /**
     * Construct a new WizardSelector. Load all the possible authoring kits 
     * from the database and construct wizards for them.
     *
     * @param model the {@link ItemSelectionModel} which will
     *              supply the wizard with its item
     *
     * @param typeModel the {@link ACSObjectSelectionModel} which will
     *                  supply the default content type
     *
     * @pre itemModel != null
     */
    public WizardSelector(ItemSelectionModel model, 
                          SingleSelectionModel typeModel) {
        super(typeModel);
        m_itemSel = model;
        super.processKit();
    }

    /**
     * Get the wizard for the given kit.
     */
    public Component instantiateKitComponent(AuthoringKit kit, 
                                             ContentType type) {

        ItemSelectionModel itemModel = new
            ItemSelectionModel(type,
                               (BigDecimalParameter)m_itemSel.getStateParameter());

        AuthoringKitWizard w = new AuthoringKitWizard(type, itemModel);        
        return w;
    }

    /**
     * @return The item selection model used by this wizard
     */
    public ItemSelectionModel getSelectionModel() {
        return m_itemSel;
    }

    // Determine the current wizard
    private Component getCurrentWizard(PageState state) {

        // Get the current item and extract its content type
        if(!m_itemSel.isSelected(state))
            throw new RuntimeException( (String) GlobalizationUtil.globalize(
                                        "cms.ui.authoring.missing_item_id")
                                        .localize());

        ContentItem item =
            (ContentItem)m_itemSel.getSelectedObject(state);

        ContentType type = item.getContentType();
        BigDecimal typeId;

        if(type == null) {
            // Try to get the default content type
            typeId = (BigDecimal)getComponentSelectionModel().getSelectedKey(state);
            if(typeId == null) {
                throw new RuntimeException((String) GlobalizationUtil.globalize(
                                          "cms.ui.authoring.missing_content_type")
                                          .localize());
            }
        } else {
            typeId = type.getID();
        }
              
        // Return the selected wizard
        return (Component)getComponent(typeId);
    }

    // Choose the right wizard and run it
    public void generateXML(PageState state, Element parent) {

        Component c = getCurrentWizard(state);

        if(c == null) {
            throw new RuntimeException( (String) GlobalizationUtil.globalize(
                                        "cms.ui.authoring.no_current_wizard")
                                        .localize());
        }

        c.generateXML(state, parent);
    }

    /**
     * Reset the state of the current wizard
     */
    public void reset(PageState state) {
        Resettable r = (Resettable)getCurrentWizard(state);
        if(r != null) r.reset(state);
    }


}
