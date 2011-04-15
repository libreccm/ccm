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
package com.arsdigita.portalserver.personal;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.page.PageDispatcher;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.web.Application;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.sitenode.SiteNodeRequestContext;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.util.Assert;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.log4j.Category;

/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortalCreatorDispatcher.java  pboy $
 */
public class PersonalPortalCreatorDispatcher implements Dispatcher {

    private static Category s_log = Category.getInstance
        (PersonalPortalCreatorDispatcher.class);

    private Dispatcher m_introDispatcher = null;

    private BigDecimal scanUserID(SiteNodeRequestContext snrc) {
        // We expect to get something like "23/"
        String url = snrc.getRemainingURLPart();

        int delimIndex = url.indexOf("/");

        try {
            if (delimIndex == -1) {
                return new BigDecimal(url);
            } else {
                return new BigDecimal(url.substring(0, delimIndex));
            }
        } catch (NumberFormatException nfe) {
            return null;
        } catch (IndexOutOfBoundsException iobe) {
            // BigDecimal(String) fails to validate its input.  An
            // empty string makes it bomb, throwing this exception.

            return null;
        }
    }

    /**
     * Has to be replaced by a way not to use SiteNode / SiteNodeRequestContext
     * @param snrc
     * @return
     */
    private Application getApplication(SiteNodeRequestContext snrc) {
        SiteNode siteNode = snrc.getSiteNode();

        Application parent = Application.retrieveApplicationForSiteNode
            (siteNode);

    //  Assert.assertNotNull(parent, "parent");
        Assert.exists(parent, "parent");

        return parent;
    }

    private User getUser(BigDecimal userID) {
        User user = null;

        try {
            user = User.retrieve(userID);
        } catch (DataObjectNotFoundException nfe) {
            s_log.error
                ("Failed to retrieve user " + userID + ".",
                 new IllegalStateException());
        }

  //    Assert.assertNotNull(user, "user");
        Assert.exists(user, "user");

        return user;
    }

    private void configurePortal(PersonalPortal portal) {
        ResourceTypeConfig config =
            portal.getApplicationType().getConfig();

    //  Assert.assertNotNull(config, "config");
        Assert.exists(config, "config");

        config.configureResource(portal);
    }

    private Page buildIntroPage() {
        Page page = new Page("", new SimpleContainer());

        page.lock();

        return page;
    }

    public void dispatch
        (HttpServletRequest request, HttpServletResponse response,
         RequestContext context)
        throws IOException, ServletException {
    //  Assert.assertTrue(context instanceof SiteNodeRequestContext);
        Assert.isTrue(context instanceof SiteNodeRequestContext);

        // XXX has to be replaced by a way NOT built upon SiteNode*
        SiteNodeRequestContext snrc = (SiteNodeRequestContext) context;

        final Application parent = getApplication(snrc);

        BigDecimal userID = scanUserID(snrc);

        if (userID == null) {
            s_log.warn
                ("Failed to read a user ID from a personal-portal link.");
        } else {
            final User user = getUser(userID);

            PersonalPortal portal =
                PersonalPortal.retrievePersonalPortal(user);

            if (portal == null) {
                // This is what we expect to happen.
                KernelExcursion rootExcursion = new KernelExcursion() {
                      protected void excurse() {
                          setParty(Kernel.getSystemParty());
                          PersonalPortal ps = 
                            PersonalPortal.createPersonalPortal (user, parent);

                      //    Assert.assertNotNull(ps, "portal");
                            Assert.exists(ps, "portal");

                            configurePortal(ps);

                            ps.save();
                        }};
                rootExcursion.run();
            } else {
                // There's a problem.  We should never get here if the
                // portal already exists, since the site node
                // dispatcher will go to an existing portal
                // directly, skipping this dispatcher.

                s_log.error
                    ("PersonalPortalCreator invoked when the portalsite " +
                     "exists already.", new IllegalStateException());
            }

            // Not synchronized, since it makes little difference.

            if (m_introDispatcher == null) {
                m_introDispatcher = new PageDispatcher(buildIntroPage());
            }

      //    Assert.assertNotNull(m_introDispatcher, "m_introDispatcher");
            Assert.exists(m_introDispatcher, "m_introDispatcher");

            DispatcherHelper.sendRedirect
                (request, response, snrc.getOriginalURL());

            // The following does not work because I need to be able to
            // reset the RequestContext before I invoke it.  Otherwise,
            // the remainingURL is incorrect.
            //DispatcherHelper.forwardRequestByPath
            //    (snrc.getOriginalURL(), request, response);
        }
    }
}
