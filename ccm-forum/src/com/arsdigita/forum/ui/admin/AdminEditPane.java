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

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.Group;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;




public class AdminEditPane extends SimpleContainer {
    private Forum m_forum;
    
    private static final Logger s_log = Logger.getLogger(AdminEditPane.class);

    public AdminEditPane() {
		
        GroupMemberDisplay members = new GroupMemberDisplay() {
                public Group getGroup(PageState state) {
                    Forum forum = ForumContext.getContext(state).getForum();
                    Group g = forum.getAdminGroup();
                    Assert.exists(g, Group.class);
                    
                    return g;
                }
            };
            members.setIdAttr("Forum-Admin");
        add(members);

        Form form = new Form("adminUserPicker", 
                             new BoxPanel(BoxPanel.VERTICAL));
        form.add(new GroupMemberPicker("admin") {
                public Group getGroup(PageState state) {
                    Forum forum = ForumContext.getContext(state).getForum();
                    return forum.getAdminGroup();
                }
            });
        form.setIdAttr("adminMemberUserPicker");
        add(form);
        
    }
}
