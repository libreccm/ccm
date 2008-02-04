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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This content type represents a Message of the Day item with
 * Message and File attribute.
 *
 * @author Aingaran Pillai
 * @version $Revision: #6 $
 */
public class MOTDItem extends ContentPage {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String FILE = "file";

    public static final int MESSAGE_LENGTH = 4000;
    public static final int SUMMARY_LENGTH = 200;

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.MOTDItem";

    public MOTDItem() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public MOTDItem(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public MOTDItem(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public MOTDItem(DataObject obj) {
        super(obj);
    }

    public MOTDItem(String type) {
        super(type);
    }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    public String getPublicationDate() {

        if (isPublished()) {

            Date startDate = getLifecycle().getStartDate();
            if( null == startDate ) return "n/a";

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            return formatter.format(startDate);
        } else {
            return "n/a";
        }
    }

    public String getMessage() {
        return (String) get(MESSAGE);
    }

    public void setMessage(String msg) {
        set(MESSAGE, msg);
    }

    public FileAsset getFile() {
        DataObject file = (DataObject) get(FILE);
        if (file != null) {
            return new FileAsset(file);
        } else {
            return null;
        }
    }

    public void setFile(FileAsset file) {
        setAssociation(FILE, file);
    }

    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getMessage(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }
}
