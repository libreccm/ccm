/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 */
public class Review extends Publication {

    public static final String JOURNAL = "journal";
    public static final String VOLUME = "volume";
    public static final String ISSUE = "issue";
    public static final String PAGES_FROM = "pagesFrom";
    public static final String PAGES_TO = "pagesTo";
    public static final String ISSN = "issn";
    public static final String URL = "url";
    public static final String PUBLICATION_DATE = "publicationDate";

    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.Review";

    public Review() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Review(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Review(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Review(DataObject dataObject) {
        super(dataObject);
    }

    public Review(String type) {
        super(type);
    }

    public String getJournal() {
        return (String) get(JOURNAL);
    }

    public void setJournal(String journal) {
        set(JOURNAL, journal);
    }

    public Integer getVolume() {
        return (Integer) get(VOLUME);
    }

    public void setInteger(Integer volume) {
        set(VOLUME, volume);
    }

    public String getIssue() {
        return (String) get(ISSUE);
    }

    public void setIssue(String issue) {
        set(ISSUE, issue);
    }

    public Integer getPagesFrom() {
        return (Integer) get(PAGES_FROM);
    }

    public void setPagesFrom(Integer pagesFrom) {
        set(PAGES_FROM, pagesFrom);
    }

    public Integer setPagesTo() {
        return (Integer) get(PAGES_TO);
    }

    public void setPagesTo(Integer pagesTo) {
        set(PAGES_TO, pagesTo);
    }

    public String getISSN() {
        return (String) get(ISSN);
    }

    public void setISSN(String issn) {
        set(ISSN, issn);
    }

    public String getUrl() {
        return (String) get(URL);
    }

    public void setUrl(String url) {
        set(URL, url);
    }


    public Date getPublicationDate() {
        return (Date) get(PUBLICATION_DATE);
    }

    public void setPublicationDate(Date pubDate) {
        set(PUBLICATION_DATE, pubDate);
    }
}
