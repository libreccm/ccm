/*
 * Copyright (c) 2014 Jens Pelzetter,
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

/**
 * Initializer for the play content type.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPlayInitializer extends ContentTypeInitializer {
    
    public SciPublicationsPlayInitializer() {
        super("empty.pdl.mf", SciPublicationsPlay.BASE_DATA_OBJECT_TYPE);
    }
    
    @Override
    public String[] getStylesheets() {
        return new String[]{INTERNAL_THEME_TYPES_DIR + "sci/SciPublicationsPlay.xsl"};
    }
    
     @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/SciPublicationsPlay.xml";
    }
    
}
