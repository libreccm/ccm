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

/**
 * <p>
 * Abstract class that represents a generic Accept HTTP header field.
 * </p>
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class AcceptField implements Comparable {
    public final static String versionId = "$Id: AcceptField.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // default quality value
    protected final static int DEFAULT_Q_VALUE = 1;

    protected double m_qvalue = DEFAULT_Q_VALUE;

    public double getQValue() {
        return m_qvalue;
    }

    protected void setQValue(String q) {
        int eq = q.indexOf('=');

        if (eq > -1) {
            setQValue(Double.parseDouble(q.substring(eq +1)));
        }
    }

    protected void setQValue(double q) {
        m_qvalue = q;
    }

    public int compareTo(Object o) {
        int rv;

        if (getQValue() < ((AcceptField) o).getQValue()) {
            rv = 1;
        } else if (getQValue() > ((AcceptField) o).getQValue()) {
            rv = -1;
        } else {
            rv = 0;
        }

        return rv;
    }
}
