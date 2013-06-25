/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.cms.contenttypes;

/**
 * Loader executes nonrecurring once at install time and loads the 
 * HTMLForm contenttype package persistently into database.
 *
 * It uses the base class to create the database schema and the required 
 * table entries for the contenttype.
 *
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: HTMLFormLoader.java#1 $
 */
public class HTMLFormLoader extends AbstractContentTypeLoader {

    /** Defines the xml file containing the FAQ content types 
     *  property definitions.                                                */
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/HTMLForm.xml"
    };

    /**
     * Provides the of HTMLForm contenttype property definitions 
     * implementing the parent's class abstract method.
     * 
     * The file defines the types name as displayed in content center
     * select box and the authoring steps. These are loaded into database.
     * 
     * @return String Atring Array of fully qualified file names 
     */
    public String[] getTypes() {
        return TYPES;
    }
}
