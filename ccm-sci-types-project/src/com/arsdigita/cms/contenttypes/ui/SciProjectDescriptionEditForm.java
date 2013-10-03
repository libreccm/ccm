package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.cms.contenttypes.SciProjectSponsorCollection;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Edit for the the detailed description of a SciProject.
 * 
 * Note about the sponsors: The PDL and Java code is prepared to handle more than one sponsor, but
 * for now the UI will only allow to add one sponsor. Will may change in future versions.
 * 
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private final static SciProjectConfig CONFIG = SciProject.getConfig();
    private final static String SPONSOR_SEARCH = "SPONSOR_SEARCH";
    private ItemSearchWidget sponsorSearch;

    public SciProjectDescriptionEditForm(final ItemSelectionModel itemModel) {
        super("SciProjectDescriptionEditForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.description")));
        final ParameterModel descParam = new StringParameter(
                SciProject.PROJECT_DESCRIPTION);
        final TextArea desc;
        if (CONFIG.getEnableDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);

        if (CONFIG.getEnableSponsor()) {
            add(new Label(SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor")));
            final Label sponsorLabel = new Label();
            sponsorLabel.addPrintListener(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final Label target = (Label) event.getTarget();
                    final PageState state = event.getPageState();

                    final SciProject project = (SciProject) getItemSelectionModel().
                            getSelectedObject(state);
                    final SciProjectSponsorCollection sponsors = project.getSponsors();

                    if ((sponsors != null) && !sponsors.isEmpty()) {
                        sponsors.next();
                        final GenericOrganizationalUnit sponsor = sponsors.getSponsor();
                        target.setLabel(sponsor.getTitle());
                        sponsors.close();
                    }


                }

            });
            add(sponsorLabel);

            add(new Label(SciProjectGlobalizationUtil.globalize("sciproject.ui.choose_sponsor")));
            sponsorSearch = new ItemSearchWidget(SPONSOR_SEARCH, ContentType.
                    findByAssociatedObjectType(CONFIG.getSponsorType()));
            add(sponsorSearch);
        }

        if (CONFIG.getEnableFunding()) {
            add(new Label(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding")));
            final ParameterModel fundingParam = new StringParameter(
                    SciProject.FUNDING);
            final TextArea funding;
            if (CONFIG.getEnableFundingDhtml()) {
                funding = new CMSDHTMLEditor(fundingParam);
            } else {
                funding = new TextArea(fundingParam);
            }
            funding.setCols(75);
            funding.setRows(8);
            add(funding);
        }

        if (CONFIG.getEnableFundingVolume()) {
            add(new Label(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding.volume")));
            final ParameterModel fundingVolumeParam = new StringParameter(
                    SciProject.FUNDING_VOLUME);
            final TextField fundingVolume = new TextField(fundingVolumeParam);
            fundingVolume.addValidationListener(new StringInRangeValidationListener(
                    0,
                    CONFIG.getFundingVolumeLength()));
            add(fundingVolume);
        }
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciProject.PROJECT_DESCRIPTION,
                 project.getProjectDescription());
        if (CONFIG.getEnableFunding()) {
            data.put(SciProject.FUNDING, project.getFunding());
        }

        if (CONFIG.getEnableFundingVolume()) {
            data.put(SciProject.FUNDING_VOLUME, project.getFundingVolume());
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().getSelectedObject(state);

        if ((project != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {

            project.setProjectDescription((String) data.get(SciProject.PROJECT_DESCRIPTION));

            if (CONFIG.getEnableSponsor()) {
                GenericOrganizationalUnit sponsor = (GenericOrganizationalUnit) data.get(
                        SPONSOR_SEARCH);

                if (sponsor != null) {
                    sponsor = (GenericOrganizationalUnit) sponsor.getContentBundle().getInstance(
                            project.getLanguage());
                    
                    project.addSponsor(sponsor);
                    sponsorSearch.publishCreatedItem(data, sponsor);
                }
            }

            if (CONFIG.getEnableFunding()) {
                project.setFunding((String) data.get(SciProject.FUNDING));
            }
            if (CONFIG.getEnableFundingVolume()) {
                project.setFundingVolume((String) data.get(SciProject.FUNDING_VOLUME));
            }

            project.save();

        }

        init(fse);
    }

}