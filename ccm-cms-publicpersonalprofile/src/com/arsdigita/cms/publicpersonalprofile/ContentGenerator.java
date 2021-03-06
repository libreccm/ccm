/*
 * Copyright (c) 2011 Jens Pelzetter
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
package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.xml.Element;

/**
 * Implementations of this interface are used to render automatic content
 * (for example a publication list).
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface ContentGenerator {
    
    /**
     * Generates the content
     * 
     * @param parent XML element to attach the content to
     * @param person The person to be used as data source
     * @param state The current page state.
     * @param profileLanguage  
     */
    void generateContent(Element parent, 
                         GenericPerson person, 
                         PageState state, 
                         String profileLanguage);
    
}
