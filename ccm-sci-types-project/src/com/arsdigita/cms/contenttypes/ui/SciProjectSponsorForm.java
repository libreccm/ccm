package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectSponsorForm extends BasicItemForm implements FormInitListener,
                                                                    FormProcessListener,
                                                                    FormSubmissionListener {

    private final SimpleEditStep editStep;
    private ItemSearchWidget itemSearch;
    private TextField fundingCode;
    private Label selectedSponsorLabel;
    private static final String ITEM_SEARCH = "setSponsor";
    private static final String FUNDING_CODE = "fundingCode";
    //private static final SciProjectConfig CONFIG = SciProject.getConfig();

    public SciProjectSponsorForm(final ItemSelectionModel itemModel,
                                 final SimpleEditStep editStep) {
        super("SciProjectSetSponsor", itemModel);
        this.editStep = editStep;
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {

        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
        itemSearch.setEditAfterCreate(false);
        itemSearch.setLabel(SciProjectGlobalizationUtil.globalize(
                            "sciproject.ui.sponsor"));
        add(itemSearch);

        selectedSponsorLabel = new Label();
        add(selectedSponsorLabel);

        fundingCode = new TextField(FUNDING_CODE);
        fundingCode.setLabel(SciProjectGlobalizationUtil.globalize(
                             "sciproject.ui.sponsor_fundingcode"));
        add(fundingCode);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final GenericOrganizationalUnit sponsor = ((SciProjectSponsorStep) editStep).
                getSelectedSponsor();
        final String sponsorFundingCode = ((SciProjectSponsorStep) editStep).
                getSelectedSponsorFundingCode();

        if (sponsor == null) {
            itemSearch.setVisible(state, true);
            selectedSponsorLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, sponsor);
            if ((sponsorFundingCode == null) || sponsorFundingCode.isEmpty()) {
                fundingCode.setValue(state, null);
            } else {
                fundingCode.setValue(state, sponsorFundingCode);
            }

            itemSearch.setVisible(state, false);
            selectedSponsorLabel.setLabel(sponsor.getTitle(), state);
            selectedSponsorLabel.setVisible(state, true);
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            final GenericOrganizationalUnit sponsor = ((SciProjectSponsorStep) editStep).
                    getSelectedSponsor();

            String sponsorFundingCode;
            if (fundingCode.getValue(state) == null) {
                sponsorFundingCode = null;
            } else {
                sponsorFundingCode = (String) fundingCode.getValue(state);
            }

//            sponsor = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
//            sponsor = (GenericOrganizationalUnit) sponsor.getContentBundle().getInstance(project.
//                    getLanguage());

            if (sponsor == null) {
                final GenericOrganizationalUnit sponsorToAdd = (GenericOrganizationalUnit) data.get(
                        ITEM_SEARCH);

                if ((sponsorFundingCode == null) || sponsorFundingCode.isEmpty()) {
                    project.addSponsor(sponsorToAdd);
                } else {
                    project.addSponsor(sponsorToAdd, sponsorFundingCode);
                }
                itemSearch.publishCreatedItem(data, sponsor);
            } else {
                final SciProjectSponsorCollection sponsors = project.getSponsors();

                while (sponsors.next()) {
                    if (sponsors.getSponsor().equals(sponsor)) {
                        break;
                    }
                }

                sponsors.setFundingCode(sponsorFundingCode);

                ((SciProjectSponsorStep) editStep).setSelectedSponsor(null);
                ((SciProjectSponsorStep) editStep).setSelectedSponsorFundingCode(null);

                sponsors.close();
            }
        }

        init(event);
    }

    @Override
    public void submitted(final FormSectionEvent event) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(event.getPageState())) {
            ((SciProjectSponsorStep) editStep).setSelectedSponsor(null);
            ((SciProjectSponsorStep) editStep).setSelectedSponsorFundingCode(null);
            
            init(event);
        }
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        boolean editing = false; //Are we editing the association?
        
        if ((((SciProjectSponsorStep) editStep).getSelectedSponsor() == null)
                && (data.get(ITEM_SEARCH) == null)) {
            data.addError(SciProjectGlobalizationUtil.globalize(
                          "sciproject.ui.sponsor_no_sponsor_selected"));
            return;
        }
        
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);
        GenericOrganizationalUnit sponsor = (GenericOrganizationalUnit) data.get(ITEM_SEARCH);
        if (sponsor == null) {
            sponsor = ((SciProjectSponsorStep) editStep).getSelectedSponsor();
            editing = true;
        }

        if (!editing) {
            final SciProjectSponsorCollection sponsors = project.getSponsors();
            sponsors.addFilter(String.format("id = %s", sponsor.getContentBundle().getID().toString()));
            if (sponsors.size() > 0) {
                data.addError(SciProjectGlobalizationUtil.globalize(
                        "sciproject.ui.sponsor.already_added"));
            }
            
            sponsors.close();
        }
    }
}
