package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisImporter;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
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
    private final Map<String, BibTeXConverter<Publication, PublicationBundle>> converters = new HashMap<String, BibTeXConverter<Publication, PublicationBundle>>();

    @SuppressWarnings("rawtypes")
    private BibTeXConverters() {
        LOGGER.debug("Loading BibTeX converters...");
        final ServiceLoader<BibTeXConverter> converterServices = ServiceLoader.load(BibTeXConverter.class);

        for (BibTeXConverter converter : converterServices) {
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

        BibTeXConverter<Publication, PublicationBundle> converter = converters.get(bibTeXEntry.
                getType().getValue());

        if (converter == null) {
            report.addMessage(String.format("No converter for BibTeX type '%s' available. Publication '%s' has not"
                                            + "been imported.",
                                            bibTeXEntry.getType().getValue(),
                                            bibTeXEntry.getKey().getValue()));

            return report;
        }

        try {
            converter = converter.getClass().newInstance();
        } catch (InstantiationException ex) {
            final StringWriter writer = new StringWriter();
            writer.append(String.format("Failed to create instance of converter for BibTeX type '%s'. Publication"
                                        + " '%s' was not imported.",
                                        bibTeXEntry.getType().getValue(),
                                        bibTeXEntry.getKey().getValue()));
            ex.printStackTrace(new PrintWriter(writer));
            report.addMessage(writer.toString());

            return report;
        } catch (IllegalAccessException ex) {
            final StringWriter writer = new StringWriter();
            writer.append(String.format("Failed to create instance of converter for BibTeX type '%s'. Publication"
                                        + " '%s' was not imported.",
                                        bibTeXEntry.getType().getValue(),
                                        bibTeXEntry.getKey().getValue()));
            ex.printStackTrace(new PrintWriter(writer));
            report.addMessage(writer.toString());

            return report;
        }

        final Publication publication = converter.createPublication(pretend);
        converter.processTitle(bibTeXEntry, publication, report, pretend);
        if (!pretend) {
            publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        }
        final PublicationBundle bundle = converter.createBundle(publication, pretend);
        report.setType(publication.BASE_DATA_OBJECT_TYPE);

        converter.processFields(bibTeXEntry, publication, importerUtil, report, pretend);

        if (!pretend) {
            publication.save();

            publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

            publication.save();

            assignFolder(publication, bundle);
            assignCategories(bundle);

            bundle.save();
            publication.save();
        }

        if (publishNewItems) {
            importerUtil.publishItem(publication);
        }

        report.setSuccessful(true);

        return report;
    }

    /**
     * Overwrite this method to put a publication of specific type into a special folder.
     *
     * @return
     */
    protected Integer getFolderId() {
        return Publication.getConfig().getDefaultPublicationsFolder();
    }

    protected void assignFolder(final Publication publication, final PublicationBundle bundle) {
        final Folder folder = new Folder(new BigDecimal(getFolderId()));
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());
        publication.setContentSection(folder.getContentSection());
    }

    protected void assignCategories(final PublicationBundle bundle) {
        final Category defaultCat = RisImporter.getConfig().getDefaultCategory();
        if (defaultCat != null) {
            defaultCat.addChild(bundle);
        }
    }
}
