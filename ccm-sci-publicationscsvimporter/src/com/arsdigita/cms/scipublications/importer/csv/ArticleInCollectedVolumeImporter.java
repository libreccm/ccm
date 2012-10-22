package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;
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
class ArticleInCollectedVolumeImporter extends AbstractPublicationImporter<ArticleInCollectedVolume> {

    public ArticleInCollectedVolumeImporter(CsvLine data, PublicationImportReport report) {
        super(data, report);
    }

    @Override
    public ArticleInCollectedVolume importPublication() {
        final ArticleInCollectedVolume article = importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        final ImporterUtil importerUtil = getImporterUtil();

        processPagesFrom(article);
        processPagesTo(article);
        
        if ((data.getChapter() != null) && !data.getChapter().isEmpty()) {
            article.setChapter(data.getChapter());
            report.addField(new FieldImportReport("Chapter", data.getChapter()));
        }

        report.setCollectedVolume(importerUtil.processCollectedVolume(article,
                                                                      data.getCollectedVolume(),
                                                                      article.getYearOfPublication(),
                                                                      parseAuthors(data.getAuthors())));


        return article;
    }
    
     private void processPagesFrom(final ArticleInCollectedVolume publication) {
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

    private void processPagesTo(final ArticleInCollectedVolume publication) {
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
    protected ArticleInCollectedVolume createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));

        final ArticleInCollectedVolume article = new ArticleInCollectedVolume();
        article.setContentSection(folder.getContentSection());
        article.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        final ArticleInCollectedVolumeBundle bundle = new ArticleInCollectedVolumeBundle(article);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        return article;
    }        
}
