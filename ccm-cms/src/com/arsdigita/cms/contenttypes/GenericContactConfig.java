
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.StringParameter;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * Stores the configuration record for the generic contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@zes.uni-bremen.de
 */
public final class GenericContactConfig extends AbstractConfig {
    
    private static Logger s_log = Logger.getLogger(GenericContactConfig.class);

    private final Parameter m_hidePerson;
    private final Parameter m_hideAddress;
    private final Parameter m_hideAddressPostalCode;
    private final Parameter m_hideAddressState;
    private final Parameter m_hideAddressCountry;
    private final Parameter m_contactEntryKeys;
    
    /**
     * Public Constructor
     */
    public GenericContactConfig() {

        /**
         * If set to true disables the possibility to attach a person ct
         */
        m_hidePerson = new BooleanParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.hide_person",
             Parameter.REQUIRED, 
             new Boolean(false));

        /**
         * If set to true disables the possibility to attach a baseAddress ct
         */
        m_hideAddress = new BooleanParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.hide_address",
             Parameter.REQUIRED, 
             new Boolean(false));

        /**
         *  If set to true hides the postal code entry field for the attached address ct
         */
        m_hideAddressPostalCode = new BooleanParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.address.hide_postal_code",
             Parameter.REQUIRED, 
             new Boolean(false));

        /**
         *  If set to true hides the state entry field for the attached address ct
         */
        m_hideAddressState = new BooleanParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.address.hide_state",
             Parameter.REQUIRED, 
             new Boolean(false));

        /**
         * If set to true hides the country selection for the attaches address ct
         */
        m_hideAddressCountry = new BooleanParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.address.hide_country",
             Parameter.REQUIRED, 
             new Boolean(false));

        /**
         */
        m_contactEntryKeys = new StringParameter
            ("com.arsdigita.cms.contenttypes.generic_contact.contact_entry_keys",
             Parameter.REQUIRED,
             "contact_type,office_hours,phone_office,phone_private,phone_mobile,email,fax,im,www");

        register(m_hidePerson);
        register(m_hideAddress);
        register(m_hideAddressPostalCode);
        register(m_hideAddressState);
        register(m_hideAddressCountry);
        register(m_contactEntryKeys);

        loadInfo();
    }

    /**
     */
    public final boolean getHidePerson() {
        return ((Boolean) get(m_hidePerson)).booleanValue();
    }

    /**
     */
    public final boolean getHideAddress() {
        return ((Boolean) get(m_hideAddress)).booleanValue();
    }

    /**
     */
    public final boolean getHideAddressPostalCode() {
        return ((Boolean) get(m_hideAddressPostalCode)).booleanValue();
    }

    /**
     */
    public final boolean getHideAddressState() {
        return ((Boolean) get(m_hideAddressState)).booleanValue();
    }

    /**
     */
    public final boolean getHideAddressCountry() {
        return ((Boolean) get(m_hideAddressCountry)).booleanValue();
    }

    /**
     * Returns the generic_contactEntryKeys as StringTokenizer.
     */
    public final StringTokenizer getContactEntryKeys() {
        return new StringTokenizer((((String) get(m_contactEntryKeys)).replace(" ","")), ",", false);
    }
    
    /**
     * Return true, if language lang is part of supported langs
     */
    public final boolean hasKey(String key) {
        return ((String) get(m_contactEntryKeys)).contains(key);
    }
    
    /**
     * Return the index value for given key
     */
    public final int getKeyIndex(String key) {
        int index = -1;
        
        if(hasKey(key)) {    
            StringTokenizer keys = getContactEntryKeys();
            while(keys.hasMoreElements()) {
                index++;
                if(keys.nextToken().equals(key)) {
                    break;
                }
            }
        }
        
        return index;
    }
}