package com.arsdigita.cms.contentassets;

import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganizationPublicationLinkTraversalAdapter
        extends RelatedLinkTraversalAdapter {
    
    public SciOrganizationPublicationLinkTraversalAdapter() {
        super();
    }
    
     public SciOrganizationPublicationLinkTraversalAdapter(
            SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }
        
    @Override
    public String getLinkListName() {
        return "SciOrganizationPublications";
    }
    
}
