package com.arsdigita.cms.contentassets;

import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectPublicationLinkTraversalAdapter
        extends RelatedLinkTraversalAdapter {

    public SciProjectPublicationLinkTraversalAdapter() {
        super();
    }

    public SciProjectPublicationLinkTraversalAdapter(
            SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getLinkListName() {
        return "SciProjectPublications";
    }
}
