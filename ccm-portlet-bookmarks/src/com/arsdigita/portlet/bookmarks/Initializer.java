/*
 * Copyright (C) 2005 Chris Gilbert  All Rights Reserved.
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

package com.arsdigita.portlet.bookmarks;


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
import com.arsdigita.portlet.bookmarks.ui.BookmarksPortletAdder;
import com.arsdigita.portlet.bookmarks.ui.BookmarksPortletEditor;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

/**
 * based on com.arsdigita.london.portal.installer.portlet
 * 
 * @author cgyg9330 (Chris Gilbert)
 * @version $Id: Initializer.java,v 1.3 2005/06/08 14:45:43 cgyg9330 Exp $
 */
public class Initializer extends CompoundInitializer {

	
	/**
     * Constructor.
     */
    public Initializer() {
		final String url = RuntimeConfig.getConfig().getJDBCURL();
		final int database = DbHelper.getDatabaseFromURL(url);

		add(
			new PDLInitializer(
				new ManifestSource(
					"ccm-wsx-bookmarks-portlet.pdl.mf",
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

        e.getFactory().registerInstantiator(
                           BookmarksPortlet.BASE_DATA_OBJECT_TYPE,
                           new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new BookmarksPortlet(dataObject);
                }
        });
		
		e.getFactory().registerInstantiator(
						Bookmark.BASE_DATA_OBJECT_TYPE,
						new ACSObjectInstantiator() {
                    @Override
					public DomainObject doNewInstance(DataObject dataObject) {
						return new Bookmark(dataObject);
					}
				});

		new ResourceTypeConfig(BookmarksPortlet.BASE_DATA_OBJECT_TYPE) {
            @Override
			public ResourceConfigFormSection getCreateFormSection(
				final ResourceType resType,
				final RequestLocal parentAppRL) {
					
				final ResourceConfigFormSection config =
					new BookmarksPortletAdder(resType, parentAppRL);

				return config;
			}

            @Override
			public ResourceConfigFormSection getModifyFormSection(
                                              final RequestLocal application) {

                final BookmarksPortletEditor config =
                                      new BookmarksPortletEditor(application);
                return config;
            }
        };
		
		
		/**
		 * implementation of framework that allows portlets to be bundled up 
         * as discrete applications
		 */
		PortletType.registerXSLFile(BookmarksPortlet.BASE_DATA_OBJECT_TYPE, 
                   "/packages/westsussex-portlets/xsl/bookmarks-portlet.xsl");

    }
}