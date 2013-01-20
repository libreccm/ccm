package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsBundle;
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
public class InProceedingsConverter implements BibTeXConverter<InProceedings, InProceedingsBundle> {

    public InProceedings createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InProceedings();
        }
    }
    
    public String getTypeName() {
        return InProceedings.class.getName();
    }

    public InProceedingsBundle createBundle(final InProceedings publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new InProceedingsBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final InProceedings publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);
    }

    public void processFields(final BibTeXEntry bibTeXEntry,
                              final InProceedings publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();
        
        importReport.setType(InProceedings.class.getName());

        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR),
                                  publication,
                                  pretend,
                                  importReport,
                                  pretend);
        bibTeXUtil.processProceedings(bibTeXEntry, publication, importReport, pretend);
        bibTeXUtil.processIntField(pubKey,
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_EDITOR),
                                  publication,
                                  pretend,
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
        return BibTeXEntry.TYPE_INPROCEEDINGS.getValue();
    }

}
