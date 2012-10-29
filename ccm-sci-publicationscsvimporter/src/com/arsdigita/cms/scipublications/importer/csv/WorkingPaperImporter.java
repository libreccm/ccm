package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class WorkingPaperImporter extends AbstractUnPublishedImporter<WorkingPaper> {

    protected WorkingPaperImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected WorkingPaper createPublication() {        
        return new WorkingPaper();        
    }

    @Override
    protected PublicationBundle createBundle(final WorkingPaper workingPaper) {
        return new PublicationBundle(workingPaper);
    }
}
