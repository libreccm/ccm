package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class InProceedingsImporter extends AbstractPublicationImporter<InProceedings> {

    public InProceedingsImporter(final CsvLine data, 
                                 final PublicationImportReport report, 
                                 final boolean pretend,
                                 final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    protected InProceedings importPublication() {
        final InProceedings inProceedings = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        final ImporterUtil importerUtil = getImporterUtil();

        processPagesFrom(inProceedings);
        processPagesTo(inProceedings);

        report.setProceedings(importerUtil.processProceedings(inProceedings,
                                                              data.getCollectedVolume(),
                                                              data.getYear(),
                                                              data.getConference(),
                                                              parseAuthors(data.getCollectedVolumeAuthors()),
                                                              data.getPublisher(),
                                                              data.getPlace(),
                                                              isPretend()));

        return inProceedings;
    }

    private void processPagesFrom(final InProceedings publication) {
        if ((getData().getPageFrom() != null) && !getData().getPageFrom().isEmpty()) {
            try {
                final int pagesFrom = Integer.parseInt(getData().getPageFrom());
                if (!isPretend()) {
                    publication.setPagesFrom(pagesFrom);
                }
                getReport().addField(new FieldImportReport("Pages from", getData().getPageFrom()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse pageFrom data in line '%d'.",
                                                     getData().getLineNumber()));
            }
        }
    }

    private void processPagesTo(final InProceedings publication) {
        try {
            final int pagesTo = Integer.parseInt(getData().getPageTo());
            if (!isPretend()) {
                publication.setPagesTo(pagesTo);
            }
            getReport().addField(new FieldImportReport("Pages to", getData().getPageTo()));
        } catch (NumberFormatException ex) {
            getReport().addMessage(String.format("Failed to parse pageTo data in line '%d'.",
                                                 getData().getLineNumber()));
        }
    }

    @Override
    protected InProceedings createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new InProceedings();
        }

    }

    @Override
    protected PublicationBundle createBundle(final InProceedings inProceedings) {
        if (isPretend()) {
            return null;
        } else {
            return new InProceedingsBundle(inProceedings);
        }
    }
    
     @Override
    protected Integer getFolderId() {
        return Publication.getConfig().getDefaultInProceedingsFolder();
    }

}
