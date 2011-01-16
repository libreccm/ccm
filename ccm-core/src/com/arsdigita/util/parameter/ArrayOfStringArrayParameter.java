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

package com.arsdigita.util.parameter;

import com.arsdigita.util.StringUtils;

/**
 *
 * @author pb
 * @version $Id: $
 */
public class ArrayOfStringArrayParameter extends StringParameter {

    /** 
     * Represents a 2-dimensional array of Strings.
     * Methods convert between a literal representation and the object back and
     * forth.
     *
     * Literal representation:
     * 1. dimension: list of comma separated strings
     * 2. dimension: list of colon separated strings
     *
     * Example:
     *    lit="exp00:exp01:exp02,exp10:exp11:exp12,exp20:exp21:exp22"
     * converts to
     *    strArr = {{exp00,exp01,exp02},{exp10,exp11,exp12},{exp20,exp21,exp22}}
     * @see com.arsdigita.util.parameter.StringArrayParameter unidimensional Par
     *
     * @param name literal representation of String[][]
     * @param multiplicity
     * @param defaalt
     */
    public ArrayOfStringArrayParameter( final String name,
                                      final int multiplicity,
                                      final Object defaalt) {
        super(name, multiplicity, defaalt);

    }

    /**
     * Converts a String[][] Object into a literal representation.
     *
     * @param value
     * @return
     */
    @Override
    protected String marshal(final Object value) {
        if (value == null) {
            return null;
        } else {
            return StringUtils.join((String[])value, ',');
        }
    }

    /**
     * 
     * @param literal
     * @param errors
     * @return
     */
    @Override
    protected Object unmarshal(final String literal,
                               final ErrorList errors) {
        
        String[][] stringArrayArray = new String[0][0];

        // Convert the first level of comma serapated literals into an array
        String[] firstLevel = StringUtils.split(literal, ',');

        for (int i = 0; i < firstLevel.length; i++) {

            // Convert the firstLevel Element of colon separated literals into
            // an array
            String[] secondLevel = StringUtils.split(firstLevel[i], ':');

            if (i == 0) {
                // reinitialize with correct number of elements.
                // @pre: rectangular 2-dim array, i.e. all elements of second
                //       dim of same number of elements
                stringArrayArray = new String[firstLevel.length]
                                             [secondLevel.length];
            }

            for (int j = 0; j < secondLevel.length; j++) {
                final String elem = secondLevel[j];

                stringArrayArray[i][j] = (String) super.unmarshal(elem, errors);

                if (!errors.isEmpty()) {
                    break;
                }
            }

        }
        return stringArrayArray;
    }

    @Override
    protected void doValidate(final Object value,
                              final ErrorList errors) {
        if (value != null) {
            final String[] strings = (String[]) value;

            for (int i = 0; i < strings.length; i++) {
                super.doValidate(strings[i], errors);

                if (!errors.isEmpty()) {
                    break;
                }
            }
        }
    }

}
