package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class CollectedVolumeImporter extends AbstractPublicationWithPublisherImporter<CollectedVolume> {

    protected CollectedVolumeImporter(final CsvLine data, 
                                      final PublicationImportReport report,
                                      final boolean pretend) {
        super(data, report, pretend);
    }

    @Override
    protected CollectedVolume createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new CollectedVolume();
        }
    }

    @Override
    protected PublicationBundle createBundle(final CollectedVolume collectedVolume) {
        if (isPretend()) {
            return null;
        } else {
            return new CollectedVolumeBundle(collectedVolume);
        }
    }

}
