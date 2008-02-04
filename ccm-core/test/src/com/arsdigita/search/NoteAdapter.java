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
package com.arsdigita.search;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;
import java.util.Date;
import java.util.Locale;

public class NoteAdapter implements MetadataProvider {


    public String getTypeSpecificInfo(DomainObject dobj) {
        Note note = (Note)dobj;
        return null;
    }
    public Locale getLocale(DomainObject dobj) {
        Note note = (Note)dobj;
        return Locale.ENGLISH;
    }
    public String getTitle(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getTitle();
    }
    public String getSummary(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getText().substring(0, 50);
    }
    public Date getCreationDate(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getCreationDate();
    }
    public Party getCreationParty(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getCreationUser();
    }
    public Date getLastModifiedDate(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getLastModifiedDate();
    }
    public Party getLastModifiedParty(DomainObject dobj) {
        Note note = (Note)dobj;
        return note.getLastModifiedUser();
    }
    public String getContentSection(DomainObject dobj) {
        return "";
    }
    public ContentProvider[] getContent(DomainObject dobj,
                                        ContentType type) {
        Note note = (Note)dobj;
        if (type == ContentType.TEXT) {
            return new ContentProvider[] {
                new StringContent(note.getText())
            };
        } else {
            return new ContentProvider[] {};
        }
    }
    
    public  boolean isIndexable(DomainObject dobj) {
	return true;
    }
    
    private class StringContent implements ContentProvider {
        
        private String m_text;

        public StringContent(String text) {
            m_text = text;
        }

        public String getContext() {
            return null;
        }

        public ContentType getType() {
            return ContentType.TEXT;
        }
        
        public byte[] getBytes() {
            return m_text.getBytes();
        }
    }
}
