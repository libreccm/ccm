/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.KernelRequestContext;  //Previously SNRC (SiteNode)
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.security.UserContext;
import org.apache.log4j.Logger;


// Note: Previously used SiteNodeRequestContext, nows using KernelRequestContext
//       may be one cause that Login doesn't survive if the brwoser window is
//       closed.
/**
 * Initializes the value of the given parameter to the current user's
 * screen name.  Strangely similar to <code>EmailInitListener</code>.
 *
 * @author <a href="mailto:cwolfe@redhat.com">Crag Wolfe</a>
 * @version $Id: ScreenNameInitListener.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ScreenNameInitListener implements FormInitListener {

    private static Logger s_log = 
                   Logger.getLogger(ScreenNameInitListener.class.getName());
    private StringParameter m_param;

    /**
     * 
     * @param param 
     */
    public ScreenNameInitListener(StringParameter param) {
        m_param = param;
    }
    
    /**
     * 
     * @param event 
     */
    public void init(FormSectionEvent event) {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        s_log.debug("START");
        UserContext ctx = KernelRequestContext
                          .getKernelRequestContext(state.getRequest())
                          .getUserContext();
        if (!ctx.isLoggedIn()) {
            s_log.debug("FAILURE not logged in");
            return;
        }
        User user = null;
        user = ctx.getUser();
        if (user.getScreenName() == null) {
            s_log.debug("FAILURE null screen name");
            return;
        }
        data.put(m_param.getName(), user.getScreenName());
        s_log.debug("SUCCESS");
    }
}
