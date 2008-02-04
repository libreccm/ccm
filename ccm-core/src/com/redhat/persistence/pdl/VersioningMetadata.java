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
package com.redhat.persistence.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.pdl.nodes.AssociationNd;
import com.redhat.persistence.pdl.nodes.Node;
import com.redhat.persistence.pdl.nodes.ObjectTypeNd;
import com.redhat.persistence.pdl.nodes.PropertyNd;
import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;
import com.arsdigita.versioning.Versions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Versioning metadata.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-18
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */
public class VersioningMetadata {
    private final static Logger s_log =
        Logger.getLogger(VersioningMetadata.class);

    private final Set m_versionedTypes;
    private final Set m_unversionedProps;

    private final static VersioningMetadata s_singleton =
        new VersioningMetadata();

    private VersioningMetadata() {
        m_versionedTypes = new HashSet();
        m_unversionedProps = new HashSet();
    }

    public static VersioningMetadata getVersioningMetadata() {
        return s_singleton;
    }

    NodeSwitch nodeSwitch(Map properties) {
        return new NodeSwitch(properties);
    }

    /**
     * <p>Returns <code>true</code> if the object type named by
     * <code>qualifiedName</code> is marked <code>versioned</code> in the PDL
     * definition.  Note that this a weaker test than checking of an object type
     * is versioned. For example, a type may be versioned if one of its ancestor
     * types is marked versioned.</p>
     *
     * <p>This method is provided for unit testing only.</p>
     *
     * @param qualifiedName the fully qualified name of an object type
     **/
    public boolean isMarkedVersioned(String qualifiedName) {
        return m_versionedTypes.contains(qualifiedName);
    }


    private static Property getProperty(String containerName,
                                        String propertyName) {

        ObjectType objType = MetadataRoot.getMetadataRoot().
            getObjectType(containerName);
        return objType.getProperty(propertyName);
    }

    /**
     * Returns <code>true</code> if the object type property whose name is
     * <code>propertyName</code> is marked <code>unversioned</code> in the PDL
     * definition.
     *
     * <p>This method is provided for unit testing only.</p>
     *
     * @param propertyName the fully qualified name of an object type property
     **/
    public boolean isMarkedUnversioned(String containerName, String propertyName) {
        return m_unversionedProps.contains
            (getProperty(containerName, propertyName));
    }

    public interface NodeVisitor {
        /**
         * This method is called whenever an object type node is traversed in
         * the PDL AST.  To reiterate, this method is called upon visiting any
         * object type, whereas {@link #onVersionedProperty(Property)} and
         * {@link #onUnversionedProperty(Property)} are only called for a subset
         * of property nodes.
         **/
        void onObjectType(ObjectType objType, boolean isMarkedVersioned);

        /**
         * This method is called whenever we traverse a property node of the PDL
         * AST that is marked <code>versioned</code>.
         **/
        void onVersionedProperty(Property property);

        /**
         * This method is called whenever we traverse a property node of the PDL
         * AST that is marked <code>unversioned</code>.
         **/
        void onUnversionedProperty(Property property);

        /**
         * This method is called when the AST traversal is finished.
         **/
        void onFinish();
    }

    public class NodeSwitch extends Node.Switch {
        private Map m_properties;

        public NodeSwitch(Map properties) {
            m_properties = properties;
        }

        public void onObjectType(ObjectTypeNd ot) {
            final String fqn = ot.getQualifiedName();

            if ( ot.isVersioned() ) {
                m_versionedTypes.add(fqn);
            }

            // This returns null for things like "global.BigDecimal".
            ObjectType objType =
                MetadataRoot.getMetadataRoot().getObjectType(fqn);

            if ( objType != null ) {
                Versions.NODE_VISITOR.onObjectType(objType, ot.isVersioned());
            }
        }

        public void onProperty(PropertyNd prop) {
            if ( !prop.isUnversioned() && !prop.isVersioned() ) return;

            String containerName = getContainerName(prop);
            Property property = 
                getProperty(containerName, prop.getName().getName());

            if ( property.isKeyProperty() ) {
                throw new IllegalStateException
                    ("Cannot mark a key property 'unversioned': " +
                     property);
            }
            m_unversionedProps.add(property);


            if ( prop.isUnversioned() ) {
                Versions.NODE_VISITOR.onUnversionedProperty(property);
            } else if ( prop.isVersioned() ) {
                if ( property.getType().isSimple() ) {
                    throw new IllegalStateException
                        ("Simple properties are versioned by default. " +
                         "They cannot be marked 'versioned'. " + property);
                }
                Versions.NODE_VISITOR.onVersionedProperty(property);
            } else {
                throw new AssertionError("es impossible");
            }

        }

        public void onFinish() {
            Versions.NODE_VISITOR.onFinish();
        }

        private String getContainerName(PropertyNd prop) {
            Node parent = prop.getParent();
            if ( parent instanceof ObjectTypeNd ) {
                return ((ObjectTypeNd) parent).getQualifiedName();
            }

            Assert.truth(parent instanceof AssociationNd,
                         "parent instanceof AssociationNd");
            AssociationNd assoc = (AssociationNd) parent;

            PropertyNd other = null;
            if ( prop.equals(assoc.getRoleOne()) ) {
                other = assoc.getRoleTwo();
            } else if ( prop.equals(assoc.getRoleTwo()) ) {
                other = assoc.getRoleOne();
            } else {
                throw new AssertionError("can't get here");
            }

            Role role = (Role) m_properties.get(other);

            if ( role == null ) {
                throw new IllegalStateException
                    ("Failed to look up property node=" + other);
            }
            return role.getType().getQualifiedName();
        }
    }
}
