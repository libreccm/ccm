/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Monograph;

/**
 *
 * @author Jens Pelzetter
 */
public class MonographPropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final String REVIEWED = "reviewed";
    private MonographPropertiesStep m_step;
    public static final String ID = "MonographEdit";

    private CheckboxGroup reviewed;

    public MonographPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public MonographPropertyForm(ItemSelectionModel itemModel,
                                 MonographPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();
       
/*        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.reviewed")));
        reviewed = new CheckboxGroup("reviewedGroup");
        reviewed.addOption(new Option(REVIEWED, ""));
        add(reviewed);*/
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.initBasicWidgets(fse);

        if ((monograph.getReviewed() != null) && (monograph.getReviewed())) {
            reviewed.setValue(fse.getPageState(), new String[]{REVIEWED});
        } else {
            reviewed.setValue(fse.getPageState(), null);
        }
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.processBasicWidgets(fse);

        if ((monograph != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            if (reviewed.getValue(fse.getPageState()) == null) {
                monograph.setReviewed(false);
            } else {
                monograph.setReviewed(true);
            }
            
            monograph.save();
        }
    }

   
}
