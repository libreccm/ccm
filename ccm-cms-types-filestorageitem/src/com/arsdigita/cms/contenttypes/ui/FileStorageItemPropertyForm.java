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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.contenttypes.FileStorageItem;
import com.arsdigita.cms.contenttypes.ui.authoring.FileStorageItemPropertiesStep;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Form to edit the basic properties of a <code>FileStorageItem</code>. These 
 * are title, name
 * 
 * This form can be extended to create forms for FileStorageItem subclasses.
 */
public class FileStorageItemPropertyForm
       extends BasicPageForm
       implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private FileStorageItemPropertiesStep m_step;
    
    /** Name of this form */
    public static final String ID = "FileStorageItem_edit";

    
    /**  *****************************
     *       Constructor Section
     *   *****************************  */

    /**
     * Creates a new form to edit the FileStorageItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the FileStorageItem
     * to work on
     **/
    public FileStorageItemPropertyForm( ItemSelectionModel itemModel ) {
        this(itemModel,null);
    }
    /**
     * Creates a new form to edit the FileStorageItem object specified by the item
     * selection model passed in.
     *
     * @param itemModel The ItemSelectionModel to use to obtain the FileStorageItem
     * to work on
     * @param step The FileStorageItemPropertiesStep which controls this form.
     **/
    public FileStorageItemPropertyForm( ItemSelectionModel itemModel, 
                                        FileStorageItemPropertiesStep step ) {
        super( ID, itemModel );
        m_step = step;
        addSubmissionListener(this);
    }

    
    /**
     * Adds widgets to the form.
     */
    @Override
    protected void addWidgets() {
        super.addWidgets();

     // add( new Label( GlobalizationUtil.globalize("cms.contenttypes.ui.lead") ) );
        ParameterModel descriptionParam = new StringParameter( FileStorageItem
                                                               .DESCRIPTION );
    //   descriptionParam.addParameterListener( new NotNullValidationListener() );
        CMSDHTMLEditor description = new CMSDHTMLEditor( descriptionParam );
        description.setLabel(GlobalizationUtil.globalize("cms.contenttypes.ui.lead"));
        description.setCols( 40 );
        description.setRows( 8 );
        add( description );

    }

    /** 
     * Form initialisation hook. Fills widgets with data. 
     * @param fse
     */
    @Override
    public void init( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        FileStorageItem glossary_item
            = (FileStorageItem) super.initBasicWidgets( fse );

        data.put( FileStorageItem.DESCRIPTION, glossary_item.getDescription() );
    }

    /** 
     * Cancels streamlined editing. 
     * @param fse
     */
    @Override
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
 
    /** Form processing hook. Saves FileStorageItem object.
     * @param fse */
    @Override
    public void process( FormSectionEvent fse ) {
        FormData data = fse.getFormData();
        
        FileStorageItem glossary_item
            = (FileStorageItem) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( glossary_item != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {
            glossary_item.setDescription( (String)
                                          data.get( FileStorageItem.DESCRIPTION ) );
            glossary_item.save();
        }
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
