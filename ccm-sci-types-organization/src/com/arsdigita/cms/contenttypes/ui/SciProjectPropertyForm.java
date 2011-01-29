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
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Form for editing the basic properties of a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public static final String ID = "SciProjectEdit";

    public SciProjectPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciProjectPropertyForm(ItemSelectionModel itemModel,
                                  SciProjectPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.begin")));
        ParameterModel beginParam = new DateParameter(SciProject.BEGIN);
        Calendar today = new GregorianCalendar();
        Date begin = new Date(beginParam);
        begin.setYearRange(1970, (today.get(Calendar.YEAR) + 2));
        add(begin);

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.end")));
        ParameterModel endParam = new DateParameter(SciProject.END);
        Date end = new Date(endParam);
        end.setYearRange(1970, (today.get(Calendar.YEAR) + 8));
        add(end);

        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.project.shortdesc")));
        ParameterModel shortDescParam = new StringParameter(
                SciProject.PROJECT_SHORT_DESCRIPTION);
        TextArea shortDesc = new TextArea(shortDescParam);
        shortDesc.setCols(75);
        shortDesc.setRows(5);
        add(shortDesc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.initBasicWidgets(fse);

        data.put(SciProject.BEGIN, project.getBegin());
        data.put(SciProject.END, project.getEnd());
        data.put(SciProject.PROJECT_SHORT_DESCRIPTION,
                 project.getProjectShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciProject project = (SciProject) super.processBasicWidgets(fse);

        if ((project != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            project.setBegin((java.util.Date) data.get(
                    SciProject.BEGIN));
            project.setEnd((java.util.Date) data.get(
                    SciProject.END));
            project.setProjectShortDescription((String) data.get(
                    SciProject.PROJECT_SHORT_DESCRIPTION));

            project.save();

            init(fse);
        }
    }

    @Override
    public String getTitleLabel() {
        return (String) PublicationGlobalizationUtil.globalize(
                "sciorganizations.ui.project.title").localize();
    }
}
