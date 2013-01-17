package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;
import com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.util.BibTeXUtil;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;

/**
 * Converts the BibTeX type {@code book} to the SciPublications type {@link Monograph}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BookConverter implements BibTeXConverter<Monograph, PublicationWithPublisherBundle> {

    public Monograph createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new Monograph();
        }
    }

    public PublicationWithPublisherBundle createBundle(final Monograph publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new PublicationWithPublisherBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final Monograph publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);

    }
 
    public void processFields(final BibTeXEntry bibTeXEntry,
                              final Monograph publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {  
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();

        importReport.setType(Monograph.class.getName());
        
        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR),
                                  publication,
                                  false,
                                  importReport,
                                  pretend);
        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR),
                                  publication,
                                  true,
                                  importReport,
                                  pretend);
        bibTeXUtil.processPublisher(pubKey,
                                    bibTeXEntry.getField(BibTeXEntry.KEY_PUBLISHER),
                                    bibTeXEntry.getField(BibTeXEntry.KEY_ADDRESS),
                                    publication,
                                    importReport,
                                    pretend);
        bibTeXUtil.processIntField(bibTeXEntry.getKey(),
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processIntField(bibTeXEntry.getKey(),
                                   BibTeXEntry.KEY_VOLUME,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_VOLUME),
                                   PublicationWithPublisher.VOLUME,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processIntField(bibTeXEntry.getKey(),
                                   BibTeXEntry.KEY_NUMBER,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_NUMBER),
                                   PublicationWithPublisher.VOLUME,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processSeries(bibTeXEntry.getKey(),
                                 bibTeXEntry.getField(BibTeXEntry.KEY_SERIES),
                                 publication,
                                 importReport,
                                 pretend);
        bibTeXUtil.processField(bibTeXEntry.getKey(),
                                BibTeXEntry.KEY_EDITION,
                                bibTeXEntry.getField(BibTeXEntry.KEY_EDITION),
                                PublicationWithPublisher.EDITION,
                                publication,
                                importReport,
                                pretend);
        bibTeXUtil.processField(bibTeXEntry.getKey(),
                                BibTeXEntry.KEY_NOTE,
                                bibTeXEntry.getField(BibTeXEntry.KEY_NOTE),
                                Publication.MISC,
                                publication,
                                importReport,
                                pretend);
    }

    public String getBibTeXType() {
        return BibTeXEntry.TYPE_BOOK.getValue();
    }

}
