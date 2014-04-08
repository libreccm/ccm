package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.util.ImageStepGlobalizationUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ImageAttachmentEditForm extends Form
    implements FormInitListener, FormProcessListener, FormSubmissionListener {

    final ImageStep imageStep;
    final SaveCancelSection saveCancelSection;

    public ImageAttachmentEditForm(final ImageStep imageStep) {
        super("ImageAttachmentEditForm");

        this.imageStep = imageStep;

        final Label label = new Label(ImageStepGlobalizationUtil.globalize(
            "cms.contentassets.ui.image_step.caption"));

        final TextField captionField = new TextField(CAPTION);

        add(label);
        add(captionField);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(this);
        addProcessListener(this);

    }

    private static final String CAPTION = "caption";

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final ItemImageAttachment attachment = imageStep.getAttachment(event.getPageState());

        event.getFormData().put(CAPTION, attachment.getCaption());
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final ItemImageAttachment attachment = imageStep.getAttachment(event.getPageState());

        attachment.setCaption(event.getFormData().getString(CAPTION));

        attachment.save();

        imageStep.showDisplayPane(event.getPageState());
    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        if (saveCancelSection.getCancelButton().isSelected(event.getPageState())) {
            imageStep.setAttachment(event.getPageState(), null);
            
            imageStep.showDisplayPane(event.getPageState());
        }
    }

}
