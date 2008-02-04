/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ContentGroup;
import com.arsdigita.cms.contenttypes.ContentGroupContainer;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Authoring step to edit the simple attributes of the Brand content
 * type (and its subclasses). The attributes edited are 
 * 'smallImage', 'largeImage', 'shortDescription', and 'longDescription'
 * <p>
 * This authoring step replaces the 
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public abstract class ContentGroupPropertiesStep extends SimpleEditStep {

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentGroupPropertiesStep.class);

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public ContentGroupPropertiesStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);

        BasicItemForm editSheet;

        editSheet = getPropertyForm(itemModel);
        add( EDIT_SHEET_NAME, "Edit", editSheet,
             editSheet.getSaveCancelSection().getCancelButton() );
        
        setDisplayComponent( getPropertySheet( itemModel ) );
    }


    /**
     * Returns a component that displays the properties of the
     * brand specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public Component getPropertySheet(ItemSelectionModel itemModel) {
        GridPanel panel = new GridPanel(2);
        panel.add(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.content_group_name")));
        panel.add(new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label label = (Label)e.getTarget();
                    ContentGroupContainer item = 
                        (ContentGroupContainer) getItemSelectionModel()
                        .getSelectedObject(e.getPageState());
                    ContentGroup group = 
                        item.getContentGroup(getMainAttributeName());
                    if (group == null) {
                        label.setLabel("None", e.getPageState());
                        label.setFontWeight(Label.ITALIC);
                    } else {
                        label.setLabel(group.getName(), e.getPageState());
                    }
                }
            }));

        panel.add(new ContentGroupItemList(getItemSelectionModel(), 
                                           getMainAttributeName()), 
                  GridPanel.FULL_WIDTH);
                  
        return panel;
    }


    /**
     *  This is the name of the attribute that should be used when processin
     *  the form.  This is needed because the classes are created through
     *  reflection so we cannot just pass this in through the constructor
     */
    public abstract String getMainAttributeName();

    /**
     * Allow subclasses to override the generated form.
     * Used to override the 
     * ContentGroupPropertyForm.getSearchContentType() method
     * to restrict the search widget to one ContentType.
     **/
    protected ContentGroupPropertyForm getPropertyForm(ItemSelectionModel itemModel) {
        return new ContentGroupPropertyForm( itemModel, 
                                             getMainAttributeName() );
    }
}
