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
package com.arsdigita.bebop;

import com.arsdigita.bebop.form.Submit;


public class YesNoSection extends FormSection {
    private Submit m_yes;
    private Submit m_no;

    public YesNoSection() {
        super(new BoxPanel(BoxPanel.HORIZONTAL, false));

        BoxPanel panel = (BoxPanel)getPanel();
        panel.setWidth("2%");

        // Submit widgets
        m_yes = new Submit("yes");
        m_yes.setButtonLabel("Yes");
        add(m_yes, BoxPanel.RIGHT);

        m_no = new Submit("no");
        m_no.setButtonLabel("No");
        add(m_no, BoxPanel.RIGHT);
    }

    public boolean yesPressed(PageState state) {
        return m_yes.isSelected(state);
    }

    public boolean noPressed(PageState state) {
        return m_no.isSelected(state);
    }
}
