/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Jens Pelzetter <jens.pelzetter@scientificcms.org>
 */
public class RelationAttributeResourceBundleControl extends ResourceBundle.Control {
    
    @Override
    public ResourceBundle newBundle(final String baseName, 
                                    final Locale locale,
                                    final String format,
                                    final ClassLoader classLoader,
                                    final boolean reload) {
        final RelationAttributeCollection values = new RelationAttributeCollection(baseName);
        values.addLanguageFilter(locale.getLanguage());
        
        final Object[][] contents = new Object[(int)values.size()][2];
        int i = 0;
        while(values.next()) {
            contents[i][0] = values.getKey();
            contents[i][1] = values.getName();
            i++;
        }
        
        return new ListResourceBundle() {

            @Override
            protected Object[][] getContents() {
                return contents;
            }
        };
        
    }
    
}
