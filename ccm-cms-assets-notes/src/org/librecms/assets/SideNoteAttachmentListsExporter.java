package org.librecms.assets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contentassets.Note;
import com.arsdigita.persistence.DataCollection;

import org.librecms.contentsection.AbstractAttachmentListsExporter;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SideNoteAttachmentListsExporter
    extends AbstractAttachmentListsExporter {
    
      @Override
    protected String getListName() {
        return "notes";
    }

    @Override
    protected boolean hasList(final ContentItem item) {
        
        final DataCollection attachments = Note.getNotes(item);
        
        final boolean result = attachments.next();
        attachments.close();
        
        return result;
    }

}
