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

package com.arsdigita.navigation;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;

/**
 * Classes implementing this interface will render in XML a property which
 * will normally have been previously added to a
 * <code>DataCollectionDefinition</code>.
 */
public interface DataCollectionPropertyRenderer {
    /**
     * Called from DataCollectionRenderer for every returned item. This method
     * will add XML for the property to the renderer's output.
     *
     * @param dc A datacollection whose cursor is currently on the item whose
     * property is to be renderered.
     * @param parent The parent XML Element.
     */
    public void render( DataCollection dc, Element parent );
}
