package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciInstitute;
import com.arsdigita.cms.contenttypes.SciInstituteConfig;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteDescriptionEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private final static SciInstituteConfig config = SciInstitute.getConfig();

    public SciInstituteDescriptionEditForm(final ItemSelectionModel itemModel) {
        super("SciInstituteDescriptionEditForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label(SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.description")));
        final ParameterModel descParam = new StringParameter(
                SciInstitute.INSTITUTE_DESCRIPTION);
        final TextArea desc;
        if (config.getEnableDescriptionDhtml()) {
            desc = new CMSDHTMLEditor(descParam);
        } else {
            desc = new TextArea(descParam);
        }
        desc.setCols(75);
        desc.setRows(25);
        add(desc);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciInstitute institute = (SciInstitute) getItemSelectionModel().
                getSelectedObject(state);

        data.put(SciInstitute.INSTITUTE_DESCRIPTION, institute.
                getInstituteDescription());

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciInstitute institute = (SciInstitute) getItemSelectionModel().
                getSelectedObject(state);

        if ((institute != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            institute.setInstituteDescription((String) data.get(
                    SciInstitute.INSTITUTE_DESCRIPTION));

            institute.save();
        }

        init(fse);
    }
}
