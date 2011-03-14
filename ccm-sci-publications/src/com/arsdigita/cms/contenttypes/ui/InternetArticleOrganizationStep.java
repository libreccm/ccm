package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class InternetArticleOrganizationStep extends SimpleEditStep {

    private String SET_INTERNET_ARTICLE_ORGANIZATION_STEP =
                   "setInternetArticleOrganizationStep";

    public InternetArticleOrganizationStep(final ItemSelectionModel itemModel,
                                           final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public InternetArticleOrganizationStep(final ItemSelectionModel itemModel,
                                           final AuthoringKitWizard parent,
                                           final String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrgaForm = new InternetArticleOrganizationForm(
                itemModel);
        add(SET_INTERNET_ARTICLE_ORGANIZATION_STEP,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.internetarticle.setOrganization").localize(),
                new WorkflowLockedComponentAccess(setOrgaForm, itemModel),
                setOrgaForm.getSaveCancelSection().getCancelButton());

        InternetArticleOrganizationSheet sheet = new InternetArticleOrganizationSheet(
                itemModel);
        setDisplayComponent(sheet);
    }
}
