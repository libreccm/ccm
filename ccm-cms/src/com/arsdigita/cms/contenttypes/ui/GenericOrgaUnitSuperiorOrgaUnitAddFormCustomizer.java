package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public interface  GenericOrgaUnitSuperiorOrgaUnitAddFormCustomizer {
    
     /**
     * 
     * @return Label for the item search widget of the add from.
     */
    GlobalizedMessage getSelectSuperiorOrgaUnitLabel();

    /**
     * 
     * @return Can be used to limit the content items shown by the item search
     * widget to a specific type.
     */
    String getSuperiorOrgaUnitType();

    /**
     * 
     * @return The value for the {@code assocType} field. When adding a
     * new superior organizational unit, the {@code assocType} field of
     * the new association will be set the value returned by this method.
     */
    String getAssocType();

    /**
     * 
     * @return Message to show if the save button has been hit without selecting
     * an item.
     */
    GlobalizedMessage getNothingSelectedMessage();

    /**
     * 
     * @return Message to show if the selected item has no suitable language
     * variant.
     */
    GlobalizedMessage getNoSuitableLanguageVariantMessage();
    
    /**
     * 
     * @return Message to show if the selected item is the same as the current
     * item.
     */
    GlobalizedMessage getAddingToItselfMessage();
    
    /**
     * 
     * @return Message to show if the selected item has already been added 
     * as a superior organizational unit.
     */
    GlobalizedMessage getAlreadyAddedMessage();
    
}
