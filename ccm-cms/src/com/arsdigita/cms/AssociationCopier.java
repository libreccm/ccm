package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.metadata.Property;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public interface AssociationCopier {
    
    boolean handlesProperty(Property property);
    void copy(DomainObject source, 
              DomainObject target, 
              DomainObject value, 
              Property property);
    
}
