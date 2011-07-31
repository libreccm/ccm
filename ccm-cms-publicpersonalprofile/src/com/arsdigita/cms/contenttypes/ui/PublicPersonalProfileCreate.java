package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileConfig;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileCreate extends PageCreate {

    private static final String SELECTED_PERSON = "selectedPerson";
    private static final PublicPersonalProfileConfig config =
                                                        new PublicPersonalProfileConfig();

    static {
        config.load();
    }

    public PublicPersonalProfileCreate(final ItemSelectionModel itemModel,
                                          final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public void addWidgets() {
        //super.addWidgets();

        ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize(
                "cms.ui.authoring.content_type")));
        add(new Label(type.getLabel()));
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.create.select_person")));
        ParameterModel ownerModel =
                       new StringParameter(PublicPersonalProfile.OWNER);
        SingleSelect ownerSelect = new SingleSelect(ownerModel);
        ownerSelect.addValidationListener(new NotNullValidationListener());
  
        String personType = config.getPersonType();
        if ((personType == null) || (personType.isEmpty())) {
            personType = "com.arsdigita.cms.contenttypes.GenericPerson";
        }

        ContentTypeCollection types = ContentType.getAllContentTypes();
        types.addFilter(String.format("className = '%s'", personType));
        if (types.size() == 0) {
            personType = "com.arsdigita.cms.contenttypes.GenericPerson";
        }
        DataCollection persons = SessionManager.getSession().retrieve(
                personType);
        persons.addFilter("profile is null");
        while (persons.next()) {
            GenericPerson person =
                          (GenericPerson) DomainObjectFactory.newInstance(persons.
                    getDataObject());
            ownerSelect.addOption(new Option(person.getID().toString(), person.
                    getFullName()));
        }
        add(ownerSelect);

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
        Folder folder = m_parent.getFolder(fse.getPageState());
        Assert.exists(folder);
        String id = (String) fse.getFormData().get(
                PublicPersonalProfile.OWNER);

        GenericPerson owner = new GenericPerson(new BigDecimal(id));

        validateNameUniqueness(folder,
                               fse,
                               String.format("%s-profile",
                                             GenericPerson.urlSave(
                owner.getFullName())));
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        final Folder folder = m_parent.getFolder(state);

        Assert.exists(section, ContentSection.class);

        String id = (String) fse.getFormData().get(
                PublicPersonalProfile.OWNER);

        GenericPerson owner = new GenericPerson(new BigDecimal(id));
        String name = String.format("%s-profile",
                                    GenericPerson.urlSave(owner.getFullName()));
        String title = String.format("%s (Profil)", owner.getFullName());

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName(name);
        item.setTitle(title);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        PublicPersonalProfile profile = new PublicPersonalProfile(item.
                getOID());
        profile.setOwner(owner);
        profile.save();

        m_parent.editItem(state, item);
    }
}