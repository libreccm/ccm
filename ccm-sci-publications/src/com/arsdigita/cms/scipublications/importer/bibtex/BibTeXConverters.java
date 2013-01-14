package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;
import org.jbibtex.BibTeXEntry;

/**
 * Central access point for retrieving {@link BibTeXConverter}s for importing publication data in the BibTeX format.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BibTeXConverters {
    
    private final static Logger LOGGER = Logger.getLogger(BibTeXConverters.class);
    private final  Map<String, BibTeXConverter<?, ?>> converters = new HashMap<String, BibTeXConverter<?, ?>>();
        
    @SuppressWarnings("rawtypes")
    private BibTeXConverters() {
        LOGGER.debug("Loading BibTeX converters...");
        final ServiceLoader<BibTeXConverter> converterServices = ServiceLoader.load(BibTeXConverter.class);
        
        for(BibTeXConverter converter : converterServices) {
            LOGGER.debug(String.format("Found converter for BibTeX type '%s'.", converter.getBibTeXType()));
            
            converters.put(converter.getBibTeXType(), converter);
        }
        LOGGER.debug(String.format("Found %d BibTeX converters.", converters.size()));                        
    }
    
    private static class Instance {
        
        private static BibTeXConverters INSTANCE = new BibTeXConverters();
        
    }
    
    public static BibTeXConverters getInstance() {
        return Instance.INSTANCE;
    }
    
    public PublicationImportReport convert(final BibTeXEntry bibTeXEntry,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        
        
        
        return report;
    }
    
}
