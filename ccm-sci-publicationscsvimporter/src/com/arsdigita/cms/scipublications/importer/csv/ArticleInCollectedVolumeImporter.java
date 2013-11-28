package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;
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
class ArticleInCollectedVolumeImporter extends AbstractPublicationImporter<ArticleInCollectedVolume> {

    public ArticleInCollectedVolumeImporter(final CsvLine data,
                                            final PublicationImportReport report,
                                            final boolean pretend,
                                            final ImporterUtil importerUtil) {
        super(data, report, pretend, importerUtil);
    }

    @Override
    public ArticleInCollectedVolume importPublication() {
        final ArticleInCollectedVolume article = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        final ImporterUtil importerUtil = getImporterUtil();

        processPagesFrom(article);
        processPagesTo(article);

        if ((data.getChapter() != null) && !data.getChapter().isEmpty()) {
            if (!isPretend()) {
                article.setChapter(data.getChapter());
            }
            report.addField(new FieldImportReport("Chapter", data.getChapter()));
        }

        if ((data.getCollectedVolume() != null) && !data.getCollectedVolume().isEmpty()) {
            report.setCollectedVolume(importerUtil.processCollectedVolume(
                    article,
                    data.getCollectedVolume(),
                    data.getYear(),
                    parseAuthors(data.getCollectedVolumeAuthors()),
                    data.getPublisher(),
                    data.getPlace(),
                    null,
                    isPretend()));
        }


        return article;
    }

    private void processPagesFrom(final ArticleInCollectedVolume publication) {
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

    private void processPagesTo(final ArticleInCollectedVolume publication) {
        if ((getData().getPageFrom() != null) && !getData().getPageFrom().isEmpty()) {
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
    }

    @Override
    protected ArticleInCollectedVolume createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new ArticleInCollectedVolume();
        }
    }

    @Override
    protected PublicationBundle createBundle(final ArticleInCollectedVolume article) {
        if (isPretend()) {
            return null;
        } else {
            return new ArticleInCollectedVolumeBundle(article);
        }
    }

    @Override
    protected Folder getFolder() {
        return Publication.getConfig().getDefaultArticlesInCollectedVolumeFolder();
    }
}
