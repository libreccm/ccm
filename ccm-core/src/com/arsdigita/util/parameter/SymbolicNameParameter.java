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

/**
 * @deprecated This class will be supplanted by other classes in other
 * locations in the near future.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: SymbolicNameParameter.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SymbolicNameParameter extends StringParameter {
    public final static String versionId =
        "$Id: SymbolicNameParameter.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    public SymbolicNameParameter(final String name) {
        super(name);
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final String string = (String) value;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isJavaIdentifierPart(string.charAt(i))) {
                final ParameterError error = new ParameterError
                    (this, "The value may contain letters, digits, " +
                     "and underscores only");
                break;
            }
        }
    }
}
