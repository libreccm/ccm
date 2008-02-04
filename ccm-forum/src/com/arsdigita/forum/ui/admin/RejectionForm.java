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

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Wizard;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.Party;
import com.arsdigita.messaging.Message;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.Notification;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

public class RejectionForm extends Wizard implements Constants {
    private static final Logger s_log = Logger.getLogger(RejectionForm.class);
    private ACSObjectSelectionModel m_postModel;

    private Notification m_notification;

    private TextArea m_textArea;

    protected final static String ALERT_BLURB
        = "This is an automated notice from the Discussion Forum system. ";

    protected final static String SEPARATOR
        = "\n\n" + StringUtils.repeat('-',20) + "\n\n";

    public RejectionForm(ACSObjectSelectionModel postModel) {
        super("postRejectionForm");
        m_postModel = postModel;
        add(dataEntryStep());
        add(confirmStep());
        addInitListener(new RejectionInitListener());
        addProcessListener(new RejectionProcessListener());
    }

    private FormStep dataEntryStep() {
        FormStep form = new FormStep("initial",
                                     new BoxPanel(BoxPanel.VERTICAL));

        form.add(new Label("Message"));
        m_textArea = new TextArea("bodyText");
        form.add(m_textArea);

        return form;
    }

    private Container confirmStep() {
        BoxPanel confirm = new BoxPanel(BoxPanel.VERTICAL);
        Label subject = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Post post = (Post)m_postModel.getSelectedObject(state);
                    Label l = (Label) e.getTarget();
                    l.setLabel((String)getHeader(post));
                }
            });

        Label body = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Post post = (Post)m_postModel.getSelectedObject(state);
                    Label l = (Label) e.getTarget();
                    l.setOutputEscaping(false);
                    String body = StringUtils.quoteHtml(getBody(post, state));
                    l.setLabel("<pre>" + body + "</pre>");
                }
            });

        Label sig = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Post post = (Post)m_postModel.getSelectedObject(state);
                    Label l = (Label) e.getTarget();
                    l.setLabel((String)getSignature());
                }
            });


        confirm.add(subject);
        confirm.add(body);
        confirm.add(sig);
        return confirm;

    }

    private class RejectionInitListener implements FormInitListener {
        public void init(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            m_textArea.setValue( state, "");
        }
    }


    private String getHeader(Post post) {
            StringBuffer header = new StringBuffer();
            header.append("Forum    : ");
            header.append(post.getForum().getDisplayName()).append("\n");
            header.append("Subject  : ");
            header.append(post.getSubject()).append("\n\n");
            return header.toString();
    }

    private String getBody(Post post, PageState state) {
        StringBuffer body = new StringBuffer();
        body.append("Your message has been rejected by the moderator.\n");

        body.append("The moderator has given the following reasons:\n\n");
        body.append((String)m_textArea.getValue(state));
        body.append("\n\n");

        body.append("The content of the message follows:\n\n");
        body.append("Subject: "  + post.getSubject() + "\n");
        body.append(post.getBody() + "\n\n\n");
        return body.toString();

    }

    private String getSignature() {
            StringBuffer sig = new StringBuffer();
            sig.append(SEPARATOR);
            sig.append(ALERT_BLURB);
            return sig.toString();
    }

    private class RejectionProcessListener implements FormProcessListener {
        public void process(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            Post post = (Post)m_postModel.getSelectedObject(state);

            MessageThread thread
                = ForumContext.getContext(state).getMessageThread();
            ThreadedMessage root
                = thread.getRootMessage();

            if (getFinish().isSelected(state)) {
                if (post.equals(root)) {
                    s_log.debug("Suppressing entire thread");
                    post.setStatus(Post.SUPPRESSED);
                } else {
                    s_log.debug("rejecting message");
                    post.setStatus(Post.REJECTED);
                }

                post.save();

                String msg = (String)m_textArea.getValue(state);
                if (!(msg == null ||
                      "".equals(msg.trim()))) {
                    sendNotice(post, state);
                } else {
                    s_log.info("Skipping notification because message is empty");
                }
            }
            fireCompletionEvent(state);
        }

        private void sendNotice(Post post, PageState state) {

            Forum  forum = ForumContext.getContext(state).getForum();

            Notification notice = new Notification();
            notice.setTo(post.getFrom());
            notice.setHeader(getHeader(post));

            Message message = new Message();
            Party noticeSender = forum.getModerationGroup();
            if (noticeSender == null ) {
                noticeSender = post.getModerator();
            }
            message.setFrom(noticeSender);
            message.setSubject("Moderation notice "
                               + post.getForum().getDisplayName());
            message.setBody(getBody(post, state), Message.TEXT_PLAIN);

            notice.setMessage(message);

            notice.setSignature(getSignature());

            s_log.debug("sending notification" + message +
                       "\n to: " + post.getFrom().getName() );

            notice.save();
        }

    }

}

