/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.domain;

import com.arsdigita.persistence.metadata.Property;

/**
 * An implementation of DomainObjectTraversal that generates a single
 * string containing all the data from the object's traversed
 * properties.
 *
 * @version $Id: DomainObjectTextRenderer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DomainObjectTextRenderer extends DomainObjectTraversal {

    private StringBuffer m_text;

    /**
     */
    public DomainObjectTextRenderer() {
        m_text = new StringBuffer("");
    }

    public String getText() {
        return m_text.toString();
    }

    protected void beginObject(DomainObject obj,
                               String path) {
    }

    protected void endObject(DomainObject obj,
                             String path) {
    }

    protected void revisitObject(DomainObject obj,
                                 String path) {
    }

    protected void handleAttribute(DomainObject obj,
                                   String path,
                                   Property property) {
        Object value = obj.get(property.getName());

        if (value != null) {
            m_text.append(" " + value);
        }
    }

    protected void beginRole(DomainObject obj,
                             String path,
                             Property property) {
    }

    protected void endRole(DomainObject obj,
                           String path,
                           Property property) {
    }


    protected void beginAssociation(DomainObject obj,
                                    String path,
                                    Property property) {
    }

    protected void endAssociation(DomainObject obj,
                                  String path,
                                  Property property) {
    }

}
