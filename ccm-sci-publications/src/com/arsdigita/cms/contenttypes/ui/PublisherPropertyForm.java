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
import com.arsdigita.cms.contenttypes.Publisher;

/**
 *
 * @author Jens Pelzetter
 */
public class PublisherPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private PublisherPropertiesStep m_step;
    public static final String PLACE = "place";
    public static final String ID = "Publisher_edit";

    public PublisherPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublisherPropertyForm(ItemSelectionModel itemModel,
                                 PublisherPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publisher.place").localize()));
        ParameterModel placeParam = new StringParameter(PLACE);
        TextField place = new TextField(placeParam);
        add(place);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Publisher publisher = (Publisher) super.initBasicWidgets(fse);

        data.put(PLACE, publisher.getPlace());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Publisher publisher = (Publisher) super.processBasicWidgets(fse);

        if ((publisher != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            publisher.setPlace((String) data.get(PLACE));
        }

        publisher.save();

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
