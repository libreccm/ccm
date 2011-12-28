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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.FileSizeValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.PostFileAttachment;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 *
 * Wizard step to attach files to a post. This step may be bypassed 
 * if a forum administrator changes the forum settings on the UI
 *
 * nb - a simpler reuseable generic wizard file attachments step has been created
 * forum will be refactored to use it in the future. see ccm-wsx-wizard-steps in contrib 
 */
public class AttachedFilesStep
	extends FormStep
	implements Constants, FormInitListener, FormProcessListener, FormValidationListener {

	private static Logger s_log = Logger.getLogger(AttachedFilesStep.class);
	private ACSObjectSelectionModel m_post;

	
	
	private AttachedFilesTable m_attachedFiles;
	private FileUpload m_upload;
	private TextArea m_description;
	private Submit m_addFile;
	private PostForm m_container;

	private ArrayParameter m_newFiles = new ArrayParameter("newFiles");
	private ArrayParameter m_existingFiles = new ArrayParameter("oldFiles");

	
	public AttachedFilesStep(ACSObjectSelectionModel post, PostForm container) {
		super("postFiles",
			new SimpleContainer(
				FORUM_XML_PREFIX + ":postFormFiles",
				FORUM_XML_NS));

		m_post = post;
		m_container = container;
		m_attachedFiles = new AttachedFilesTable(m_newFiles, m_existingFiles, this);

		add(m_attachedFiles);

		m_upload = new FileUpload("file", true);
		m_upload.addValidationListener(new NotEmptyValidationListener(
				Text
				.gz("forum.ui.validation.file_null")) {
			public void validate(ParameterEvent e) {
				//	don't fire validation if the next or previous button of the wizard has been pressed

				if (m_addFile.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});
		// this validation can fire any time - if there is no file then it won't fail
		m_upload.addValidationListener(
			new FileSizeValidationListener(Forum.getConfig().getMaxFileSize()));
		add(m_upload);
		m_description = new TextArea("fileDescription");
		m_description.setCols(20);
		m_description.setRows(5);

		m_description
			.addValidationListener(new StringInRangeValidationListener(
				0,
				4000,
				Text.gz("forum.ui.validation.file_description_too_long")) {
			public void validate(ParameterEvent e)
				throws FormProcessException {
				//	don't fire validation if the next or previous button of the wizard has been pressed

				if (m_addFile.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});
		add(m_description);
		m_addFile = new Submit("Add File");
		add(m_addFile);
		addInitListener(this);
		addProcessListener(this);
		addValidationListener(this);
	}

	/**
	 * Uploads the file and creates an orphaned PostFileAttachment
         * that will be associated with the post when the wizard is completed
         * or deleted if the wizard is cancelled.
	 */
	public void process(FormSectionEvent e) throws FormProcessException {
		FormData data = e.getFormData();
		PageState state = e.getPageState();
		if (m_addFile.isSelected(state)) {

			PostFileAttachment attachment;
			s_log.debug("adding file");
			try {
				String fileName = (String) m_upload.getValue(state);
				s_log.debug("filename is " + fileName);
				File file =
					(
						(MultipartHttpServletRequest) e
							.getPageState()
							.getRequest())
							.getFile(
						"file");
				s_log.debug("uploaded file is " + file.getName());
				//MimeType mimeType = MimeType.guessMimeTypeFromFile(fileName);
				//s_log.debug("mime type is " + mimeType.getMimeType());
				attachment = new PostFileAttachment();
				attachment.loadFromFile(
					fileName,
					file,
					"application/octet-stream");
				attachment.setDescription(
					(String) m_description.getValue(state));
				BigDecimal id = attachment.getID();
				String[] current = (String[]) state.getValue(m_newFiles);
				if (current == null) {
					current = new String[] { id.toString()};
				} else {
					List files = Arrays.asList(current);
					current = new String[files.size() + 1];
					Iterator it = files.iterator();
					int i = 0;
					while (it.hasNext()) {
						current[i++] = (String) it.next();
					}
					current[i] = id.toString();
				}
				state.setValue(m_newFiles, current);
				s_log.debug("File Uploaded");
				m_description.setValue(state, null);

			} catch (Exception ex) {
				throw new FormProcessException(ex);
			}
		}
	}

	/**
	 * Prevent users navigating away from this page unintentionally without
	 * adding an uploaded file first
	 */
	public void validate(FormSectionEvent e) throws FormProcessException {
		PageState state = e.getPageState();
		if (!m_addFile.isSelected(state)
			&& StringUtils.isNotBlank((String) m_upload.getValue(state))) {
			throw new FormProcessException(
				(String) Text
					.gz("forum.ui.validation.file_not_uploaded")
					.localize());
		}

	}
	
	public void removeFile(BigDecimal id, PageState state) {
		String[] existingArray = (String[]) state.getValue(m_existingFiles);
		if (existingArray != null) {
			List existing = Arrays.asList(existingArray);
			if (!existing.contains(id)) {
				// this has been added during edit (or this is a new post)
				s_log.debug("new file - I will actually delete it");
				PostFileAttachment attachment = new PostFileAttachment(id);
				attachment.delete();
			}
		}
		// m_new_files cannot contain null if request is to delete file
		Assert.exists(state.getValue(m_newFiles));
		List newFiles =
			new ArrayList(Arrays.asList((String[]) state.getValue(m_newFiles)));
		newFiles.remove(id);
		String[] current = new String[newFiles.size()];
		Iterator it = newFiles.iterator();
		int i = 0;
		while (it.hasNext()) {
			current[i++] = (String) it.next();
		}
		s_log.debug("size of new files array = " + current.length);
		state.setValue(m_newFiles, current);

	}

	public DataCollection getCurrentFiles(PageState state) {

		String[] currentArray = (String[]) state.getValue(m_newFiles);
		if (currentArray != null && currentArray.length != 0) {
			List current = Arrays.asList((String[]) state.getValue(m_newFiles));

			DataCollection files =
				SessionManager.getSession().retrieve(
					PostFileAttachment.BASE_DATA_OBJECT_TYPE);
			files.addFilter("id in :files").set("files", current);
			return files;
		}
		return null;

	}

	/**
	 * called at the end of the wizard when the form is processed
	 * set the files attached to the post according to the 
	 * file attachment step
	 * @param post
	 * @param state
	 */
	public void attachFiles(Post post, PageState state) {

		List newFiles = Collections.EMPTY_LIST;
		String[] newFilesArray = (String[]) state.getValue(m_newFiles);
		if (newFilesArray != null) {
			newFiles = new ArrayList(Arrays.asList(newFilesArray));
		}
		String[] existing = (String[]) state.getValue(m_existingFiles);

		if (existing != null) {
			// we are editing a post - leave any unchanged files, delete any that have been 
			// removed 
			for (int i = 0; i < existing.length; i++) {
				if (newFiles.contains(existing[i])) {
					// no change to this one
					newFiles.remove(existing[i]);
				} else {
					// file has been deleted in edit
					PostFileAttachment file =
						new PostFileAttachment(new BigDecimal(existing[i]));
					post.removeFile(file);
					// file deleted

				}
			}
		}
		Iterator it = newFiles.iterator();
		while (it.hasNext()) {
			// new files added 
			PostFileAttachment file =
				new PostFileAttachment(new BigDecimal((String) it.next()));
			post.addFile(file);
		}
		state.setValue(m_newFiles, null);
		state.setValue(m_existingFiles, null);

	}

	public void register(Page p) {
		super.register(p);

		p.addGlobalStateParam(m_existingFiles);
		p.addGlobalStateParam(m_newFiles);
	}


	
	public void init(FormSectionEvent e) throws FormProcessException {
		PageState state = e.getPageState();
		
		if (m_container
			.getContext(state)
			.equals(PostForm.EDIT_CONTEXT)) {

			Post editPost = (Post) m_post.getSelectedObject(state);
			DataAssociationCursor files = editPost.getFiles();
			if (files.size() > 0) {

				String[] fileIDs =
					new String[new Long(files.size()).intValue()];
				int i = 0;
				while (files.next()) {

					PostFileAttachment file =
						(PostFileAttachment) DomainObjectFactory.newInstance(
							files.getDataObject());
					fileIDs[i++] = file.getID().toString();
				}
				state.setValue(m_existingFiles, fileIDs);
				state.setValue(m_newFiles, fileIDs);
			}
		}

	}
	
	/**
	 * generate xml for the confirmation step (unfortunately we can't just 
	 * traverse the post because it hasn't been created at this point)
	 * @param state
	 * @param p
	 */
	public void generatePostXML(PageState state, Element p) {
		DataCollection files = getCurrentFiles(state);
		if (files == null) {
			return;
			
		}
		while(files.next()) {
			PostFileAttachment file = (PostFileAttachment) DomainObjectFactory.newInstance(
										files.getDataObject());
			DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(p.newChildElement(FORUM_XML_PREFIX + ":files", FORUM_XML_NS));
			xr.setWrapRoot(false);
			xr.setWrapAttributes(true);
			xr.setWrapObjects(false);
			xr.walk(file, ConfirmStep.class.getName());
		}
	}

	
	protected void clearParameters(PageState state) {
		state.setValue(m_existingFiles, null);
		state.setValue(m_newFiles, null);
		
	}
			
		

}
