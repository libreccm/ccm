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
 * An example implementation of the {@link ContentGenerator} interface.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ExampleGenerator implements ContentGenerator {

    public void generateContent(final Element parent,
                                final GenericPerson person,
                                final PageState state,
                                final String profileLanguage) {
        Element message = parent.newChildElement("message");

        message.setText("Hello World!");
    }
}
