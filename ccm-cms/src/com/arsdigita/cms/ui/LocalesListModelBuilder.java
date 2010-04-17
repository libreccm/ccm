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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SectionLocaleCollection;
import com.arsdigita.util.LockableImpl;

import java.util.NoSuchElementException;


/**
 * Builds a list of locales registered to a content section.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: LocalesListModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class LocalesListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    public LocalesListModelBuilder() {
        super();
    }

    public ListModel makeModel(List l, final PageState state) {
        return new ListModel() {

                private SectionLocaleCollection m_locales = getLocales(state);

                private SectionLocaleCollection getLocales(PageState param_state) {
                    ContentSection section = CMS.getContext().getContentSection();

                    SectionLocaleCollection locales = section.getLocales();
                    locales.addOrder("language");
                    locales.addOrder("country");
                    locales.addOrder("variant");
                    return locales;
                }

                public boolean next() throws NoSuchElementException {
                    return m_locales.next();
                }

                public Object getElement() {
                    return m_locales.getLocale().toJavaLocale().getDisplayName();
                }

                public String getKey() {
                    return m_locales.getLocale().getID().toString();
                }
            };
    }

}
