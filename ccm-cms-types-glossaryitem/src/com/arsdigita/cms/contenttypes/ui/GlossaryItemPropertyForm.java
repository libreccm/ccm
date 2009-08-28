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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.GlossaryItem;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit the basic properties of an GlossaryItem. This form can be
 * extended to create forms for GlossaryItem subclasses.
 */
public class GlossaryItemPropertyForm
    extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private GlossaryItemPropertiesStep m_step;

    /** parameter names */
    public static final String DEFINITION = "definition";

    /** Name of this form */
    public static final String ID = "GlossaryItem_edit";
    
    /**
     * Creates a new form to edit the GlossaryItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the GlossaryItem to
     * work on
     **/
    public GlossaryItemPropertyForm( ItemSelectionModel itemModel ) {
        this(itemModel,null);
    }
    /**
     * Creates a new form to edit the GlossaryItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the GlossaryItem to
     * work on
     * @param step The GlossaryItemPropertiesStep which controls this form.
     **/
    public GlossaryItemPropertyForm( ItemSelectionModel itemModel, GlossaryItemPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }


    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        GlossaryItemWidgetBuilder builder = new GlossaryItemWidgetBuilder();
        add(builder.makeDefinitionLabel());
        add(builder.makeDefinitionArea());
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        GlossaryItem glossary_item
            = (GlossaryItem) super.initBasicWidgets( fse );

        data.put( DEFINITION, glossary_item.getDefinition() );
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
 
    /** Form processing hook. Saves GlossaryItem object. */
    public void process( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        
        GlossaryItem glossary_item
            = (GlossaryItem) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( glossary_item != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {
            glossary_item.setDefinition( (String)
                                         data.get( DEFINITION ) );
            glossary_item.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
