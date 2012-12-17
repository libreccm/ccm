package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisImporter implements SciPublicationsImporter {

    private static final Logger LOGGER = Logger.getLogger(RisImporter.class);
    private final RisConverters converters = RisConverters.getInstance();  

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("RIS", new MimeType("application/x-research-info-systems"), "ris");
        } catch (MimeTypeParseException ex) {
            LOGGER.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("RIS",
                                         null,
                                         "RIS");
        }
    }

    public ImportReport importPublications(final String publications,
                                           final boolean pretend,
                                           final boolean publishNewItems) throws SciPublicationsImportException {
        final String[] lines = publications.split("\r\n");

        final RisParser parser = new RisParser();
        final List<RisDataset> datasets = parser.parse(lines);

        final ImportReport report = new ImportReport();
        report.setImporter("RIS Importer");
        report.setPretend(pretend);

        final ImporterUtil importerUtil = new ImporterUtil(publishNewItems);
        
        for (RisDataset dataset : datasets) {
           processPublication(dataset, report, importerUtil, pretend, publishNewItems);
        }

        return report;
    }

    private void processPublication(final RisDataset dataset,
                                    final ImportReport report,
                                    final ImporterUtil importerUtil,
                                    final boolean pretend,
                                    final boolean publishNewItems) {
        try {
            report.addPublication(converters.convert(dataset, importerUtil, pretend, publishNewItems));
        } catch (RisConverterException ex) {
            final PublicationImportReport importReport = new PublicationImportReport();
            importReport.addMessage(String.format("Failed to create converter for RIS type '%s'.", 
                                                  dataset.getType().toString()));
        }
    }

}
