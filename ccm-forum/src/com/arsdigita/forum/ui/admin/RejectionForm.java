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

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Wizard;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailValidationListener;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.mail.Mail;
import com.arsdigita.messaging.Message;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.Notification;
import com.arsdigita.util.HtmlToText;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

public class RejectionForm extends Wizard implements Constants {
    private static final Logger s_log = Logger.getLogger(RejectionForm.class);
    private ACSObjectSelectionModel m_postModel;

	private Label m_recipientLabel;
	private TextField m_recipient;
    private TextArea m_textArea;

    protected final static String ALERT_BLURB
        = "This is an automated notice from the Discussion Forum system. ";

    protected final static String SEPARATOR
        = "\n\n" + StringUtils.repeat('-',20) + "\n\n";
	protected final static String HTML_SEPARATOR
	= "<hr>";

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

		m_recipientLabel = new Label("Recipient Email (Optional)");
		form.add(m_recipientLabel);
		m_recipient = new TextField("recipient");
		m_recipient.addValidationListener(new EmailValidationListener() {
		/* (non-Javadoc)
		 * @see com.arsdigita.bebop.parameters.EmailValidationListener#validate(com.arsdigita.bebop.event.ParameterEvent)
		 */
		public void validate(ParameterEvent e) throws FormProcessException {
			if (!StringUtils.emptyString(m_recipient.getValue(e.getPageState()))) {
			
				super.validate(e);
			}
		}
		});
		m_recipient.setMetaDataAttribute("label","Recipient email(optional)" );
		form.add(m_recipient);
        form.add(new Label("Message"));

        m_textArea = new TextArea("bodyText");
		m_textArea.setMetaDataAttribute("label", "Message");
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
                    l.setOutputEscaping(false);
		    l.setLabel((String)getHeader(post));
                }
            });

        Label body = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Post post = (Post)m_postModel.getSelectedObject(state);
                    Label l = (Label) e.getTarget();
                    l.setOutputEscaping(false);
					//String body = StringUtils.quoteHtml(getBody(post, state));
					//l.setLabel("<pre>" + body + "</pre>");
					l.setLabel(getBody(post, state));
                }
            });

        Label sig = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    PageState state = e.getPageState();
                    Post post = (Post)m_postModel.getSelectedObject(state);
                    Label l = (Label) e.getTarget();
		    l.setOutputEscaping(false);
					
					l.setLabel(Mail.getConfig().sendHTMLMessageAsHTMLEmail() ? getHTMLSignature() : StringUtils.replace(getSignature(), "\n", "<br>"));
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
			m_recipient.setValue(state, "");
			if (!ForumContext.getContext(state).getForum().anonymousPostsAllowed()) {
				// optional recipient field is only relevent if post was made
				// anonymously
				m_recipientLabel.setVisible(state, false);
				m_recipient.setVisible(state, false);
			}
        }
    }


    private String getHeader(Post post) {
            StringBuffer header = new StringBuffer();
            header.append("Forum    : ");
			header.append(post.getForum().getDisplayName()).append("<br>");
            header.append("Subject  : ");
			header.append(post.getSubject()).append("<br><br>");
            return header.toString();
    }

    private String getBody(Post post, PageState state) {
        StringBuffer body = new StringBuffer();
		body.append("<br><br>Your message has been rejected by the moderator.<br><br>");

		body.append("The moderator has given the following reasons:<br><br>");
        body.append((String)m_textArea.getValue(state));
		body.append("<br><br>");

		body.append("The content of the message follows:<br><br>");
		body.append("Subject: "  + post.getSubject() + "<br>");
		body.append(post.getBody() + "<br><br><br>");
		// additional comments cg. Need to make this whole email configurable - see notifications. Much better 
		String instructions = Forum.getConfig().getRejectionMessage();
		if (instructions != null) {
			body.append(instructions);
			body.append("<br><br>");
		}
		

        return body.toString();

    }

    private String getSignature() {
            StringBuffer sig = new StringBuffer();
            sig.append(SEPARATOR);
            sig.append(ALERT_BLURB);
            return sig.toString();
    }

	private String getHTMLSignature() {
		StringBuffer sig = new StringBuffer();
		sig.append(HTML_SEPARATOR);
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

		/**
		 * if optional email address has been entered on the form, send rejection
		 * notice to that address. Otherwise, sent to post author (unless anonymous post)
		 * 
		 * @param post
		 * @param state
		 */
        private void sendNotice(Post post, PageState state) {

            Forum  forum = ForumContext.getContext(state).getForum();
			Party noticeSender = forum.getModerationGroup();
			if (noticeSender == null ) {
				noticeSender = post.getModerator();
			}
			
			StringBuffer rejectionNoticeBody = new StringBuffer();
			rejectionNoticeBody.append(getHeader(post));
			rejectionNoticeBody.append(getBody(post, state));
			StringBuffer nonHTMLRejectionNotice = new StringBuffer(new HtmlToText().convert(rejectionNoticeBody.toString()));
			rejectionNoticeBody.append(getHTMLSignature());
			nonHTMLRejectionNotice.append(getSignature());
			if (!StringUtils.emptyString(m_recipient.getValue(state) )) {
				Mail mailMessage = new Mail();
				mailMessage.setTo((String)m_recipient.getValue(state));
				mailMessage.setFrom(noticeSender.getPrimaryEmail().getEmailAddress());
				mailMessage.setSubject("Moderation notice "	+ post.getForum().getDisplayName());
				if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
					rejectionNoticeBody.append(getHTMLSignature());
					mailMessage.setBody(rejectionNoticeBody.toString(), nonHTMLRejectionNotice.toString());
				} else {
					mailMessage.setBody(nonHTMLRejectionNotice.toString());
				}
				
				try {
					mailMessage.send();
				} catch (SendFailedException e) {
					s_log.error("Couldn't send forum post rejection message for post " + post.getID(), e);
				} catch (MessagingException e) {
					s_log.error("Couldn't send forum post rejection message for post " + post.getID(), e);
				}
				return;
			} else if (!post.getFrom().equals(Kernel.getPublicUser())) {

            Notification notice = new Notification();
            notice.setTo(post.getFrom());
            notice.setHeader(getHeader(post));

            Message message = new Message();
				
				
            message.setFrom(noticeSender);
            message.setSubject("Moderation notice "
                               + post.getForum().getDisplayName());
				if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
					notice.setHeader(getHeader(post));
					message.setBody(getBody(post, state), Message.TEXT_HTML);
					notice.setSignature(getHTMLSignature());
					
				} else {
					notice.setHeader(new HtmlToText().convert(getHeader(post)));
					message.setBody(new HtmlToText().convert(getBody(post, state)), Message.TEXT_PLAIN);
					notice.setSignature(getSignature());

				}
            notice.setMessage(message);


            s_log.debug("sending notification" + message +
                       "\n to: " + post.getFrom().getName() );


    }
		}

}

}
