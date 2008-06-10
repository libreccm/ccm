/*
 * CategoryLocalizationAddForm.java
 *
 * Created on 18. April 2008, 12:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.ui.category;


import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.categorization.CategorizationConfig;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.cms.util.GlobalizationUtil;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Erzeugt ein Formular zum Anlegen einer neuen Lokalisierungen zu der aktuellen Kategorie.
 * Diese Klasse ist Teil der Admin-Oberfläche von APLAWS+ und erweitert die Standardformulare
 * um die Formulare für die Bearbeitung der neuen, mehrsprachigen Kategorien.
 *
 * @author quasi
 */
public class CategoryLocalizationAddForm extends CategoryLocalizationForm {
    
    public static final String versionId =
            "$Id: CategoryLocalizationAddForm.java 287 2005-02-22 00:29:02Z sskracic $" +
            "$Author: sskracic $" +
            "$DateTime: 2004/08/17 23:15:09 $";
    
    private static final Logger s_log = Logger.getLogger
            (CategoryAddForm.class);
    
    /** Creates a new instance of CategoryLocalizationAddForm */
    public CategoryLocalizationAddForm(final CategoryRequestLocal category) {
        
        super("AddCategoryLocalization", gz("cms.ui.category.localization.add"), category);
        
        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
        
    }
    
    // Deaktivate this widget, if category is root
//    public boolean isVisible(PageState state) {
//        return !m_category.getCategory(state).isRoot();
//    }
    
    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
        throws FormProcessException {
            
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);
            
            // Select one entry
            m_locale.addOption(new Option("", new Label((String) GlobalizationUtil.globalize("cms.ui.select_one").localize())), state);
            
            // Für alle Sprachen, die unterstützt werden (registry-Eintrag)
            CategorizationConfig catConfig = new CategorizationConfig();
            StringTokenizer strTok = catConfig.getSupportedLanguages();
            
            while(strTok.hasMoreTokens()) {
                
                String code = strTok.nextToken();
                
                // Wenn die Sprache bereits existiert, dann entferne sie aus der Auswahlliste
                if(!category.getCategoryLocalizationCollection().localizationExists(code)) {
                    m_locale.addOption(new Option(code, new Locale(code).getDisplayLanguage()), state);
                }                
            }
        }
    }
    
    
    private final class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
        throws FormProcessException {
            s_log.debug("Adding a categoryLocalization to category " + m_category);
            
            final PageState state = e.getPageState();
            
            final Category category = m_category.getCategory(state);
            final String locale = (String) m_locale.getValue(state);
            final String name = (String) m_name.getValue(state);
            final String description = (String) m_description.getValue(state);
            final String url = (String) m_url.getValue(state);
            final String isEnabled = (String) m_isEnabled.getValue(state);
            
            // Was soll das??
            //Assert.assertNotNull(parent, "Category parent");
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding localization for locale " + locale + " to category " + category);
            }
            
            if (category.canEdit()) {
                category.addLanguage(locale, name, description, url);
                category.setEnabled("yes".equals(isEnabled), locale);
                category.save();
                
            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }
    }
}
