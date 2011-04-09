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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Proceedings;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ProceedingsPropertiesStep m_step;
    public static final String ID = "proceedingsEdit";

    public ProceedingsPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ProceedingsPropertyForm(ItemSelectionModel itemModel,
                                   ProceedingsPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.name_of_conference")));
        ParameterModel nameOfConfParam = new StringParameter(
                Proceedings.NAME_OF_CONFERENCE);
        TextField nameOfConf = new TextField(nameOfConfParam);
        nameOfConf.addValidationListener(new NotNullValidationListener());
        nameOfConf.addValidationListener(new NotEmptyValidationListener());
        add(nameOfConf);

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.place_of_conference")));
        ParameterModel placeOfConfParam = new StringParameter(
                Proceedings.PLACE_OF_CONFERENCE);
        TextField placeOfConf = new TextField(placeOfConfParam);
        placeOfConf.addValidationListener(new NotNullValidationListener());
        placeOfConf.addValidationListener(new NotEmptyValidationListener());
        add(placeOfConf);

        Calendar today = new GregorianCalendar();
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_from_of_conference")));
        ParameterModel dateFromParam = new DateParameter(
                Proceedings.DATE_FROM_OF_CONFERENCE);
        Date dateFrom = new Date(dateFromParam);
        dateFrom.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        dateFrom.addValidationListener(new NotNullValidationListener());
        dateFrom.addValidationListener(new NotEmptyValidationListener());
        add(dateFrom);

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_to_of_conference")));
        ParameterModel dateToParam = new DateParameter(
                Proceedings.DATE_TO_OF_CONFERENCE);
        Date dateTo = new Date(dateToParam);
        dateTo.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        dateTo.addValidationListener(new NotNullValidationListener());
        dateTo.addValidationListener(new NotEmptyValidationListener());
        add(dateTo);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {

        super.init(fse);

        FormData data = fse.getFormData();
        Proceedings proceedings = (Proceedings) super.initBasicWidgets(fse);

        data.put(Proceedings.NAME_OF_CONFERENCE,
                 proceedings.getNameOfConference());
        data.put(Proceedings.PLACE_OF_CONFERENCE,
                 proceedings.getPlaceOfConference());
        data.put(Proceedings.DATE_FROM_OF_CONFERENCE,
                 proceedings.getDateFromOfConference());
        data.put(Proceedings.DATE_TO_OF_CONFERENCE,
                 proceedings.getDateToOfConference());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Proceedings proceedings = (Proceedings) super.processBasicWidgets(fse);


        if ((proceedings != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            proceedings.setNameOfConference((String) data.get(
                    Proceedings.NAME_OF_CONFERENCE));
            proceedings.setPlaceOfConference((String) data.get(
                    Proceedings.PLACE_OF_CONFERENCE));
            proceedings.setDateFromOfConference((java.util.Date) data.get(
                    Proceedings.DATE_FROM_OF_CONFERENCE));
            proceedings.setDateToOfConference((java.util.Date) data.get(
                    Proceedings.DATE_TO_OF_CONFERENCE));

            proceedings.save();
        }
    }
}
