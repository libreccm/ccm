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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.WorkingPaper;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperPropertyForm
        extends UnPublishedPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private static final String REVIEWED = "reviewed";
    private WorkingPaperPropertiesStep m_step;
    public static final String ID = "WorkingPaperEdit";
    private CheckboxGroup reviewed;

    public WorkingPaperPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public WorkingPaperPropertyForm(ItemSelectionModel itemModel,
                                    WorkingPaperPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.workingpaper.reviewed")));
        reviewed = new CheckboxGroup("reviewedGroup");
        reviewed.addOption(new Option(REVIEWED, ""));
        add(reviewed);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        WorkingPaper paper = (WorkingPaper) super.initBasicWidgets(fse);

        if ((paper.getReviewed() != null) && (paper.getReviewed())) {
            reviewed.setValue(fse.getPageState(), new String[]{REVIEWED});
        } else {
            reviewed.setValue(fse.getPageState(), null);
        }
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        WorkingPaper paper = (WorkingPaper) super.processBasicWidgets(fse);

        if ((paper != null) && getSaveCancelSection().getSaveButton().isSelected(
                fse.getPageState())) {

            if (reviewed.getValue(fse.getPageState()) == null) {
                paper.setReviewed(false);
            } else {
                paper.setReviewed(true);
            }

            paper.save();
        }
    }
}
