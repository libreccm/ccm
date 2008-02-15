/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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

/**
 * A component that allows users and groups to be added to 
 * a choice of access groups. 
 * 
 * Note, as privileges cascade, any user only needs
 * to be in one group (though it doesn't matter if
 * they are put into more than one
 */
public class PermissionsView extends SimpleContainer {
    private static final Logger s_log = Logger.getLogger
        (PermissionsView.class);
    
    private AdminEditPane m_adminPane;
    private ModeratorEditPane m_moderatorPane;
    private ThreadCreatorEditPane m_creatorPane;
    private ThreadResponderEditPane m_responderPane;
    private ReaderEditPane m_readerPane;
    
    

    public PermissionsView() {
        
	m_adminPane = new AdminEditPane();
	add(m_adminPane);
        
        m_moderatorPane = new ModeratorEditPane();
        add(m_moderatorPane);
        m_creatorPane = new ThreadCreatorEditPane();
        add(m_creatorPane);
        m_responderPane = new ThreadResponderEditPane();
        add(m_responderPane);
        m_readerPane = new ReaderEditPane();
        add(m_readerPane);
    }
}
