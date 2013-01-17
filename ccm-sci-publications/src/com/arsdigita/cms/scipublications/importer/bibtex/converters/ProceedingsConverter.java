package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
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
public class ProceedingsConverter implements BibTeXConverter<Proceedings, ProceedingsBundle> {

    public Proceedings createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new Proceedings();
        }
    }

    public ProceedingsBundle createBundle(final Proceedings publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ProceedingsBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final Proceedings publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);
    }

    public void processFields(final BibTeXEntry bibTeXEntry,
                              final Proceedings publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();

        importReport.setType(Proceedings.class.getName());
        
        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR),
                                  publication,
                                  true,
                                  importReport,
                                  pretend);
        bibTeXUtil.processIntField(bibTeXEntry.getKey(),
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processPublisher(pubKey,
                                    bibTeXEntry.getField(BibTeXEntry.KEY_PUBLISHER),
                                    bibTeXEntry.getField(BibTeXEntry.KEY_ADDRESS),
                                    publication,
                                    importReport,
                                    pretend);
        bibTeXUtil.processSeries(bibTeXEntry.getKey(),
                                 bibTeXEntry.getField(BibTeXEntry.KEY_SERIES),
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
        return BibTeXEntry.TYPE_PROCEEDINGS.getValue();
    }

}
