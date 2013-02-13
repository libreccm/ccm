/*
 * Copyright (C) 2003 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.news;

import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

import com.arsdigita.portlet.news.ui.NewsPortletEditor;

import org.apache.log4j.Logger;


/**
 * based on com.arsdigita.london.portal.installer.portlet
 * @author Chris Gilbert (cgyg9330) &lt;chris.gilbert@westsussex.gov.uk&gt;
 * @version $Id: Initializer.java,v 1.2 2005/03/07 13:48:49 cgyg9330 Exp $
 */
public class Initializer extends CompoundInitializer {

    /** Private Logger instance for debugging purpose.                        */
	private static final Logger s_log = Logger.getLogger(Initializer.class);

	public Initializer() {
		final String url = RuntimeConfig.getConfig().getJDBCURL();
		final int database = DbHelper.getDatabaseFromURL(url);

		add(
			new PDLInitializer(
				new ManifestSource(
					"ccm-portlet-latestnews.pdl.mf",
					new NameFilter(
						DbHelper.getDatabaseSuffix(database),
						"pdl"))));
	}

    /**
     * 
     * @param e 
     */
    @Override
	public void init(DomainInitEvent e) {
		super.init(e);


        /* Register portlet with the domain coupling machinery               */
        e.getFactory().registerInstantiator(
                NewsPortlet.BASE_DATA_OBJECT_TYPE,
				new ACSObjectInstantiator() {
                    @Override
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new NewsPortlet(dataObject);
                    }
                });


        /*  */
        new ResourceTypeConfig(NewsPortlet.BASE_DATA_OBJECT_TYPE) {
                @Override
                public ResourceConfigFormSection getCreateFormSection(
                            final ResourceType resType,
                            final RequestLocal parentAppRL) {
                                    final ResourceConfigFormSection config = new 
                                        NewsPortletEditor(resType, parentAppRL);
                            return config;
                            }

               @Override
               public ResourceConfigFormSection getModifyFormSection(
                            final RequestLocal application) {
                                    final NewsPortletEditor config = new 
                                        NewsPortletEditor(application);
                            return config;
                            }
		};

        /* Register internal default themes's stylesheet which concomitantly
         * serves as a fallback if a custom theme is used without supporting
         * this portlet.                                                      */
		PortletType.registerXSLFile(
                NewsPortlet.BASE_DATA_OBJECT_TYPE, 
                PortletType.INTERNAL_THEME_PORTLET_DIR + "news-portlet.xsl");

	}
}