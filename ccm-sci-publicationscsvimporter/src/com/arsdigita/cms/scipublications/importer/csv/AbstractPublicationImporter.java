package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.AuthorData;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
abstract class AbstractPublicationImporter<T extends Publication> {

    private static final String AUTHORS_SEP = ";";
    private static final String AUTHOR_NAME_SEP = ",";
    private static final String EDITOR_STR = "(Hrsg.)";
    private final CsvLine data;
    private final PublicationImportReport report;
    private final ImporterUtil importerUtil = new ImporterUtil();

    public AbstractPublicationImporter(final CsvLine data, final PublicationImportReport report) {
        this.data = data;
        this.report = report;
    }

    protected CsvLine getData() {
        return data;
    }

    protected ImporterUtil getImporterUtil() {
        return importerUtil;
    }

    protected PublicationImportReport getReport() {
        return report;
    }

    /**
     * This method is called by {@link PublicationsImporter}.
     */
    public final void doImport(final boolean publishNewItems) {
        final T publication = importPublication();

        publication.save();

        assignCategories(publication.getPublicationBundle());

        if (publishNewItems) {
            final Calendar now = new GregorianCalendar();
            final LifecycleDefinitionCollection lifecycles = publication.getContentSection().getLifecycleDefinitions();
            lifecycles.next();
            final LifecycleDefinition lifecycleDef = lifecycles.getLifecycleDefinition();
            publication.publish(lifecycleDef, now.getTime());
        }
        
        report.setSuccessful(true);

    }

    /**
     * Overwrite to set the properites for the content type, using this pattern:
     * <pre>    
     * public T importPublication() {
     *     final T publication = super.importPublication();
     * 
     *     //Set additional properites of the publication type
     *     publication.set...
     * 
     *     //Output properties of publication. 
     *    super.printPublication(publication);  
     *  
     *    return publication;
     * }
     * </pre>
     * 
     * @return 
     */
    protected T importPublication() {
        final T publication = createPublication();

        processTitleAndName(publication);
                        
        publication.save();
        
        final Integer folderId = Publication.getConfig().getDefaultPublicationsFolder();
        final Folder folder = new Folder(new BigDecimal(folderId));
        publication.setContentSection(folder.getContentSection());
        publication.setLanguage(Kernel.getConfig().getLanguagesIndependentCode());

        publication.save();

        final PublicationBundle bundle = createBundle(publication);
        bundle.setParent(folder);
        bundle.setContentSection(folder.getContentSection());

        bundle.save();
        
        publication.setAbstract(data.getAbstract());
        publication.setMisc(data.getMisc());
        processReviewed(publication);
        processAuthors(publication);

        publication.save();
        
        return publication;
    }

    /**
     * Overwrite this method to create an instance of an publication type.
     *   
     * @return 
     */
    protected abstract T createPublication();

    protected abstract PublicationBundle createBundle(final T publication);

    private void processTitleAndName(final T publication) {
        publication.setTitle(data.getTitle());
        publication.setName(normalizeString(data.getTitle()));
        report.setTitle(publication.getTitle());
    }

    private void processReviewed(final T publication) {
        final String reviewedStr = data.getReviewed();
        final boolean reviewed;

        if ("y".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else if ("j".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else if ("yes".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else if ("ja".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else if ("t".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else if ("true".equalsIgnoreCase(reviewedStr)) {
            reviewed = true;
        } else {
            reviewed = false;
        }

        publication.setReviewed(reviewed);
        report.addField(new FieldImportReport("reviewed", Boolean.toString(reviewed)));
    }

    private void processAuthors(final T publication) {
        final List<AuthorData> authorsData = parseAuthors(data.getAuthors());

        AuthorImportReport authorReport;
        for (AuthorData authorData : authorsData) {
            authorReport = importerUtil.processAuthor(publication, authorData);
            report.addAuthor(authorReport);
        }
    }

    protected final String normalizeString(final String str) {
        if (str == null) {
            return "null";
        }
        return str.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").
                replace(
                "Ä", "Ae").replace("Ü", "Ue").replace("Ö", "Oe").replace("ß",
                                                                         "ss").
                replace(" ", "-").
                replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase().trim();
    }

    protected List<AuthorData> parseAuthors(final String authorsStr) {
        final List<AuthorData> authors = new ArrayList<AuthorData>();

        final String[] tokens = authorsStr.split(AUTHORS_SEP);
        for (String token : tokens) {
            parseAuthor(token, authors);
        }

        return authors;
    }

    private void parseAuthor(final String authorToken, final List<AuthorData> authors) {
        final String[] nameTokens = authorToken.split(AUTHOR_NAME_SEP);

        if (nameTokens.length == 1) {
            final AuthorData author = new AuthorData();
            author.setSurname(nameTokens[0]);
            authors.add(author);
        } else if (nameTokens.length == 2) {
            final AuthorData author = new AuthorData();
            author.setSurname(nameTokens[0]);
            if (nameTokens[1].endsWith(EDITOR_STR)) {
                author.setEditor(true);
                author.setGivenName(nameTokens[1].substring(0, nameTokens[1].length() - EDITOR_STR.length()));
            } else {
                author.setGivenName(nameTokens[1]);
            }
            authors.add(author);
        } else {
            final AuthorData author = new AuthorData();
            author.setSurname(nameTokens[0]);
            if (nameTokens[1].endsWith(EDITOR_STR)) {
                author.setEditor(true);
                author.setGivenName(nameTokens[1].substring(0, nameTokens[1].length() - EDITOR_STR.length()));
            } else {
                author.setGivenName(nameTokens[1]);
            }
            authors.add(author);
        }
    }

    private void assignCategories(final PublicationBundle publicationBundle) {
        if ((data.getScope() != null) && "Persönlich".equals(data.getScope())) {
            //Don't assign to a category, publications in only for personal profile
            return;
        }

        final String[] departments = data.getDepartment().split(",");

        final Category defaultCat = PublicationsImporter.getConfig().getDefaultCategory();
        if (defaultCat != null) {
            defaultCat.addChild(publicationBundle);
        }

        final Map<String, Category> depCats = PublicationsImporter.getConfig().getDepartmentCategories();
        Category category;
        for (String department : departments) {
            category = depCats.get(department);
            if (category != null) {
                category.addChild(publicationBundle);
            }
        }
    }

}
