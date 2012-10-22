package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class MonographImporter extends AbstractPublicationWithPublisherImporter<Monograph> {

    protected MonographImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected Monograph createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final Monograph monograph = new Monograph();
        monograph.setContentSection(folder.getContentSection());
        monograph.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final PublicationWithPublisherBundle bundle = new PublicationWithPublisherBundle(monograph);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());
        
        return monograph;
    }

}
