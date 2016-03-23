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

package com.arsdigita.bundle.ui;

import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.ui.AbstractTermItemSummary;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.parameters.StringParameter;

public class TermItemSummary extends AbstractTermItemSummary {

    private StringParameter m_key;
    private String m_defaultKey = "LGSL";

    public TermItemSummary() {
        m_key = new StringParameter("key");
    }

    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_key);
    }

    public void setDefaultDomain(String key) {
        m_defaultKey = key;
    }

    protected Domain getDomain(PageState state) {
        String key = (String)state.getValue(m_key);
        
        if (key == null) {
            key = m_defaultKey;
        }

        return Domain.retrieve(key);
    }

}
