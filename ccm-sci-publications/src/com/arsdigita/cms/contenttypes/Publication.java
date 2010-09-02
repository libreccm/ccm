/*
 * Copyright (c) 2010 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

    public Integer getYearOfPublication() {
        return (Integer) get(YEAR_OF_PUBLICATION);
    }

    public void setYearOfPublication(Integer year) {
        set(YEAR_OF_PUBLICATION, year);
    }

    public String getAbstract() {
        return (String) get(ABSTRACT);
    }

    public void setAbstract(String theAbstract) {
        set(ABSTRACT,
            theAbstract);
    }

    public String getMisc() {
        return (String) get(MISC);
    }

    public void setMisc(String misc) {
        set(MISC, misc);
    }

    public AuthorshipCollection getAuthors() {
        return new AuthorshipCollection((DataCollection) get(AUTHORS));
    }

    public void addAuthor(GenericPerson author, Boolean editor) {
        Assert.exists(author, GenericPerson.class);

        DataObject link = add(AUTHORS, author);

        link.set(EDITOR, editor);
        link.set(AUTHOR_ORDER, Integer.valueOf((int) getAuthors().size()));
    }

    public void removeAuthor(GenericPerson author) {
        Assert.exists(author, GenericPerson.class);
        remove(AUTHORS, author);
    }

    public boolean hasAuthors() {
        return !this.getAuthors().isEmpty();
    }
}
