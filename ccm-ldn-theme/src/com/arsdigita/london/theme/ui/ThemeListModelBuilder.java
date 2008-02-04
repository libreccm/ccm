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
 */

package com.arsdigita.london.theme.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.london.theme.Theme;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.bebop.list.AbstractListModelBuilder;


/**
 *  This builds a list of all of the themes in the system
 */
class ThemeListModelBuilder extends AbstractListModelBuilder {
    public ListModel makeModel(List list, PageState state) {
        return new ThemeListModelImpl(state);
    }

    private class ThemeListModelImpl implements ListModel {
        private DataCollection m_themes;
        private Theme m_current;
        public ThemeListModelImpl(PageState state) {
            m_themes = SessionManager.getSession().retrieve
                (Theme.BASE_DATA_OBJECT_TYPE);
            m_themes.addOrder("lower(" + Theme.TITLE + ")");
        }
        
        public Object getElement() {
            return m_current.getTitle() + " (" + m_current.getURL() + ")";
        }
        
        public String getKey() {
            return m_current.getID().toString();
        }
        
        public boolean next() {
            if ( m_themes.next() ) {
                m_current = new Theme(m_themes.getDataObject());
                return true;
            } else {
                return false;
            }
        }
    }
}
