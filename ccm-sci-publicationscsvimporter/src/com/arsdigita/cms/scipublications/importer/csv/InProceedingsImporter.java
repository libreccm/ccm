package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class InProceedingsImporter extends AbstractPublicationImporter<InProceedings> {

    public InProceedingsImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected InProceedings importPublication() {
        final InProceedings inProceedings = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        final ImporterUtil importerUtil = getImporterUtil();

        processPagesFrom(inProceedings);
        processPagesTo(inProceedings);

        importerUtil.processProceedings(inProceedings,
                                        data.getCollectedVolume(),
                                        inProceedings.getYearOfPublication(),
                                        data.getConference(),
                                        parseAuthors(data.getCollectedVolumeAuthors()));

        return inProceedings;
    }

    private void processPagesFrom(final InProceedings publication) {
        if ((getData().getPageFrom() != null) && !getData().getPageFrom().isEmpty()) {
            try {
                final int pagesFrom = Integer.parseInt(getData().getPageFrom());
                publication.setPagesFrom(pagesFrom);
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
            publication.setPagesFrom(pagesTo);
            getReport().addField(new FieldImportReport("Pages to", getData().getPageFrom()));
        } catch (NumberFormatException ex) {
            getReport().addMessage(String.format("Failed to parse pageTo data in line '%d'.",
                                                 getData().getLineNumber()));
        }
    }

    @Override
    protected InProceedings createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final InProceedings inProceedings = new InProceedings();
        inProceedings.setContentSection(folder.getContentSection());
        inProceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final InProceedingsBundle bundle = new InProceedingsBundle(inProceedings);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return inProceedings;
    }

}
