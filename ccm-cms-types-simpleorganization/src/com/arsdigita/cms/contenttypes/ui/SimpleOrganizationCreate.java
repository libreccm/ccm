package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundle;
import com.arsdigita.cms.contenttypes.SimpleOrganizationBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SimpleOrganizationCreate extends GenericOrganizationalUnitCreate {
    
    public SimpleOrganizationCreate(final ItemSelectionModel itemModel,
                                    final CreationSelector parent) {
        super(itemModel, parent);
    }
    
    @Override
    protected GenericOrganizationalUnitBundle createBundle(
            final ContentItem primary) {
        return new SimpleOrganizationBundle(primary);
    }
    
}
