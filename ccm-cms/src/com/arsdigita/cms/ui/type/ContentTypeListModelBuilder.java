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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.util.LockableImpl;

import java.util.NoSuchElementException;

/**
 * Builds a dynamic list of content types for a content section.
 */
class ContentTypeListModelBuilder extends LockableImpl
        implements ListModelBuilder {
    public ListModel makeModel(List l, final PageState state) {
        return new Model();
    }

    private class Model implements ListModel {
        private ContentTypeCollection m_types;

        Model() {
            final ContentSection section =
                CMS.getContext().getContentSection();

            m_types = section.getContentTypes(true);
            m_types.addOrder(ContentType.LABEL);
            m_types.rewind();
        }

        public boolean next() throws NoSuchElementException {
            return m_types.next();
        }

        public Object getElement() {
            return m_types.getContentType().getLabel();
        }

        public String getKey() {
            return m_types.getContentType().getID().toString();
        }
    }
}
