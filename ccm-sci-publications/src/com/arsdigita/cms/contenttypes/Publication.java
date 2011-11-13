/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * This is the base class for all other Publication Content types. The
 * following UML class diagram shows an overview of the classes of the 
 * Publications module. Please note that the UML diagram shown an general 
 * overview of the classes/object types of the publications module. It shows
 * the attributes of the content types and theirs associations between them. Not
 * all classes shown in the UML have a Java counterpart. These classes are
 * representing associations <em>with</em> extra attributes. The associations
 * are defined in the PDL files of this module.
 * </p>
 * <p>
 * <img src="doc-files/PublicationModule.png" width="100%">
 * </p>
 * <p>
 * This class is not a directly usable Content type. Its is only used to
 * define some common attributes needed for all kinds of publications.
 * </p>
 * <p>
 * As of version 6.6.1 of this module, the reviewed property has been moved 
 * from the types {@link ArticleInCollectedVolume}, {@link ArticleInJournal}, 
 * {@link CollectedVolume}, {@link Monograph} and {@link WorkingPaper} to this
 * class. This has been done for performance reasons. Several use cases demanded
 * the use of a data query (for performance reasons) over all publications with
 * the option to filter for reviewed publications. Since the reviewed property
 * was only available for some types this was not possible, even with joins.
 * The publications types which do not need the reviewed property will not show
 * this property in their forms. Also, the reviewed property was excluded from
 * the XML of these types using their traversal adapters.
 * </p>
 *
 *
 * @author Jens Pelzetter
 */
public class Publication extends ContentPage {

    public final static String YEAR_OF_PUBLICATION = "yearOfPublication";
    public final static String ABSTRACT = "abstract";
    public final static String MISC = "misc";
    public final static String AUTHORS = "authors";
    private final static String AUTHORS_STR = "authorsStr"; //authorsStr is an interal field only for use in data queries, and is updated automatically
    public final static String EDITOR = "editor";
    public final static String AUTHOR_ORDER = "authorOrder";
    public final static String SERIES = "series";
    public final static String ORGAUNITS = "orgaunits";
    public final static String ORGAUNIT_PUBLICATIONS = "publications";
    public static final String REVIEWED = "reviewed";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Publication";
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public Publication() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Publication(final BigDecimal id) throws
            DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Publication(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Publication(final DataObject obj) {
        super(obj);
    }

    public Publication(final String type) {
        super(type);
    }

    public static PublicationsConfig getConfig() {
        return config;
    }

    /**
     * Gets the year of publications.
     *
     * @return Year of publication
     */
    public Integer getYearOfPublication() {
        return (Integer) get(YEAR_OF_PUBLICATION);
    }

    /**
     * Sets the year of publication
     *
     * @param year The year when the publication was published.
     */
    public void setYearOfPublication(final Integer year) {
        set(YEAR_OF_PUBLICATION, year);
    }

    /**
     * Retrieves the abstract of the publication.
     *
     * @return Abstract of the publication, if any.
     */
    public String getAbstract() {
        return (String) get(ABSTRACT);
    }

    /**
     * Sets the abstract of the publication.
     *
     * @param theAbstract A string describing the contents of the publication
     */
    public void setAbstract(final String theAbstract) {
        set(ABSTRACT,
            theAbstract);
    }

    /**
     * Retrieves the contents of the misc field. This field can be used for
     * all sorts of remarks etc. which do not fit in the other fields.
     *
     * @return Contents of the misc field.
     */
    public String getMisc() {
        return (String) get(MISC);
    }

    /**
     * Sets teh content of the misc field.
     *
     * @param misc The new content of the misc field.
     */
    public void setMisc(String misc) {
        set(MISC, misc);
    }

    public Boolean getReviewed() {
        return (Boolean) get(REVIEWED);
    }

    public void setReviewed(Boolean reviewed) {
        set(REVIEWED, reviewed);
    }

    /**
     * Retrieves a collection of the authors of the publication.
     *
     * @return Collection of the authors of the publication.
     */
    public AuthorshipCollection getAuthors() {
        return new AuthorshipCollection((DataCollection) get(AUTHORS));
    }

    /**
     * Adds an author to the publication
     *
     * @param author The author to add. This can an instance of any content type
     * which is derivated from the {@link GenericPerson} type.
     * @param editor Is the author an editor?
     */
    public void addAuthor(final GenericPerson author, final Boolean editor) {
        Assert.exists(author, GenericPerson.class);

        DataObject link = add(AUTHORS, author);

        link.set(EDITOR, editor);
        link.set(AUTHOR_ORDER, Integer.valueOf((int) getAuthors().size()));
        
        updateAuthorsStr();
    }

    /**
     * Removes an author.
     *
     * @param author The author to remove.
     */
    public void removeAuthor(final GenericPerson author) {
        Assert.exists(author, GenericPerson.class);
        remove(AUTHORS, author);
        
        updateAuthorsStr();
    }
    
    public void swapWithPreviousAuthor(final GenericPerson author) {
        getAuthors().swapWithPrevious(author);
        updateAuthorsStr();
    }
    
    public void swapWithNextAuthor(final GenericPerson author) {
        getAuthors().swapWithNext(author);
        updateAuthorsStr();
    }
    
    protected void updateAuthorsStr() {
        final AuthorshipCollection authors = getAuthors();
        StringBuilder builder = new StringBuilder();
        while(authors.next()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(authors.getSurname());
            builder.append(", ");
            builder.append(authors.getGivenName());
        }
        set(AUTHORS_STR, builder.toString());        
    }

    /**
     * Method to check if the publication has authors.
     *
     * @return {@code true} if the publications has authors, {@code false}
     * otherwise.
     */
    public boolean hasAuthors() {
        return !getAuthors().isEmpty();
    }

    public SeriesCollection getSeries() {
        return new SeriesCollection((DataCollection) get(SERIES));
    }

    public void addSeries(final Series series) {
        Assert.exists(series, Series.class);

        add(SERIES, series);
    }

    public void removeSeries(final Series series) {
        Assert.exists(series, Series.class);

        remove(SERIES, series);
    }

    public boolean hasSeries() {
        return !getSeries().isEmpty();
    }

    public PublicationGenericOrganizationalsUnitCollection getOrganizationalUnits() {
        return new PublicationGenericOrganizationalsUnitCollection((DataCollection) get(
                ORGAUNITS));
    }

    public void addOrganizationalUnit(final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        add(ORGAUNITS, orgaunit);
    }

    public void removeOrganizationalUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        remove(ORGAUNITS, orgaunit);
    }

    public boolean hasOrganizationalUnits() {
        return !getOrganizationalUnits().isEmpty();
    }

    public static GenericOrganizationalUnitPublicationsCollection getPublications(
            final GenericOrganizationalUnit orgaunit) {
        final DataCollection dataCollection = (DataCollection) orgaunit.get(
                ORGAUNIT_PUBLICATIONS);

        return new GenericOrganizationalUnitPublicationsCollection(
                dataCollection);
    }

    public static void addPublication(final GenericOrganizationalUnit orgaunit,
                                      final Publication publication) {
        Assert.exists(publication);

        orgaunit.add(ORGAUNIT_PUBLICATIONS, publication);
    }

    public static void removePublication(
            final GenericOrganizationalUnit orgaunit,
            final Publication publication) {
        Assert.exists(publication);

        orgaunit.remove(ORGAUNIT_PUBLICATIONS, publication);
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();        
        generators.add(new SciPublicationExtraXmlGenerator());        
        return generators;
    }
}
