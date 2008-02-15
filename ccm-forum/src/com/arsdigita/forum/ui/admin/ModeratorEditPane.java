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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.Group;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;



/** 
 * Wrapper class to have com.arsdigita.ui.permissions.PermissionsPane
 * play nice with Workspaces.
 */


public class ModeratorEditPane extends SimpleContainer {
    private Forum m_forum;
    
    private static final Logger s_log = Logger.getLogger(ModeratorEditPane.class);

    public ModeratorEditPane() {

        GroupMemberDisplay members = new GroupMemberDisplay() {
                public Group getGroup(PageState state) {
                    Forum forum = ForumContext.getContext(state).getForum();
                    Group g = forum.getModerationGroup();
                    Assert.exists(g, Group.class);
                    if (s_log.isDebugEnabled()) {
                        // check that moderator group has the right permissions
                        if (forum.canModerate(g)) {
                            s_log.debug(g.getName() + "has the moderation permission");
                        } else {
                            s_log.debug(g.getName() 
                                        + "Insufficient permissions for group");
                        }

                    }
                    return g;
                }
            };
        add(members);

        Form form = new Form("userPicker", 
                             new BoxPanel(BoxPanel.VERTICAL));
        form.add(new GroupMemberPicker("moderator") {
                public Group getGroup(PageState state) {
                    Forum forum = ForumContext.getContext(state).getForum();
                    return forum.getModerationGroup();
                }
            });
        form.setIdAttr("memberUserPicker");
        add(form);
        
    }
}
