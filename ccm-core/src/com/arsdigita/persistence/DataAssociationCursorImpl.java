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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Link;
import com.redhat.persistence.metadata.ObjectType;

/**
 * DataAssociationCursorImpl
 *
 * @author Archit Shah &lt;ashah@mit.edu&gt;
 * @version $Revision: #18 $ $Date: 2004/08/16 $
 **/

class DataAssociationCursorImpl extends DataCollectionImpl
    implements DataAssociationCursor {

    public final static String versionId = "$Id: DataAssociationCursorImpl.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private DataAssociationImpl m_assn;

    DataAssociationCursorImpl(Session ssn, DataObject data, Property prop) {
        super(ssn, ssn.getProtoSession().getDataSet
              (data, C.prop(ssn.getRoot(), prop)));
    }

    protected final void setAssociation(DataAssociationImpl assn) {
        m_assn = assn;
    }

    public DataAssociation getDataAssociation() {
        return m_assn;
    }

    private boolean hasLinkAttributes() {
        return (m_assn.getProperty() instanceof Link);
    }

    ObjectType getTypeInternal() {
        if (m_originalSig.isSource(Path.get("link"))) {
            return m_originalSig.getSource
                (Path.get(com.redhat.persistence.Session.LINK_ASSOCIATION))
                .getObjectType();
        } else {
            return super.getTypeInternal();
        }
    }

    boolean hasProperty(Path p) {
        boolean superAnswer = super.hasProperty(p);

        if (superAnswer || !hasLinkAttributes()) {
            return superAnswer;
        }

        return m_originalSig.exists(p);
    }

    protected Path resolvePath(Path path) {
        if (!hasLinkAttributes()) {
            return super.resolvePath(path);
        }

        if (path == null || getTypeInternal().getProperty(path) != null) {
            path = Path.add
                (com.redhat.persistence.Session.LINK_ASSOCIATION, path);
        }

        return super.resolvePath(path);
    }

    public DataObject getLink() {
        if (!hasLinkAttributes()) {
            return null;
        }

        return (DataObject) get("link");
    }


    /**
     *  This returns the Link Property specified by the passed in parameter.
     *  For instance, if there is a sortKey specifying how to sort
     *  the association, calling getLinkProperty("sortKey") would return
     *  the sortKey for the given Association.
     *
     *  @param name The name of the Link Property to return
     *  @return The Link Property specified by the parameter
     */
    public Object getLinkProperty(String name) {
        if (!hasLinkAttributes()) {
            return null;
        }

        return get("link." + name);
    }

    public void remove() {
        m_assn.remove(getDataObject());
    }
}
