package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.GreyLiterature;
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
class GreyLiteratureImporter extends AbstractUnPublishedImporter<GreyLiterature> {

    protected GreyLiteratureImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    protected GreyLiterature importPublication() {
        final GreyLiterature publication = super.importPublication();

        processPagesFrom(publication);
        processPagesTo(publication);

        return publication;
    }

    @Override
    protected GreyLiterature createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final GreyLiterature greyLiterature = new GreyLiterature();
        greyLiterature.setContentSection(folder.getContentSection());
        greyLiterature.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final PublicationBundle bundle = new PublicationBundle(greyLiterature);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return greyLiterature;
    }

    private void processPagesFrom(final GreyLiterature publication) {
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

    private void processPagesTo(final GreyLiterature publication) {
        try {
            final int pagesTo = Integer.parseInt(getData().getPageTo());
            publication.setPagesFrom(pagesTo);
            getReport().addField(new FieldImportReport("Pages to", getData().getPageFrom()));
        } catch (NumberFormatException ex) {
            getReport().addMessage(String.format("Failed to parse pageTo data in line '%d'.",
                                                 getData().getLineNumber()));
        }
    }

}
