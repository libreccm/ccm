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
package com.arsdigita.forum.ui;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

public class ThreadDisplay extends SimpleComponent implements Constants {

    private static final Logger s_log =
        Logger.getLogger(ThreadDisplay.class);

    private IntegerParameter m_pageNumber =
        new IntegerParameter(PAGINATOR_PARAM);
	private int m_pageSize = Forum.getConfig().getThreadPageSize();

    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_REPLY = "reply";
    private static final String ACTION_APPROVE = "approve";
    private static final String ACTION_REJECT = "reject";

    private ThreadComponent m_threadComponent;
    private ACSObjectSelectionModel m_post;

    public ThreadDisplay(ACSObjectSelectionModel post,
                         ThreadComponent threadComponent) {
        m_threadComponent = threadComponent;
        m_post = post;
    }


    public void register(Page p) {
        super.register(p);

        p.addGlobalStateParam(m_pageNumber);
    }

    public void respond(PageState state)
        throws ServletException {
        super.respond(state);

        String key = state.getControlEventName();
        String value = state.getControlEventValue();

        OID oid = new OID(Post.BASE_DATA_OBJECT_TYPE,
                          new BigDecimal(value));

        ForumContext ctx = ForumContext.getContext(state);
        Post post = (Post)DomainObjectFactory.newInstance(oid);

        if (ACTION_EDIT.equals(key)) {
            m_post.setSelectedObject(state, post);
            m_threadComponent.makeEditFormVisible(state);
        } else if (ACTION_DELETE.equals(key)) {
            Assert.isTrue(ctx.canAdminister(), "can administer forums");

            MessageThread thread = ctx.getMessageThread();
            ThreadedMessage root = thread.getRootMessage();

            if ( s_log.isDebugEnabled() ) {
                s_log.debug("message: " + post.getOID() +
                            " root: " + root.getOID() +
                            " thread: " + thread.getOID());
            }

            if ( ctx.getForum().isModerated() ) {
                if ( !ctx.canModerate() ) {
                    post.setStatus(Post.SUPPRESSED);
                    post.save();
                } else if (post.equals(root)) {
                    s_log.debug("Deleting entire thread");
                    post.delete();

                    Forum forum = ctx.getForum();
                    URL url = URL.there(state.getRequest(), forum, null );
                    throw new RedirectSignal( url, true );
                } else {
                    s_log.debug("Deleting message");
                    post.delete();
                }
            } else if (post.equals(root)) {
                s_log.debug("Deleting entire thread");
                post.delete();

                Forum forum = ctx.getForum();
                URL url = URL.there(state.getRequest(), forum, null );
                throw new RedirectSignal( url, true );
            } else {
                s_log.debug("Deleting message");
                post.delete();
            }
        } else if (ACTION_REPLY.equals(key)) {
            m_post.setSelectedObject(state, post);
            m_threadComponent.makeReplyFormVisible(state);
        } else if (ACTION_APPROVE.equals(key)) {
            post.setStatus(Post.APPROVED);
            post.save();
            post.sendNotifications(null);
        } else if (ACTION_REJECT.equals(key)) {
            m_post.setSelectedObject(state, post);
            m_threadComponent.makeRejectFormVisible(state);
        }

        state.clearControlEvent();
        try {
            throw new RedirectSignal( state.stateAsURL(), true );
        } catch( IOException ex ) {
            throw new UncheckedWrapperException( ex );
        }
    }

    private DomainCollection getMessages(PageState state) {

        ForumContext context = ForumContext.getContext(state);
        Party party = Kernel.getContext().getParty();
        Forum forum = context.getForum();

        BigDecimal rootID = context.getMessageThread().
            getRootMessage().getID();

        DataCollection messages = SessionManager.getSession().retrieve
            (Post.BASE_DATA_OBJECT_TYPE);

        // Hide replies if we're in noticeboard mode
        if (forum.isNoticeboard()) {
            messages.addEqualsFilter("id", rootID);
            return new DomainCollection(messages);
        }

        FilterFactory ff = messages.getFilterFactory();
        messages.addFilter(
            ff.or()
            .addFilter(ff.and()
                       .addFilter(ff.equals("root", null))
                       .addFilter(ff.equals("id", rootID)))
            .addFilter(ff.equals("root", rootID)));

        messages.addOrderWithNull("sortKey", "---", true);

        // Add a filter to only show approved messages
        if (forum.isModerated() && !forum.canModerate(party)) {
            messages.addFilter(ff.or()
                               .addFilter(ff.equals("status", Post.APPROVED))
                               .addFilter(ff.equals("sender.id", party == null ?
                                                    null : party.getID()))
                              );
        }

        return new DomainCollection(messages);
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement(FORUM_XML_PREFIX + ":threadDisplay",
                                                 FORUM_XML_NS);
        exportAttributes(content);

        Forum forum = ForumContext.getContext(state).getForum();
        content.addAttribute("forumTitle", forum.getTitle());
        content.addAttribute("noticeboard", (new Boolean(forum.isNoticeboard())).toString());
        DomainCollection messages = getMessages(state);

        Integer page = (Integer)state.getValue(m_pageNumber);
        int pageNumber = (page == null ? 1 : page.intValue());
        long objectCount = messages.size();
        int pageCount = (int)Math.ceil((double)objectCount / (double)m_pageSize);

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }

