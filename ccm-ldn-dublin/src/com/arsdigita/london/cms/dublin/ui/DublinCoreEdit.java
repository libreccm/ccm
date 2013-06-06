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

package com.arsdigita.london.cms.dublin.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/***
 * Dublin Core authoring kit editing step.
 * 
 * Main entry point into authoring functionality as defined in 
 * DublinCoreInitializer. It just overwrites the constructor to create a page
 * to display the current values and a page to edit the modifiable properties.
 * DublinCoreAddform
 *
 * must call setDisplayComponent();
 * must call addComponent() 0 or more times;
 *
 * @author slater@arsdigita.com
 */
public class DublinCoreEdit extends SimpleEditStep {

    /**
     * Constructor, invokes super class and creates two page elements to display
     * all properties and another one to edit the modifiable ones.
     * 
     * @param itemModel
     * @param parent 
     */
    public DublinCoreEdit(ItemSelectionModel itemModel, 
                          AuthoringKitWizard parent) {    

        super(itemModel, parent);

        
        DublinCoreForm edit = new DublinCoreForm(itemModel);
        DublinCoreSummary display = new DublinCoreSummary(itemModel);
        
        setDisplayComponent(display);
        add("edit", 
            "edit", 
            new WorkflowLockedComponentAccess(edit, itemModel),
            edit.getCancelButton()
           );
    }
}
