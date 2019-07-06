/*
 * Copyright (c) 2011 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfileConfig;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.ui.authoring.PageCreate;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicPersonalProfileCreate extends PageCreate {

    //private static final String SELECTED_PERSON = "selectedPerson";
    private static final PublicPersonalProfileConfig config =
                                                     PublicPersonalProfiles.getConfig();

    public PublicPersonalProfileCreate(final ItemSelectionModel itemModel,
                                       final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public void addWidgets() {
        //super.addWidgets();

        final ContentType type = getItemSelectionModel().getContentType();
        m_workflowSection = new ApplyWorkflowFormSection(type);
        add(m_workflowSection, ColumnPanel.INSERT);
        add(new Label(GlobalizationUtil.globalize(
                "cms.ui.authoring.content_type")));
        add(new Label(type.getName()));   // the title or name of the type
        add(new Label(GlobalizationUtil.globalize("cms.ui.language.field")));
        add(new LanguageWidget(LANGUAGE));

        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                      "publicpersonalprofile.ui.create.select_person")));
        final ParameterModel ownerModel = new 
                             StringParameter(PublicPersonalProfileBundle.OWNER);
        final SingleSelect ownerSelect = new SingleSelect(ownerModel);
        ownerSelect.addValidationListener(new NotNullValidationListener());

        try {

            ownerSelect.addPrintListener(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final SingleSelect ownerSelect = (SingleSelect) event.getTarget();
                    ownerSelect.clearOptions();

                    String personType = config.getPersonType();
                    if ((personType == null) || (personType.isEmpty())) {
                        personType = "com.arsdigita.cms.contenttypes.GenericPerson";
                    }

                    ContentTypeCollection types = ContentType.getAllContentTypes();
                    types.addFilter(String.format("className = '%s'", personType));
                    if (types.size() == 0) {
                        personType = "com.arsdigita.cms.contenttypes.GenericPerson";
                    }
                    DataCollection persons = SessionManager.getSession()
                                                           .retrieve(personType);
                    //persons.addFilter("profile is null");
                    persons.addFilter(String.format("version = '%s'", 
                                                    ContentItem.DRAFT));
                    persons.addOrder("surname asc");
                    persons.addOrder("givenname asc");
                    persons.addOrder("language asc");
                    ownerSelect.addOption(new Option("", ""));

                    //Store the parent ids of processed items to remove double entries.
                    final List<BigDecimal> processed = new ArrayList<BigDecimal>();
                    while (persons.next()) {
                        GenericPerson person = (GenericPerson) DomainObjectFactory.newInstance(persons.getDataObject());
                        if (processed.contains(person.getParent().getID())) {
                            continue;
                        } else {
                            if (person.getGenericPersonBundle().get("profile") == null) {
                                continue;
                            } else {
                                ownerSelect.addOption(new Option(person.getID().toString(), person.getFullName()));
                                processed.add(person.getParent().getID());
                            }
                        }
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        add(ownerSelect);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date")));
            final ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
            final com.arsdigita.bebop.form.Date launchDate = new com.arsdigita.bebop.form.Date(launchDateParam);
            if (ContentSection.getConfig().getRequireLaunchDate()) {
                launchDate.addValidationListener(
                        new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }
    }

    /**
     * Ensure name uniqueness. Note: We can't call {@code super.validate(FormSectionEvent)} here because the super
     * method {@link BasicPageForm#validate(com.arsdigita.bebop.event.FormSectionEvent)} tries to access properties
     * which are not set yet.
     *
     * @param fse
     */
    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final Folder folder = m_parent.getFolder(fse.getPageState());
        Assert.exists(folder);
        final String personId = (String) fse.getFormData().get(PublicPersonalProfileBundle.OWNER);

        if ((personId == null) || personId.trim().isEmpty()) {
            fse.getFormData().addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.person.required"));
            return;
        }

        final GenericPerson owner = new GenericPerson(new BigDecimal(personId));

        validateNameUniqueness(folder, fse, String.format("%s-profile",
                                                          GenericPerson.urlSave(owner.getFullName())));
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        final ContentSection section = m_parent.getContentSection(state);
        final Folder folder = m_parent.getFolder(state);

        Assert.exists(section, ContentSection.class);

        final String personId = (String) fse.getFormData().get(PublicPersonalProfileBundle.OWNER);

        final GenericPerson owner = new GenericPerson(new BigDecimal(personId));
        final String name = String.format("%s-profile",
                                          GenericPerson.urlSave(owner.getFullName()));
        final String title = String.format("%s (Profil)", owner.getFullName());

        final ContentPage item = createContentPage(state);
        item.setLanguage((String) data.get(LANGUAGE));
        item.setName(name);
        item.setTitle(title);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }

        final PublicPersonalProfileBundle bundle = new PublicPersonalProfileBundle(item);
        bundle.setParent(folder);
        bundle.setContentSection(m_parent.getContentSection(state));
        bundle.save();

        m_workflowSection.applyWorkflow(state, item);

        final PublicPersonalProfile profile = new PublicPersonalProfile(item.getOID());
        profile.setOwner(owner);
        profile.setProfileUrl(createProfileUrl(owner));
        profile.save();

        m_parent.editItem(state, item);
    }

    private String createProfileUrl(final GenericPerson owner) {
        
        String profileUrl = GenericPerson.urlSave(owner.getSurname().toLowerCase());
        int counter = 1;

        final DataCollection profiles = SessionManager.getSession().
                retrieve(PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
        profiles.addFilter(String.format("profileUrl = '%s'", profileUrl));
        profiles.addFilter(String.format("version = '%s'", ContentItem.DRAFT));

        while (profiles.size() > 0) {
            counter++;

            profileUrl = String.format("%s%d", owner.getSurname().toLowerCase(), counter);
            profiles.reset();
            profiles.addFilter(String.format("profileUrl = '%s'", profileUrl));
            profiles.addFilter(String.format("version = '%s'", ContentItem.DRAFT));
        }

        return GenericPerson.urlSave(profileUrl).toLowerCase();
    }
}
