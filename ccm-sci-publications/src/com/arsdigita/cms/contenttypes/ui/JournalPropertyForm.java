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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Journal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class JournalPropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log =
                                Logger.getLogger(JournalPropertyForm.class);
    private JournalPropertiesStep m_step;
    public static final String ID = "JournalEdit";

    public JournalPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public JournalPropertyForm(ItemSelectionModel itemModel,
                               JournalPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.issn").localize()));
        ParameterModel issnParam = new StringParameter(Journal.ISSN);
        TextField issn = new TextField(issnParam);
        issn.addValidationListener(new ParameterListener() {

            public void validate(ParameterEvent event) throws
                    FormProcessException {
                ParameterData data = event.getParameterData();
                String value = (String) data.getValue();

                if (value.isEmpty()) {
                    return;
                }

                value = value.replace("-", "");

                if (value.length() != 8) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_issn"));
                }

                try {
                    Long num = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize(
                            "publications.ui.invalid_issn"));
                }
            }
        });
        add(issn);

        add(new Label((String) PublicationGlobalizationUtil.globalize("publications.ui.journal.lastYearOfPublication").localize()));
        ParameterModel lastYearParam = new IntegerParameter(Journal.LAST_YEAR);
        TextField lastYear = new TextField(lastYearParam);
        add(lastYear);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Journal journal = (Journal) super.initBasicWidgets(fse);

        data.put(Journal.ISSN, journal.getISSN());
        data.put(Journal.LAST_YEAR, journal.getLastYear());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Journal journal = (Journal) super.initBasicWidgets(fse);

        if ((journal != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {

            String issn = (String) data.get(Journal.ISSN);
            issn = issn.replace("-", "");
            journal.setISSN(issn);

            Integer lastYear = (Integer) data.get(Journal.LAST_YEAR);
            journal.setLastYear(lastYear);

            journal.save();
        }
    }
}
