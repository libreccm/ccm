package org.librecms.assets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.persistence.DataCollection;

import org.librecms.contentsection.AbstractAttachmentListsExporter;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAttachmentListsExporter
    extends AbstractAttachmentListsExporter {

    @Override
    protected String getListName() {
        return "files";
    }

    @Override
    protected boolean hasList(final ContentItem item) {
        
        final DataCollection attachments = FileAttachment.getAttachments(item);
        
        final boolean result = attachments.next();
        attachments.close();
        
        return result;
    }
}
