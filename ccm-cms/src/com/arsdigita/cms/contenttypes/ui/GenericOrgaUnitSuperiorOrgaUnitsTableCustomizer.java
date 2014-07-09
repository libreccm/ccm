package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface GenericOrgaUnitSuperiorOrgaUnitsTableCustomizer {
    
      /**
     * @return The label used instead of an empty table.
     */
    GlobalizedMessage getEmptyViewLabel();

    /**
     * 
     * @return Label for the column heading of the first column which shows
     * the titles of the superior organizational units.
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
     * for sorting the superior organizational units.
     */
    GlobalizedMessage getUpColumnLabel();

    /**
     * 
     * @return Column heading for the column containing the {@code Down} links
     * for sorting the superior organizational units.
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
     * or the empty string, the collection of superior organizational units 
     * is filtered using the return value.
     */
    String getAssocType();
    
    /**
     * Content type to restrict to item shown to. May be {@code null}.
     * @return 
     */
    String getContentType();
    
}
