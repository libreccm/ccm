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
 */
package com.arsdigita.cms.contenttypes.ldn.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ldn.ESDService;
import com.arsdigita.cms.contenttypes.ldn.util.ESDServiceGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 * Form to edit basic properties of <code>ESDService</code> content type object.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServicePropertiesForm.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ESDServicePropertiesForm extends BasicPageForm {

    /** Name of this form */
    private static final String ID = "ESDService_edit";
    
    private final ESDServicePropertiesStep step;

    /**
     * @param itemModel
     * @param step
     */
    public ESDServicePropertiesForm(ItemSelectionModel itemModel, ESDServicePropertiesStep step) {
        super(ID, itemModel);
        this.step = step;
    }

    /**
     * Adds widgets to the form.
     **/
    @Override
    protected void addWidgets() {

        /* Add standard widgets Title & name/url    */
        super.addWidgets();

        add(new Label(ESDServiceGlobalizationUtil.globalize(
                "london.contenttypes.ui.esdservice.servicetimes")));
        ParameterModel serviceTimesParam = new StringParameter(ESDService.SERVICE_TIMES);
        TextField serviceTimes = new TextField(serviceTimesParam);
        add(serviceTimes);
    }

    /**
     * Initialize Form with values if already set.
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        ESDService esdService = (ESDService) super.initBasicWidgets(fse);
        data.put(ESDService.SERVICE_TIMES, esdService.getServiceTimes());
    }

    /**
     * Process this form and set the values from form.
     * 
     * @param fse
     */
    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        ESDService esdService = (ESDService) super.processBasicWidgets(fse);

        // save only if save button was pressed
        if (esdService != null
            && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            esdService.setServiceTimes((String) data.get(ESDService.SERVICE_TIMES));
            
            step.maybeForwardToNextStep(fse.getPageState());
        }
    }

}
