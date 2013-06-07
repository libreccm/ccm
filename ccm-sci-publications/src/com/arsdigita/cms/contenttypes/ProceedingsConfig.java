/*
 * Copyright (c) 2010 Jens Pelzetter
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
