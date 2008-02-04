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
 */

package com.arsdigita.london.theme.ui.listeners;

import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.theme.ThemeConstants;
import com.arsdigita.london.theme.ThemeFile;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *    This listener schedules selected theme file for removal.
 *  On the next run of development file theme manager, it will be erased
 *  from the disk.
 *
 *  @author Sebastian Skracic sskracic@redhat.com
 */
public class FileRemovalRequestListener implements ThemeConstants, RequestListener {

    private static final Logger s_log =
        Logger.getLogger(FileRemovalRequestListener.class);

    public void pageRequested(RequestEvent e) {
        String fileID = e.getPageState().getRequest().getParameter("fileID");
        if (fileID != null) {
            ThemeFile tf = (ThemeFile) DomainObjectFactory.newInstance(
                  new OID(ThemeFile.BASE_DATA_OBJECT_TYPE, new BigDecimal(fileID)));
            tf.setDeleted(true);
            tf.setLastModifiedDate(new Date());
            tf.save();
        }
    }
}

