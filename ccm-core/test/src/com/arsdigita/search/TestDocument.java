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
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.util.Date;
import java.util.Locale;

public class TestDocument {
    
    private OID m_oid;
    private Locale m_locale;
    private String m_title;
    private String m_summary;
    private Date m_creationDate;
    private Party m_creationParty;
    private Date m_modifiedDate;
    private Party m_modifiedParty;
    private String m_text;

    public TestDocument(DomainObject dobj,
                        MetadataProvider metadata) {
        m_oid = dobj.getOID();
        m_locale = metadata.getLocale(dobj);
        m_title = metadata.getTitle(dobj);
        m_summary = metadata.getSummary(dobj);
        m_creationDate = metadata.getCreationDate(dobj);
        m_creationParty = metadata.getCreationParty(dobj);
        m_modifiedDate = metadata.getLastModifiedDate(dobj);
        m_modifiedParty = metadata.getLastModifiedParty(dobj);
        
        ContentProvider[] content = metadata.getContent(dobj,
                                                        ContentType.TEXT);
        StringBuffer buf = new StringBuffer("");
        for (int i = 0 ; i < content.length ; i++) {
            Assert.truth(content[i].getType() == ContentType.TEXT,
                         "content is text");
            buf.append(new String(content[i].getBytes()));
        }
        m_text = buf.toString();
    }

    public int hashCode() {
        return m_oid.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof TestDocument) {
            return m_oid.equals(((TestDocument)o).m_oid);
        }
        return false;
    }

    public OID getOID() {
        return m_oid;
    }
    public Locale getLocale() {
        return m_locale;
    }
    public String getTitle() {
        return m_title;
    }
    public String getSummary() {
        return m_summary;
    }
    public Date getCreationDate() {
        return m_creationDate;
    }
    public Party getCreationParty() {
        return m_creationParty;
    }
    public Date getLastModifiedDate() {
        return m_modifiedDate;
    }
    public Party getLastModifiedParty() {
        return m_modifiedParty;
    }
    public String getText() {
        return m_text;
    }
}
