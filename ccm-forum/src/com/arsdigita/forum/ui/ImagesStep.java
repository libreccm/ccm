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

import com.arsdigita.forum.util.GlobalizationUtil;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormStep;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
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
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.PostImageAttachment;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Wizard step for adding images, and displaying list of images
 * already added
 */
public class ImagesStep
	extends FormStep
	implements Constants, FormProcessListener, FormValidationListener, FormInitListener {

	private static Logger s_log = Logger.getLogger(ImagesStep.class);
	private ACSObjectSelectionModel m_post;

	private ImagesTable m_images;
	private FileUpload m_upload;
	private TextArea m_description;
	private Submit m_addImage;

	private PostForm m_container;
	private ArrayParameter m_newImages = new ArrayParameter("images");
	private ArrayParameter m_existingImages = new ArrayParameter("oldImages");
	
	public ImagesStep(ACSObjectSelectionModel post, PostForm container) {
		super(
			"postImages",
			new SimpleContainer(
				FORUM_XML_PREFIX + ":postFormImages",
				FORUM_XML_NS));

		m_post = post;
		m_container = container;
		m_images = new ImagesTable(m_newImages, m_existingImages, this);

		add(m_images);

		m_upload = new FileUpload("image", true);
		m_upload
			.addValidationListener(new NotEmptyValidationListener(
				GlobalizationUtil
				.gz("forum.ui.validation.image_file_null")) {
			public void validate(ParameterEvent e) {
				if (m_addImage.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});
		m_upload
			.addValidationListener(new FileSizeValidationListener(
				Forum
				.getConfig()
				.getMaxImageSize()) {
			public void validate(ParameterEvent e) {
				if (m_addImage.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});
		add(m_upload);
		m_description = new TextArea("imageDescription");
		m_description.setCols(20);
		m_description.setRows(5);
		m_description
			.addValidationListener(new NotEmptyValidationListener(
				GlobalizationUtil
				.gz("forum.ui.validation.image_description_null")) {
			public void validate(ParameterEvent e) {
				if (m_addImage.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});
		m_description
			.addValidationListener(new StringInRangeValidationListener(
				0,
				4000,
				GlobalizationUtil.gz("forum.ui.validation.image_description_too_long")) {
			public void validate(ParameterEvent e)
				throws FormProcessException {
				if (m_addImage.isSelected(e.getPageState())) {
					super.validate(e);
				}
			}
		});

		add(m_description);
		m_addImage = new Submit("Add Image");
		add(m_addImage);
		addInitListener(this);

		addProcessListener(this);
		addValidationListener(this);
	}

	public void process(FormSectionEvent e) throws FormProcessException {
		s_log.debug("process event fired");
		FormData data = e.getFormData();
		PageState state = e.getPageState();
		if (m_addImage.isSelected(state)) {
			PostImageAttachment a;
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
						m_upload.getName());

				s_log.debug("uploaded file is " + file.getName());
				MimeType mimeType = MimeType.guessMimeTypeFromFile(fileName);
				s_log.debug(
					"Upload mime type is instance of " + mimeType.getClass());
				s_log.debug(
					"mime type is "
						+ (mimeType == null ? null : mimeType.getMimeType()));
				a = new PostImageAttachment();
				a.loadFromFile(fileName, file, null);
				a.setDescription((String) m_description.getValue(state));
				a.setMimeType(mimeType);
				BigDecimal id = a.getID();
				String[] current = (String[]) state.getValue(m_newImages);
				if (current == null) {
					current = new String[] { id.toString()};
				} else {
					List images = Arrays.asList(current);
					current = new String[images.size() + 1];
					Iterator it = images.iterator();
					int i = 0;
					while (it.hasNext()) {
						current[i++] = (String) it.next();
					}
					current[i] = id.toString();
				}
				state.setValue(m_newImages, current);
				s_log.debug("File Uploaded");

				m_description.setValue(state, null);
			} catch (Exception ex) {
				throw new FormProcessException(ex);
			}
		}
	}

	/**
	 * Prevent users navigating away from this page unintentionally without
	 * adding an uploaded image first
	 */
	public void validate(FormSectionEvent e) throws FormProcessException {
		PageState state = e.getPageState();
		if (!m_addImage.isSelected(state)
			&& StringUtils.isNotBlank((String) m_upload.getValue(state))) {
			throw new FormProcessException( GlobalizationUtil
					.gz("forum.ui.validation.image_not_uploaded"));
		}
	}

	/**
	 * @param post
	 * @param state
	 */
	public void attachImages(Post post, PageState state) {
		List newImages = Collections.EMPTY_LIST;
		String[] newImagesArray = (String[]) state.getValue(m_newImages);
		if (newImagesArray != null) {
			newImages = new ArrayList(Arrays.asList(newImagesArray));
		}

		String[] existing = (String[]) state.getValue(m_existingImages);
		if (existing != null) {

			for (int i = 0; i < existing.length; i++) {
				if (newImages.contains(existing[i])) {
					// no change to this one
					newImages.remove(existing[i]);
				} else {
					// file has been deleted in edit
					PostImageAttachment image =
						new PostImageAttachment(new BigDecimal(existing[i]));
					post.removeImage(image);

				}
			}
		}
		Iterator it = newImages.iterator();
		while (it.hasNext()) {
			// new files added 
			PostImageAttachment image =
				new PostImageAttachment(new BigDecimal((String) it.next()));
			post.addImage(image);
		}
		state.setValue(m_newImages, null);
		state.setValue(m_existingImages, null);

	}

	public void register(Page p) {
		super.register(p);

		p.addGlobalStateParam(m_existingImages);
		p.addGlobalStateParam(m_newImages);
	}

	public void init(FormSectionEvent e) throws FormProcessException {
		PageState state = e.getPageState();
		if (m_container
			.getContext(state)
			.equals(PostForm.EDIT_CONTEXT)) {
			Post editPost = (Post) m_post.getSelectedObject(state);
			DataAssociationCursor files = editPost.getImages();
			if (files.size() > 0) {

				String[] fileIDs =
					new String[new Long(files.size()).intValue()];
				int i = 0;
				while (files.next()) {

					PostImageAttachment image =
						(PostImageAttachment) DomainObjectFactory.newInstance(
							files.getDataObject());
					fileIDs[i++] = image.getID().toString();
				}
				state.setValue(m_existingImages, fileIDs);
				state.setValue(m_newImages, fileIDs);
			}
		}

	}

	public DataCollection getCurrentImages(PageState state) {

		String[] currentArray = (String[]) state.getValue(m_newImages);
		if (currentArray != null && currentArray.length != 0) {
			List current =
				Arrays.asList((String[]) state.getValue(m_newImages));

			DataCollection images =
				SessionManager.getSession().retrieve(
					PostImageAttachment.BASE_DATA_OBJECT_TYPE);
			images.addFilter("id in :files").set("files", current);
			return images;
		}
		return null;

	}

	/**
		 * generate xml for the confirmation step (unfortunately we can't just 
		 * traverse the post because it hasn't been created at this point)
		 * @param state
		 * @param p
		 */
	public void generatePostXML(PageState state, Element p) {
		DataCollection files = getCurrentImages(state);
		if (files == null) {
			return;

		}

		while (files.next()) {
			PostImageAttachment image =
				(PostImageAttachment) DomainObjectFactory.newInstance(
					files.getDataObject());
			DomainObjectXMLRenderer xr =
				new DomainObjectXMLRenderer(p.newChildElement("images"));
			xr.setWrapRoot(false);
			xr.setWrapAttributes(true);
			xr.setWrapObjects(false);
			xr.walk(image, ConfirmStep.class.getName());

		}
	}
	
	protected void clearParameters (PageState state) {
		state.setValue(m_existingImages, null);
		state.setValue(m_newImages, null);
	}

}
