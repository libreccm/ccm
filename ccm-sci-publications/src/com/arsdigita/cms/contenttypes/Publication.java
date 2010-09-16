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
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

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
 *
 *
 * @author Jens Pelzetter
 */
public class Publication extends ContentPage {

    public final static String YEAR_OF_PUBLICATION = "yearOfPublication";
    public final static String ABSTRACT = "abstract";
    public final static String MISC = "misc";
    public final static String AUTHORS = "authors";
    public final static String EDITOR = "editor";
    public final static String AUTHOR_ORDER = "authorOrder";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Publication";

    public Publication() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Publication(BigDecimal id) throws
            DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Publication(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Publication(DataObject obj) {
        super(obj);
    }

    public Publication(String type) {
        super(type);
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
    public void setYearOfPublication(Integer year) {
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
    public void setAbstract(String theAbstract) {
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
    public void addAuthor(GenericPerson author, Boolean editor) {
        Assert.exists(author, GenericPerson.class);

        DataObject link = add(AUTHORS, author);

        link.set(EDITOR, editor);
        link.set(AUTHOR_ORDER, Integer.valueOf((int) getAuthors().size()));
    }

    /**
     * Removes an author.
     *
     * @param author The author to remove.
     */
    public void removeAuthor(GenericPerson author) {
        Assert.exists(author, GenericPerson.class);
        remove(AUTHORS, author);
    }

    /**
     * Method to check if the publication has authors.
     *
     * @return {@code true} if the publications has authors, {@code false}
     * otherwise.
     */
    public boolean hasAuthors() {
        return !this.getAuthors().isEmpty();
    }
}
