/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumePropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
                                        CollectedVolumePropertyForm.class);
    private static final String REVIEWED = "reviewed";
    private CollectedVolumePropertiesStep m_step;
    public static final String ID = "CollectedVolumeEdit";
    private CheckboxGroup reviewed;

    public CollectedVolumePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public CollectedVolumePropertyForm(ItemSelectionModel itemModel,
                                       CollectedVolumePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {

        super.addWidgets();

        reviewed = new CheckboxGroup("reviewedGroup");
        reviewed.addOption(new Option(REVIEWED, ""));
        reviewed.setLabel(PublicationGlobalizationUtil.globalize(
                          "publications.ui.collectedVolume.reviewed"));
        add(reviewed);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.initBasicWidgets(fse);

        if ((collectedVolume.getReviewed() != null)
                &&(collectedVolume.getReviewed())) {
            reviewed.setValue(fse.getPageState(), new String[]{REVIEWED});
        } else {
            reviewed.setValue(fse.getPageState(), null);
        }
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.processBasicWidgets(fse);

        if ((collectedVolume != null) && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            if (reviewed.getValue(fse.getPageState()) == null) {
                collectedVolume.setReviewed(false);
            } else {
                collectedVolume.setReviewed(true);
            }

            collectedVolume.save();
        }
    }
}
