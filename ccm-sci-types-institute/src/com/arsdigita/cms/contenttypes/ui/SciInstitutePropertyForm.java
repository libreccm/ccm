package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.SciInstituteConfig;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstitutePropertyForm
        extends GenericOrganizationalUnitPropertyForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    public final static String ID = "SciInstituteEdit";
    private final static SciInstituteConfig config = SciInstitute.getConfig();

    public SciInstitutePropertyForm(final ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciInstitutePropertyForm(final ItemSelectionModel itemModel,
                                    final SciInstitutePropertiesStep step) {
        super(itemModel, step);
        addSubmissionListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.shortdesc")));
        ParameterModel shortDescParam = new StringParameter(
                SciInstitute.INSTITUTE_SHORT_DESCRIPTION);
        TextArea shortDesc = new TextArea(shortDescParam);
        shortDesc.addValidationListener(
                new StringInRangeValidationListener(0,
                                                    config.getShortDescMaxLength()));
        shortDesc.setCols(75);
        shortDesc.setRows(5);
        add(shortDesc);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        super.init(fse);

        final FormData data = fse.getFormData();
        final SciInstitute institute = (SciInstitute) super.initBasicWidgets(
                fse);

        data.put(SciInstitute.INSTITUTE_SHORT_DESCRIPTION,
                 institute.getInstituteShortDescription());
    }
    
     @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        super.process(fse);

        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final SciInstitute institute = (SciInstitute) super.
                processBasicWidgets(fse);

        if ((institute != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            institute.setInstituteShortDescription((String) data.get(
                    SciInstitute.INSTITUTE_SHORT_DESCRIPTION));

            institute.save();
        }

        init(fse);
    }

    @Override
    public GlobalizedMessage getTitleLabel() {
        return SciInstituteGlobalizationUtil.globalize("sciinstitute.ui.title");
    }
}
