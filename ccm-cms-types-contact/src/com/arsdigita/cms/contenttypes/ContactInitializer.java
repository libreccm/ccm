
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * Executes at each system startup and initializes the Agenda content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Sören Bernstein;
 * @version $Id: ContactInitializer.java 1596 2007-07-10 16:25:57Z p_boy $
 */
public class ContactInitializer extends ContentTypeInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(ContactInitializer.class);
    
    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public ContactInitializer() {
        super("ccm-cms-types-contact.pdl.mf",
              Contact.BASE_DATA_OBJECT_TYPE);
    }
    
    /**
     * Retrieves fully qualified traversal adapter file name.
     * @return 
     */
    @Override
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Contact.xml";
    }
    
    /**
     * Retrieve location of this content type's internal default theme 
     * stylesheet(s) which concomitantly serve as a fallback if a custom theme 
     * is engaged. 
     * 
     * Custom themes usually will provide their own stylesheet(s) and their own
     * access method, but may not support every content type.
     * 
     * Overwrites parent method with AgendaItem specific value for use by the 
     * parent class worker methods.
     * 
     * @return String array of XSL stylesheet files of the internal default theme
     */
    @Override
    public String[] getStylesheets() {
        return new String[] {
            INTERNAL_THEME_TYPES_DIR + "Contact.xsl"
        };
    }
    
}
