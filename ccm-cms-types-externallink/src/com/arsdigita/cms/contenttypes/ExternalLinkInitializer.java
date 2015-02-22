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

import com.arsdigita.cms.contenttypes.ContentTypeInitializer;

/**
 * Runtime initialization for the ExternalLink content type, executes at each
 * system startup.
 * 
 * Just uses the super class methods.
 * 
 * This is done by runtimeRuntime startup method which runs the init() methods 
 * of all initializers (this one just using the parent implementation).
 *
 * @author tosmers
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLinkInitializer extends ContentTypeInitializer {

    /**
     * Constructor, just sets the PDL manifest file and object type string.
     */
    public ExternalLinkInitializer() {
        super("ccm-cms-types-externallink.pdl.mf",
              ExternalLink.BASE_DATA_OBJECT_TYPE);
    }

}
