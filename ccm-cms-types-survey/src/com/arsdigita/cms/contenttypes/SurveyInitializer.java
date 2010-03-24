package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.DomainInitEvent;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Sören Bernstein;
 */
public class SurveyInitializer extends ContentTypeInitializer {

    public final static String versionId =
            "$Id: SurveyInitializer.java $" +
            "$Author: quasi $" +
            "$DateTime: 2010/02/18 $";
    private static final Logger s_log = Logger.getLogger(SurveyInitializer.class);

    public SurveyInitializer() {
        super("ccm-cms-types-survey.pdl.mf",
                Survey.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]{
                    "/static/content-types/com/arsdigita/cms/contenttypes/Survey.xsl"
                };
    }

    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Survey.xml";
    }

    @Override
    public void init(DomainInitEvent evt) {

        List widgets = Arrays.asList(
                Arrays.asList("ct_survey", "Checkbox group", "Checkbox groups",
                "com.arsdigita.formbuilder.PersistentCheckboxGroup",
                "com.arsdigita.formbuilder.ui.editors.CheckboxGroupEditor"),
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

        new com.arsdigita.formbuilder.installer.Initializer(widgets, processListeners, dataQueries);
    }
}
