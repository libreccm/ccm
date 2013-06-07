/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInJournalPropertyForm
        extends PublicationPropertyForm
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

    private static final String REVIEWED = "reviewed";
    private ArticleInJournalPropertiesStep m_step;
    public static final String ID = "ArticleInJournalEdit";
    private Label reviewedLabel;
    private CheckboxGroup reviewed;

    public ArticleInJournalPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ArticleInJournalPropertyForm(ItemSelectionModel itemModel,
                                        ArticleInJournalPropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.volume").localize()));
        ParameterModel volumeParam =
                       new IntegerParameter(ArticleInJournal.VOLUME);
        TextField volume = new TextField(volumeParam);
        //volume.addValidationListener(new NotNullValidationListener());
        //volume.addValidationListener(new NotEmptyValidationListener());
        add(volume);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.issue").localize()));
        ParameterModel issueParam = new StringParameter(ArticleInJournal.ISSUE);
        TextField issue = new TextField(issueParam);
        add(issue);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.pages_from").localize()));
        ParameterModel pagesFromParam =
                       new IntegerParameter(ArticleInJournal.PAGES_FROM);
        TextField pagesFrom = new TextField(pagesFromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.pages_to").localize()));
        ParameterModel pagesToParam =
                       new IntegerParameter(ArticleInJournal.PAGES_TO);
        TextField pagesTo = new TextField(pagesToParam);
        add(pagesTo);

        Calendar today = new GregorianCalendar();
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.publicationDate").
                localize()));
        ParameterModel pubDateParam =
                       new DateParameter(ArticleInJournal.PUBLICATION_DATE);
        com.arsdigita.bebop.form.Date pubDate =
                                      new com.arsdigita.bebop.form.Date(
                pubDateParam);
        pubDate.setYearRange(1900, today.get(Calendar.YEAR) + 2);
        add(pubDate);

        reviewedLabel = new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.reviewed"));
        add(reviewedLabel);
        reviewed = new CheckboxGroup("reviewedGroup");
        reviewed.addOption(new Option(REVIEWED, ""));
        add(reviewed);
    }
    
    protected final Label getReviewedLabel() {
        return reviewedLabel;
    }
    
    protected final CheckboxGroup getReviewed() {
        return reviewed;
    }
    
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        ArticleInJournal article = (ArticleInJournal) initBasicWidgets(fse);

        data.put(ArticleInJournal.VOLUME, article.getVolume());
        data.put(ArticleInJournal.ISSUE, article.getIssue());
        data.put(ArticleInJournal.PAGES_FROM, article.getPagesFrom());
        data.put(ArticleInJournal.PAGES_TO, article.getPagesTo());
        data.put(ArticleInJournal.PUBLICATION_DATE,
                 article.getPublicationDate());

        if ((article.getReviewed() != null) && (article.getReviewed())) {
            reviewed.setValue(fse.getPageState(), new String[]{REVIEWED});
        } else {
            reviewed.setValue(fse.getPageState(), null);
        }
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        ArticleInJournal article = (ArticleInJournal) processBasicWidgets(fse);

        if ((article != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            article.setVolume((Integer) data.get(ArticleInJournal.VOLUME));
            article.setIssue((String) data.get(ArticleInJournal.ISSUE));
            article.setPagesFrom(
                    (Integer) data.get(ArticleInJournal.PAGES_FROM));
            article.setPagesTo(
                    (Integer) data.get(ArticleInJournal.PAGES_TO));
            article.setPublicationDate(
                    (Date) data.get(ArticleInJournal.PUBLICATION_DATE));

            if (reviewed.getValue(fse.getPageState()) == null) {
                article.setReviewed(false);
            } else {
                article.setReviewed(true);
            }

            article.save();
        }
    }

}
