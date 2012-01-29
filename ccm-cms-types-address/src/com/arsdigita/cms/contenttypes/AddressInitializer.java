package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * Executes at each system startup and initializes the Address content type.
 * 
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @version $Id: AddressInitializer.java 1596 2007-07-10 16:25:57Z p_boy $
 */
public class AddressInitializer extends ContentTypeInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private static final Logger s_log = Logger.getLogger(AddressInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public AddressInitializer() {
        super("ccm-cms-types-address.pdl.mf",
              Address.BASE_DATA_OBJECT_TYPE);
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
        return new String[]{
                    "/themes/heirloom/contenttypes/Address.xsl"
                };
    }
}
