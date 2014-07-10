/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms;

import com.arsdigita.cms.contenttypes.ui.GenericOrganizationalUnitPersonAddForm;
import com.arsdigita.globalization.GlobalizedMessage;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This implementation of {@link ResourceBundle.Control} is intended to be used for showing 
 * RelationAttribute values (data base driven enum} values in Bebop Controls using 
 * {@link GlobalizedMessage}. Used it by creating a {@link GlobalizedMessage} like this:
 * 
 * <pre>
 * new GlobalizedMessage("attribute_key",
                         "attribute_name",
                         new RelationAttributeResourceBundleControl());
 * </pre>
 * 
 * Replace <code>attribute_key</code> with the key of the attribute value and 
 * <code>attribute_name</code> with the name of the relation attribute. 
 * 
 * To see this action, please refer for example to {@link GenericOrganizationalUnitPersonAddForm}.
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
