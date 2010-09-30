package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
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

    private MonographPropertiesStep m_step;
    public static final String ID = "MonographEdit";

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
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.initBasicWidgets(fse);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.processBasicWidgets(fse);              
    }
}
