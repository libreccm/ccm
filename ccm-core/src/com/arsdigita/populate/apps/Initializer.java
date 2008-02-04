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

import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.util.Assert;

/**
 * @author bche
 */
public class Initializer extends BaseInitializer {
	private static final Logger s_log =
		Logger.getLogger(Initializer.class.getName());
	private Configuration m_conf = new Configuration();

	/* (non-Javadoc)
	 * @see com.arsdigita.initializer.Initializer#getConfiguration()
	 */
	public Configuration getConfiguration() {
                m_conf.initParameter("baseStringSeed",
                        "The base string seed to use for generating String names.",
                        String.class,
                        null);        
		m_conf.initParameter(
			"numApps",
			"the number of instances to create for each application",
			Integer.class);
		m_conf.initParameter(
			"PopulateAppPair",
			"A list pair, consisting of the PopulateApp class with which to populate and"
				+ "the arguments for that class",
			List.class);
		return m_conf;
	}

	/* (non-Javadoc)
	 * @see com.arsdigita.kernel.BaseInitializer#doStartup()
	 */
	protected void doStartup() {
                String sBaseStringSeed = (String)m_conf.getParameter("baseStringSeed");        
		int iApps = ((Integer) m_conf.getParameter("numApps")).intValue();
		List popAppsList = ((List) m_conf.getParameter("PopulateAppPair"));
		
                if (s_log.isDebugEnabled()) {
                    s_log.debug("iApps: " + iApps);
                    s_log.debug("popAppsList: " + popAppsList);
                    s_log.debug("Using BaseStringSeed " + sBaseStringSeed);
                }

		PopulateAppPairCollection popAppsColl = new PopulateAppPairCollection();

		int iSize = popAppsList.size();
		for (int i = 0; i < iSize; i++) {
			List popAppParam = (List) popAppsList.get(i);
			Assert.assertTrue(popAppParam.size() == 2);

			String sPopApp = (String) popAppParam.get(0);
			s_log.debug("PopulateApp is " + sPopApp);
			PopulateApp popApp;
			try {
				popApp = (PopulateApp) Class.forName(sPopApp).newInstance();
			} catch (Exception e) {
				throw new InitializationException(e.getMessage());
			}
			List args = (List) popAppParam.get(1);
			s_log.debug("args are" + args);
			PopulateAppPair popAppPair = new PopulateAppPair(popApp, args);
			popAppsColl.addPopulateApp(popAppPair);
		}

		//create the apps
		s_log.info("Begin populating applications");

		PopulateApps popApps = new PopulateApps(popAppsColl);
                popApps.setBaseStringSeed(sBaseStringSeed);
		popApps.populate(iApps);
		s_log.info("End populating applications");
	}

	/* (non-Javadoc)
	 * @see com.arsdigita.kernel.BaseInitializer#doShutdown()
	 */
	protected void doShutdown() {
	}

}
