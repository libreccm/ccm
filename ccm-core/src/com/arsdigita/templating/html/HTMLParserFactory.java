/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.templating.html;

import com.arsdigita.templating.html.HTMLParserConfigurationException;

/**
 * Different projects may need different {@link
 * com.arsdigita.templating.html.HTMLParser HTML parsers}. Some people would be
 * happy with the {@link com.arsdigita.templating.html.XHTMLParser} which only
 * accepts well-formed XHTML fragments, while others may want to write a parser
 * that accepts malformed HTML. This factory can be configured to return a
 * particular desired implementation of the {@link
 * com.arsdigita.templating.html.HTMLParser} interface.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-09-09
 * @version $Date: 2004/08/16 $
 **/
public class HTMLParserFactory {

    /**
     * @post return != null
     **/
    public static HTMLParser newInstance()
        throws HTMLParserConfigurationException, HTMLParserException {

        // FIXME: this should be driven by a config file.
        return new XHTMLParser();
    }
}
