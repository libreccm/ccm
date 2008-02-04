/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.templating.PatternGenerator;
import com.arsdigita.templating.PatternStylesheetResolver;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

/**
 *  This looks to see if there is a given item and if there is it returns
 *  the oid for that item as the gererated value
 */
public class ItemDelegatedURLPatternGenerator implements PatternGenerator {
    // this is used to prevent an infinite loop where this stylesheet
    // always matches.  If we did not use then then the call to 
    // the PatternStylesheetResolver would cause the loop
    private static final String DELEGATED_ALREADY_GENERATED = 
        "delegatedAlreadyGenerated";

    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        if (req.getAttribute(DELEGATED_ALREADY_GENERATED) == null) {
            // this has to be before the call to delegator.resolver otherwise
            // we end up in an infinite loop
            req.setAttribute(DELEGATED_ALREADY_GENERATED, Boolean.TRUE);
            PatternStylesheetResolver delegator = 
                new PatternStylesheetResolver();
            URL delegated = delegator.resolve(req);
            
            String[] del = new String[1];
            del[0] = delegated.toString();
            return del;
        } else {
            return new String[] {};
        }
    }
}
