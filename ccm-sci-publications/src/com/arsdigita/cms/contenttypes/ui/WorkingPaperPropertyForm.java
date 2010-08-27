package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.ItemSelectionModel;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperPropertyForm
        extends UnPublishedPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private WorkingPaperPropertiesStep m_step;
    public static final String ID = "WorkingPaperEdit";

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
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);
    }
}
