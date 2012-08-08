package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfilePropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener,
                   FormValidationListener {

    private PublicPersonalProfilePropertiesStep step;
    public static final String ID = "PublicPersonalProfile_edit";
    private ItemSelectionModel itemModel;

    public PublicPersonalProfilePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicPersonalProfilePropertyForm(
            ItemSelectionModel itemModel,
            PublicPersonalProfilePropertiesStep step) {
        super(ID, itemModel);
        this.step = step;
        this.itemModel = itemModel;
        addValidationListener(this);
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
            profile.setProfileUrl(((String) data.get(
                                   PublicPersonalProfile.PROFILE_URL)).
                    toLowerCase());

            profile.save();
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        super.validate(fse);
        
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();


        String profilesUrl =
               (String) data.get(PublicPersonalProfile.PROFILE_URL);
        if ((profilesUrl == null) || profilesUrl.isEmpty()) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.required"));
        }

        if ("admin".equalsIgnoreCase(profilesUrl)) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.reserved"));
        }

        DataCollection profiles = SessionManager.getSession().retrieve(
                PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
        profiles.addFilter(String.format("profileUrl = '%s'",
                                         ((String) data.get(
                                          PublicPersonalProfile.PROFILE_URL)).
                toLowerCase()));
        profiles.addFilter(String.format("version = '%s'", ContentItem.DRAFT));
        if (profiles.size() > 1) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.already_in_use"));
        } else if (profiles.size() == 1) {
            profiles.next();
            PublicPersonalProfile profile =
                                  (PublicPersonalProfile) DomainObjectFactory.
                    newInstance(profiles.getDataObject());

            if (!(profile.getID().equals(
                  itemModel.getSelectedItem(state).getID()))) {
                data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                        "publicpersonalprofile.ui.profile_url.already_in_use"));
            }
        }
    }
}
