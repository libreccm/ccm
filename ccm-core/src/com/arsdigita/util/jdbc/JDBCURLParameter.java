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
package com.arsdigita.util.jdbc;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: JDBCURLParameter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class JDBCURLParameter extends StringParameter {
    public final static String versionId =
        "$Id: JDBCURLParameter.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Perl5Util s_perl = new Perl5Util();
    private static final String s_regex = "/^jdbc:[^:]+:.+$/";

    public JDBCURLParameter(final String name) {
        super(name);
    }

    public JDBCURLParameter(final String name,
			    final int multiplicity,
			    final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final String url = (String) value;

        if (!s_perl.match(s_regex, url)) {
            final String message =
                "The value must start with \"jdbc:\" and take the " +
                "form jdbc:subprotocol:subname";

            errors.add(new ParameterError(this, message));
        }
    }
}
