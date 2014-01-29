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
package com.arsdigita.search.intermedia;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * The SearchContent class represents an single
 * row in the search_content table.
 *
 * @author Joseph Bank
 * @version 1.0
 **/
class SearchContent extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.intermedia.SearchContent";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    private static final byte[] EMTPY_CONTENT = new byte[0];
    // Creates a s_logging category with name = to the full name of class
    private static final Logger s_log =
        Logger.getLogger( SearchContent.class.getName() );

    /**
     * Retrieve an SearchContent object for an Object.
     *
     * @exception RuntimeException thrown if the specified user does not
     * have any associated user authentication object.
     */
    private static final String retrieveQuery =
        "com.arsdigita.search.intermedia.searchContentForObject";
    public static SearchContent retrieveForObject(OID oid) {
        DataQuery query = SessionManager.getSession().retrieveQuery(retrieveQuery);
        try {
            Filter f = query.addFilter("id=:id");
            f.set("id", oid.get("id"));
            if (!query.next()) {
                return null;
            }
            try {
                return new SearchContent(new OID(BASE_DATA_OBJECT_TYPE, query.get("id")));
            } catch (DataObjectNotFoundException e) {
                s_log.debug( "Unable to lookup object" );
                return null;
            }
        } finally {
            query.close();
        }
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "SearchContent".
     **/
    public SearchContent() throws DataObjectNotFoundException {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public SearchContent(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    private final static String OBJECT_ID = "id";
    private final static String OBJECT_TYPE = "object_type";
    private final static String SUMMARY = "summary";
    private final static String LINK_TEXT = "link_text";
    private final static String URL_STUB = "url_stub";
    private final static String RAW_CONTENT = "raw_content";
    private final static String XML_CONTENT = "xml_content";
    private final static String LANGUAGE = "language";

    private final static String CONTENT_SECTION = "content_section";


    public void setObjectId(BigDecimal id) {
        set(OBJECT_ID, id);
    }

    public void setContentObjectType(String object_type) {
        set(OBJECT_TYPE, object_type);
    }

    public void setSummary(String summary) {
        set(SUMMARY, summary);
    }

    public void setLinkText(String linktext) {
        set(LINK_TEXT, linktext);
    }

    public void setUrlStub(String urlstub) {
        set(URL_STUB, urlstub);
    }

    public void setXMLContent(String xmlContent) {
        set(XML_CONTENT, xmlContent);
    }

    public void setRawContent(byte[] rawcontent) {
        set(RAW_CONTENT, rawcontent);
    }

    // public void setRawContent(java.io.InputStream rawcontent) {
    public void setLanguage(String language) {
        set(LANGUAGE, language);
    }

    public void setContentSection(String contentSection) {
	set(CONTENT_SECTION, contentSection);
    }

    //accessors
    public BigDecimal getObjectId() {
        return (BigDecimal)get(OBJECT_ID);
    }

    //Note this needs a different name from getObjectType
    public String getContentObjectType() {
        return (String)get(OBJECT_TYPE);
    }

    public String getSummary() {
        return (String)get(SUMMARY);
    }

    public String getLinkText() {
        return (String)get(LINK_TEXT);
    }

    public String getUrlStub() {
        return (String)get(URL_STUB);
    }

    public String getXMLContent() {
        Object obj = get(XML_CONTENT);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String)obj;
        } else if (obj instanceof Clob) {
            try {
                Clob clob = (Clob)obj;
                return clob.getSubString(1L, (int)clob.length());
            } catch (SQLException sqle) {
                throw new UncheckedWrapperException("SQLE: " + sqle.getMessage(), sqle);
            }
        } else {
            throw new RuntimeException("Bad getClob datatype" + obj.getClass() );
        }
    }

    public byte[] getRawContent() {
        Object obj = get(RAW_CONTENT);
        if (obj == null) {
            return EMTPY_CONTENT;
        }
        if (obj instanceof byte[]) {
            return (byte[])obj;
        } else if (obj instanceof Blob) {
            try {
                Blob blob = (Blob)obj;
                return blob.getBytes(1L, (int)blob.length());
            } catch (SQLException sqle) {
                throw new UncheckedWrapperException("SQL Error retieving raw content!",  sqle);
            }
        } else {
            throw new RuntimeException("Bad getBlob datatype" + obj.getClass());
        }
    }

    public String getLanguage() {
        return (String)get(LANGUAGE);
    }

    public String getContentSection() {
	return (String)get(CONTENT_SECTION);
    }

}
