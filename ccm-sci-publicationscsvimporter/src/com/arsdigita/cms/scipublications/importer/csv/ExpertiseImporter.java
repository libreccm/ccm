package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ExpertiseImporter extends AbstractPublicationImporter<Expertise> {

    protected ExpertiseImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected Expertise importPublication() {
        final Expertise expertise = super.importPublication();        
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        
        if ((data.getPlace() != null) && !data.getPlace().isEmpty()) {
            expertise.setPlace(data.getPlace());
            report.addField(new FieldImportReport("Place", data.getPlace()));
        }
        
        processNumberOfPages(expertise);
        
        return expertise;
    }
    
    @Override
    protected Expertise createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final Expertise expertise = new Expertise();
        expertise.setContentSection(folder.getContentSection());
        expertise.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final PublicationBundle bundle = new PublicationBundle(expertise);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return expertise;
    }

      private void processNumberOfPages(final Expertise publication) {
        if ((getData().getNumberOfPages() != null) && !getData().getNumberOfPages().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfPages());
                publication.setNumberOfPages(volume);
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }
}
