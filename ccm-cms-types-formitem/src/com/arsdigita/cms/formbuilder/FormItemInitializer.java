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
package com.arsdigita.cms.formbuilder;

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;

import org.apache.log4j.Logger;

/**
 * Initializes the Form Item content type.
 *
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map).
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 **/

public class FormItemInitializer extends ContentTypeInitializer {

    private static final Logger s_log = 
        Logger.getLogger(FormItemInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public FormItemInitializer() {
        super("ccm-cms-types-formitem.pdl.mf",
              FormItem.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Retrieve location of traversal adapter
     * @return
     */
    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/" +
            "cms/formbuilder/FormItem.xml";
    }

    /**
     * Retrieve location of this content types stylesheet. Overwrites parent
     * method with FormItem specific value.
     * @return
     */
    public String[] getStylesheets() {
        return new String[] { 
            "/static/content-types/com/arsdigita/" + 
            "cms/contenttypes/FormItem.xsl" 
        };
    }
}
