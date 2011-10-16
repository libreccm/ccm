package com.arsdigita.cms.contenttypes.ui;

/**
 * Implementations of this class are used to customize an instance of
 * {@link GenericOrganizationalUnitSubordinateOrgaUnitAddForm}. Methods ending 
 * with {@code Label} or {@code Message}, for example 
 * {@link #getEmptyViewLabel()} are supposed
 * to return the <em>localized</em> content of a label component. This means
 * that implementations of such methods will delegate to some globalization 
 * utility.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface GenericOrgaUnitSubordinateOrgaUnitAddFormCustomizer {
    
    /**
     * 
     * @return Label for the item search widget of the add from.
     */
    String getSelectSubordinateOrgaUnitLabel();

    /**
     * 
     * @return Can be used to limit the content items shown by the item search
     * widget to a specific type.
     */
    String getSubordinateOrgaUnitType();

    /**
     * 
     * @return The value for the {@code assocType} field. When adding a
     * new subordinate organizational unit, the {@code assocType} field of
     * the new association will be set the value returned by this method.
     */
    String getAssocType();

    /**
     * 
     * @return Message to show if the save button has been hit without selecting
     * an item.
     */
    String getNothingSelectedMessage();

    /**
     * 
     * @return Message to show if the selected item has no suitable language
     * variant.
     */
    String getNoSuitableLanguageVariantMessage();
    
    /**
     * 
     * @return Message to show if the selected item is the same as the current
     * item.
     */
    String getAddingToItselfMessage();
    
    /**
     * 
     * @return Message to show if the selected item has already been added 
     * as a subordinate organizational unit.
     */
    String getAlreadyAddedMessage();
    
}
