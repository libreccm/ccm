package org.libreccm.pagemodel;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContainerModelCollection extends DomainCollection {
    
    public ContainerModelCollection(final DataCollection dataCollection) {
        super(dataCollection);
    }
    
    public ContainerModel getContainerModel() {
        return (ContainerModel) DomainObjectFactory
            .newInstance(m_dataCollection.getDataObject());
    }
    
}
