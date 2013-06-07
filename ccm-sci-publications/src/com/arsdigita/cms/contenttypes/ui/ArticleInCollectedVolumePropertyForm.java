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
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumePropertyForm
        extends PublicationPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final String REVIEWED = "reviewed";
    private ArticleInCollectedVolumePropertiesStep m_step;
    public static final String ID = "ArticleInCollectedVolumeEdit";
    private CheckboxGroup reviewed;

    public ArticleInCollectedVolumePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public ArticleInCollectedVolumePropertyForm(
            ItemSelectionModel itemModel,
            ArticleInCollectedVolumePropertiesStep step) {
        super(itemModel, step);
        m_step = step;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.article_in_collected_volume.pages_from").
                localize()));
        ParameterModel fromParam = new IntegerParameter(
                ArticleInCollectedVolume.PAGES_FROM);
        TextField pagesFrom = new TextField(fromParam);
        add(pagesFrom);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.article_in_collected_volume.pages_to").
                localize()));
        ParameterModel toParam = new IntegerParameter(
                ArticleInCollectedVolume.PAGES_TO);
        TextField pagesTo = new TextField(toParam);
        add(pagesTo);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.article_in_collected_volume.chapter").
                localize()));
        ParameterModel chapterParam = new StringParameter(
                ArticleInCollectedVolume.CHAPTER);
        TextField chapter = new TextField(chapterParam);
        add(chapter);

         add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.reviewed")));
        reviewed = new CheckboxGroup("reviewedGroup");
        reviewed.addOption(new Option(REVIEWED, ""));
        add(reviewed);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        ArticleInCollectedVolume article =
                                 (ArticleInCollectedVolume) initBasicWidgets(
                fse);

        data.put(ArticleInCollectedVolume.PAGES_FROM, article.getPagesFrom());
        data.put(ArticleInCollectedVolume.PAGES_TO, article.getPagesTo());
        data.put(ArticleInCollectedVolume.CHAPTER, article.getChapter());

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
        ArticleInCollectedVolume article =
                                 (ArticleInCollectedVolume) processBasicWidgets(
                fse);

        if ((article != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {
            article.setPagesFrom((Integer) data.get(
                    ArticleInCollectedVolume.PAGES_FROM));
            article.setPagesTo((Integer) data.get(
                    ArticleInCollectedVolume.PAGES_TO));
            article.setChapter((String) data.get(
                    ArticleInCollectedVolume.CHAPTER));

            if (reviewed.getValue(fse.getPageState()) == null) {
                article.setReviewed(false);
            } else {
                article.setReviewed(true);
            }

            article.save();
        }
    }
}
