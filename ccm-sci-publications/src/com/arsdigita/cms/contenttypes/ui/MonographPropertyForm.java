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

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.volume").
                localize()));
        ParameterModel volumeParam = new IntegerParameter(Monograph.VOLUME);
        TextField volume = new TextField(volumeParam);
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.numberOfVolumes").localize()));
        ParameterModel numberOfVolumesParam = new IntegerParameter(
                Monograph.NUMBER_OF_VOLUMES);
        TextField numberOfVolumes = new TextField(numberOfVolumesParam);
        add(numberOfVolumes);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.numberOfPages").localize()));
        ParameterModel numberOfPagesParam = new IntegerParameter(
                Monograph.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.monograph.edition").localize()));
        ParameterModel editionParam = new StringParameter(Monograph.EDITION);
        TextField edition = new TextField(editionParam);
        add(edition);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.initBasicWidgets(fse);

        data.put(Monograph.VOLUME, monograph.getVolume());
        data.put(Monograph.NUMBER_OF_VOLUMES, monograph.getNumberOfVolumes());
        data.put(Monograph.NUMBER_OF_PAGES, monograph.getNumberOfPages());
        data.put(Monograph.EDITION, monograph.getEdition());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Monograph monograph = (Monograph) super.processBasicWidgets(fse);

        if ((monograph != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            monograph.setVolume((Integer) data.get(Monograph.VOLUME));
            monograph.setNumberOfVolumes((Integer) data.get(
                    Monograph.NUMBER_OF_VOLUMES));
            monograph.setNumberOfPages((Integer) data.get(
                    Monograph.NUMBER_OF_PAGES));
            monograph.setEdition((String) data.get(Monograph.EDITION));

            monograph.save();
        }      
    }
}
