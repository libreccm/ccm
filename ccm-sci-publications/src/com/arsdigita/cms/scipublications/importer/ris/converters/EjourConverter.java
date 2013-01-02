package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 * Converter for the RIS {@code EJOUR} to the SciPublications type {@link ArticleInJournal}
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class EjourConverter implements RisConverter {

    public PublicationImportReport convert(final RisDataset dataset, 
                                           final ImporterUtil importerUtil, 
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(ArticleInJournal.BASE_DATA_OBJECT_TYPE);
        
        final ArticleInJournal article = new ArticleInJournal();
        
        
        
        return report;        
    }

    public RisType getRisType() {
        return RisType.EJOUR;
    }
    
    
    
}
