package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class WorkingPaperImporter extends AbstractUnPublishedImporter<WorkingPaper> {

    protected WorkingPaperImporter(final CsvLine data, 
                                   final PublicationImportReport report, 
                                   final boolean pretend,
                                   final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    protected WorkingPaper createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new WorkingPaper();
        }
    }

    @Override
    protected PublicationBundle createBundle(final WorkingPaper workingPaper) {
        if (isPretend()) {
            return null;
        } else {
            return new PublicationBundle(workingPaper);
        }
    }

}
