package com.arsdigita.cms;

import com.arsdigita.persistence.metadata.Property;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public interface AssociationCopier {

    /**
     * <p>
     * Return the property which is handled by this implementation. Format:
     * </p>
     * <p>
     * {@code $type::$property}
     * </p>
     * <p>
     * Where {@code $type} is the fully qualified name of the class/type owing 
     * the property and {@code property} is the name the property. Example
     * </p>
     * <p>
     * {@code com.arsdigita.cms.contenttypes.GenericPerson::publications}
     * </p>
     * <p>
     * This indicates that the implementation handles a property 
     * {@code publications} added to the {@code GenericPerson} type by some
     * module via an PDL association.
     * </p>
     * 
     * @return 
     */
    String forProperty();

    boolean copyProperty(CustomCopy source, Property property, ItemCopier copier);
}
