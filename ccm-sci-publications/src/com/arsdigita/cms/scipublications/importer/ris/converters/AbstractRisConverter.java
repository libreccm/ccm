package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.RisImporter;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @param <T>
 * @param <B>
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public abstract class AbstractRisConverter<T extends Publication, B extends PublicationBundle>
        implements RisConverter {

    protected abstract T createPublication(boolean pretend);

    protected abstract String getTypeName();

    protected abstract B createBundle(T publication, boolean pretend);

    protected String getYear(final RisDataset dataset) {
        final List<String> values = dataset.getValues().get(RisField.PY);
        if ((values == null) || values.isEmpty()) {
            return "0";
        } else {
            return values.get(0);
        }
    }

    protected abstract void processFields(final RisDataset dataset,
                                          final T publication,
                                          final ImporterUtil importerUtil,
                                          final PublicationImportReport importReport,
                                          final boolean pretend);

    public final PublicationImportReport convert(final RisDataset dataset,
                                                 final ImporterUtil importerUtil,
                                                 final boolean pretend,
                                                 final boolean publishNewItems) {
        final PublicationImportReport importReport = new PublicationImportReport();

        if (isPublicationAlreadyInDatabase(dataset, getTypeName(), importReport)) {
            return importReport;
        }

        final T publication = createPublication(pretend);
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        fieldUtil.processTitle(dataset, publication, importReport);
        if (!pretend) {
            publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        }
        final B bundle = createBundle(publication, pretend);
        importReport.setType(publication.BASE_DATA_OBJECT_TYPE);

        processFields(dataset, publication, importerUtil, importReport, pretend);

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

        importReport.setSuccessful(true);

        return importReport;
    }

    /**
     * Overwrite this method to put a publication of specific type into a special folder.
     *
     * @return
     */
    protected Folder getFolder() {
        return Publication.getConfig().getDefaultProceedingsFolder();
    }

    protected void assignFolder(final Publication publication, final PublicationBundle bundle) {
        final Folder folder = getFolder();
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

    private boolean isPublicationAlreadyInDatabase(final RisDataset dataset,
                                                   final String type,
                                                   final PublicationImportReport importReport) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(true);
        final String title = fieldUtil.getTitle(dataset);
        final String year = getYear(dataset);

        int yearOfPublication;
        try {
            yearOfPublication = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            yearOfPublication = 0;
        }

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(type);
        final FilterFactory filterFactory = collection.getFilterFactory();
        final Filter titleFilter = filterFactory.equals(Publication.TITLE, title);
        final Filter yearFilter = filterFactory.equals(Publication.YEAR_OF_PUBLICATION,
                                                       yearOfPublication);
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
