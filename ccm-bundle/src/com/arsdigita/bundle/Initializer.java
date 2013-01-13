/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.bundle;

import com.arsdigita.categorization.Categorization;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.templating.PatternStylesheetResolver;

import com.arsdigita.london.terms.TermCategoryListener;

import org.apache.log4j.Logger;


/**
 * The CCM bundle initializer.
 *
 * @version $Id: Initializer.java 1232 2006-06-22 12:01:30Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    private static Logger s_log = Logger.getLogger
        (Initializer.class.getName());


    /**
     * Package Implementation of  {@link Initializer#init(DomainInitEvent)}. 
     * 
     * @param evt
     */
    @Override
    public void init(DomainInitEvent evt) {
        super.init(evt);

    //  Moved to terms initializer because it is a central responsibility of
    //  terms itself.
    //  /* Create new term in the proper terms domain whenever a new category 
    //   * is created through CMS interface, keeping both insync              */
    //  Categorization.addCategoryListener(new TermCategoryListener());

     // /* Register additional PatternStyleSheetResolver for Web app.    
     //  * With all modules installing in one context no longer required.    */
     // PatternStylesheetResolver.registerPatternGenerator(
     //     "webapp",
     //     new WebAppPatternGenerator()
     // );
    }
}
