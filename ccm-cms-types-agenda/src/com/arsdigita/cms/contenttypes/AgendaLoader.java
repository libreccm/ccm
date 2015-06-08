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

/**
 * Loader executes nonrecurring once at install time and loads the Agenda 
 * contenttype package persistently into database.
 *
 * It uses the base class to create the database schema and the required 
 * table entries for the contenttype.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 * @version $Id: AgendaLoader.java 1596 2007-07-10 16:25:57Z p_boy $
 */
public class AgendaLoader extends AbstractContentTypeLoader {

    /** Defines the xml file containing the EForm content types property 
     * definitions.                                                          */
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/Agenda.xml"
    };

   /**
     * Provides the of Agenda contenttype property definitions implementing
     * the parent's class abstract method.
     * 
     * The file defines the types name as displayed in content center
     * select box and the authoring steps. These are loaded into database.
     * 
     * @return String Array of fully qualified file names 
     */
    @Override
    public String[] getTypes() {
        return TYPES;
    }
}
