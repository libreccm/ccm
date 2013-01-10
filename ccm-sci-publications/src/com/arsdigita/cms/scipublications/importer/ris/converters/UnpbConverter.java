package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.UnPublishedBundle;
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
public class UnpbConverter extends AbstractRisConverter {

    @Override
    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(GreyLiterature.BASE_DATA_OBJECT_TYPE);
        
        final GreyLiterature publication = new GreyLiterature();
        publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        final UnPublishedBundle bundle = new UnPublishedBundle(publication);
        
        processTitle(dataset, publication, report, pretend);
        
        processYear(dataset, pretend, publication, report);
        
        processAuthors(dataset, RisField.AU, importerUtil, publication, report, pretend);
        
        processOrganization(dataset, RisField.PB, pretend, publication, importerUtil, report);
        
        processSeries(dataset, RisField.T2, publication, importerUtil, pretend, report);
        
        processField(dataset, RisField.AB, publication, "abstract", report, pretend);
        processField(dataset, RisField.CY, publication, "place", report, pretend);
        processField(dataset, RisField.M1, publication, "number", report, pretend);
        processIntField(dataset, RisField.SP, publication, "numberOfPages", report, pretend);
        
        
        
        return report;
    }

    public RisType getRisType() {
        return RisType.RPRT;
    }

}
