/*
 * Copyright (C) 2014 Jens Pelzetter All Rights Reserved.
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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.HistoricDate;
import com.arsdigita.cms.contenttypes.HistoricDateGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class HistoricDatePropertyForm extends BasicPageForm implements FormProcessListener,
                                                                       FormSubmissionListener,
                                                                       FormInitListener {

    public static final String FORM_ID = "historic_date_edit";
    private HistoricDatePropertiesStep step;
    private static final String APPROX = "approx";
    private CheckboxGroup dateIsApprox;

    public HistoricDatePropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public HistoricDatePropertyForm(final ItemSelectionModel itemModel,
                                    final HistoricDatePropertiesStep step) {
        super(FORM_ID, itemModel);
        this.step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(HistoricDateGlobalizationUtil.globalize("historicdate.ui.year")));
        final IntegerParameter yearParam = new IntegerParameter(HistoricDate.YEAR);
        final TextField year = new TextField(yearParam);
        year.setLabel(HistoricDateGlobalizationUtil.globalize("historicdate.ui.year"));
        add(year);

        add(new Label(HistoricDateGlobalizationUtil.globalize("historicdate.ui.month")));
        final IntegerParameter monthParam = new IntegerParameter(HistoricDate.MONTH);
        final TextField month = new TextField(monthParam);
        month.setLabel(HistoricDateGlobalizationUtil.globalize("historicdate.ui.month"));
        add(month);

        add(new Label(HistoricDateGlobalizationUtil.globalize("historicdate.ui.day_of_month")));
        final IntegerParameter dayOfMonthParam = new IntegerParameter(HistoricDate.DAY_OF_MONTH);
        final TextField dayOfMonth = new TextField(dayOfMonthParam);
        dayOfMonth.setLabel(HistoricDateGlobalizationUtil.globalize("historicdate.ui.day_of_month"));
        add(dayOfMonth);

        add(new Label(HistoricDateGlobalizationUtil.globalize("historicdate.ui.date_is_approx")));
        dateIsApprox = new CheckboxGroup(HistoricDate.DATE_IS_APPROX + "Group");
        dateIsApprox.setLabel(HistoricDateGlobalizationUtil.globalize(
            "historicdate.ui.date_is_approx"));
        dateIsApprox.addOption(new Option(HistoricDate.DATE_IS_APPROX, ""));
        add(dateIsApprox);

        add(new Label(HistoricDateGlobalizationUtil.globalize("historicdate.ui.lead")));
        final ParameterModel leadParam = new StringParameter(HistoricDate.LEAD);
        final TextArea lead = new TextArea(leadParam);
        lead.setCols(80);
        lead.setRows(24);
        lead.setLabel(HistoricDateGlobalizationUtil.globalize("historicdate.ui.lead"));
        add(lead);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final HistoricDate historicDate = (HistoricDate) super.initBasicWidgets(event);

        data.put(HistoricDate.YEAR, historicDate.getYear());
        data.put(HistoricDate.MONTH, historicDate.getMonth());
        data.put(HistoricDate.DAY_OF_MONTH, historicDate.getDayOfMonth());

        if ((historicDate.getDateIsApprox() == null) || !historicDate.getDateIsApprox()) {
            dateIsApprox.setValue(event.getPageState(), "");
        } else {
            dateIsApprox.setValue(event.getPageState(), new String[]{APPROX});
        }

        data.put(HistoricDate.LEAD, historicDate.getLead());
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final HistoricDate historicDate = (HistoricDate) super.processBasicWidgets(event);

        if ((historicDate != null)
            && getSaveCancelSection().getSaveButton().isSelected(event.getPageState())) {
            historicDate.setYear((Integer) data.get(HistoricDate.YEAR));
            historicDate.setMonth((Integer) data.get(HistoricDate.MONTH));
            historicDate.setDayOfMonth((Integer) data.get(HistoricDate.DAY_OF_MONTH));

            if (dateIsApprox.getValue(event.getPageState()) == null) {
                historicDate.setDateIsApprox(false);
            } else {
                historicDate.setDateIsApprox(true);
            }

            historicDate.setLead((String) data.get(HistoricDate.LEAD));

            historicDate.save();
        }

        if (step != null) {
            step.maybeForwardToNextStep(event.getPageState());
        }

    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        if ((step != null)
            && getSaveCancelSection().getCancelButton().isSelected(event.getPageState())) {
            step.cancelStreamlinedCreation(event.getPageState());
        }
    }

}
