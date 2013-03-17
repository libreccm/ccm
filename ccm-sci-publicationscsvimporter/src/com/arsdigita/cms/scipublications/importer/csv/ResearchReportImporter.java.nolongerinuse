package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.ResearchReport;
import com.arsdigita.cms.contenttypes.UnPublishedBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;



/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ResearchReportImporter extends AbstractUnPublishedImporter<ResearchReport> {

    protected ResearchReportImporter(final CsvLine data,
                                     final PublicationImportReport report,
                                     final boolean pretend,
                                     final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }
    
    @Override
    protected ResearchReport createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new ResearchReport();
        }
    }

    @Override
    protected PublicationBundle createBundle(final ResearchReport report) {
        if (isPretend()) {
            return null;
        } else {
            return new UnPublishedBundle(report);
        }
    }
    
    
    
}
