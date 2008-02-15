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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.forum.Post;
import com.arsdigita.forum.PostFileAttachment;
import com.arsdigita.forum.PostImageAttachment;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.messaging.MessagePart;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

/**
 * Semantic table with delete link. This could be extended to provide
 * additional links eg move up/move down
 *
 * @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 * @version $Revision: 1.4 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public class ImagesTable extends SimpleComponent implements Constants {

	// I have not implemented an edit link or a move up/down link although the data model 
	// is able to manage this, it seemed like a lot of work for a minor requirement for a forum
	// currently, files are listed in the order they are added.

	// to add extra links, define new keys for the control event and check key value in the 
	// respond method (see ThreadDisplay for example code)
	//
	// only issue is that when editing a post, the editor cannot change the description easily 
	// (though they could download the file to their PC, delete the existing one and recreate it)
	private static Logger s_log = Logger.getLogger(ImagesTable.class);
	protected static final String ACTION_DELETE = "delete";

	private ArrayParameter m_newImages;
	// ie when post is being edited
	private ArrayParameter m_existingImages;

	private ImagesStep m_parent;

	public ImagesTable(
		ArrayParameter newImages,
		ArrayParameter existingImages,
		ImagesStep parent) {
		m_newImages = newImages;
		m_existingImages = existingImages;
		m_parent = parent;

	}

	/**
	 * if image has been added during this session then delete it completely.
	 * If we are editing a post and we delete an image that was in the original 
	 * post then just flag the image as deleted in case the editing is cancelled
	 */
	public void respond(PageState state) throws ServletException {
		super.respond(state);

		String key = state.getControlEventName();
		String value = state.getControlEventValue();

		if (ACTION_DELETE.equals(key)) {
			List existing = Collections.EMPTY_LIST;
			String[] existingArray =
				(String[]) state.getValue(m_existingImages);
			if (existingArray != null) {
				existing = Arrays.asList(existingArray);
			}
			if (!existing.contains(value)) {
				// this has been added during edit
				OID oid =
					new OID(
						PostImageAttachment.BASE_DATA_OBJECT_TYPE,
						new BigDecimal(value));

				DomainObjectFactory.newInstance(oid).delete();
			}
			List newImages =
				new ArrayList(
					Arrays.asList((String[]) state.getValue(m_newImages)));
			newImages.remove(value);
			String[] current = new String[newImages.size()];
			Iterator it = newImages.iterator();
			int i = 0;
			while (it.hasNext()) {
				current[i++] = (String) it.next();
			}
			state.setValue(m_newImages, current);

		}

	}
	public void generateXML(PageState state, Element p) {
		Element mainElement =
			p.newChildElement(
				FORUM_XML_PREFIX + ":attachedImages",
				FORUM_XML_NS);
		DataCollection images = m_parent.getCurrentImages(state);
		if (images == null) {
			return;
		}

		while (images.next()) {

			PostImageAttachment image =
				(PostImageAttachment) DomainObjectFactory.newInstance(
					images.getDataObject());
			Element imageElement =
				mainElement.newChildElement(
					FORUM_XML_PREFIX + ":image",
					FORUM_XML_NS);
			imageElement.addAttribute("name", image.getName());
			imageElement.addAttribute("src", Utilities.getAssetURL(image));
			imageElement.addAttribute("caption", image.getDescription());
			generateActionXML(state, imageElement, image);
		}

	}

	private void generateActionXML(
		PageState state,
		Element parent,
		PostImageAttachment image) {
		//separate method so that if future links require some logic 
		//to decide whether to display, it can be implemented here
		parent.addAttribute("deleteLink", makeURL(state, ACTION_DELETE, image));
	}

	protected String makeURL(
		PageState state,
		String action,
		PostImageAttachment image) {
		state.setControlEvent(this, action, image.getID().toString());

		String url = null;
		try {
			url = state.stateAsURL();
		} catch (IOException ex) {
			throw new UncheckedWrapperException("cannot create url", ex);
		}
		state.clearControlEvent();
		return url;
	}

}
