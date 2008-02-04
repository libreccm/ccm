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

package com.arsdigita.london.util.junit;

import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.london.util.devsupport.QueryLogger;

import org.apache.log4j.Logger;

public class BaseTestCase extends com.arsdigita.tools.junit.framework.BaseTestCase {

    private QueryLogger m_logger;

    public BaseTestCase(String name) {
        super(name);
    }

    protected void baseSetUp() {
        super.baseSetUp();
        
        Logger log = Logger.getLogger(getClass().getName() + ".sql");
        if (log.isInfoEnabled()) {
            m_logger = new QueryLogger(".");
            if (log.isDebugEnabled()) {
                m_logger.setCaptureStackTrace(true);
            }
            DeveloperSupport.addListener(m_logger);            
        }

        DeveloperSupport.requestStart(this);
    }

    protected void baseTearDown() {
        DeveloperSupport.requestEnd(this);

        if (m_logger != null) {
            DeveloperSupport.removeListener(m_logger);
            m_logger = null;
        }

        super.baseTearDown();
    }
 
}
