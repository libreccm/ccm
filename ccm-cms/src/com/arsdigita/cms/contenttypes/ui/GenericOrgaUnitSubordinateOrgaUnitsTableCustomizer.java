/*
 * Copyright (c) 2011 Jens Pelzetter
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

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Implementations of this class are used to customize an instance of
 * {@link GenericOrganizationalUnitSubordinateOrgaUnitTable}. Methods ending 
 * with {@code Label}, for example {@link #getEmptyViewLabel()} are supposed
 * to return the <em>localized</em> content of a label component. This means
 * that implementations of such methods will delegate to some globalization 
 * utility.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface GenericOrgaUnitSubordinateOrgaUnitsTableCustomizer {

    /**
     * @return The label used instead of an empty table.
     */
    GlobalizedMessage getEmptyViewLabel();

    /**
     * 
     * @return Label for the column heading of the first column which shows
     * the titles of the subordinate organizational units.
     */
    GlobalizedMessage getNameColumnLabel();

    /**
     * @return The column heading for the second column which displays delete 
     * links for the associations.
     */
    GlobalizedMessage getDeleteColumnLabel();

    /**
     * 
     * @return Column heading for the column containing the {@code Up} links
     * for sorting the subordinate organizational units.
     */
    GlobalizedMessage getUpColumnLabel();

    /**
     * 
     * @return Column heading for the column containing the {@code Down} links
     * for sorting the subordinate organizational units.
     */
    GlobalizedMessage getDownColumnLabel();

    /**
     * 
     * @return Label for the delete links.
     */
    GlobalizedMessage getDeleteLabel();

    /**
     * 
     * @return Label for the up links
     */
    GlobalizedMessage getUpLabel();

    /**
     * 
     * @return Label for the down links
     */
    GlobalizedMessage getDownLabel();

    /**
     * 
     * @return Text for the confirmation message when deleting an association.
     */
    GlobalizedMessage getConfirmRemoveLabel();

    /**
     * 
     * @return The value of the {@code assocType} property of the association
     * to filter for.  If this method returns something different from null
     * or the empty string, the collection of subordinate organizational units 
     * is filtered using the return value.
     */
    String getAssocType();

    /**
     * Content type to restrict to item shown to. May be {@code null}.
     * @return 
     */
    String getContentType();
}
