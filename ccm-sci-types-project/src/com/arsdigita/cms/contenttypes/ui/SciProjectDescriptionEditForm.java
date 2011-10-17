package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectConfig;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private final static SciProjectConfig config = SciProject.getConfig();

    public SciProjectDescriptionEditForm(final ItemSelectionModel itemModel) {
        super("SciProjectDescriptionEditForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciProjectGlobalizationUtil.globalize(
                "sciorganization.ui.project.description")));
        final ParameterModel descParam = new StringParameter(
                SciProject.PROJECT_DESCRIPTION);
        final TextArea desc;
        if (config.getEnableDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);

        if (config.getEnableFunding()) {
            add(new Label(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding")));
            final ParameterModel fundingParam = new StringParameter(
                    SciProject.FUNDING);
            final TextArea funding;
            if (config.getEnableFundingDhtml()) {
                funding = new CMSDHTMLEditor(fundingParam);
            } else {
                funding = new TextArea(fundingParam);
            }
            funding.setCols(75);
            funding.setRows(8);
            add(funding);
        }

        if (config.getEnableFundingVolume()) {
            add(new Label(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding_volume")));
            final ParameterModel fundingVolumeParam = new StringParameter(
                    SciProject.FUNDING_VOLUME);
            final TextField fundingVolume = new TextField(fundingVolumeParam);
            fundingVolume.addValidationListener(new StringInRangeValidationListener(
                    0,
                    config.getFundingVolumeLength()));
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
        if (config.getEnableFunding()) {
            data.put(SciProject.FUNDING, project.getFunding());
        }

        if (config.getEnableFundingVolume()) {
            data.put(SciProject.FUNDING_VOLUME, project.getFundingVolume());
        }

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if ((project != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            project.setProjectDescription((String) data.get(
                    SciProject.PROJECT_DESCRIPTION));
            if (config.getEnableFunding()) {
                project.setFunding((String) data.get(
                        SciProject.FUNDING));
            }
            if (config.getEnableFundingVolume()) {
                project.setFundingVolume((String) data.get(
                        SciProject.FUNDING_VOLUME));
            }

            project.save();
            
        }
        
        init(fse);
    }
}