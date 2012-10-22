package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

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
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final CollectedVolume collectedVolume = new CollectedVolume();
        collectedVolume.setContentSection(folder.getContentSection());
        collectedVolume.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final CollectedVolumeBundle bundle = new CollectedVolumeBundle(collectedVolume);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return collectedVolume;
    }
}
