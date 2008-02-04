/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.ui;

import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Form;
import com.arsdigita.formbuilder.util.Placeholders;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.PageState;


public class PlaceholdersInitListener implements FormInitListener {

    public void init(FormSectionEvent e)
        throws FormProcessException {

        Form f = (Form)e.getSource();

        Placeholders placeholders = new Placeholders();

        PlaceholdersTraversal t = new PlaceholdersTraversal(placeholders, e.getPageState());
        t.preorder(f);
    }

    private class PlaceholdersTraversal extends Traversal {
        Placeholders m_placeholders;
        PageState m_state;

        public PlaceholdersTraversal(Placeholders placeholders,
                                     PageState state) {
            m_placeholders = placeholders;
            m_state = state;
        }

        public void act(Component c) {
            try {
                Widget w = (Widget)c;
                String value = (String)w.getParameterModel().getDefaultValue();
                if (value != null)
                    w.setValue(m_state, m_placeholders.interpolate(value));
            } catch (ClassCastException ex) {
                // Nada
            }
        }
    }
}
