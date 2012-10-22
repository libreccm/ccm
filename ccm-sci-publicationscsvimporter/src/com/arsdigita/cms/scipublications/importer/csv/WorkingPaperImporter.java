package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class WorkingPaperImporter extends AbstractUnPublishedImporter<UnPublished> {

    protected WorkingPaperImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected WorkingPaper createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final WorkingPaper workingPaper = new WorkingPaper();
        workingPaper.setContentSection(folder.getContentSection());
        workingPaper.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final PublicationBundle bundle = new PublicationBundle(workingPaper);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return workingPaper;
    }

}
