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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contenttypes.HealthCareFacilityContactCollection;
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Generates a form for creating new localisations for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public class HealthCareFacilityContactAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(HealthCareFacilityContactAddForm.class);
    private HealthCareFacilityAddressPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "healthCareFacilityAddress";
    private ItemSelectionModel m_itemModel;

    /** Creates a new instance of CategoryLocalizationAddForm */
    public HealthCareFacilityContactAddForm(ItemSelectionModel itemModel) {

        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;

    }

    @Override
    protected void addWidgets() {

        // Attach a Contact object
        add(new Label((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.select_contact").localize()));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.findByAssociatedObjectType("com.arsdigita.cms.contenttypes.Contact"));
        add(this.m_itemSearch);

        // Contact type field
        add(new Label(HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact.type")));
        ParameterModel contactTypeParam = new StringParameter(HealthCareFacilityContactCollection.CONTACT_TYPE);
        SingleSelect contactType = new SingleSelect(contactTypeParam);
        contactType.addValidationListener(new NotNullValidationListener());
        contactType.addOption(new Option("", new Label((String) BasetypesGlobalizationUtil.globalize("cms.ui.select_one").localize())));

        // Add the Options to the SingleSelect widget
        StringTokenizer keyList = HealthCareFacility.getConfig().getContactTypeKeys();
        while (keyList.hasMoreElements()) {
            String currentKey = keyList.nextToken();
            contactType.addOption(new Option(currentKey, ((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contactType.key." + currentKey).localize())));
        }

        add(contactType);
    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        HealthCareFacility healthCareFacility = (HealthCareFacility) getItemSelectionModel().getSelectedObject(state);

        setVisible(state, true);
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        HealthCareFacility healthCareFacility = (HealthCareFacility) getItemSelectionModel().getSelectedObject(state);

        //
        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {
            healthCareFacility.addContact((com.arsdigita.cms.basetypes.Contact) data.get(ITEM_SEARCH),
                    (String) data.get(HealthCareFacilityContactCollection.CONTACT_TYPE));
        }

        init(fse);
    }
}
