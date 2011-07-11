package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciPublicPersonalProfile;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicPersonalProfileCreate extends PageCreate {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "owner";

    public SciPublicPersonalProfileCreate(final ItemSelectionModel itemModel,
                                          final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public void addWidgets() {
        ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize(
                "cms.ui.authoring.content_type")));
        add(new Label(type.getLabel()));
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                SciMember.class.getName()));
        add(itemSearch);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            add(new Label(GlobalizationUtil.globalize(
                    "cms.ui.authoring.page_launch_date")));
            ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate =
                                          new com.arsdigita.bebop.form.Date(
                    launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(
                        new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final Folder folder = m_parent.getFolder(fse.getPageState());
        Assert.exists(folder);
        validateNameUniqueness(folder, fse, getProfileName(fse));
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        final Folder folder = m_parent.getFolder(state);
        
        final String profileName = getProfileName(fse);
        final SciMember owner = (SciMember) data.get(ITEM_SEARCH);
        
        Assert.exists(section, ContentSection.class);
        
        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName(profileName);
        item.setTitle(getProfileTitle(fse));
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }
                
        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();
                
        m_workflowSection.applyWorkflow(state, item);
        
        SciPublicPersonalProfile profile = new SciPublicPersonalProfile(item.getID());
        profile.setOwner(owner);
        profile.save();
        
        m_parent.editItem(state, item);
    }

    private String getProfileName(FormSectionEvent fse) {
        final FormData data = fse.getFormData();

        SciMember owner = (SciMember) data.get(ITEM_SEARCH);
        if (owner == null) {
            return null;
        } else {
            return String.format("profile_%s_%s",
                                 owner.getGivenName(),
                                 owner.getSurname());
        }
    }
    
     private String getProfileTitle(FormSectionEvent fse) {
        final FormData data = fse.getFormData();

        SciMember owner = (SciMember) data.get(ITEM_SEARCH);
        if (owner == null) {
            return null;
        } else {
            return String.format("Profile %s %s",
                                 owner.getGivenName(),
                                 owner.getSurname());
        }
    }
}
