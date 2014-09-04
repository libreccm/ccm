/*
 * Copyright (C) 2013 University of Bremen. All Rights Reserved.
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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author quasi
 */
public class GenericContactAttachPersonPropertyForm extends BasicPageForm
    implements FormProcessListener, FormInitListener, FormSubmissionListener {

    private static final Logger logger = Logger.getLogger(
        GenericContactPropertyForm.class);
    private GenericContactPersonPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "contactPerson";
    private final String CONTACTS_KEY = GenericPersonContactCollection.CONTACTS_KEY;
    /**
     * ID of the form
     */
    public static final String ID = "ContactAttachPerson";

    /**
     * Constrctor taking an ItemSelectionModel
     *
     * @param itemModel
     */
    public GenericContactAttachPersonPropertyForm(ItemSelectionModel itemModel) {
        this(itemModel, null);
    }

    /**
     * Constructor taking an ItemSelectionModel and an instance of ContactPropertiesStep.
     *
     * @param itemModel
     * @param step
     */
    public GenericContactAttachPersonPropertyForm(ItemSelectionModel itemModel,
                                                  GenericContactPersonPropertiesStep step) {
        super(ID, itemModel);
        addSubmissionListener(this);

        addSaveCancelSection();

        addInitListener(this);
        addSubmissionListener(this);

    }

    @Override
    public void addWidgets() {

      //add(new Label(ContenttypesGlobalizationUtil.globalize(
      //    "cms.contenttypes.ui.contact.select_person")));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                                                 findByAssociatedObjectType(
                                                     "com.arsdigita.cms.contenttypes.GenericPerson"));
        m_itemSearch.setLabel(ContenttypesGlobalizationUtil.globalize(
                              "cms.contenttypes.ui.contact.select_person"));
        m_itemSearch.setDisableCreatePane(true);
        add(this.m_itemSearch);

        // GenericContact type field
        ParameterModel contactTypeParam = new StringParameter(CONTACTS_KEY);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.setLabel(ContenttypesGlobalizationUtil.globalize(
            "cms.contenttypes.ui.genericperson.contact.type"));
        contactType.addValidationListener(new NotNullValidationListener());
        
        try {
            contactType.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();
                    
                    target.addOption(new Option("",
                                         new Label(GlobalizationUtil
                                             .globalize("cms.ui.select_one"))));
                    
                    final GenericContactTypeCollection contacttypes
                                                       = new GenericContactTypeCollection();
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

        // Add the Options to the SingleSelect widget
        add(contactType);
    }

    @Override
    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
            getSelectedObject(state);

        setVisible(state, true);

        if (contact != null) {
            data.put(ITEM_SEARCH, contact.getPerson());
            data.put(CONTACTS_KEY, contact.getContactType());
        }
    }

    @Override
    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        GenericContact contact = (GenericContact) getItemSelectionModel().
            getSelectedObject(state);

        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

            person = (GenericPerson) person.getContentBundle().getInstance(contact.
                getLanguage());

            contact.setPerson(person, (String) data.get(CONTACTS_KEY));
        }
        init(fse);
    }

    /**
     * Creates the section with the save and the cancel button.
     */
    @Override
    public void addSaveCancelSection() {
        try {
            getSaveCancelSection().getSaveButton().addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent e) {
                    GenericContact contact = (GenericContact) getItemSelectionModel().
                        getSelectedObject(e.getPageState());
                    Submit target = (Submit) e.getTarget();

                    if (contact.getPerson() != null) {
                        target.setButtonLabel(ContenttypesGlobalizationUtil.
                            globalize(
                                "cms.contenttypes.ui.contact.select_person.change"));
                    } else {
                        target.setButtonLabel(ContenttypesGlobalizationUtil.
                            globalize(
                                "cms.contenttypes.ui.contact.select_person.add"));
                    }
                }

            });
        } catch (Exception ex) {
            throw new UncheckedWrapperException("this cannot happen", ex);
        }
    }

    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        //Calling super.validate(e) here causes an exception because the 
        //super method checks things which not available here.

        final PageState state = e.getPageState();
        final FormData data = e.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            throw new FormProcessException(ContenttypesGlobalizationUtil.
                globalize(
                    "cms.contenttypes.ui.contact.select_person.wrong_type"));
        }

        GenericContact contact = (GenericContact) getItemSelectionModel().
            getSelectedObject(state);
        GenericPerson person = (GenericPerson) data.get(ITEM_SEARCH);

        if (!(person.getContentBundle().hasInstance(contact.getLanguage(),
                                                    Kernel.getConfig().
                                                    languageIndependentItems()))) {
            data.addError(ContenttypesGlobalizationUtil.
                globalize(
                "cms.contenttypes.ui.contact.select_person.no_suitable_language_variant"));
        }

    }

    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(e.getPageState())) {
            init(e);
            throw new FormProcessException(ContenttypesGlobalizationUtil.
                globalize(
                    "cms.contenttypes.ui.contact.select_person.cancelled"));
        }
    }

}
