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
public class PublicPersonalProfileMiscEditForm
        extends BasicItemForm
        implements FormInitListener, FormProcessListener {
            
    public PublicPersonalProfileMiscEditForm(final ItemSelectionModel itemModel) {
        super("PublicPersonalProfileEditMisc", itemModel);
    }
    
    @Override
    protected void addWidgets() {
          add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.misc")));
        final ParameterModel miscParam = new StringParameter(
                PublicPersonalProfile.MISC);
        final TextArea misc = new CMSDHTMLEditor(miscParam);
        misc.setCols(75);
        misc.setRows(8);
        add(misc);
    }
    
     @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile =
                                    (PublicPersonalProfile) getItemSelectionModel().
                getSelectedItem(state);

        data.put(PublicPersonalProfile.MISC, profile.getMisc());

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse)
            throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile =
                                    (PublicPersonalProfile) getItemSelectionModel().
                getSelectedItem(state);

        if ((profile != null)
            && getSaveCancelSection().getSaveButton().isSelected(state)) {
            profile.setMisc(
                    (String) data.get(PublicPersonalProfile.MISC));

            profile.save();
        }

        init(fse);
    }
    
    
}
