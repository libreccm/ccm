package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 * Interface for importing RisConverter.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public interface RisConverter {
    
    /**
     * Converts a RIS dataset for a publiction of the type supported by the converter to an instance of SciPublication.
     * An implementation of this method is also responsible for publishing the publication item created (if 
     * {@code publishNewItems} is set to {@code true}. 
     * 
     * @param dataset
     * @param importerUtil 
     * @param pretend
     * @param publishNewItems
     * @return  
     * 
     */
    PublicationImportReport convert(RisDataset dataset, 
                                    ImporterUtil importerUtil, 
                                    boolean pretend, 
                                    boolean publishNewItems);
    
    /**
     * 
     * @return The RIS type supported by the converter implementation.
     */
    RisType getRisType();
    
//    BigDecimal getFolderId();
    
}
