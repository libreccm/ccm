package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciPublicPersonalProfile;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.text.StyledEditorKit.BoldAction;
import org.apache.log4j.Logger;

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
    private CheckboxGroup showPublicationList;
    private CheckboxGroup showProjectList;
    private String SHOW_PUBLICATION_LIST = "showPublicationList";
    private String SHOW_PROJECT_LIST = "showProjectList";
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
            profile.setProfileUrl((String) data.get(
                    SciPublicPersonalProfile.PROFILE_URL));
            
            profile.save();
        }
    }
}