        long begin = ((pageNumber-1) * m_pageSize);
        int count = (int)Math.min(m_pageSize, (objectCount - begin));
        long end = begin + count;

        generatePaginatorXML(content,
                             pageNumber,
                             pageCount,
                             m_pageSize,
                             begin,
                             end,
                             objectCount);

        if (begin != 0 || end != 0) {
            messages.setRange(new Integer((int)begin+1),
                             new Integer((int)end+1));
        }

        while (messages.next()) {
            Post message = (Post)messages.getDomainObject();
            Element messageEl = content.newChildElement(FORUM_XML_PREFIX + ":message",
                                                        FORUM_XML_NS);

            generateActionXML(state, messageEl, message);

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(messageEl);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);

            xr.walk(message, ThreadDisplay.class.getName());
        }

    }

    protected void generateActionXML(PageState state,
                                     Element parent,
                                     Post post) {
        ForumContext ctx = ForumContext.getContext(state);

        String status = post.getStatus();
        if (ctx.canModerate()) {
            if (!status.equals(Post.REJECTED) &&
                !status.equals(Post.SUPPRESSED) ) {
                parent.addAttribute("rejectURL",
                                    makeURL(state, ACTION_REJECT, post));
            }
        }

        if (ctx.canModerate() &&
            !post.getStatus().equals(post.APPROVED)) {
            parent.addAttribute("approveURL",
                                makeURL(state, ACTION_APPROVE, post));
        }

        if (ctx.canAdminister()) {
            parent.addAttribute("deleteURL",
                                makeURL(state, ACTION_DELETE, post));
        }


        Party party = Kernel.getContext().getParty();
        if (party == null) {
        	party = Kernel.getPublicUser();
        }
        if (post.canEdit(party)) {
            parent.addAttribute("editURL",
                                makeURL(state, ACTION_EDIT, post));
        }

		PermissionDescriptor canRespond = new PermissionDescriptor(PrivilegeDescriptor.get(Forum.RESPOND_TO_THREAD_PRIVILEGE), Kernel.getContext().getResource(), party);
		
        if (!ctx.getForum().isNoticeboard() && PermissionService.checkPermission(canRespond)) {
            parent.addAttribute("replyURL",
                                makeURL(state, ACTION_REPLY, post));
        }

    }

    protected String makeURL(PageState state,
                             String action,
                             Post post) {
        state.setControlEvent(this, action,post.getID().toString());

        String url = null;
        try {
            url = state.stateAsURL();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("cannot create url", ex);
        }
        state.clearControlEvent();
        return url;
    }

    protected void generatePaginatorXML(Element parent,
                                        int pageNumber,
                                        int pageCount,
                                        int pageSize,
                                        long begin,
                                        long end,
                                        long objectCount) {
        Element paginator = parent.newChildElement(FORUM_XML_PREFIX + ":paginator", FORUM_XML_NS);

        URL here = Web.getContext().getRequestURL();
        ParameterMap params = new ParameterMap(here.getParameterMap());
        params.clearParameter(PAGINATOR_PARAM);

        URL url = new URL(here.getScheme(),
                          here.getServerName(),
                          here.getServerPort(),
                          here.getContextPath(),
                          here.getServletPath(),
                          here.getPathInfo(),
                          params);

        paginator.addAttribute("param", PAGINATOR_PARAM);
        paginator.addAttribute("baseURL", XML.format(url));
        paginator.addAttribute("pageNumber", XML.format(new Integer(pageNumber)));
        paginator.addAttribute("pageCount", XML.format(new Integer(pageCount)));
        paginator.addAttribute("pageSize", XML.format(new Integer(pageSize)));
        paginator.addAttribute("objectBegin", XML.format(new Long(begin+1)));
        paginator.addAttribute("objectEnd", XML.format(new Long(end)));
        paginator.addAttribute("objectCount", XML.format(new Long(objectCount)));
    }

}
