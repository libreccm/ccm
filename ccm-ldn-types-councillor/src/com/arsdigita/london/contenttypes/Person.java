/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.coventry.cms.contenttypes;

import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.TextPage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * This content type represents a Coventry Person.
 *
 * @version $Revision: #5 $ $Date: 2004/04/08 $
 **/
public class Person extends TextPage {

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Person.class);

    public static final String CONTACT_DETAILS = "contactDetails";
    public static final String DESCRIPTION = "description";
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.coventry.cms.contenttypes.Person";

    public Person() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Person(OID id) {
        super(id);
    }

    public Person(DataObject obj) {
        super(obj);
    }

    public Person(String type) {
        super(type);
    }

    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getDescription(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

    public String getContactDetails() {
        return (String) get(CONTACT_DETAILS);
    }

    public void setContactDetails(String contactDetails) {
        set(CONTACT_DETAILS, contactDetails);
    }

    public String getBodyText() {
        TextAsset asset = getTextAsset();
        if (asset == null) {
            return "";
        }
        return asset.getText();
    }

    public void setBodyText(String body) {
        TextAsset asset = getTextAsset();
        if (asset == null) {
            asset = new TextAsset();
            asset.setName(getName() + "_text_" + getID());
            asset.setParent(this);
            setTextAsset(asset);
        }
        asset.setText(body);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
}
