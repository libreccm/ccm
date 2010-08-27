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
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumePropertyForm
        extends PublicationWithPublisherPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private ArticleInCollectedVolumePropertiesStep m_step;
    public static final String ID = "ArticleInCollectedVolumeEdit";

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

            article.save();
        }

        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }
}
