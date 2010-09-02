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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.ui.ItemSearchWidget;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzeter
 */
public class InternetArticlePropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private InternetArticlePropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "organization";
    public static final String ID = "InternetArticleEdit";

    public InternetArticlePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public InternetArticlePropertyForm(ItemSelectionModel itemModel,
                                       InternetArticlePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.place").localize()));
        ParameterModel placeParam =
                       new StringParameter(InternetArticle.PLACE);
        TextField place = new TextField(placeParam);
        add(place);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.organization").localize()));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                            ContentType.
                findByAssociatedObjectType(
                GenericOrganizationalUnit.BASE_DATA_OBJECT_TYPE));
        add(m_itemSearch);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number").localize()));
        ParameterModel numberParam =
                       new StringParameter(InternetArticle.NUMBER);
        TextField number = new TextField(numberParam);
        add(number);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.number_of_pages").localize()));
        ParameterModel numberOfPagesParam =
                       new IntegerParameter(InternetArticle.NUMBER_OF_PAGES);
        TextField numberOfPages = new TextField(numberOfPagesParam);
        add(numberOfPages);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.edition").localize()));
        ParameterModel editionParam =
                       new StringParameter(InternetArticle.EDITION);
        TextField edition = new TextField(editionParam);
        add(edition);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publicatons.ui.internetarticle.issn").localize()));
        ParameterModel issnParam =
                       new StringParameter(InternetArticle.ISSN);
        TextField issn = new TextField(issnParam);
        add(issn);

        Calendar today = new GregorianCalendar();
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.publicationdate").
                localize()));
        ParameterModel pubDateParam =
                       new DateParameter(InternetArticle.PUBLICATION_DATE);
        com.arsdigita.bebop.form.Date pubDate =
                                      new com.arsdigita.bebop.form.Date(
                pubDateParam);
        pubDate.setYearRange(1900, today.get(Calendar.YEAR) + 2);
        add(pubDate);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        InternetArticle article = (InternetArticle) initBasicWidgets(fse);

        data.put(InternetArticle.PLACE, article.getPlace());
        data.put(ITEM_SEARCH, article.getOrganization());
        data.put(InternetArticle.NUMBER, article.getNumber());
        data.put(InternetArticle.NUMBER_OF_PAGES, article.getNumberOfPages());
        data.put(InternetArticle.EDITION, article.getEdition());
        data.put(InternetArticle.ISSN, article.getISSN());
        data.put(InternetArticle.PUBLICATION_DATE,
                 article.getPublicationDate());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        InternetArticle article = (InternetArticle) processBasicWidgets(fse);

        if ((article != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            article.setPlace((String) data.get(InternetArticle.PLACE));
            article.setOrganization(
                    (GenericOrganizationalUnit) data.get(ITEM_SEARCH));
            article.setNumber((String) data.get(InternetArticle.NUMBER));
            article.setNumberOfPages(
                    (Integer) data.get(InternetArticle.NUMBER_OF_PAGES));
            article.setEdition((String) data.get(InternetArticle.EDITION));
            article.setISSN((String) data.get(InternetArticle.ISSN));
            article.setPublicationDate(
                    (Date) data.get(InternetArticle.PUBLICATION_DATE));

            article.save();

            if (m_step != null) {
                m_step.maybeForwardToNextStep(fse.getPageState());
            }
        }
    }
}
