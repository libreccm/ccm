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
package com.arsdigita.search.ui.filters;

import java.util.Date;

public class DateRange {

    
    private Date m_start;
    private Date m_end;
    
    public DateRange(Date start,
                     Date end) {
        m_start = start;
        m_end = end;
    }
    
    public DateRange(String encoded) {
        int offset = encoded.indexOf(":");
        m_start = new Date(encoded.substring(0, offset));
        m_end = new Date(encoded.substring(offset+1));
    }
    
    public Date getStartDate() {
        return m_start;
    }
    
    public Date getEndDate() {
        return m_end;
    }

    public String toString() {
        return m_start.toString() + ":" + m_end.toString();
    }
}
