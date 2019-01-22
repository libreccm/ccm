package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ComponentModelCollection extends DomainCollection {
    
    public ComponentModelCollection(final DataCollection dataCollection) {
        
        super(dataCollection);
    }
    
    public ComponentModel getComponentModel() {
        
        return (ComponentModel) DomainObjectFactory
            .newInstance(m_dataCollection.getDataObject());
    }
    
}
