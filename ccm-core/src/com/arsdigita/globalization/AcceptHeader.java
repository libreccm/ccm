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
 *
 */
package com.arsdigita.globalization;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * <p>
 * Abstract class, manages Accept HTTP headers as defined in RFC 2616.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 * @version $Id: AcceptHeader.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class AcceptHeader {

    // as passed in by the client browser.
    protected String m_acceptHeader = "";

    // our representation.
    protected AcceptField[] m_acceptFields = null;

    public AcceptField[] getAcceptFields() {
        return m_acceptFields;
    }

    protected void setAcceptFields() {
        StringTokenizer tokenizer =
            new StringTokenizer(m_acceptHeader, ",");

        AcceptField[] afArr = new AcceptField[tokenizer.countTokens()];

        int i = 0;
        AcceptField af = null;
        while (tokenizer.hasMoreTokens()) {
            af = createAcceptField(tokenizer.nextToken());

            if (af.getQValue() > 0) {
                afArr[i++] = af;
            }
        }

        m_acceptFields = new AcceptField[i];
        System.arraycopy(afArr, 0, m_acceptFields, 0, i);

        Arrays.sort(m_acceptFields);
    }

    abstract protected AcceptField createAcceptField(String acceptField);
}
