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

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolume extends PublicationWithPublisher {

    public final static String PAGES_FROM = "pagesFrom";
    public final static String PAGES_TO = "pagesTo";
    public final static String CHAPTER = "chapter";
    public final static String COLLECTED_VOLUME = "collectedVolume";

    public final static String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.contenttypes.ArticleInCollectedVolume";

    public ArticleInCollectedVolume() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ArticleInCollectedVolume(BigDecimal id)
            throws DataObjectNotFoundException{
        this(new OID(BASE_DATA_OBJECT_TYPE, id));

    }

    public ArticleInCollectedVolume(OID oid) throws
            DataObjectNotFoundException{
        super(oid);
    }

    public ArticleInCollectedVolume(DataObject dataObject) {
        super(dataObject);
    }

    public ArticleInCollectedVolume(String type) {
        super(type);
    }

    public Integer getPagesFrom() {
        return (Integer)get(PAGES_FROM);
    }

    public void setPagesFrom(Integer pagesFrom) {
        set(PAGES_FROM, pagesFrom);
    }

    public Integer getPagesTo() {
        return (Integer)get(PAGES_TO);
    }

    public void setPagesTo(Integer pagesTo) {
        set(PAGES_TO, pagesTo);
    }

    public CollectedVolume getCollectedVolume() {
        return (CollectedVolume) get(COLLECTED_VOLUME);
    }

    public void setCollectedVolume(CollectedVolume collectedVolume) {
        set(COLLECTED_VOLUME, collectedVolume);
    }
}
