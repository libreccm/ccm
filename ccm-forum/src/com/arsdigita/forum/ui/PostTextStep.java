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
package com.arsdigita.forum.ui;

import java.text.DateFormat;
import java.util.Date;

import org.apache.log4j.Logger;


import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.Post;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.toolbox.ui.TextTypeWidget;
import com.arsdigita.util.MessageType;
import com.arsdigita.xml.Element;

/**
 * @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 *
 * Shared behaviour for text step
 */
public abstract class PostTextStep extends FormStep implements Constants {

	private static Logger s_log = Logger.getLogger(PostTextStep.class);
	private PostForm m_container;

	private static final String FCK_FORUM_CONFIG =
		"/assets/fckeditor/config/fckconfig_forum.js";
	private TextField m_subject;
	private TextArea m_body;
	
	private TextTypeWidget m_bodyType;


	private ACSObjectSelectionModel m_post;

	/**
	 * create a text step with title and message fields and message type selection list
	 * TO DO - add config parameter to conditionally replace latter 2 with a single FCKEditor instance
	 * @param post
	 * @param container
	 */
	public PostTextStep(ACSObjectSelectionModel post, PostForm container) {
		super(
			"postText",
			new SimpleContainer(FORUM_XML_PREFIX + ":postForm", FORUM_XML_NS));
		m_container = container;
		m_post = post;
		m_subject = new TextField(new StringParameter("subject"));
		m_subject.addValidationListener(
			new NotEmptyValidationListener(
				Text.gz("forum.ui.validation.subject_null")));
		m_subject.setMaxLength(250);
		m_subject.setSize(60);
		add(m_subject);

		if (Forum.getConfig().useWysiwygEditor()) {
			m_body = new DHTMLEditor("message");
			m_body.setRows(8);
			m_body.setCols(60);
			m_body.setMetaDataAttribute("width", "450");
			m_body.setMetaDataAttribute("height", "300");
			m_body.setWrap(DHTMLEditor.SOFT);
			if (Bebop
				.getConfig()
				.getDHTMLEditor()
				.equals(BebopConstants.BEBOP_FCKEDITOR)) {
	
				((DHTMLEditor) m_body).setConfig(
					new DHTMLEditor.Config("forum", FCK_FORUM_CONFIG));
			} else {
	
				// remove this so end users cannot browse through back end folder system
				 ((DHTMLEditor) m_body).hideButton("insertlink");
			}
		} else {
			m_body = new TextArea(new StringParameter("message"),
	                8, 60, TextArea.SOFT);
		}
		// otherwise, standard HTMLArea config - may need to change this if anyone is still
		// using html area

		m_body.addValidationListener(
			new NotEmptyValidationListener(
				Text.gz("forum.ui.validation.body_null")));

		add(m_body);

		m_bodyType = new TextTypeWidget(new StringParameter("bodyType"),
                MessageType.TEXT_PLAIN);
				
		add(m_bodyType);

		addInitListener(new InitTextStepListener());

	}

	/**
	 * allow subclasses to initialise subject field
	 * @param state
	 * @param text
	 */
	protected void setSubject(PageState state, String text) {
		m_subject.setValue(state, text);
	}
	/**
	 * allow subclasses to initialise message and message type fields
	 * @param state
	 * @param body
	 * @param bodyType
	 */
	protected void setBody(PageState state, String body, String bodyType) {
		m_body.setValue(state, body);
		m_bodyType.setValue(state, bodyType);

	}

	protected void initWidgets(PageState state, Post post) {
		StringBuffer body = new StringBuffer();
		if (m_container.getContext(state).equals(PostForm.EDIT_CONTEXT)) {
			if (Forum.getConfig().useWysiwygEditor()) {
				body.append("<p>\n<i>\n");
				body.append(
					"Edited on "
						+ DateFormat.getDateInstance(DateFormat.SHORT).format(
							new Date())
						+ " by "
						+ ((User) Kernel.getContext().getParty()).getName());
				body.append("\n</i>\n</p>\n\n<br/>\n\n");
			} else {
				body.append("\n");
				body.append(
						"Edited on "
							+ DateFormat.getDateInstance(DateFormat.SHORT).format(
								new Date())
							+ " by "
							+ ((User) Kernel.getContext().getParty()).getName());
				body.append("\n\n");
			}

		}
		body.append(post.getBody());
		setSubject(state, post.getSubject());
		setBody(state, body.toString(), post.getBodyType());

	}

	/*
	 * 
	 * 
	 * If a delete link is used, the parameter that tells the wizard not to 
	 * reinitialize steps is ALSO lost, and so init listeners are fired again
	 *
	 */
	private class InitTextStepListener implements FormInitListener {

		public void init(FormSectionEvent e) throws FormProcessException {
			
			
			s_log.debug("Init called on Text Step");
			PageState state = e.getPageState();

			if (Forum.getConfig().useWysiwygEditor()) {
				m_bodyType.setValue(state, MessageType.TEXT_HTML);
				m_bodyType.setVisible(state, false);
			}
			if (m_post != null) {
				Post post = (Post) m_post.getSelectedObject(state);
				initWidgets(state, post);
			} 

		}

	}
	/**
	 * 
	 */
	public void setText(Post post, PageState state) {
		post.setSubject((String) m_subject.getValue(state));
		
		post.setBody((String) m_body.getValue(state), (String) m_bodyType.getValue(state));

	}

	/**
	 * @param state
	 * @param content
	 */
	public void generatePostXML(PageState state, Element content) {
		Element subject = content.newChildElement("subject");
		subject.setText((String) m_subject.getValue(state));
		Element body = content.newChildElement("body");
		body.setText((String) m_body.getValue(state));

	}
}
