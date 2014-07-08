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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class JournalPropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(JournalPropertyForm.class);
    private final JournalPropertiesStep m_step;
    public static final String ID = "JournalEdit";

    public JournalPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public JournalPropertyForm(final ItemSelectionModel itemModel, final JournalPropertiesStep step) {
        super(ID, itemModel);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        final ParameterModel symbolModel = new StringParameter(Journal.SYMBOL);
        final TextField symbol = new TextField(symbolModel);
        symbol.setLabel(PublicationGlobalizationUtil.globalize(
                        "publications.ui.journal.symbol"));
        add(symbol);

        final ParameterModel issnParam = new StringParameter(Journal.ISSN);
        final TextField issn = new TextField(issnParam);
        issn.addValidationListener(new ParameterListener() {
            @Override
            public void validate(final ParameterEvent event) throws FormProcessException {
                final ParameterData data = event.getParameterData();
                String value = (String) data.getValue();

                if (value.isEmpty()) {
                    return;
                }

                value = value.replace("-", "");

                if (value.length() != 8) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize("publications.ui.invalid_issn"));
                }

                try {
                    final Long num = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    data.invalidate();
                    data.addError(PublicationGlobalizationUtil.globalize("publications.ui.invalid_issn"));
                }
            }

        });
        issn.setLabel(PublicationGlobalizationUtil.globalize(
                      "publications.ui.journal.issn"));
        add(issn);

        final ParameterModel firstYearParam = new IntegerParameter(Journal.FIRST_YEAR);
        final TextField firstYear = new TextField(firstYearParam);
        firstYear.setLabel(PublicationGlobalizationUtil.globalize(
                           "publications.ui.journal.firstYearOfPublication"));
        add(firstYear);

        final ParameterModel lastYearParam = new IntegerParameter(Journal.LAST_YEAR);
        final TextField lastYear = new TextField(lastYearParam);
        lastYear.setLabel(PublicationGlobalizationUtil.globalize(
                          "publications.ui.journal.lastYearOfPublication"));
        add(lastYear);

        final ParameterModel abstractParam = new StringParameter(Journal.ABSTRACT);
        final TextArea abstractArea = new TextArea(abstractParam);
        abstractArea.setLabel(PublicationGlobalizationUtil.globalize(
                              "publications.ui.journal.abstract"));
        abstractArea.setCols(60);
        abstractArea.setRows(18);
        add(abstractArea);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();

        //Can' use basic initBasicWidgets for journal because we are doing some magic with the title to make it possible
        //to filter for the symbol of the journal
        //final Journal journal = (Journal) super.initBasicWidgets(fse);

        final Journal journal = (Journal) getItemSelectionModel().getSelectedObject(fse.getPageState());

        data.put(CONTENT_ITEM_ID, journal.getID().toString());
        data.put(NAME, journal.getName());
        data.put(TITLE, journal.getTitle());
        data.put(Journal.SYMBOL, journal.getSymbol());
        data.put(Journal.ISSN, journal.getISSN());
        data.put(Journal.FIRST_YEAR, journal.getFirstYear());
        data.put(Journal.LAST_YEAR, journal.getLastYear());
        data.put(Journal.ABSTRACT, journal.getAbstract());

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            data.put(LAUNCH_DATE, journal.getLaunchDate());
            // if launch date is required, help user by suggesting today's date
            if (ContentSection.getConfig().getRequireLaunchDate() && journal.getLaunchDate() == null) {
                data.put(LAUNCH_DATE, new Date());
            }
        }
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        //final Journal journal = (Journal) super.processBasicWidgets(fse);

        final Journal journal = (Journal) getItemSelectionModel().getSelectedObject(fse.getPageState());

        if ((journal != null) && getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

            final String name = (String) data.get(NAME);
            journal.setName(name);

            final String title = (String) data.get(TITLE);
            journal.setTitle(title);

            final String symbol = (String) data.get(Journal.SYMBOL);
            journal.setSymbol(symbol);

            String issn = (String) data.get(Journal.ISSN);
            issn = issn.replace("-", "");
            journal.setISSN(issn);

            final Integer firstYear = (Integer) data.get(Journal.FIRST_YEAR);
            journal.setFirstYear(firstYear);

            final Integer lastYear = (Integer) data.get(Journal.LAST_YEAR);
            journal.setLastYear(lastYear);

            final String abstractStr = (String) data.get(Journal.ABSTRACT);
            journal.setAbstract(abstractStr);

            if (!ContentSection.getConfig().getHideLaunchDate()) {
                journal.setLaunchDate((Date) data.get(LAUNCH_DATE));
            }

            journal.save();
        }
    }

    @Override
    public void submitted(final FormSectionEvent fse) throws FormProcessException {
        if ((m_step != null) && getSaveCancelSection().getCancelButton().isSelected(fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

}
