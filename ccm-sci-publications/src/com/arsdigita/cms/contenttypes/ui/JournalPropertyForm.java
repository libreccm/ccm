package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
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
        add(issn);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Journal journal = (Journal) super.initBasicWidgets(fse);

        data.put(Journal.ISSN, journal.getISSN());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Journal journal = (Journal) super.initBasicWidgets(fse);

        if ((journal != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            journal.setISSN((String) data.get(Journal.ISSN));

            journal.save();;
        }
    }
}
