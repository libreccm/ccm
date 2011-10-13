package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.contenttypes.LinkTraversalAdapter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.metadata.Property;

/**
 *
 * @author Jens Pelzetter
 */
public class RelatedLinkTraversalAdapter extends LinkTraversalAdapter {

    public RelatedLinkTraversalAdapter() {
        super();
    }

    public RelatedLinkTraversalAdapter(
            SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    public String getLinkListName() {
        return "NONE";
    }

    @Override
    public boolean processProperty(DomainObject obj,
                                   String path,
                                   Property prop,
                                   String context) {
        DomainObject nObj = obj;

        if (obj instanceof ContentBundle) {

            nObj = ((ContentBundle) obj).
                    getInstance(GlobalizationHelper.getNegotiatedLocale(), true);
        }

        if (nObj instanceof RelatedLink) {
            RelatedLink link = (RelatedLink) nObj;
            if (getLinkListName().equals(link.getLinkListName())) {
                return super.processProperty(nObj, path, prop, context);
            } else {
                return false;
            }
        } else {
            return super.processProperty(nObj, path, prop, context);
        }
    }
}
