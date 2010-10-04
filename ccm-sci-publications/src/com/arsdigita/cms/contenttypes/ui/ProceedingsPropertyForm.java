package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.ui.ItemSearchWidget;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ProceedingsPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "organizerOfConference";
    public static final String ID = "proceedingsEdit";

    public ProceedingsPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ProceedingsPropertyForm(ItemSelectionModel itemModel,
                                   ProceedingsPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.organizer_of_conference").
                localize()));
        m_itemSearch =
        new ItemSearchWidget(ITEM_SEARCH,
                             ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        add(m_itemSearch);

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.name_of_conference")));
        ParameterModel nameOfConfParam = new StringParameter(
                Proceedings.NAME_OF_CONFERENCE);
        TextField nameOfConf = new TextField(nameOfConfParam);
        add(nameOfConf);

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.place_of_conference")));
        ParameterModel placeOfConfParam = new StringParameter(
                Proceedings.PLACE_OF_CONFERENCE);
        TextField placeOfConf = new TextField(placeOfConfParam);
        add(placeOfConf);

        Calendar today = new GregorianCalendar();
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_from_of_conference")));
        ParameterModel dateFromParam = new DateParameter(
                Proceedings.DATE_FROM_OF_CONFERENCE);
        Date dateFrom = new Date(dateFromParam);
        dateFrom.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        add(dateFrom);

        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.date_to_of_conference")));
        ParameterModel dateToParam = new DateParameter(
                Proceedings.DATE_TO_OF_CONFERENCE);
        Date dateTo = new Date(dateToParam);
        dateTo.setYearRange(1900, today.get(Calendar.YEAR) + 3);
        add(dateTo);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        Proceedings proceedings = (Proceedings) super.initBasicWidgets(fse);

        data.put(ITEM_SEARCH, proceedings.getOrganizerOfConference());
        data.put(Proceedings.NAME_OF_CONFERENCE,
                 proceedings.getNameOfConference());
        data.put(Proceedings.PLACE_OF_CONFERENCE,
                 proceedings.getPlaceOfConference());
        data.put(Proceedings.DATE_FROM_OF_CONFERENCE,
                 proceedings.getDateFromOfConference());
        data.put(Proceedings.DATE_TO_OF_CONFERENCE,
                 proceedings.getDateToOfConference());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Proceedings proceedings = (Proceedings) super.processBasicWidgets(fse);


        if ((proceedings != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            proceedings.setOrganizerOfConference(
                    (GenericOrganizationalUnit) data.get(ITEM_SEARCH));
            proceedings.setNameOfConference((String) data.get(
                    Proceedings.NAME_OF_CONFERENCE));
            proceedings.setPlaceOfConference((String) data.get(
                    Proceedings.PLACE_OF_CONFERENCE));
            proceedings.setDateFromOfConference((java.util.Date) data.get(
                    Proceedings.DATE_FROM_OF_CONFERENCE));
            proceedings.setDateToOfConference((java.util.Date) data.get(
                    Proceedings.DATE_TO_OF_CONFERENCE));

            proceedings.save();
        }
    }
}
