package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeBundle;
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
public class InCollectionConverter implements BibTeXConverter<ArticleInCollectedVolume, ArticleInCollectedVolumeBundle> {

    public ArticleInCollectedVolume createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInCollectedVolume();
        }
    }

    public ArticleInCollectedVolumeBundle createBundle(final ArticleInCollectedVolume publication,
                                                       final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInCollectedVolumeBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final ArticleInCollectedVolume publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);
    }

    public void processFields(final BibTeXEntry bibTeXEntry,
                              final ArticleInCollectedVolume publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();

        importReport.setType(ArticleInCollectedVolume.class.getName());
        
        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR),
                                  publication,
                                  pretend,
                                  importReport,
                                  pretend);
        bibTeXUtil.processCollectedVolume(bibTeXEntry, publication, importReport, pretend);
        bibTeXUtil.processIntField(pubKey,
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processField(pubKey,
                                BibTeXEntry.KEY_CHAPTER,
                                bibTeXEntry.getField(BibTeXEntry.KEY_CHAPTER),
                                ArticleInCollectedVolume.CHAPTER,
                                publication,
                                importReport,
                                pretend);
        bibTeXUtil.processPages(pubKey, bibTeXEntry.getField(BibTeXEntry.KEY_PAGES), publication, importReport, pretend);
        bibTeXUtil.processField(bibTeXEntry.getKey(),
                                BibTeXEntry.KEY_NOTE,
                                bibTeXEntry.getField(BibTeXEntry.KEY_NOTE),
                                Publication.MISC,
                                publication,
                                importReport,
                                pretend);
    }

    public String getBibTeXType() {
        return BibTeXEntry.TYPE_INCOLLECTION.getValue();
    }

}
