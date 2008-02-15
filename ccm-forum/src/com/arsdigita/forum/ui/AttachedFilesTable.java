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
 * additional links eg move up/move down. New generic wizard
 * file attachment table does include this functionality
 * so it will be introduced when forum is refactored  
 *
 * @author Chris Gilbert <a href="mailto:chris.gilbert@westsussex.gov.uk">chris.gilbert@westsussex.gov.uk</a>
 * @version $Revision: 1.4 $ $DateTime: 2004/08/17 23:15:09 $
 **/
public class AttachedFilesTable extends SimpleComponent implements Constants {

	// I have not implemented an edit link or a move up/down link although the data model 
	// is able to manage this, it seemed like a lot of work for a minor requirement for a forum
	// currently, files are listed in the order they are added.

	// to add extra links, define new keys for the control event and check key value in the 
	// respond method (see ThreadDisplay for example code)
	//
	// only issue is that when editing a post, the editor cannot change the description easily 
	// (though they could download the file to their PC, delete the existing one and recreate it)
	private static Logger s_log = Logger.getLogger(AttachedFilesTable.class);
	protected static final String ACTION_DELETE = "delete";

	private ArrayParameter m_newFiles;
	private ArrayParameter m_existingFiles;

	private AttachedFilesStep m_parent;

	public AttachedFilesTable(
		ArrayParameter newFiles,
		ArrayParameter existingFiles,
		AttachedFilesStep parent) {
		m_newFiles = newFiles;
		m_existingFiles = existingFiles;
		m_parent = parent;

	}

	public void respond(PageState state) throws ServletException {
		super.respond(state);

		String key = state.getControlEventName();
		String value = state.getControlEventValue();
		if (ACTION_DELETE.equals(key)) {
			List existing = Collections.EMPTY_LIST;
			String[] existingArray = (String[]) state.getValue(m_existingFiles);
			if (existingArray != null) {
				existing = Arrays.asList(existingArray);
			}
			if (!existing.contains(value)) {
				// this has been added during edit
				OID oid =
					new OID(
						PostFileAttachment.BASE_DATA_OBJECT_TYPE,
						new BigDecimal(value));

				DomainObjectFactory.newInstance(oid).delete();
			}
			List newFiles =
				new ArrayList(
					Arrays.asList((String[]) state.getValue(m_newFiles)));
			newFiles.remove(value);
			String[] current = new String[newFiles.size()];
			Iterator it = newFiles.iterator();
			int i = 0;
			while (it.hasNext()) {
				current[i++] = (String) it.next();
			}
			state.setValue(m_newFiles, current);
			

		}

	}
	public void generateXML(PageState state, Element p) {
		Element mainElement =
			p.newChildElement(
				FORUM_XML_PREFIX + ":attachedFiles",
				FORUM_XML_NS);

		DataCollection files = m_parent.getCurrentFiles(state);
		if (files == null) {
			return;
		}
		while (files.next()) {

			PostFileAttachment file =
				(PostFileAttachment) DomainObjectFactory.newInstance(
					files.getDataObject());
			Element fileElement =
				mainElement.newChildElement(
					FORUM_XML_PREFIX + ":file",
					FORUM_XML_NS);
			fileElement.addAttribute("name", file.getName());
			fileElement.addAttribute("url", Utilities.getAssetURL(file));
			fileElement.addAttribute("description", file.getDescription());
			generateActionXML(state, fileElement, file);
		}

	}

	private void generateActionXML(
		PageState state,
		Element parent,
		PostFileAttachment image) {
		//separate method so that if future links require some logic 
		//to decide whether to display, it can be implemented here
		parent.addAttribute("deleteLink", makeURL(state, ACTION_DELETE, image));
	}

	protected String makeURL(
		PageState state,
		String action,
		PostFileAttachment file) {
		state.setControlEvent(this, action, file.getID().toString());

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
