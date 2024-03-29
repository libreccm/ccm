/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 * Form for adding an related contact to an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactAddForm
        extends BasicItemForm
        implements FormSubmissionListener {

    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitContactAddForm.class);
    private GenericOrganizationalUnitPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "personAddress";
    private ItemSelectionModel m_itemModel;
    private GenericOrganizationalUnitContactPropertiesStep editStep;
    private Label selectedContactLabel;

    public GenericOrganizationalUnitContactAddForm(ItemSelectionModel itemModel,
                                                   GenericOrganizationalUnitContactPropertiesStep editStep) {
        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;
        this.editStep = editStep;
        addSubmissionListener(this);
    }

    @Override
    protected void addWidgets() {
     // add(new Label(ContenttypesGlobalizationUtil.globalize(
        //     "cms.contenttypes.ui.genericorgaunit.select_contact")));
        m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                                            findByAssociatedObjectType(GenericContact.class
                                                    .getName()));
        m_itemSearch.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_contact"));
        m_itemSearch.setDisableCreatePane(false);
        add(m_itemSearch);

        selectedContactLabel = new Label();
        add(selectedContactLabel);

        ParameterModel contactTypeParam = new StringParameter(
                GenericOrganizationalUnitContactCollection.CONTACT_TYPE);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.type"));
        contactType.addValidationListener(new NotNullValidationListener());

        try {
            contactType.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    target.addOption(new Option("",
                                                     new Label(ContenttypesGlobalizationUtil.
                                                             globalize("cms.ui.select_one"))));

                    final GenericOrganizationContactTypeCollection contacttypes
                                                                   = new GenericOrganizationContactTypeCollection();
                    contacttypes.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().
                            getLanguage());

                    while (contacttypes.next()) {
                        RelationAttribute ct = contacttypes.getRelationAttribute();
                        target.addOption(new Option(ct.getKey(), ct.getName()));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Something has gone terribly wrong", ex);
        }
        add(contactType);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        GenericContact selectedContact = editStep.getSelectedContact();
        String selectedContactType = editStep.getSelectedContactType();

        if (selectedContact == null) {
            selectedContactLabel.setVisible(state, false);
        } else {
            data.put(ITEM_SEARCH, selectedContact);
            data.put(GenericOrganizationalUnitContactCollection.CONTACT_TYPE,
                     selectedContactType);

            m_itemSearch.setVisible(state, false);
            selectedContactLabel.setLabel(selectedContact.getTitle(), state);
            selectedContactLabel.setVisible(state, true);
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericContact selectedContact;
            selectedContact = editStep.getSelectedContact();

            if (selectedContact == null) {
                GenericContact contact = (GenericContact) data.get(ITEM_SEARCH);

                if (orgaunit.getLanguage().equals(
                        GlobalizationHelper.LANG_INDEPENDENT)) {
                    contact = (GenericContact) contact.getContentBundle().
                            getPrimaryInstance();
                } else {
                    contact = (GenericContact) contact.getContentBundle().
                            getInstance(orgaunit.getLanguage());
                }

                orgaunit.addContact(contact,
                                    (String) data.get(
                                            GenericOrganizationalUnitContactCollection.CONTACT_TYPE));
            } else {
                GenericOrganizationalUnitContactCollection contacts;

                contacts = orgaunit.getContacts();

                while (contacts.next()) {
                    if (contacts.getContact().equals(selectedContact)) {
                        break;
                    }
                }

                contacts.setContactType((String) data.get(
                        GenericOrganizationalUnitContactCollection.CONTACT_TYPE));

                editStep.setSelectedContact(null);
                editStep.setSelectedContactType(null);
                contacts.close();
            }
        }

        init(fse);
    }

    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            editStep.setSelectedContact(null);
            editStep.setSelectedContactType(null);

            init(fse);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if ((editStep.getSelectedContact() == null)
                    && (data.get(ITEM_SEARCH) == null)) {
            data.addError((ContenttypesGlobalizationUtil.globalize(
                           "cms.contenttypes.ui.genericorgaunit.select_contact.no_contact_selected")));

            return;
        }

        if (editStep.getSelectedContact() == null) {
            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) getItemSelectionModel()
                    .getSelectedObject(state);

            GenericContact contact = (GenericContact) data.get(ITEM_SEARCH);

            if (!(orgaunit.getLanguage().equals(
                  GlobalizationHelper.LANG_INDEPENDENT))) {
                if (!(contact.getContentBundle().hasInstance(orgaunit.
                      getLanguage(),
                                                             Kernel.getConfig().
                                                             languageIndependentItems()))) {
                    data.addError(
                            ContenttypesGlobalizationUtil.globalize(
                                    "cms.contenttypes.ui.genericorgaunit.select_contact.no_suitable_language_variant"));

                    return;
                }
            }

            if (orgaunit.getLanguage().equals(
                    GlobalizationHelper.LANG_INDEPENDENT)) {
                contact = (GenericContact) contact.getContentBundle().
                        getPrimaryInstance();
            } else {
                contact = (GenericContact) contact.getContentBundle().
                        getInstance(orgaunit.getLanguage());
            }
            GenericOrganizationalUnitContactCollection contacts = orgaunit.
                    getContacts();

            contacts.addFilter(String.format("id = %s",
                                             contact.getID().toString()));
            if (contacts.size() > 0) {
                data.addError(
                        ContenttypesGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.genericorgaunit.select_contact.already_added"));
            }

            contacts.close();
        }
    }

}
