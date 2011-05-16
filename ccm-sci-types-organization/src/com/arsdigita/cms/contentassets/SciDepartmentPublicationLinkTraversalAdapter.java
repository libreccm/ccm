package com.arsdigita.cms.contentassets;

import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentPublicationLinkTraversalAdapter
        extends RelatedLinkTraversalAdapter {

       public SciDepartmentPublicationLinkTraversalAdapter() {
        super();
    }
    
     public SciDepartmentPublicationLinkTraversalAdapter(
            SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }
    
    @Override
    public String getLinkListName() {
        return "SciDepartmentPublications";
    }
}
