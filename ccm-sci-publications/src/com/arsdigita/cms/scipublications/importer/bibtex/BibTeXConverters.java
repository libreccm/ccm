package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.bibtex.util.BibTeXUtil;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisImporter;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.ParseException;

/**
 * Central access point for retrieving {@link BibTeXConverter}s for importing publication data in the BibTeX format.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BibTeXConverters {

    private final static Logger LOGGER = Logger.getLogger(BibTeXConverters.class);
    private final Map<String, BibTeXConverter<Publication, PublicationBundle>> converters =
                                                                               new HashMap<String, BibTeXConverter<Publication, PublicationBundle>>();

    /**
     * Private constructor to ensure that no instances of this class can be created.
     */
    @SuppressWarnings("rawtypes")
    private BibTeXConverters() {
        //Nothing
    }

    private static class Instance {

        private static BibTeXConverters INSTANCE = new BibTeXConverters();
    }

    public static BibTeXConverters getInstance() {
        return Instance.INSTANCE;
    }

    public static void register(final BibTeXConverter converter) {
        getInstance().registerConverter(converter);
    }

    public void registerConverter(final BibTeXConverter converter) {
        converters.put(converter.getBibTeXType(), converter);
    }

    public PublicationImportReport convert(final BibTeXEntry bibTeXEntry,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();

        BibTeXConverter<Publication, PublicationBundle> converter = converters.get(bibTeXEntry.
                getType().getValue().toLowerCase());

        if (converter == null) {
            report.addMessage(String.format("No converter for BibTeX type '%s' available. Publication '%s' has not"
                                            + "been imported.",
                                            bibTeXEntry.getType().getValue(),
                                            bibTeXEntry.getKey().getValue()));

            return report;
        }

        if (isPublicationAlreadyInDatabase(bibTeXEntry, converter.getTypeName(), report)) {
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

    private boolean isPublicationAlreadyInDatabase(final BibTeXEntry bibTeXEntry,
                                                   final String type,
                                                   final PublicationImportReport importReport) {
        final String title;
        final String year;

        try {
            final BibTeXUtil bibTeXUtil = new BibTeXUtil(null);

            title = bibTeXUtil.toPlainString(bibTeXEntry.getField(BibTeXEntry.KEY_TITLE));
            year = bibTeXUtil.toPlainString(bibTeXEntry.getField(BibTeXEntry.KEY_YEAR));
        } catch (IOException ex) {
            return false;
        } catch (ParseException ex) {
            return false;
        }

        int yearOfPublication;
        try {
            yearOfPublication = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            yearOfPublication = 0;
        }

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Publication.BASE_DATA_OBJECT_TYPE);
        final FilterFactory filterFactory = collection.getFilterFactory();
        final Filter titleFilter = filterFactory.equals("title", title);
        final Filter yearFilter = filterFactory.equals("yearOfPublication", yearOfPublication);
        collection.addFilter(titleFilter);
        collection.addFilter(yearFilter);

        final boolean result = !collection.isEmpty();
        collection.close();

        if (result) {
            importReport.setTitle(title);
            importReport.setType(type);
            importReport.addField(new FieldImportReport(Publication.YEAR_OF_PUBLICATION, year));
            importReport.setAlreadyInDatabase(result);
        }

        return result;
    }

}
