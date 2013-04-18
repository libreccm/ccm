package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;

/** 
 * Special configuration file for the {@link Proceedings} type. The parameters in this configuration
 * can be used to decide if some fields of the {@link Proceedings} type are mandatory.
 *   
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsConfig extends AbstractConfig {

    private final Parameter beginOfConferenceMandatory;
    private final Parameter endOfConferenceMandatory;
    private final Parameter placeOfConferenceMandatory;

    public ProceedingsConfig() {
        super();
        
        beginOfConferenceMandatory = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.proceedings.beginOfConferenceIsMandatory",
                Parameter.REQUIRED,
                Boolean.TRUE);

        endOfConferenceMandatory = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.proceedings.endOfConferenceIsMandatory",
                Parameter.REQUIRED,
                Boolean.TRUE);

        placeOfConferenceMandatory = new BooleanParameter(
                "com.arsdigita.cms.contenttypes.publications.proceedings.placeOfConferenceIsMandatory",
                Parameter.REQUIRED,
                Boolean.TRUE);

        register(beginOfConferenceMandatory);
        register(endOfConferenceMandatory);
        register(placeOfConferenceMandatory);
        
        loadInfo();
    }
    
    public Boolean isBeginOfConferenceMandatory() {
        return (Boolean) get(beginOfConferenceMandatory);
    }
    
    public Boolean isEndOfConferenceMandatory() {
        return (Boolean) get(endOfConferenceMandatory);
    }
    
    public Boolean isPlaceOfConferenceMandatory() {
        return (Boolean) get(placeOfConferenceMandatory);
    }
}
