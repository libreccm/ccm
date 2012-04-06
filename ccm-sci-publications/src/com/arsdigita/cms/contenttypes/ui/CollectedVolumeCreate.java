package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CollectedVolumeCreate extends PublicationCreate {

    public CollectedVolumeCreate(final ItemSelectionModel itemModel,
                                 final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public PublicationBundle createBundle(final ContentItem primary) {
        return new CollectedVolumeBundle(primary);
    }
}
