package com.arsdigita.cms.scipublications.importer.bibtex.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.contenttypes.UnPublishedBundle;
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
public class MasterThesisConverter implements BibTeXConverter<GreyLiterature, UnPublishedBundle> {

    public GreyLiterature createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new GreyLiterature();
        }
    }

    public UnPublishedBundle createBundle(final GreyLiterature publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new UnPublishedBundle(publication);
        }
    }

    public void processTitle(final BibTeXEntry bibTeXEntry,
                             final GreyLiterature publication,
                             final PublicationImportReport importReport,
                             final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);
        bibTeXUtil.processTitle(bibTeXEntry, publication, importReport, pretend);
    }

    public void processFields(final BibTeXEntry bibTeXEntry,
                              final GreyLiterature publication,
                              final ImporterUtil importerUtil,
                              final PublicationImportReport importReport,
                              final boolean pretend) {
        final BibTeXUtil bibTeXUtil = new BibTeXUtil(importerUtil);
        final Key pubKey = bibTeXEntry.getKey();
        
        importReport.setType(GreyLiterature.class.getName());

        bibTeXUtil.processAuthors(pubKey,
                                  bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR),
                                  publication,
                                  false,
                                  importReport,
                                  pretend);
        bibTeXUtil.processOrganization(pubKey,
                                       bibTeXEntry.getField(BibTeXEntry.KEY_SCHOOL),
                                       publication,
                                       importerUtil,
                                       importReport,
                                       pretend);
        bibTeXUtil.processIntField(pubKey,
                                   BibTeXEntry.KEY_YEAR,
                                   bibTeXEntry.getField(BibTeXEntry.KEY_YEAR),
                                   Publication.YEAR_OF_PUBLICATION,
                                   publication,
                                   importReport,
                                   pretend);
        bibTeXUtil.processField(pubKey,
                                BibTeXEntry.KEY_ADDRESS,
                                bibTeXEntry.getField(BibTeXEntry.KEY_ADDRESS),
                                UnPublished.PLACE,
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
        return BibTeXEntry.TYPE_MASTERSTHESIS.getValue();
    }

}
