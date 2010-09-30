package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInJournalPropertiesStep extends PublicationPropertiesStep {

    public ArticleInJournalPropertiesStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    public static Component getArticleInJournalPropertySheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = (DomainObjectPropertySheet) PublicationPropertiesStep.
                getPublicationPropertySheet(itemModel);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.journal"),
                  ArticleInJournal.JOURNAL);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.volume"),
                ArticleInJournal.VOLUME);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.issue"),
                ArticleInJournal.ISSUE);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.pages_from"),
                ArticleInJournal.PAGES_FROM);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.pages_to"),
                ArticleInJournal.PAGES_TO);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.issn"),
                ArticleInJournal.ISSN);

        sheet.add(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.publication_date"),
                ArticleInJournal.PUBLICATION_DATE);

        return sheet;
    }

    @Override
    protected void addBasicProperties(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        SimpleEditStep basicProperties = new SimpleEditStep(itemModel,
                                                            parent,
                                                            EDIT_SHEET_NAME);

        BasicPageForm editBasicSheet =
                      new ArticleInJournalPropertyForm(itemModel, this);

        basicProperties.add(EDIT_SHEET_NAME,
                            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.articleinjournal.edit_basic_sheet").
                localize(),
                            new WorkflowLockedComponentAccess(editBasicSheet,
                                                              itemModel),
                            editBasicSheet.getSaveCancelSection().
                getCancelButton());

        basicProperties.setDisplayComponent(
                getArticleInJournalPropertySheet(itemModel));

        getSegmentedPanel().addSegment(
                new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publication.basic_properties").localize()),
                basicProperties);
    }
}
