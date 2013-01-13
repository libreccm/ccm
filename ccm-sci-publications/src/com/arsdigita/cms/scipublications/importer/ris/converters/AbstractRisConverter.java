package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.RisImporter;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 *
 * @param <T> 
 * @param <B> 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public abstract class AbstractRisConverter<T extends Publication, B extends PublicationBundle> implements RisConverter {

    protected abstract T createPublication(boolean pretend);

    protected abstract B createBundle(T publication, boolean pretend);

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

        final T publication = createPublication(pretend);
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        fieldUtil.processTitle(dataset, publication, importReport);
        if (!pretend) {
            publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());
        }
        final B bundle = createBundle(publication, pretend);
        importReport.setTitle(publication.BASE_DATA_OBJECT_TYPE);

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
