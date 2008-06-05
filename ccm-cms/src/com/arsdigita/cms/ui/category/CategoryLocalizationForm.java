/*
 * CategoryLocalizationForm.java
 *
 * Created on 18. April 2008, 12:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

/**
 * Basisklasse für CategoryLocalizationAddForm und CategoryLocalizationEditForm.
 * Diese Klasse ist Teil der Admin-Oberfläche von APLAWS+ und erweitert die Standardformulare
 * um die Formulare für die Bearbeitung der neuen, mehrsprachigen Kategorien.
 *
 * @author quasi
 */
public class CategoryLocalizationForm extends BaseForm {
    
    final CategoryRequestLocal m_category;
    final SingleSelect m_locale;
    final TextField m_name;
    final TextArea m_description;
    final TextField m_url;
    final RadioGroup m_isEnabled;
    private Label m_script = new Label("<script language=\"javascript\" src=\"/javascript/manipulate-input.js\"></script>", false);
    
    private final static String LOCALE      = "locale";
    private final static String NAME        = "name";
    private final static String DESCRIPTION = "description";
    private final static String URL         = "url";
    private final static String IS_ENABLED  = "isEnabled";
    
    /** Creates a new instance of CategoryLocalizationForm */
    public CategoryLocalizationForm(final String key,
            final GlobalizedMessage heading,
            final CategoryRequestLocal category) {
        
        super(key, heading);
        
        m_category = category;
        
        // Parameter-Model für den SingleSelect
        ParameterModel localeParam = new StringParameter(LOCALE);
        localeParam.addParameterListener(new StringInRangeValidationListener(0, 2));
        
        m_locale = new SingleSelect(localeParam);        
        m_locale.addValidationListener(new ParameterListener() {
            
            public void validate(ParameterEvent e) throws FormProcessException {
                
                // the --select one-- option is not allowed
                ParameterData data = e.getParameterData();
                String code = (String) data.getValue() ;
                if (code == null || code.length() == 0) {
                    data.addError(
                            (String)GlobalizationUtil.globalize(
                            "cms.ui.category.localization.error_locale").localize());
                }
            }
        });
        
        addField(gz("cms.ui.category.localization.locale"), m_locale);
        
        m_name = new TextField(new TrimmedStringParameter(NAME));
        addField(gz("cms.ui.name"), m_name);
        
        m_name.setSize(30);
        m_name.setMaxLength(200);
        m_name.addValidationListener(new NotNullValidationListener());
        m_name.setOnFocus("if (this.form." + URL + ".value == '') { " +
                " defaulting = true; this.form." + URL +
                ".value = urlize(this.value); }");
        m_name.setOnKeyUp("if (defaulting) { this.form." + URL +
                ".value = urlize(this.value) }");
        
        // is enabled?
        m_isEnabled = new RadioGroup(IS_ENABLED);
        m_isEnabled.addOption(new Option("no", new Label(gz("cms.ui.no"))));
        m_isEnabled.addOption(new Option("yes", new Label(gz("cms.ui.yes"))));
        addField(gz("cms.ui.category.is_enabled"),m_isEnabled);
        
        m_description = new TextArea
                (new TrimmedStringParameter(DESCRIPTION));
        addField(gz("cms.ui.description"), m_description);
        
        m_description.setWrap(TextArea.SOFT);
        m_description.setRows(5);
        m_description.setCols(40);
        
        // URL
        // JavaScript auto-url generation is off by default.
        // It is turned on under the following circumstances
        //
        // * If the url is null, upon starting edit of the title
        // * If the url is null, upon finishing edit of name
        //
        // The rationale is that, auto-url generation is useful
        // if the url is currently null, but once a name has been
        // created you don't want to subsequently change it since
        // it breaks URLs & potentially overwrites the user's
        // customizations.
        m_url = new TextField(new TrimmedStringParameter(URL));
        m_url.setSize(30);
        m_url.setMaxLength(200);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.setOnFocus("defaulting = false");
        m_url.setOnBlur("if (this.value == '') " +
                "{ defaulting = true; this.value = urlize(this.form." + NAME +
                ".value) }");
        addField(gz("cms.ui.category.url"),m_url);
        
        addAction(new Finish());
        addAction(new Cancel());
        
    }
    
    public void generateXML(PageState ps, Element parent) {
        m_script.generateXML(ps, parent);
        super.generateXML(ps, parent);
    }
    
    // Muß erweitert werden um folgende Funktion: Die Namen müssen eindeutig sein in der
    // gewählten Sprache
    class NameUniqueListener implements ParameterListener {
        private final CategoryRequestLocal m_category;
        private final Widget m_widget;
        private final int m_type;
        public final static int NAME_FIELD = 1;
        public final static int URL_FIELD = 2;
        
        NameUniqueListener(final CategoryRequestLocal category) {
            this(category,m_name,NAME_FIELD);
        }
        NameUniqueListener(final CategoryRequestLocal category,
                Widget widget, int type) {
            m_category = category;
            m_widget = widget;
            m_type = type;
        }
        
        
        // XXX Muß noch angepaßt werden
        public final void validate(final ParameterEvent e)
        throws FormProcessException {
            final PageState state = e.getPageState();
            final String title = (String) m_widget.getValue(state);
            
            final Category category = m_category.getCategory(state);
            
            final CategoryCollection children = category.getChildren();
            
            while (children.next()) {
                final Category child = children.getCategory();
                String compField =
                        (m_type == URL_FIELD) ? child.getURL() : child.getName();
                if (compField.equalsIgnoreCase(title)
                && (m_category == null
                        || !m_category.getCategory(state).equals(child))) {
                    throw new FormProcessException
                            (lz("cms.ui.category.name_not_unique"));
                }
            }
        }
    }
}
