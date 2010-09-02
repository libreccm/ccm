package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InProceedings;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsPropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private InProceedingsPropertiesStep m_step;
    public static final String ID = "InProceedingsEdit";

    public InProceedingsPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public InProceedingsPropertyForm(ItemSelectionModel itemModel,
                                     InProceedingsPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.organizer_of_conference").
                localize()));
        ParameterModel organizerParam =
                       new StringParameter(InProceedings.ORGANIZER_OF_CONFERENCE);
        TextField organizer = new TextField(organizerParam);
        add(organizer);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.name_of_conference").
                localize()));
        ParameterModel nameOfConferenceParam =
                       new StringParameter(InProceedings.NAME_OF_CONFERENCE);
        TextField nameOfConference = new TextField(nameOfConferenceParam);
        add(nameOfConference);

        Calendar today = new GregorianCalendar();
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproccedings.date_to_of_conference").
                localize()));
        ParameterModel dateToParam =
                       new DateParameter(InProceedings.DATE_TO_OF_CONFERENCE);
        com.arsdigita.bebop.form.Date dateTo =
                                      new com.arsdigita.bebop.form.Date(
                dateToParam);
        dateTo.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        add(dateTo);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.data_form_of_conference").
                localize()));
        ParameterModel dateFromParam =
                       new DateParameter(InProceedings.DATE_FROM_OF_CONFERENCE);
        com.arsdigita.bebop.form.Date dateFrom =
                                      new com.arsdigita.bebop.form.Date(
                dateFromParam);
        dateFrom.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        add(dateFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.place_of_conference").
                localize()));
        ParameterModel placeParam = new StringParameter(
                InProceedings.PLACE_OF_CONFERENCE);
        TextField place = new TextField(placeParam);
        add(place);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.volume").localize()));
        ParameterModel volumeParam = new IntegerParameter(InProceedings.VOLUME);
        TextField volume = new TextField(volumeParam);
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.number_of_volumes").localize()));
        ParameterModel numberOfVolumesParam =
                       new IntegerParameter(InProceedings.NUMBER_OF_VOLUMES);
        TextField numberOfVolumes = new TextField(numberOfVolumesParam);
        add(numberOfVolumes);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.number_of_pages").localize()));
        ParameterModel numberOfPagesParam =
                       new IntegerParameter(InProceedings.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.pages_from").localize()));
        ParameterModel pagesFromParam =
                       new IntegerParameter(InProceedings.PAGES_FROM);
        TextField pagesFrom = new TextField(pagesFromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.inproceedings.pages_from").localize()));
        ParameterModel pagesToParam =
                       new IntegerParameter(InProceedings.PAGES_TO);
        TextField pagesTo = new TextField(pagesToParam);
        add(pagesTo);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        InProceedings inProceedings =
                      (InProceedings) super.initBasicWidgets(fse);

        data.put(InProceedings.ORGANIZER_OF_CONFERENCE,
                 inProceedings.getOrganizerOfConference());
        data.put(InProceedings.NAME_OF_CONFERENCE,
                 inProceedings.getNameOfConference());
        data.put(InProceedings.DATE_FROM_OF_CONFERENCE,
                 inProceedings.getDateFromOfConference());
        data.put(InProceedings.DATE_TO_OF_CONFERENCE,
                 inProceedings.getDateToOfConference());
        data.put(InProceedings.PLACE_OF_CONFERENCE,
                 inProceedings.getPlaceOfConference());
        data.put(InProceedings.VOLUME,
                 inProceedings.getVolume());
        data.put(InProceedings.NUMBER_OF_VOLUMES,
                 inProceedings.getNumberOfVolumes());
        data.put(InProceedings.NUMBER_OF_PAGES,
                 inProceedings.getNumberOfPages());
        data.put(InProceedings.PAGES_FROM,
                 inProceedings.getPagesFrom());
        data.put(InProceedings.PAGES_TO,
                 inProceedings.getPagesTo());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        InProceedings inProceedings =
                      (InProceedings) super.processBasicWidgets(fse);

        if ((inProceedings != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            inProceedings.setOrganizerOfConference(
                    (String) data.get(InProceedings.ORGANIZER_OF_CONFERENCE));
            inProceedings.setNameOfConference(
                    (String) data.get(InProceedings.NAME_OF_CONFERENCE));
            inProceedings.setDateFromOfConference(
                    (Date) data.get(InProceedings.DATE_FROM_OF_CONFERENCE));
            inProceedings.setDateToOfConference(
                    (Date) data.get(InProceedings.DATE_TO_OF_CONFERENCE));
            inProceedings.setPlaceOfConference(
                    (String) data.get(InProceedings.PLACE_OF_CONFERENCE));
            inProceedings.setVolume(
                    (Integer) data.get(InProceedings.VOLUME));
            inProceedings.setNumberOfVolumes(
                    (Integer) data.get(InProceedings.NUMBER_OF_VOLUMES));
            inProceedings.setNumberOfPages(
                    (Integer) data.get(InProceedings.NUMBER_OF_PAGES));
            inProceedings.setPagesFrom(
                    (Integer) data.get(InProceedings.PAGES_FROM));
            inProceedings.setPagesTo(
                    (Integer) data.get(InProceedings.PAGES_TO));

            inProceedings.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
