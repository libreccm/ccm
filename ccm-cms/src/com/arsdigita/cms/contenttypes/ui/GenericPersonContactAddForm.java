/*
 * Copyright (C) 2010 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.util.GlobalizationUtil;

import com.arsdigita.globalization.GlobalizationHelper;

import com.arsdigita.kernel.Kernel;
import org.apache.log4j.Logger;

/**
 * Generates a form for creating new localisations for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class GenericPersonContactAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(
                                               GenericPersonContactAddForm.class);
    private GenericPersonPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "personAddress";
    private ItemSelectionModel m_itemModel;

    /** Creates a new instance of CategoryLocalizationAddForm */
    public GenericPersonContactAddForm(ItemSelectionModel itemModel) {

        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;

    }

    /**
     * Add widgets to the form.
     */
    @Override
    protected void addWidgets() {

        // Attach a GenericContact object
        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.select_contact")));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.GenericContact"));
        add(this.m_itemSearch);

        // GenericContact type field
        add(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericperson.contact.type")));
        ParameterModel contactTypeParam =
                       new StringParameter(
                GenericPersonContactCollection.CONTACTS_KEY);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.addValidationListener(new NotNullValidationListener());
        contactType.addOption(new Option("",
                                         new Label(GlobalizationUtil.
                                             globalize("cms.ui.select_one"))));

        // Add the Options to the SingleSelect widget
        GenericContactTypeCollection contacttypes =
                                     new GenericContactTypeCollection();
        contacttypes.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                getLanguage());

        while (contacttypes.next()) {
            RelationAttribute ct = contacttypes.getRelationAttribute();
            contactType.addOption(new Option(ct.getKey(), ct.getName()));
        }

        add(contactType);
    }

    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
//        GenericPerson person = (GenericPerson) getItemSelectionModel().getSelectedObject(state);

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();
        GenericPerson person = (GenericPerson) getItemSelectionModel().
                getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            GenericContact contact = (GenericContact) data.get(ITEM_SEARCH);

            contact = (GenericContact) contact.getContentBundle().getInstance(
                    person.getLanguage());

            person.addContact(contact,
                              (String) data.get(
                    GenericPersonContactCollection.CONTACTS_KEY));
            m_itemSearch.publishCreatedItem(data, contact);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
              ContenttypesGlobalizationUtil.globalize(
              "cms.contenttypes.ui.genericperson.select_contact.none_selected"));

            return;
        }

        GenericPerson person = (GenericPerson) getItemSelectionModel().
                getSelectedObject(state);

        GenericContact contact = (GenericContact) data.get(ITEM_SEARCH);

        if (!(contact.getContentBundle().hasInstance(person.getLanguage(),
                                                     Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
              ContenttypesGlobalizationUtil.globalize(
              "cms.contenttypes.ui.genericperson.select_contact.no_suitable_language_variant"));

            return;
        }

        contact = (GenericContact) contact.getContentBundle().getInstance(person.
                getLanguage());
        GenericPersonContactCollection contacts = person.getContacts();

        contacts.addFilter(String.format("id = %s", contact.getID().toString()));
        if (contacts.size() > 0) {
            data.addError(
                    ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.genericperson.select_contact.already_added"));
        }

        contacts.close();
    }
}
