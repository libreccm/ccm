package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class MonographImporter extends AbstractPublicationWithPublisherImporter<Monograph> {

    protected MonographImporter(final CsvLine data, 
                                final PublicationImportReport report, 
                                final boolean pretend,
                                final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    protected Monograph createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new Monograph();
        }
    }

    @Override
    protected PublicationBundle createBundle(final Monograph monograph) {
        if (isPretend()) {
            return null;
        } else {
            return new PublicationWithPublisherBundle(monograph);
        }
    }

}
