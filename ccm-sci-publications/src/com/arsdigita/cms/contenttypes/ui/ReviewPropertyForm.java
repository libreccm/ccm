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
import com.arsdigita.cms.contenttypes.Review;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class ReviewPropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ReviewPropertiesStep m_step;
    public static final String ID = "ReviewEdit";

    public ReviewPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ReviewPropertyForm(ItemSelectionModel itemModel,
                              ReviewPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.journal").localize()));
        ParameterModel journalParam = new StringParameter(Review.JOURNAL);
        TextField journal = new TextField(journalParam);
        add(journal);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.volume").localize()));
        ParameterModel volumeParam = new IntegerParameter(Review.VOLUME);
        TextField volume = new TextField(volumeParam);
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.issue").localize()));
        ParameterModel issueParam = new StringParameter(Review.ISSUE);
        TextField issue = new TextField(issueParam);
        add(issue);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.pagesFrom").localize()));
        ParameterModel pagesFromParam = new IntegerParameter(Review.PAGES_FROM);
        TextField pagesFrom = new TextField(pagesFromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publcations.ui.review.pagesTo").localize()));
        ParameterModel pagesToParam = new IntegerParameter(Review.PAGES_TO);
        TextField pagesTo = new TextField(pagesToParam);
        add(pagesTo);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.issn").localize()));
        ParameterModel issnParam = new StringParameter(Review.ISSN);
        TextField issn = new TextField(issnParam);
        add(issn);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.url").localize()));
        ParameterModel urlParam = new StringParameter(Review.URL);
        TextField url = new TextField(urlParam);
        add(url);

        Calendar today = new GregorianCalendar();
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.review.publicationdate").
                localize()));
        ParameterModel pubDateParam =
                       new DateParameter(Review.PUBLICATION_DATE);
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
        Review review = (Review) initBasicWidgets(fse);

        data.put(Review.JOURNAL, review.getJournal());
        data.put(Review.VOLUME, review.getVolume());
        data.put(Review.ISSUE, review.getIssue());
        data.put(Review.PAGES_FROM, review.getPagesFrom());
        data.put(Review.PAGES_TO, review.getPagesTo());
        data.put(Review.ISSN, review.getISSN());
        data.put(Review.URL, review.getUrl());
        data.put(Review.PUBLICATION_DATE, review.getPublicationDate());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        Review review = (Review) processBasicWidgets(fse);

        if ((review != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            review.setJournal((String) data.get(Review.JOURNAL));
            review.setVolume((Integer) data.get(Review.VOLUME));
            review.setIssue((String) data.get(Review.ISSUE));
            review.setPagesFrom((Integer) data.get(Review.PAGES_FROM));
            review.setPagesTo((Integer) data.get(Review.PAGES_TO));
            review.setISSN((String) data.get(Review.ISSN));
            review.setUrl((String) data.get(Review.URL));
            review.setPublicationDate((Date) data.get(Review.PUBLICATION_DATE));

            review.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }

    }
}
