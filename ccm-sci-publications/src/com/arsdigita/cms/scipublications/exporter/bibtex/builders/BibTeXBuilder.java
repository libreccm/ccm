/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import com.arsdigita.cms.contenttypes.GenericPerson;

/**
 * Interface implemented by all <code>BibTeXBuilder</code>.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public interface BibTeXBuilder {

    /**
     * Add an author to the BibTeX reference.
     *
     * @param author The author to add.
     */
    void addAuthor(GenericPerson author);

    /**
     * Add an editor to the BibTeX reference.
     *
     * @param editor The editor to add.
     */
    void addEditor(GenericPerson editor);

    /**
     * Add a field to the BibTeX reference.
     *
     * @param name The name of the field.
     * @param value The value of the field.
     * @throws UnsupportedFieldException If the field is not supported by the
     * BibTeX reference type provided by the implementing builder.
     */
    void setField(BibTeXField name, String value) throws
            UnsupportedFieldException;

    /**
     *
     * @return The BibTeX reference type provided by this builder.
     */
    String getBibTeXType();

    /**
     *
     * @return Converts the data passed to the builder to a string formatted
     * according to the BibTeX format.
     */
    String toBibTeX();
}
