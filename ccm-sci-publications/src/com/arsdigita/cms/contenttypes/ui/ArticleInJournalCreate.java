package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ArticleInJournalCreate extends PublicationCreate {
    
    public ArticleInJournalCreate(final ItemSelectionModel itemModel,
                                  final CreationSelector parent) {
        super(itemModel, parent);
    }
    
    @Override
    protected PublicationBundle createBundle(final ContentItem primary) {
        return new ArticleInJournalBundle(primary);
    }    
}
