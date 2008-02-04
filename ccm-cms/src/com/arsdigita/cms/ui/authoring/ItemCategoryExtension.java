package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.SimpleComponent;

/**
 * NOOP base implementation of category authoring step extension.
 * Summary component returned by #getSummary() is show on category summary page,
 * usually an ActionLink which activates the Form component returned by #getForm().
 * 
 * @author Alan Pevec
 */
public class ItemCategoryExtension {

    public SimpleComponent[] getSummary() {
        return new SimpleComponent[0]; 
    }
    
    public SimpleComponent[] getForm() {
        return new SimpleComponent[0];
    }

}
