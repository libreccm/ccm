/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.User;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Document;

import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 *
 * @author pb
 */
public class PersonalPortalCreatorServlet extends BaseApplicationServlet {

    /** Logger instance for debugging */
    private static final Logger s_log = Logger.getLogger(
                                            PersonalPortalCreatorServlet.class);

    private static final String XSL_HOOK = "portal-sitemap";

    private static final PresentationManager s_presManager =
                                             Templating.getPresentationManager();

    private static Page s_introPage ;
    


    /**
     * User extension point, do some initializing
     * 
     * @throws ServletException 
     */
    @Override
    public void doInit() throws ServletException {
        s_log.debug("PortalSiteMapServlet doInit() initialization executing!");

        // do nothing for now.
        s_introPage = buildIntroPage();
    }
        
    /**
     * 
     * @param sreq
     * @param sresp
     * @param app
     * @throws ServletException
     * @throws IOException 
     */
    public void doService(HttpServletRequest sreq,
                          HttpServletResponse sresp,
                          Application app)
                throws ServletException, IOException {
        s_log.debug("PortalSiteMApServlet.doService called for request '" +
                    sreq.getRequestURI() + "'");

        /* NOTE:
         * Resolves currently to SiteNodeRequestContext which will be removed.*/
        RequestContext ctx = DispatcherHelper.getRequestContext();        
        /* Problem: what application is treated as parent? We assume it is the
         * application of the current request, so we get is as parameter. 
         * The original dispatcher class uses the current context to determine
         * the parent application which should provide the same result.       */
        // final Application parent = getApplication(ctx);
        final Application parent = app;

        BigDecimal userID = scanUserID(sreq);

        if (userID == null) {
            s_log.warn
                ("Failed to read a user ID from a personal-portal link.");
        } else {
            final User user = getUser(userID);

            PersonalPortal portal = PersonalPortal.retrievePersonalPortal(user);

            if (portal == null) {
                // This is what we expect to happen so we have to create a new
                // PersonalPortal for that user.
                KernelExcursion rootExcursion = new KernelExcursion() {
                    protected void excurse() {
                        setParty(Kernel.getSystemParty());
                        PersonalPortal ps = PersonalPortal
                                            .createPersonalPortal(user, parent);
                        Assert.exists(ps, "portal");

                        configurePortal(ps);
                        ps.save();
                    }
                };
                rootExcursion.run();
            } else {
                // There's a problem.  We should never get here if the
                // portal already exists, since the site node
                // dispatcher will go to an existing portal
                // directly, skipping this dispatcher.

                s_log.error("PersonalPortalCreator invoked when the portalsite " +
                            "already exists.", new IllegalStateException());
            }
        }

            
        /* After crfeation of a new PersonalPortal show intro page.          */
        Document doc = null;
        doc = s_introPage.buildDocument(sreq, sresp);
        s_presManager.servePage(doc, sreq, sresp);

    }

    /**
     * 
     * @return 
     */
    private Page buildIntroPage() {

        Page page = new Page("", new SimpleContainer());
        page.lock();

        return page;

    }


    /**
     * Has to be replaced by a way not to use SiteNode / SiteNodeRequestContext
     * @param snrc
     * @return
     */
    private Application getApplication(RequestContext ctx) {

    //  SiteNode siteNode = snrc.getSiteNode();

    //  Application parent = Application.retrieveApplicationForSiteNode
    //      (siteNode);
    //  Assert.exists(parent, "parent");

    //  return parent;
        return null;
    }

    /**
     * Scan the ServletRequest for the userId part. 
     * We expect something like "1023/"

     * @param sreq
     * @return  UserId as BigDecimal 
     */
    private BigDecimal scanUserID(HttpServletRequest sreq) {

        String url = sreq.getPathInfo();

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
     * Retrieve the user object based on userId.
     * 
     * @param userID
     * @return User object
     */
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

    /**
     * 
     * @param portal 
     */
    private void configurePortal(PersonalPortal portal) {

        ResourceTypeConfig config = portal.getApplicationType().getConfig();
        Assert.exists(config, "config");
        config.configureResource(portal);

    }
    
}
