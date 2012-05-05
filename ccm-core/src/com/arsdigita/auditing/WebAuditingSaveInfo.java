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
package com.arsdigita.auditing;

import com.arsdigita.kernel.User;
import com.arsdigita.web.Web;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Interface for auditing save information.
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 * @version $Id: WebAuditingSaveInfo.java 2089 2010-04-17 07:55:43Z pboy $
 **/
public class WebAuditingSaveInfo implements AuditingSaveInfo {

    private static final Logger s_log = Logger.getLogger
        (WebAuditingSaveInfo.class);

    private User m_user;
    private Date m_date;
    private String m_ip;

    public WebAuditingSaveInfo() {
        m_user = Web.getContext().getUser();
        // The user may be null.
        
        HttpServletRequest req = Web.getRequest();

        if (req == null) {
            m_ip = "127.0.0.1";
        } else {
            m_ip = req.getRemoteAddr();
        }

        m_date = new Date();
    }

    public User getSaveUser() {
        return m_user;
    }

    public Date getSaveDate() {
        return m_date;
    }

    public String getSaveIP() {
        return m_ip;
    }

    public AuditingSaveInfo newInstance() {
        return new WebAuditingSaveInfo();
    }
}
