/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Wizard;
import com.arsdigita.bebop.event.FormCancelListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.ThreadSubscription;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.xml.Element;

/**
 * Abstract form for behaviour shared by all types of post. Different types of
 * post (new or reply) have different text screens, retrieved using
 * the abstract getTextStep method and also may behave
 * differently in final processing (final meaning on the last step
 * of the wizard) by overriding the processWidgets method.
 * 
 * They all share the remainder of the steps (currently add files, add images
 * and preview) and processing relating to shared behaviour
 *
 * @author Jon Orris (jorris@arsdigita.com)
 * @author rewritten by Chris Gilbert 
 * @version $Revision #1 $DateTime: 2004/08/17 23:26:27 $
 * @version $Id: PostForm.java 2137 2011-01-14 09:16:46Z pboy $
 */
public abstract class PostForm extends Wizard implements Constants {

    private static final Logger s_log = Logger.getLogger(PostForm.class);

	private PostTextStep m_textStep;
	private ImagesStep m_attachImages;
	private AttachedFilesStep m_attachFiles;
	private ConfirmStep m_confirm;
	private ACSObjectSelectionModel m_post;

	/**
	 * context is used because when editing, we might use the root post
	 * form (with topic selection) or the reply to post form (without
	 * topic selection). context allows form to behave appropriately
	 * 
	 */
	private StringParameter m_context = new StringParameter("context");

	public static final String NEW_CONTEXT = "new";
	public static final String REPLY_CONTEXT = "reply";
	public static final String EDIT_CONTEXT = "edit";

	/**
	 * 
	 * Form used for new thread - no existing post object
	 * 
	 */
    public PostForm(String name) {
		this(name, null);
    }

	/**
	 * Used when editing an existing post
	 * @param name
	 * @param post
	 */
	public PostForm(String name, ACSObjectSelectionModel post) {
		super(name, new SimpleContainer(), Forum.getConfig().quickFinishAllowed(), true);
		// note that encoding must be multipart/form-data,and method must be post
		// in order for attachments to be uploaded. Ensure that these properties
		// are carried through in XSL if not simply applying the default bebop
		// templates
		setEncType("multipart/form-data");
		setMethod(POST);
		//setMethod(GET);
		m_post = post;

    }

	public void setContext(PageState state, String context) {
		state.setValue(m_context, context);
    }

	public String getContext(PageState state) {
		return (String) state.getValue(m_context);
	}

	protected void setupComponent() {

		m_textStep = getTextStep(m_post);
		add(m_textStep);
		m_attachImages = new ImagesStep(m_post, this);
		add(m_attachImages);
		m_attachFiles = new AttachedFilesStep(m_post, this);
		add(m_attachFiles);
		m_confirm = new ConfirmStep(m_post, this);
		add(m_confirm);

		addInitListener(new PostInitListener());
		addCancelListener(new PostCancelListener());
		addSubmissionListener(new PostSubmissionListener());
		addProcessListener(new PostProcessListener());

    }

	public void register(Page p) {
		super.register(p);
                
		p.addGlobalStateParam(m_context);
                    
                }
        
	/**
	 * return text step appropriate for the type of post - reply doesn't give the option 
	 * to choose a topic
	 * @param post
	 * @return
	 */
	protected abstract PostTextStep getTextStep(ACSObjectSelectionModel m_post);

	/**
	 * potentially create a new post object (unless we are editing one) Subclasses
	 * should create a post in whatever way is appropriate (eg a reply form 
	 * will need to tie the new post to the one that is being replied to)
	 * @param state
	 * @return
	 */
	protected abstract Post getPost(PageState state);
    
	//
	//
	// FORM EVENT LISTENERS. NOTE THAT INDIVIDUAL STEP LISTENERS ARE 
	// NOTIFIED AFTER THE MAIN FORM LISTENERS - BE AWARE
	//
	//
	//

    private class PostInitListener implements FormInitListener {        
        public void init(FormSectionEvent e) {
			s_log.debug("init called on parent form");
            PageState state = e.getPageState();
            
			if (Kernel.getContext().getParty() == null
				&& !ForumContext
					.getContext(state)
					.getForum()
					.anonymousPostsAllowed()) {
                UserContext.redirectToLoginPage(state.getRequest());
            }
            
        }
    }
	private class PostSubmissionListener implements FormSubmissionListener {

		/**
		 * potentially skip steps of the wizard if the forum does not
		 * allow images or file attachments
		 */
		public void submitted(FormSectionEvent e) {
			s_log.debug("page submitted");
			PageState state = e.getPageState();
			ForumContext ctx = ForumContext.getContext(state);
			Forum forum = ctx.getForum();
			if (!forum.allowImageUploads()) {
				hideStep(1, state);
			}
			if (!forum.allowFileAttachments()) {
				hideStep(2, state);
			}
		}
	}
            
	private class PostCancelListener implements FormCancelListener {
		public void cancel(FormSectionEvent e) throws FormProcessException {
            
			PageState state = e.getPageState();
			clearParameters(state);
			fireCompletionEvent(state);
		}
	}
        
	private class PostProcessListener implements FormProcessListener {
		public void process(FormSectionEvent e) throws FormProcessException {
			s_log.debug("process called on Parent Form");
			final PageState state = e.getPageState();
			Post post = getPost(state);
			//m_post.setSelectedObject(state, post);
			post.setStatus(state);
			m_textStep.setText(post, state);
            post.save();
			m_attachFiles.attachFiles(post, state);
			m_attachImages.attachImages(post, state);

			// sort out notifications
			if (getContext(state).equals(NEW_CONTEXT)) {
				s_log.debug("new thread - create subscription");
				ThreadSubscription sub = post.createThreadSubscription();
				ForumContext ctx = ForumContext.getContext(state);
				Forum forum = ctx.getForum();
				if (forum.autoSubscribeThreadStarter()) {
					User threadStarter = (User) Kernel.getContext().getParty();
					if (threadStarter != null
						&& !threadStarter.equals(Kernel.getPublicUser())) {
						s_log.debug("auto subscribing current user");
						sub.subscribe(threadStarter);
					}
				}
			}
			post.sendNotifications((String) state.getValue(m_context));
			post.sendModeratorAlerts();
			clearParameters(state);
            fireCompletionEvent(state);

            }
	}
	

	public Post getSelectedPost(PageState state) {
		if (m_post == null) {
			return null;
		} else {
			return (Post) m_post.getSelectedObject(state);
        }
    }

	/**
	 * required for confirmation step. Post hasn't been created so we
	 * cannot traverse a domain object. Instead, the form is responsible
	 * for retrieving a representation of the post as it will appear when saved
	 * @param state
	 * @param content
	 */
	protected void generatePostXML(PageState state, Element content) {
		m_textStep.generatePostXML(state, content);
		m_attachFiles.generatePostXML(state, content);
		m_attachImages.generatePostXML(state, content);

        }
	
	private void clearParameters(PageState state) {
		state.setValue(m_context, null);
		m_attachImages.clearParameters(state);
		m_attachFiles.clearParameters(state);
    }
}
