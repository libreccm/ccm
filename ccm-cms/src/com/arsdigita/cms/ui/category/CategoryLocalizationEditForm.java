/*
 * CategoryLocalizationEditForm.java
 *
 * Created on 18. April 2008, 12:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Erzeugt ein Formular zum Bearbeiten einer vorhandenen Lokalisierungen zu der aktuellen Kategorie.
 * Diese Klasse ist Teil der Admin-Oberfläche von APLAWS+ und erweitert die Standardformulare
 * um die Formulare für die Bearbeitung der neuen, mehrsprachigen Kategorien.
 *
 * @author quasi
 */
public class CategoryLocalizationEditForm extends CategoryLocalizationForm {
    
    public static final String versionId =
            "$Id: CategoryLocalizationEditForm.java 287 2005-02-22 00:29:02Z sskracic $" +
            "$Author: sskracic $" +
            "$DateTime: 2004/08/17 23:15:09 $";
    
    private static final Logger s_log = Logger.getLogger
            (CategoryLocalizationEditForm.class);
    
    private final String m_categoryLocalizationLocale;
    
    /**
     * Creates a new instance of CategoryLocalizationEditForm
     */
    public CategoryLocalizationEditForm(final CategoryRequestLocal category,
            final String locale) {
        
        super("EditCategoryLocalization", gz("cms.ui.category.localization.edit"), category);
        
        // Speichere Locale ab
        m_categoryLocalizationLocale = locale;
        
        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
        
    }
    
    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
        throws FormProcessException {
            
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            // Verstecke Locale-Widget und sperre es (read-only)
            m_locale.addOption(new Option(m_categoryLocalizationLocale, new Locale(m_categoryLocalizationLocale).getDisplayLanguage()), state);
//            m_locale.setValue(state, m_categoryLocalizationLocale);
//            m_locale.setVisible(state, false);
            m_locale.lock();
            
            m_name.setValue(state, category.getName((String) m_locale.getValue(state)));
            m_description.setValue(state, category.getDescription((String) m_locale.getValue(state)));
            m_url.setValue(state, category.getURL((String) m_locale.getValue(state)));
            if (category.isEnabled((String) m_locale.getValue(state))) {
                m_isEnabled.setValue(state, "yes");
            } else {
                m_isEnabled.setValue(state, "no");
            }
        }
    }
    
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
        throws FormProcessException {
            
            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Editing localization for locale " + m_locale + " for category " + category);
            }
            
            if (category.canEdit()) {
                category.setName((String) m_name.getValue(state), (String) m_locale.getValue(state));
                category.setDescription((String) m_description.getValue(state), (String) m_locale.getValue(state));
                category.setURL((String) m_url.getValue(state), (String) m_locale.getValue(state));
                category.setEnabled("yes".equals((String) m_isEnabled.getValue(state)), (String) m_locale.getValue(state));
                category.save();
            } else {
                throw new AccessDeniedException();
            }
        }
    }
}
