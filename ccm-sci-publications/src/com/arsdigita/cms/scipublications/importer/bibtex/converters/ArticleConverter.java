package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.util.BibTeXUtil;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ArticleConverter implements BibTeXConverter<ArticleInJournal, ArticleInJournalBundle> {

    public ArticleInJournal createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInJournal();
        }
    }

    public ArticleInJournalBundle createBundle(final ArticleInJournal publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInJournalBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final ArticleInJournal publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);
    }

    public void processFields(final BibTeXEntry bibTeXEntry,
                              final ArticleInJournal publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();
        
        importReport.setType(ArticleInJournal.class.getName());

        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR),
                                  publication,
                                  pretend,
                                  importReport,
                                  pretend);
        bibTeXUtil.processJournal(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_JOURNAL),
                                  publication,
                                  importReport,
                                  pretend);
        bibTeXUtil.processIntField(pubKey,
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processIntField(pubKey,
                                   BibTeXEntry.KEY_VOLUME,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_VOLUME),
                                   ArticleInJournal.VOLUME,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processField(pubKey,
                                BibTeXEntry.KEY_NUMBER,
                                bibTeXEntry.getField(BibTeXEntry.KEY_NUMBER),
                                ArticleInJournal.ISSUE,
                                publication,
                                importReport,
                                pretend);
        bibTeXUtil.processPages(pubKey,
                                bibTeXEntry.getField(BibTeXEntry.KEY_PAGES),
                                publication,
                                importReport,
                                pretend);
        bibTeXUtil.processField(pubKey,
                                BibTeXEntry.KEY_PAGES, bibTeXEntry.getField(BibTeXEntry.KEY_PAGES),
                                Publication.MISC,
                                publication,
                                importReport,
                                pretend);
    }

    public String getBibTeXType() {
        return BibTeXEntry.TYPE_ARTICLE.getValue();
    }

}
