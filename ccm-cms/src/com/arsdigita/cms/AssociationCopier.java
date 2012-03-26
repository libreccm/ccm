package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.metadata.Property;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public interface AssociationCopier {
    
    boolean copyReverseProperty(CustomCopy source, 
                                DomainObject target,
                                Property property,                                
                                DomainObject value,
                                ItemCopier copier);
    
    
}
