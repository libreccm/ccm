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
 * Executes at each system startup and initializes the HTMLForm content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: com/arsdigita/cms/contenttypes/HTMLFormInitializer.java#2 $
 */
public class HTMLFormInitializer extends ContentTypeInitializer {

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public HTMLFormInitializer() {
        super("ccm-cms-types-htmlform.pdl.mf", HTMLForm.BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Retrieve location of this content type's internal default theme 
     * stylesheet(s) which concomitantly serve as a fallback if a custom theme 
     * is engaged. 
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own
     * access method, but may not support every content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the 
     * parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[] { 
        //  "/static/content-types/com/arsdigita/cms/contenttypes/HTMLForm.xsl" };
             INTERNAL_THEME_TYPES_DIR + "HTMLForm.xsl" };
    }
}
