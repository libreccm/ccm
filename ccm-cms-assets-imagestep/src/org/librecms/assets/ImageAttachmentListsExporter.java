package org.librecms.assets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.persistence.DataCollection;

import org.librecms.contentsection.AbstractAttachmentListsExporter;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImageAttachmentListsExporter
    extends AbstractAttachmentListsExporter {

    @Override
    protected String getListName() {
        return "images";
    }

    @Override
    protected boolean hasList(final ContentItem contentItem) {

        final DataCollection images = ItemImageAttachment
            .getImageAttachments(contentItem);
        
        final boolean result = images.next();
        images.close();
        
        return result;
    }

}
