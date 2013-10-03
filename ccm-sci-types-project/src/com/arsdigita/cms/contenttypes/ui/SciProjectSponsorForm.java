package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectSponsorForm extends BasicItemForm implements FormInitListener,
                                                                    FormProcessListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "setSponsor";
    private final static SciProjectConfig CONFIG = SciProject.getConfig();

    public SciProjectSponsorForm(final ItemSelectionModel itemModel) {
        super("SciProjectSetSponsor", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor")));

        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        itemSearch.setEditAfterCreate(false);
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit sponsor = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
            sponsor = (GenericOrganizationalUnit) sponsor.getContentBundle().getInstance(project.
                    getLanguage());

            if ((project.getSponsors() != null) && !(project.getSponsors().isEmpty())) {
                final SciProjectSponsorCollection sponsors = project.getSponsors();
                sponsors.next();
                project.removeSponsor(sponsors.getSponsor());
                sponsors.close();
            }

            project.addSponsor(sponsor);
            itemSearch.publishCreatedItem(data, sponsor);
        }

        init(event);
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.sponsor_no_sponsor_selected"));

            return;
        }

        final SciProject project = (SciProject) getItemSelectionModel().getSelectedItem(state);
        final GenericOrganizationalUnit sponsor = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
        if (!(sponsor.getContentBundle().hasInstance(project.getLanguage(),
                                                     Kernel.getConfig().languageIndependentItems()))) {
            data.addError(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.sponsor.no_suitable_language_variant"));
        }

    }

}
