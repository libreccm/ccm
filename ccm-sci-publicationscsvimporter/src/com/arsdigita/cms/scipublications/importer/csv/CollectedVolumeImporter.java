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

    protected CollectedVolumeImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected CollectedVolume createPublication() {
        return new CollectedVolume();
    }

    @Override
    protected PublicationBundle createBundle(final CollectedVolume collectedVolume) {
        return new CollectedVolumeBundle(collectedVolume);
    }

}
