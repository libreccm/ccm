/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.cms.contenttypes.ldn;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;

/**
 * Executes at each system startup and initializes the ESDService content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ESDServiceInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ESDServiceInitializer extends ContentTypeInitializer {

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public ESDServiceInitializer() {
        super("ccm-ldn-types-esdservice.pdl.mf", ESDService.BASE_DATA_OBJECT_TYPE);
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
        return new String [] {
        INTERNAL_THEME_TYPES_DIR + "ldn/ESDService.xsl",
        };
    }
  
    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/ldn/ESDService.xml";
    }
  
}
