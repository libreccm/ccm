package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import org.jbibtex.BibTeXEntry;

/**
 *
 * @param <T> 
 * @param <B> 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public interface BibTeXConverter<T extends Publication, B extends PublicationBundle> {

    T createPublication(boolean pretend);
    
    String getTypeName();
    
    B createBundle(T publication, boolean pretend);
    
    void processTitle(final BibTeXEntry bibTeXEntry,
                      final T publication,
                      final PublicationImportReport importReport,
                      final boolean pretend);
    
    void processFields(final BibTeXEntry bibTeXEntry,
                       final T publication,
                       final ImporterUtil importerUtil,
                       final PublicationImportReport importReport,
                       final boolean pretend);
        
    /**
     * Returns the supported BibTeX type.
     * 
     * @return 
     */
    String getBibTeXType();

}
