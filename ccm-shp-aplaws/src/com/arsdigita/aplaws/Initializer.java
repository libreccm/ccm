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

package com.arsdigita.aplaws;

import com.arsdigita.categorization.Categorization;
import com.arsdigita.london.terms.TermCategoryListener;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import org.apache.log4j.Logger;

import com.arsdigita.templating.PatternStylesheetResolver;

/**
 * The APLAWS initializer.
 *
 * @version $Id: Initializer.java 1232 2006-06-22 12:01:30Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    private static Logger s_log = Logger.getLogger
        (Initializer.class.getName());


    /**
     * An empty implementation of {@link Initializer#init(DataInitEvent)}.
     */
    public void init(DataInitEvent evt) {}


    /**
     * Package Implementation of  {@link Initializer#init(DomainInitEvent)}. 
     * 
     * @param evt
     */
    public void init(DomainInitEvent evt) {
        super.init(evt);

        Categorization.addCategoryListener(new TermCategoryListener());

        PatternStylesheetResolver.registerPatternGenerator(
            "webapp",
            new WebAppPatternGenerator()
        );
    }


    /**
     * An empty implementation of {@link Initializer#close()}.
     */
    public void close(ContextCloseEvent evt) {}

}
