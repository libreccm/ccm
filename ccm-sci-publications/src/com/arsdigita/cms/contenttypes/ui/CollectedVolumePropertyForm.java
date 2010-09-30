package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
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

    private static final Logger s_log =
                                Logger.getLogger(
            CollectedVolumePropertyForm.class);
    private CollectedVolumePropertiesStep m_step;
    public static final String ID = "CollectedVolumeEdit";

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
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.initBasicWidgets(fse);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.processBasicWidgets(fse);
    }
}
