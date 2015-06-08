/*
 * Copyright (C) 2015 University of Bremen. All Rights Reserved.
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
 * Unloader deinstalles/removes when callen the ExternalLink contenttype
 * persistantly from the database.
 * 
 * @author Tobias Osmers <tosmers@uni-bremen.de>
 * @version $Revision: #1 $ $Date: 2015/04/08 $
 */
public class ExternalLinkUnloader extends AbstractContentTypeUnloader {
    
    /**
     * Defines the xml file containing the ExternalLink content types 
     * property definitions.
     */
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ExternalLink.xml"
    };
    
    /**
     * Provides the ExternalLink's contenttype property definitions.
     *
     * The file defines the types name as displayed in content center 
     * select box and the authoring steps. These are loaded into database.
     *
     * Implements the method of the parent class.
     *
     * @return String array of fully qualified file names
     */
    @Override
    public String[] getTypes() {
        return TYPES;
    }
}