package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfilePropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener {

    private PublicPersonalProfilePropertiesStep step;    
    public static final String ID = "PublicPersonalProfile_edit";

    public PublicPersonalProfilePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicPersonalProfilePropertyForm(
            ItemSelectionModel itemModel,
            PublicPersonalProfilePropertiesStep step) {
        super(ID, itemModel);
        this.step = step;
    }

    @Override
    public void addWidgets() {
        super.addWidgets();
        
        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.profile.url")));
        ParameterModel profileUrlParam =
                       new StringParameter(PublicPersonalProfile.PROFILE_URL);
        TextField profileUrl = new TextField(profileUrlParam);
        add(profileUrl);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile =
                                       (PublicPersonalProfile) super.
                initBasicWidgets(fse);

        data.put(PublicPersonalProfile.PROFILE_URL, profile.getProfileUrl());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        final PublicPersonalProfile profile =
                                       (PublicPersonalProfile) processBasicWidgets(
                fse);
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        if ((profile != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {            
            profile.setProfileUrl((String) data.get(
                    PublicPersonalProfile.PROFILE_URL));

            profile.save();
        }
    }
}
