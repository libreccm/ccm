package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public static final String ID = "SciOrganizationEdit";

    public SciOrganizationPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciOrganizationPropertyForm(ItemSelectionModel itemModel,
                                       SciOrganizationPropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        Label descLabel = new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganizations.ui.organization.shortdescription"));
        add(descLabel);
        ParameterModel descParam = new StringParameter(
                SciOrganization.ORGANIZATION_SHORT_DESCRIPTION);
        TextArea desc = new TextArea(descParam);
        desc.setCols(75);
        desc.setRows(5);
        add(desc);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        FormData data = fse.getFormData();
        SciOrganization orga = (SciOrganization) super.initBasicWidgets(fse);

        data.put(SciOrganization.ORGANIZATION_SHORT_DESCRIPTION,
                 orga.getOrganizationShortDescription());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        FormData data = fse.getFormData();
        SciOrganization organization = (SciOrganization) super.
                processBasicWidgets(fse);

        if ((organization != null) && getSaveCancelSection().getSaveButton().
                isSelected(fse.getPageState())) {

            organization.setOrganizationShortDescription((String) data.get(
                    SciOrganization.ORGANIZATION_SHORT_DESCRIPTION));

            organization.save();
        }
    }
}
