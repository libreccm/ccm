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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.PublicPersonalProfile;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileBundle;
import com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfiles;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicPersonalProfilePropertyForm extends BasicPageForm implements FormProcessListener,
                                                                                FormInitListener,
                                                                                FormValidationListener {

    //private PublicPersonalProfilePropertiesStep step;
    public static final String ID = "PublicPersonalProfile_edit";
    private ItemSelectionModel itemModel;

    public PublicPersonalProfilePropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    public PublicPersonalProfilePropertyForm(final ItemSelectionModel itemModel,
                                             final PublicPersonalProfilePropertiesStep step) {
        super(ID, itemModel);
        //this.step = step;
        this.itemModel = itemModel;
        addValidationListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize("publicpersonalprofile.ui.create.select_person")));
        final ParameterModel ownerModel = new StringParameter(PublicPersonalProfileBundle.OWNER);
        final SingleSelect ownerSelect = new SingleSelect(ownerModel);
        ownerSelect.addValidationListener(new NotNullValidationListener());
        add(ownerSelect);

        try {
            ownerSelect.addPrintListener(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final SingleSelect ownerSelect = (SingleSelect) event.getTarget();
                    ownerSelect.clearOptions();

                    final PublicPersonalProfile profile = (PublicPersonalProfile) itemModel.getSelectedItem(event.
                            getPageState());
                    final GenericPerson owner = profile.getOwner();

                    String personType = PublicPersonalProfiles.getConfig().getPersonType();
                    if ((personType == null) || (personType.isEmpty())) {
                        personType = "com.arsdigita.cms.contenttypes.GenericPerson";
                    }

                    ContentTypeCollection types = ContentType.getAllContentTypes();
                    types.addFilter(String.format("className = '%s'", personType));
                    if (types.size() == 0) {
                        personType = "com.arsdigita.cms.contenttypes.GenericPerson";
                    }
                    DataCollection persons = SessionManager.getSession().retrieve(personType);
                    //persons.addFilter("profile is null");
                    persons.addFilter(String.format("version = '%s'", ContentItem.DRAFT));
                    if (owner != null) {
                        persons.addFilter(String.format("alias.id = '%s'", owner.getID().toString()));
                    }
                    persons.addOrder("surname asc");
                    persons.addOrder("givenname asc");
                    persons.addOrder("language asc");


//                    final GenericPerson owner = profile.getOwner();
//                    final GenericPerson alias = owner.getAlias();

                    if (owner != null) {
                        ownerSelect.addOption(new Option(owner.getID().toString(), owner.getFullName()));
                    }

                    if (!persons.isEmpty()) {
                        final List<BigDecimal> processed = new ArrayList<BigDecimal>();
                        while (persons.next()) {
                            GenericPerson person = (GenericPerson) DomainObjectFactory.newInstance(persons.
                                    getDataObject());
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
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        add(new Label(PublicPersonalProfileGlobalizationUtil.globalize("publicpersonalprofile.ui.profile.url")));
        final ParameterModel profileUrlParam = new StringParameter(PublicPersonalProfile.PROFILE_URL);
        final TextField profileUrl = new TextField(profileUrlParam);
        add(profileUrl);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PublicPersonalProfile profile = (PublicPersonalProfile) super.initBasicWidgets(fse);

        data.put(PublicPersonalProfile.PROFILE_URL, profile.getProfileUrl());
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final PublicPersonalProfile profile = (PublicPersonalProfile) processBasicWidgets(fse);
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        if ((profile != null) && getSaveCancelSection().getSaveButton().isSelected(state)) {

            final String ownerId = (String) data.get(PublicPersonalProfileBundle.OWNER);
            if ((profile.getOwner() != null) && !profile.getOwner().getID().equals(new BigDecimal(ownerId))) {
                final GenericPerson newOwner = new GenericPerson(new BigDecimal(ownerId));
                profile.setOwner(newOwner);
            }

            profile.setProfileUrl(((String) data.get(PublicPersonalProfile.PROFILE_URL)).toLowerCase());

            profile.save();
        }
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        super.validate(fse);

        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        final String profilesUrl = (String) data.get(PublicPersonalProfile.PROFILE_URL);
        if ((profilesUrl == null) || profilesUrl.isEmpty()) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.required"));
        }

        if ("admin".equalsIgnoreCase(profilesUrl)) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.reserved"));
        }

        final DataCollection profiles = SessionManager.getSession().retrieve(
                PublicPersonalProfile.BASE_DATA_OBJECT_TYPE);
        profiles.addFilter(String.format("profileUrl = '%s'", ((String) data.get(PublicPersonalProfile.PROFILE_URL)).
                toLowerCase()));
        profiles.addFilter(String.format("version = '%s'", ContentItem.DRAFT));
        if (profiles.size() > 1) {
            data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                    "publicpersonalprofile.ui.profile_url.already_in_use"));
        } else if (profiles.size() == 1) {
            profiles.next();
            final PublicPersonalProfile profile = (PublicPersonalProfile) DomainObjectFactory.newInstance(profiles.
                    getDataObject());

            if (!(profile.getID().equals(
                  itemModel.getSelectedItem(state).getID()))) {
                data.addError(PublicPersonalProfileGlobalizationUtil.globalize(
                        "publicpersonalprofile.ui.profile_url.already_in_use"));
            }
        }
    }

}
