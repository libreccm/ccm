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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelConfig;
import java.util.Locale;
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
public class GenericContactTypeAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(
                                        GenericContactTypeAddForm.class);

    public final static String KEY = RelationAttribute.KEY;
    public final static String LANGUAGE = RelationAttribute.LANGUAGE;
    public final static String NAME = RelationAttribute.NAME;
    private GenericPersonPropertiesStep m_step;
    private SaveCancelSection m_saveCancelSection;
    private ItemSelectionModel m_itemModel;
    private SingleSelect language;

    /** Creates a new instance of CategoryLocalizationAddForm */
    public GenericContactTypeAddForm(ItemSelectionModel itemModel) {

        super("ContactEntryAddForm", itemModel);
        m_itemModel = itemModel;

    }

    @Override
    protected void addWidgets() {
        // Key
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.contacttypes.key")));
        ParameterModel keyParam = new StringParameter(KEY);
        keyParam.addParameterListener(new NotNullValidationListener());
        keyParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField key = new TextField(keyParam);
        add(key);

        // Language
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.contacttypes.language")));
        ParameterModel languageParam = new StringParameter(LANGUAGE);
        language = new SingleSelect(languageParam);
        language.addValidationListener(new NotNullValidationListener());
        language.addOption(new Option("", new Label((String) ContenttypesGlobalizationUtil.globalize("cms.ui.select_one").localize())));

        // Name
        add(new Label(ContenttypesGlobalizationUtil
                      .globalize("cms.contenttypes.ui.contacttypes.name")));
        ParameterModel nameParam = new StringParameter(NAME);
        nameParam.addParameterListener(new NotNullValidationListener());
        nameParam.addParameterListener(new StringInRangeValidationListener(0, 1000));
        TextField name = new TextField(nameParam);
        add(name);


    }

    public void init(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        RelationAttribute contacttype = (RelationAttribute) 
                                        getItemSelectionModel()
                                        .getSelectedObject(state);
        GenericContactTypeCollection contacttypeCollection = new 
                GenericContactTypeCollection(contacttype.getKey());

        // all supported languages (by registry entry)
        KernelConfig kernelConfig = Kernel.getConfig();
        StringTokenizer strTok = kernelConfig.getSupportedLanguagesTokenizer();

        while (strTok.hasMoreTokens()) {

            String code = strTok.nextToken();

            // If lanuage exists, remove it from the selection list
            if (!contacttypeCollection.hasLanguage(code)) {
                language.addOption(new 
                        Option(code, 
                               new Locale(code).getDisplayLanguage()), 
                               state);
            }
        }

        data.put(KEY, contacttype.getKey());
        data.put(LANGUAGE, contacttype.getLanguage());
        data.put(NAME, contacttype.getName());

        setVisible(state, true);
    }

    public void process(FormSectionEvent fse) {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        RelationAttribute contacttype = (RelationAttribute) 
                                        getItemSelectionModel()
                                        .getSelectedObject(state);

        //
        if (!this.getSaveCancelSection().getCancelButton().isSelected(state)) {

            contacttype.setKey((String) data.get(KEY));
            contacttype.setLanguage((String) data.get(LANGUAGE));
            contacttype.setName((String) data.get(NAME));

        }

        init(fse);
    }
}
