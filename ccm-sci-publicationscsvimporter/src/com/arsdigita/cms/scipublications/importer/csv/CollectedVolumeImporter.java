package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class CollectedVolumeImporter extends AbstractPublicationWithPublisherImporter<CollectedVolume> {

    protected CollectedVolumeImporter(final CsvLine data, 
                                      final PublicationImportReport report,
                                      final boolean pretend,
                                      final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
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

     @Override
    protected Folder getFolder() {
        return Publication.getConfig().getDefaultCollectedVolumesFolder();
    }
}
