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

package com.arsdigita.cms.contenttypes;

import com.arsdigita.formbuilder.util.FormbuilderSetup;
import com.arsdigita.runtime.DomainInitEvent;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Executes at each system startup and initializes the Survey content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Sören Bernstein;
 * @version $Id: SurveyInitializer.java $
 */
public class SurveyInitializer extends ContentTypeInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(SurveyInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public SurveyInitializer() {
        super("ccm-cms-types-survey.pdl.mf",
                Survey.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Retrieve location of this content type's internal default theme 
     * stylesheet(s) which concomitantly serve as a fallback if a custom theme 
     * is engaged. 
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own
     * access method, but may not support every content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the 
     * parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[]{
                    INTERNAL_THEME_TYPES_DIR + "Survey.xsl"
                };
    }

    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Survey.xml";
    }

    @Override
    public void init(DomainInitEvent evt) {
        
        super.init(evt);

        List widgets = Arrays.asList(
                Arrays.asList("ct_survey", "Checkbox group", "Checkbox groups",
                "com.arsdigita.formbuilder.PersistentCheckboxGroup",
                "com.arsdigita.formbuilder.ui.editors.CheckboxGroupEditor"),
                Arrays.asList("ct_survey", "Scale", "Scales",
                "com.arsdigita.cms.contenttypes.PersistentScale",
                "com.arsdigita.cms.contenttypes.ui.editors.ScaleEditor"),
                Arrays.asList("ct_survey", "Date field", "Date fields",
                "com.arsdigita.formbuilder.PersistentDate",
                "com.arsdigita.formbuilder.ui.editors.DateForm"),
                Arrays.asList("ct_survey", "Hidden field", "Hidden fields",
                "com.arsdigita.formbuilder.PersistentHidden",
                "com.arsdigita.formbuilder.ui.editors.HiddenForm"),
                Arrays.asList("ct_survey", "Multiple select box", "Multiple select boxes",
                "com.arsdigita.formbuilder.PersistentMultipleSelect",
                "com.arsdigita.formbuilder.ui.editors.MultipleSelectEditor"),
                Arrays.asList("ct_survey", "Radio group", "Radio groups",
                "com.arsdigita.formbuilder.PersistentRadioGroup",
                "com.arsdigita.formbuilder.ui.editors.RadioGroupEditor"),
                Arrays.asList("ct_survey", "Single select box", "Single select boxes",
                "com.arsdigita.formbuilder.PersistentSingleSelect",
                "com.arsdigita.formbuilder.ui.editors.SingleSelectEditor"),
                Arrays.asList("ct_survey", "Text area", "Text areas",
                "com.arsdigita.formbuilder.PersistentTextArea",
                "com.arsdigita.formbuilder.ui.editors.TextAreaForm"),
                Arrays.asList("ct_survey", "Text field", "Text fields",
                "com.arsdigita.formbuilder.PersistentTextField",
                "com.arsdigita.formbuilder.ui.editors.TextFieldForm"),
                Arrays.asList("ct_survey", "Text Description", "Text Descriptions",
                "com.arsdigita.formbuilder.PersistentText",
                "com.arsdigita.formbuilder.ui.editors.TextForm"),
                Arrays.asList("ct_survey", "Text Heading", "Text Headings",
                "com.arsdigita.formbuilder.PersistentHeading",
                "com.arsdigita.formbuilder.ui.editors.HeadingForm"),
                Arrays.asList("ct_survey", "Section Break", "Section Break",
                "com.arsdigita.formbuilder.PersistentHorizontalRule",
                "com.arsdigita.formbuilder.ui.editors.HorizontalRuleForm"),
                Arrays.asList("ct_survey", "User Email Field", "User Email Fields",
                "com.arsdigita.formbuilder.PersistentEmailField",
                "com.arsdigita.formbuilder.ui.editors.EmailFieldForm"));

        List processListeners = Arrays.asList(
                Arrays.asList("ct_survey", "Confirmation email", "Confirmation emails",
                "com.arsdigita.formbuilder.actions.ConfirmEmailListener",
                "com.arsdigita.formbuilder.ui.editors.ConfirmEmailForm"),
                Arrays.asList("ct_survey", "URL redirect", "URL redirects",
                "com.arsdigita.formbuilder.actions.ConfirmRedirectListener",
                "com.arsdigita.formbuilder.ui.editors.ConfirmRedirectForm"),
                Arrays.asList("ct_survey", "Simple email", "Simple emails",
                "com.arsdigita.formbuilder.actions.SimpleEmailListener",
                "com.arsdigita.formbuilder.ui.editors.SimpleEmailForm"),
                Arrays.asList("ct_survey", "Templated email", "Templated emails",
                "com.arsdigita.formbuilder.actions.TemplateEmailListener",
                "com.arsdigita.formbuilder.ui.editors.TemplateEmailForm"));

        List dataQueries = Arrays.asList();

        // new com.arsdigita.formbuilder.installer.Initializer(widgets, processListeners, dataQueries);

        // Loading forms widget into database
        // It is a loading task and should be moved to loader.
    //  When invoking from initializer you may eventually need a transactoion.
    //  TransactionContext txn = SessionManager.getSession()
    //                                         .getTransactionContext();
    //  txn.beginTxn();
        FormbuilderSetup fbs = new FormbuilderSetup();
        fbs.setup(widgets, processListeners, dataQueries);
    //  txn.commitTxn();


    }
}
