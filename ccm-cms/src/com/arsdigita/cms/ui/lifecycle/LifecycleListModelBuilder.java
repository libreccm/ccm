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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.NoSuchElementException;

/**
 * Loads all the current lifecycles from the database so that they may
 * be displayed in a list.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $
 */
public final class LifecycleListModelBuilder extends LockableImpl
        implements ListModelBuilder {
    public static final String versionId =
        "$Id: LifecycleListModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    public final ListModel makeModel(final List l, final PageState state) {
        return new Model(state);
    }

    private class Model implements ListModel {
        private LifecycleDefinitionCollection m_cycles;

        public Model(final PageState state) {
            m_cycles = getCollection(state);
        }

        private final LifecycleDefinitionCollection getCollection
                (final PageState state) {
            ContentSection section = CMS.getContext().getContentSection();

            // MP: Remove this extra step if possible.  The content
            // section needs to be refreshed before fetching the
            // lifecycle definitions.

            try {
                section = new ContentSection(section.getOID());
            } catch (DataObjectNotFoundException donfe) {
                throw new UncheckedWrapperException(donfe);
            }

            final LifecycleDefinitionCollection cycles =
                section.getLifecycleDefinitions();

            cycles.addOrder("upper(label)");

            return cycles;
        }

        public boolean next() throws NoSuchElementException {
            return m_cycles.next();
        }

        public Object getElement() {
            return m_cycles.getLifecycleDefinition().getLabel();
        }

        public String getKey() {
            return m_cycles.getLifecycleDefinition().getID().toString();
        }
    }
}
