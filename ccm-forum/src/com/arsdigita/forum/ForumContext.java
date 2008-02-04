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
package com.arsdigita.forum;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * The entry point into all the global state parameters that the Forum
 * application expects to have available to it when running, e.g. the current
 * forum, thread, etc.
 *
 * <p>This is a request local object.</p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @see com.arsdigita.kernel.KernelContext
 */
public final class ForumContext {

    private static final Logger s_log = Logger.getLogger(ForumContext.class);

    private static final RequestLocal s_context = new RequestLocal() {
            public Object initialValue(PageState state) {
                return new ForumContext();
            }
        };

    private BigDecimal m_threadID;
    private BigDecimal m_categorySelection;
    private MessageThread m_thread;
    private Forum m_forum;

    private boolean m_canEdit;
    private boolean m_canAdminister;
    private boolean m_canModerate;

    ForumContext() {
        m_forum = (Forum)Kernel.getContext().getResource();
        Assert.exists(m_forum, Forum.class);
        
        Party party = Kernel.getContext().getParty();
        
        m_canEdit = m_forum.canEdit(party);
        m_canModerate = m_forum.canModerate(party);
        m_canAdminister = m_forum.canAdminister(party);
    }

    public static ForumContext getContext(PageState state) {
        return (ForumContext)s_context.get(state);
    }

    static void setContext(PageState state, ForumContext context) {
        s_context.set(state, context);
    }

    /**
     * Retrieves the thread ID.
     */
    public BigDecimal getThreadID() {
        return m_threadID;
    }

    /**
     * Sets the current thread ID.
     */
    public void setThreadID(BigDecimal threadID) {
        if ( m_threadID == threadID) {
            return;
        }
        m_threadID = threadID;
        try {
            MessageThread thread = new MessageThread(threadID);
            // FIXME: (Seb 20040521)
            // Although 'Public' doesn't seem to have problem with it,
            // the assertion fails for normal registered users ??!?!?
            // Commenting it out until we find better solution.
            // thread.assertPrivilege(PrivilegeDescriptor.READ);
            m_thread =  thread;
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(
                "Couldn't find a MessageThread for " + threadID, ex);
        }
    }

    /**
     * Retrieves the current message thread.
     **/
    public MessageThread getMessageThread() {
        return m_thread;
    }

    /**
     * Retrieves the current Forum.
     **/
    public Forum getForum() {
        return m_forum;
    }
    
    
    public boolean canEdit() {
        return m_canEdit;
    }
    
    public boolean canAdminister() {
        return m_canAdminister;
    }
    
    public boolean canModerate() {
        return m_canModerate;
    }
    

    public BigDecimal getCategorySelection() {
        return m_categorySelection;
    }

    public void setCategorySelection(BigDecimal categorySelection) {
        m_categorySelection = categorySelection;
    }
}
