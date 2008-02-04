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
 *
 */
package com.arsdigita.persistence.metadata;

import com.redhat.persistence.metadata.Root;

/**
 * The ModelElement class represents metadata elements that are components of
 * a Model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/

abstract public class ModelElement extends Element {

    public final static String versionId = "$Id: ModelElement.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Root m_root;
    private com.redhat.persistence.metadata.Model m_model;

    ModelElement(Root root,
		 com.redhat.persistence.metadata.Model model,
		 Object obj) {
	super(root, obj);
	m_root = root;
	m_model = model;
    }


    /**
     * Returns the Model that this ModelElement is contained in. This value
     * may be null if this ModelElement hasn't been added to a model.
     *
     * @return The Model that this ModelElement is contained in or null.
     **/

    public Model getModel() {
	return Model.wrap(m_root, m_model);
    }

}
