/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.contenttypes.GenericContactEntryKeys;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.globalization.GlobalizationHelper;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class GenericContactEntryAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(GenericContactEntryAddForm.class);

    private ItemSelectionModel m_itemModel;
    private ParameterSingleSelectionModel m_selectedEntry;
    private GenericContactEntriesEditor m_editor;

    /**
     * Creates a new instance of CategoryLocalizationAddForm.
     *
     * @param itemModel
     */
    public GenericContactEntryAddForm(ItemSelectionModel itemModel) {

        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;

    }

    public GenericContactEntryAddForm(ItemSelectionModel itemModel,
                                      GenericContactEntriesEditor editor,
                                      ParameterSingleSelectionModel selectedEntry) {

        this(itemModel);
        m_selectedEntry = selectedEntry;
        m_editor = editor;
    }

    @Override
    protected void addWidgets() {

        // Key field
        ParameterModel contactEntryKeyParam = new StringParameter(GenericContactEntry.KEY);
        SingleSelect contactEntryKey = new SingleSelect(contactEntryKeyParam);
        contactEntryKey.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericcontact.contactEntry.key"));
        contactEntryKey.addValidationListener(new NotNullValidationListener());
        try {
            contactEntryKey.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    target.clearOptions();

                    target.addOption(new Option("", new Label(GlobalizationUtil.globalize(
                                                         "cms.ui.select_one"))));

                    final GenericContactEntryKeys keyList = new GenericContactEntryKeys();
                    keyList.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale()
                        .getLanguage());
                    while (keyList.next()) {
                        String currentKey = keyList.getKey();
                        //target.addOption(new Option(currentKey, keyList.getName()));
                        target.addOption(new Option(
                                currentKey,
                                new Label(new GlobalizedMessage(
                                                currentKey,
                                                GenericContactEntryKeys.ATTRIBUTE_NAME,
                                                new RelationAttributeResourceBundleControl()))));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Something has gone terribly wrong", ex);
        }
        // Add the Options to the SingleSelect widget

        add(contactEntryKey);

        // Value field
        ParameterModel contactEntryValueParam = new StringParameter(GenericContactEntry.VALUE);
        TextField contactEntryValue = new TextField(contactEntryValueParam);
        contactEntryValue.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericcontact.contactEntry.value"));
        contactEntryValue.addValidationListener(new NotNullValidationListener());
        add(contactEntryValue);

        // Description field, only for internal usage
        ParameterModel contactEntryDescriptionParam = new StringParameter(
                GenericContactEntry.DESCRIPTION);
        TextField contactEntryDescription = new TextField(contactEntryDescriptionParam);
        contactEntryDescription.setLabel(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericcontact.contactEntry.description"));
        add(contactEntryDescription);

    }

    /**
     * Does nothing here.
     *
     * @param fse
     */
    @Override
    public void init(FormSectionEvent fse) {
        final PageState state = fse.getPageState();

        if (m_selectedEntry.getSelectedKey(state) != null) {
            final GenericContactEntry entry =
                new GenericContactEntry(new BigDecimal((String) m_selectedEntry.getSelectedKey(state)));

                final FormData data = fse.getFormData();

                data.put(GenericContactEntry.KEY, entry.getKey());
                data.put(GenericContactEntry.VALUE, entry.getValue());
                data.put(GenericContactEntry.DESCRIPTION, entry.getDescription());
        }
    }

    /**
     *
     * @param fse
     */
    @Override
    public void process(FormSectionEvent fse) {
        final PageState state = fse.getPageState();
        FormData data = fse.getFormData();
        GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(fse.getPageState());

        // save only if save button was pressed
        if (contact != null
                    && getSaveCancelSection().getSaveButton()
                .isSelected(fse.getPageState())) {

            if (m_selectedEntry.getSelectedKey(state) == null) {
                GenericContactEntry contactEntry = new GenericContactEntry(
                        contact,
                        (String) data.get(GenericContactEntry.KEY),
                        (String) data.get(GenericContactEntry.VALUE),
                        (String) data.get(GenericContactEntry.DESCRIPTION));

                contact.addContactEntry(contactEntry);
            } else {
                final GenericContactEntry entry =
                    new GenericContactEntry(new BigDecimal((String) m_selectedEntry.getSelectedKey(state)));
                entry.setKey((String) data.get(GenericContactEntry.KEY));
                entry.setValue((String) data.get(GenericContactEntry.VALUE));
                entry.setDescription((String) data.get(GenericContactEntry.DESCRIPTION));

                entry.save();
            }
        }

        if (m_editor != null) {
            m_editor.hideContactEntryForm(fse.getPageState());
        }
    }

}
