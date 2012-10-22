package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ProceedingsImporter extends AbstractPublicationWithPublisherImporter<Proceedings> {

    public ProceedingsImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }
     
    @Override
    public Proceedings importPublication() {
        final Proceedings proceedings = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        
        if ((data.getConference() != null) && !data.getConference().isEmpty()) {
            proceedings.setNameOfConference(data.getConference());
            report.addField(new FieldImportReport("Name of conference", data.getConference()));
        }
                       
        return proceedings;
    }        
    
    @Override
    protected Proceedings createPublication() {
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));
        
        final Proceedings proceedings = new Proceedings();
        proceedings.setContentSection(folder.getContentSection());
        proceedings.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        
        final ProceedingsBundle bundle = new ProceedingsBundle(proceedings);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());
        
        return proceedings;                
    }            
}
