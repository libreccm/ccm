package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ElecConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset, 
                                           final ImporterUtil importerUtil, 
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(InternetArticle.BASE_DATA_OBJECT_TYPE);
        
        final InternetArticle article = new InternetArticle();
        
        processTitle(dataset, article, report, pretend);
        
        processYear(dataset, pretend, article, report);
        
        processAuthors(dataset, RisField.AU, importerUtil, article, report, pretend);
        
        processField(dataset, RisField.AB, article, "abstract", report, pretend);
        
        processSeries(dataset, RisField.T2, article, importerUtil, pretend, report);
        
        processField(dataset, RisField.CY, article, "place", report, pretend);
        
        processField(dataset, RisField.DO, article, "doi", report, pretend);
        
        processField(dataset, RisField.ET, article, "edition", report, pretend);
        
        processField(dataset, RisField.UR, article, "url", report, pretend);
                    
        return report;
    }

    public RisType getRisType() {
        return RisType.ELEC;
    }
    
    
    
}
