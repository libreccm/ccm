package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.InternetArticleBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class InternetArticleCreate extends PublicationCreate {

    public InternetArticleCreate(final ItemSelectionModel itemModel,
                                 final CreationSelector parent) {
        super(itemModel, parent);
    }

    @Override
    public PublicationBundle createBundle(final ContentItem primary) {
        return new InternetArticleBundle(primary);
    }
}
