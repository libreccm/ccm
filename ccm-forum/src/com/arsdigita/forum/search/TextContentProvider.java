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
package com.arsdigita.forum.search;


import com.arsdigita.domain.DomainObjectTextRenderer;
import com.arsdigita.forum.Post;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;

public class TextContentProvider implements ContentProvider {

	private Post m_post;
	private String m_context;

	public TextContentProvider(String context, Post post) {
		m_context = context;
		m_post = post;
	}

	public String getContext() {
		return m_context;
	}

	public ContentType getType() {
		return ContentType.TEXT;
	}

	public byte[] getBytes() {
		        
		DomainObjectTextRenderer renderer = new DomainObjectTextRenderer();

		renderer.walk(m_post, PostMetadataProvider.class.getName());

		String text = renderer.getText();
		
		// if required, retrieve file attachments here and add their content
		
		return text.getBytes();
	}

}
