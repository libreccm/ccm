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
package com.arsdigita.forum.ui.admin;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SimpleContainer;


import org.apache.log4j.Logger;

public class ModerationView extends SimpleContainer {
    private static final Logger s_log = Logger.getLogger
        (ModerationView.class);
    private ModerationFormSection m_modSection;

    private ModeratorEditPane m_moderatorPane;

    private Form m_modForm;
    public ModerationView() {
        m_modForm = new Form("moderationForm", new ColumnPanel(3));
        m_modForm.setRedirecting(true);
        m_modSection = new ModerationFormSection();
        m_modForm.add(m_modSection, ColumnPanel.INSERT);
        m_modForm.add(new NoticeboardFormSection(), ColumnPanel.INSERT);
        add(m_modForm);

        m_moderatorPane = new ModeratorEditPane();
        add(m_moderatorPane);
    }
}
