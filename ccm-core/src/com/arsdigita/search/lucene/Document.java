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
package com.arsdigita.search.lucene;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Document.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public class Document extends DomainObject {
    private static final Logger s_log = Logger.getLogger(Document.class);

    public final static String versionId =
        "$Id: Document.java 287 2005-02-22 00:29:02Z sskracic $" +
        " by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.search.lucene.Document";

    public static final String ID                  = "id";
    public static final String LANGUAGE            = "language";
    public static final String COUNTRY             = "country";
    public static final String TYPE                = "type";
    public static final String TYPE_SPECIFIC_INFO  = "typeSpecificInfo";
    public static final String TITLE               = "title";
    public static final String SUMMARY             = "summary";
    public static final String CONTENT             = "content";
    public static final String CREATION_DATE       = "creationDate";
    public static final String CREATION_PARTY      = "creationParty";
    public static final String LAST_MODIFIED_DATE  = "lastModifiedDate";
    public static final String LAST_MODIFIED_PARTY = "lastModifiedParty";

    public static final String CONTENT_SECTION     = "contentSection";


    static final String IS_DELETED                 = "isDeleted";
    static final String DIRTY                      = "dirty";
    static final String TIMESTAMP                  = "timestamp";

    /**
     * The binary representation of the maximum positive int value 2147483647
     * looks like so:
     *
     *    01111111 11111111 111111111 1111111
     *
     * The reason why we don't use ~0 instead (which is all 1's in binary) is
     * because we couldn't get it working with Postgres bitwise arithmetic
     * operators.  See SDM 227126.
     *
     * This magic value is also used in
     * sql/postgres/lucene/proc-update-dirty.sql and
     * sql/oracle-se/lucene/proc-update-dirty.sql.
     **/
    private final static Integer BITMASK = new Integer(Integer.MAX_VALUE);

    protected static final Document create(BigDecimal id) {
        Session ssn = SessionManager.getSession();
        DataObject data = ssn.create(new OID(BASE_DATA_OBJECT_TYPE, id));
        Document doc = new Document(data);
        doc.setDeleted(false);
        return doc;
    }

    protected static final Document retrieve(BigDecimal id) {
        Session ssn = SessionManager.getSession();
        return retrieve(ssn.retrieve(new OID(BASE_DATA_OBJECT_TYPE, id)));
    }

    protected static final Document retrieve(DataObject data) {
        if (data == null) {
            return null;
        } else {
            return new Document(data);
        }
    }

    public void initialize() {
        super.initialize();
        
        if (isNew()) {
            setDirty(true);
        }
    }

    private Document(DataObject data) {
        super(data);
    }

    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

    public void setType(String type) {
        set(TYPE, type);
    }

    public String getType() {
        return (String) get(TYPE);
    }

    public void setTypeSpecificInfo(String info) {
        set(TYPE_SPECIFIC_INFO, info);
    }

    public String getTypeSpecificInfo() {
        return (String) get(TYPE_SPECIFIC_INFO);
    }


    public Date getTimestamp() {
        return (Date) get(TIMESTAMP);
    }

    public void setDeleted(boolean value) {
        set(IS_DELETED, new Boolean(value));
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(get(IS_DELETED));
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            set(LANGUAGE, null);
            set(COUNTRY, null);
        } else {
            set(LANGUAGE, locale.getLanguage());
            set(COUNTRY, locale.getCountry());
        }
    }

    public Locale getLocale() {
        String language = (String) get(LANGUAGE);
        String country = (String) get(COUNTRY);
        if (language == null && country == null) {
            return null;
        } else {
            if (language == null) {
                language = "";
            }
            if (country == null) {
                country = "";
            }
            return new Locale(language, country);
        }
    }

    public void setTitle(String title) {
        if (StringUtils.emptyString(title)) {
            s_log.warn("Adding empty title! " + title);
        }
        Assert.exists(title, String.class);
        set(TITLE, title);
    }

    public String getTitle() {
        return (String) get(TITLE);
    }

    public void setSummary(String summary) {
        set(SUMMARY, summary);
    }

    public String getSummary() {
        return (String) get(SUMMARY);
    }

    public void setContent(String content) {
        set(CONTENT, content);
    }

    public String getContent() {
        return (String) get(CONTENT);
    }

    public void setCreationDate(Date creationDate) {
        set(CREATION_DATE, creationDate);
    }

    public Date getCreationDate() {
        return (Date) get(CREATION_DATE);
    }

    public void setCreationParty(BigDecimal creationParty) {
        set(CREATION_PARTY, creationParty);
    }

    public BigDecimal getCreationParty() {
        return (BigDecimal) get(CREATION_PARTY);
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        set(LAST_MODIFIED_DATE, lastModifiedDate);
    }

    public Date getLastModifiedDate() {
        return (Date) get(LAST_MODIFIED_DATE);
    }

    public void setLastModifiedParty(BigDecimal lastModifiedParty) {
        set(LAST_MODIFIED_PARTY, lastModifiedParty);
    }

    public BigDecimal getLastModifiedParty() {
        return (BigDecimal) get(LAST_MODIFIED_PARTY);
    }

    boolean isDirty() {
        int dirty = ((Integer) get(DIRTY)).intValue();
        return (dirty & (1 << Index.getIndexID())) != 0;
    }

    void setDirty(boolean dirty) {
        if (dirty) {
            set(DIRTY, BITMASK);
            set(TIMESTAMP, new Date());
        } else {
            set(DIRTY, new Integer(~(1 << Index.getIndexID())));
            // a workaround for a persistence quirk. --2003-06-27
            set(TIMESTAMP, getTimestamp());
        }
    }

    /**
     * Sets the name of the content section for the Document.
     **/
    public void setContentSection(String contentSection) {
        set(CONTENT_SECTION, contentSection);
    }

    /**
     * Gets the name of the content section for this Document.
     **/
    public String getContentSection() {
        return (String) get(CONTENT_SECTION);
    }


    protected void beforeSave() {
        super.beforeSave();

        // This check lets the Indexer thread explicitly mark the Document as
        // clean when the index is synced.
        if (!isPropertyModified(DIRTY)) {
            set(DIRTY, BITMASK);
        }
    }
}
