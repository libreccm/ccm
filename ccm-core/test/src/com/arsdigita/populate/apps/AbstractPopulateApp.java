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
package com.arsdigita.populate.apps;

import org.apache.log4j.Logger;

import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;


// ///////////////////////////////////////////////////////////////////////////
//
// NOT USED anywhere in the source tree.
// No documentation available.
// Retained temporarily until refactoring of test cases is completed.
//
// (pboy 2011.01.30)
//
// ///////////////////////////////////////////////////////////////////////////


/**
 * @author bche
 */
public abstract class AbstractPopulateApp implements PopulateApp {
    private Application m_app = null;
    private AppPortlet m_portlet = null;
    private String m_sBaseStringSeed = null;
    protected Logger s_log = Logger.getLogger(PopulateApp.class);

    private Application createAppInstance(String sTitle, Application parent) {
        return Application.createApplication(getAppType(), sTitle, sTitle, parent);
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#setBaseStringSeed(java.lang.String sSeed)
     */
    public void setBaseStringSeed(String sSeed) {
        m_sBaseStringSeed = sSeed;
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getBaseStringSeed()
     */
    public String getBaseStringSeed() {
        return m_sBaseStringSeed;
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#createApp(java.lang.String, com.arsdigita.web.Application)
     */
    public void createApp(String sTitle, Application parent) {
        m_app = createAppInstance(sTitle, parent);
        m_portlet = null;
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#populateApp(java.util.List args)
     */
    public abstract void populateApp(java.util.List args);

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getArgsDescription()
     */
    public abstract String getArgsDescription();

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getApp()
     */
    public Application getApp() {
        return m_app;
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getAppType()
     */
    public abstract ApplicationType getAppType();

    /**
     * Checks that the number of arguments in args is equal to numArgs.  
     * Also checks that the args are all ints.  Throws
     * an IllegalArgumentException with sMsg if args not valid
     * @param args List of arguments to check
     * @param numArgs how many arguments should be there
     * @param sMsg message to throw if the number of arguments is wrong
     * @throws IllegalArgumentException
     */
    protected void validateArgs(java.util.List args, int numArgs, String sMsg)
        throws IllegalArgumentException {
        int iSize = args.size();
        s_log.debug("args size is " + iSize);
        if (iSize != numArgs) {
            throw new IllegalArgumentException("wrong number of args; " + sMsg);
        }
        for (int i = 0; i < iSize; i++) {
            try {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("arg is a " + args.get(i).getClass());
                }
                Integer arg = (Integer) args.get(i);
            } catch (Exception e) {
                throw new IllegalArgumentException("args not all ints; " + sMsg);
            }
        }
    }

    /* (non-Javadoc)
     * @see com.arsdigita.populate.apps.PopulateApp#getPortlet()
     */
    public AppPortlet getPortlet() {
        if (m_portlet == null && m_app != null) {
            m_portlet = (AppPortlet) Portlet.createPortlet(getPortletType(), m_app);
            if (s_log.isDebugEnabled()) {
                s_log.debug(
                    "setting m_portlet to "
                        + m_portlet.getTitle()
                        + " with id "
                        + m_portlet.getID());
            }
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug(
                "getting portlet "
                    + m_portlet.getTitle()
                    + " with id "
                    + m_portlet.getID());
        }
        return m_portlet;
    }

    /**
     * Returns the BaseDataObjectType of the portlet that getPortlet() returns
     * @return the BaseDataObjectType of the portlet that getPortlet() returns
     */
    protected abstract String getPortletType();
}
