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
package com.arsdigita.portal.apportlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.portal.Portlet;
import java.util.Date;

/**
 * TimestampCachingPortletRenderer
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public abstract class TimestampCachingPortletRenderer
    extends AbstractPortletRenderer {


    private Portlet m_portlet;

    public TimestampCachingPortletRenderer(Portlet portlet) {
        m_portlet = portlet;
    }

    public boolean isDirty(PageState state) {
        Date cached = getDateCached(state);
        Date timestamp = getTimestamp();

        if (cached == null || timestamp == null) {
            return true;
        } else {
            return cached.getTime() < timestamp.getTime() ||
                cached.getTime() < m_portlet.getTimestamp().getTime();
        }
    }

    public String getCacheKey(PageState state) {
        return m_portlet.getID().toString();
    }

    public abstract Date getTimestamp();

}
