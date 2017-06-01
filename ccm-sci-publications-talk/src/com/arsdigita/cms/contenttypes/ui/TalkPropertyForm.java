package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Talk;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TalkPropertyForm
    extends PublicationPropertyForm
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {

    private TalkPropertiesStep step;
    private static final String ID = "TaskEdit";

    public TalkPropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public TalkPropertyForm(final ItemSelectionModel itemModel,
                            TalkPropertiesStep step) {
        super(itemModel, step);
        this.step = step;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {

        super.addWidgets();

        final ParameterModel placeParameter
                                 = new StringParameter(Talk.PLACE);
        final TextField placeField = new TextField(placeParameter);
        add(placeField);

        final ParameterModel dateParameter
                                 = new DateParameter(Talk.DATE_OF_TALK);
        final Date dateField = new Date(dateParameter);
        add(dateField);

        final ParameterModel eventParameter
                                 = new StringParameter(Talk.EVENT);
        final TextField eventField = new TextField(eventParameter);
        add(eventField);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        super.init(event);

        final FormData data = event.getFormData();
        final Talk talk = (Talk) initBasicWidgets(event);

        data.put(Talk.EVENT, talk.getEvent());
        data.put(Talk.DATE_OF_TALK, talk.getDateOfTalk());
        data.put(Talk.PLACE, talk.getPlace());
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        super.process(event);

        final FormData data = event.getFormData();
        final Talk talk = (Talk) processBasicWidgets(event);

        if (talk != null
                && getSaveCancelSection()
                .getSaveButton()
                .isSelected(event.getPageState())) {
            
            talk.setDateOfTalk((java.util.Date) data.get(Talk.DATE_OF_TALK));
            talk.setEvent((String) data.get(Talk.EVENT));
            talk.setPlace((String) data.get(Talk.PLACE));

            talk.save();
        }

    }

}
