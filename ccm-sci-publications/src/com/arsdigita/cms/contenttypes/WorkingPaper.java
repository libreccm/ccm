/*
 * Copyright (c) 2010 Jens Pelzetter,
 * ScientificCMS Team, http://www.scientificcms.org
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

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class WorkingPaper extends UnPublished {

    //public static final String REVIEWED = "reviewed";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.WorkingPaper";

    public WorkingPaper() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public WorkingPaper(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public WorkingPaper(OID oid) {
        super(oid);
    }

    public WorkingPaper(DataObject dataObject) {
        super(dataObject);
    }

    public WorkingPaper(String type) {
        super(type);
    }

    /*public Boolean getReviewed() {
        return (Boolean) get(REVIEWED);
    }

    public void setReviewed(Boolean reviewed) {
        set(REVIEWED, reviewed);
    }*/
}
