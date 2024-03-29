/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.parameter;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Subject to change.
 *
 * A parameter representing a Java <code>URL</code>.
 *
 * @see java.net.URL
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: URLParameter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class URLParameter extends StringParameter {

    public URLParameter(final String name) {
        super(name);
    }

    public URLParameter(final String name,
                        final int multiplicity,
                        final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        try {
            return new URL(value);
        } catch (MalformedURLException mue) {
            errors.add(new ParameterError(this, mue));
            return null;
        }
    }
}
