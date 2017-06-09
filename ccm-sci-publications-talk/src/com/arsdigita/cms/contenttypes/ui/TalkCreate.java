package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.TalkBundle;
import com.arsdigita.cms.ui.authoring.CreationSelector;

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TalkCreate extends PublicationCreate {
    
    public TalkCreate(final ItemSelectionModel itemSelectionModel,
                      final CreationSelector creationSelector) {
        super(itemSelectionModel, creationSelector);
    }
    
    @Override
    public PublicationBundle createBundle(final ContentItem primary) {
        return new TalkBundle(primary);
    }
    
}
