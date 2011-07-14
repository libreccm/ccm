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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciPublicPersonalProfile;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicPageForm;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfilePropertyForm
        extends BasicPageForm
        implements FormProcessListener,
                   FormInitListener {

    private SciPublicPersonalProfilePropertiesStep step;
    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "owner";
    public static final String ID = "SciPublicPersonalProfile_edit";

    public SciPublicPersonalProfilePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public SciPublicPersonalProfilePropertyForm(
            ItemSelectionModel itemModel,
            SciPublicPersonalProfilePropertiesStep step) {
        super(ID, itemModel);
        this.step = step;
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(SciPublicPersonalProfileGlobalizationUtil.globalize(
                "scipublicpersonalprofile.ui.profile.owner")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(SciMember.class.getName()));
        add(itemSearch);

        add(new Label(SciPublicPersonalProfileGlobalizationUtil.globalize(
                "scipublicpersonalprofile.ui.profile.url")));
        ParameterModel profileUrlParam =
                       new StringParameter(SciPublicPersonalProfile.PROFILE_URL);
        TextField profileUrl = new TextField(profileUrlParam);
        add(profileUrl);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final SciPublicPersonalProfile profile =
                                       (SciPublicPersonalProfile) super.
                initBasicWidgets(fse);

        data.put(SciPublicPersonalProfile.PROFILE_URL, profile.getProfileUrl());
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        final SciPublicPersonalProfile profile =
                                       (SciPublicPersonalProfile) processBasicWidgets(
                fse);
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        if ((profile != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            SciMember owner = (SciMember) data.get(ITEM_SEARCH);
            profile.setOwner(owner);
            
            profile.setProfileUrl((String) data.get(
                    SciPublicPersonalProfile.PROFILE_URL));

            profile.save();
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(SciPublicPersonalProfileGlobalizationUtil.globalize(
                    "scipublicpersonalprofile.ui.profile.missing_owner"));
        }
    }
}
