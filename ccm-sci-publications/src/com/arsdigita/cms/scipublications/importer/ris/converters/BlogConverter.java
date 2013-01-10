package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.InternetArticleBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BlogConverter extends AbstractRisConverter {

    public PublicationImportReport convert(final RisDataset dataset, 
                                           final ImporterUtil importerUtil, 
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(InternetArticle.BASE_DATA_OBJECT_TYPE);
        
        final InternetArticle article = new InternetArticle();
        article.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final InternetArticleBundle bundle = new InternetArticleBundle(article);                
                
        processTitle(dataset, article, report, pretend);
        
        processYear(dataset, pretend, article, report);
        
        processAuthors(dataset, RisField.AU, importerUtil, article, report, pretend);
        
        processEditors(dataset, RisField.A2, importerUtil, article, report, pretend);
        
        processField(dataset, RisField.AB, article, "abstract", report, pretend);        
        processField(dataset, RisField.CY, article, "place", report, pretend);        
        processField(dataset, RisField.ET, article, "edition", report, pretend);
        
        processOrganization(dataset, RisField.PB, pretend, article, importerUtil, report);
        processOrganization(dataset, RisField.T3, pretend, article, importerUtil, report);
        
        processField(dataset, RisField.UR, article, "url", report, pretend);
        processDateField(dataset, RisField.Y2, article, "lastAccessed", report, pretend);
        
        return report;
    }

    public RisType getRisType() {
       return RisType.BLOG;
    }
    
    
    
}
