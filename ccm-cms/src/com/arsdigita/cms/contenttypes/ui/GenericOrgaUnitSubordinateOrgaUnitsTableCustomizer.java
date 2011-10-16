package com.arsdigita.cms.contenttypes.ui;

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
    String getEmptyViewLabel();

    /**
     * 
     * @return Label for the column heading of the first column which shows
     * the titles of the subordinate organizational units.
     */
    String getNameColumnLabel();

    /**
     * @return The column heading for the second column which displays delete 
     * links for the associations.
     */
    String getDeleteColumnLabel();

    /**
     * 
     * @return Column heading for the column containing the {@code Up} links
     * for sorting the subordinate organizational units.
     */
    String getUpColumnLabel();

    /**
     * 
     * @return Column heading for the column containing the {@code Down} links
     * for sorting the subordinate organizational units.
     */
    String getDownColumnLabel();

    /**
     * 
     * @return Label for the delete links.
     */
    String getDeleteLabel();

    /**
     * 
     * @return Label for the up links
     */
    String getUpLabel();

    /**
     * 
     * @return Label for the down links
     */
    String getDownLabel();    

    /**
     * 
     * @return Text for the confirmation message when deleting an association.
     */
    String getConfirmRemoveLabel();
    
    /**
     * 
     * @return The value of the {@code assocType} property of the association
     * to filter for.  If this method returns something different from null
     * or the empty string, the collection of subordinate organizational units 
     * is filtered using the return value.
     */
    String getAssocType();
}
