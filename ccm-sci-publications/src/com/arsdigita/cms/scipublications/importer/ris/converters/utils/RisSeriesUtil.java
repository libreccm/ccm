package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.report.SeriesImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisSeriesUtil {
    
    private final ImporterUtil importerUtil;
    private final boolean pretend;
    
    public RisSeriesUtil(final ImporterUtil importerUtil, final boolean pretend) {
        this.importerUtil = importerUtil;
        this.pretend = pretend;
    }

    public void processSeries(final RisDataset dataset, 
                              final RisField field, 
                              final Publication publication, 
                              final PublicationImportReport report) {
        final List<String> series = dataset.getValues().get(field);
        if ((series != null) && !series.isEmpty()) {
            final SeriesImportReport seriesReport = importerUtil.processSeries(publication, series.get(0), pretend);
            report.setSeries(seriesReport);
        }
    }
    
    
    
}
