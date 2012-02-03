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
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicPersonalProfileResearchInterestsEditForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    public PublicPersonalProfileResearchInterestsEditForm(
            final ItemSelectionModel itemModel) {
        super("PublicPersonalProfileEditResearchInterests",
              itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.research_interests")));
        final ParameterModel riParam =
                             new StringParameter(
                PublicPersonalProfile.INTERESTS);
        final TextArea researchInterests = new CMSDHTMLEditor(riParam);
        researchInterests.setCols(75);
        researchInterests.setRows(16);
        add(researchInterests);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile =
                                    (PublicPersonalProfile) getItemSelectionModel().
                getSelectedItem(state);

        data.put(PublicPersonalProfile.INTERESTS,
                 profile.getResearchInterests());

        setVisible(state, true);


    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile =
                                    (PublicPersonalProfile) getItemSelectionModel().
                getSelectedItem(state);

        if ((profile != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            profile.setResearchInterests((String) data.get(
                    PublicPersonalProfile.INTERESTS));

            profile.save();
        }

        init(fse);
    }
}
