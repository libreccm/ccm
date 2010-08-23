package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
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

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.volume").localize()));
        ParameterModel volumeParam = new IntegerParameter(
                CollectedVolume.VOLUME);
        TextField volume = new TextField(volumeParam);
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.number_of_volumes").
                localize()));
        ParameterModel numberOfVolumesParam =
                       new IntegerParameter(CollectedVolume.NUMBER_OF_VOLUMES);
        TextField numberOfVolumes = new TextField(numberOfVolumesParam);
        add(numberOfVolumes);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.number_of_pages").
                localize()));
        ParameterModel numberOfPagesParam = new IntegerParameter(
                CollectedVolume.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.edition").
                localize()));
        ParameterModel editionModel = new StringParameter(
                CollectedVolume.EDITION);
        TextField edition = new TextField(editionModel);
        add(edition);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.initBasicWidgets(fse);

        data.put(CollectedVolume.VOLUME, collectedVolume.getVolume());
        data.put(CollectedVolume.NUMBER_OF_VOLUMES,
                 collectedVolume.getNumberOfVolumes());
        data.put(CollectedVolume.NUMBER_OF_PAGES,
                 collectedVolume.getNumberOfPages());
        data.put(CollectedVolume.EDITION, collectedVolume.getEdition());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        CollectedVolume collectedVolume =
                        (CollectedVolume) super.processBasicWidgets(fse);

        if ((collectedVolume != null) && getSaveCancelSection().
                getSaveButton().isSelected(fse.getPageState())) {
            collectedVolume.setVolume((Integer) data.get(
                    CollectedVolume.VOLUME));
            collectedVolume.setNumberOfVolumes((Integer) data.get(
                    CollectedVolume.NUMBER_OF_VOLUMES));
            collectedVolume.setNumberOfPages((Integer) data.get(
                    CollectedVolume.NUMBER_OF_PAGES));
            collectedVolume.setEdition((String) data.get(
                    CollectedVolume.EDITION));

            collectedVolume.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
