package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class EchapConverter extends ChapConverter {
    
    @Override
    public RisType getRisType() {
        return RisType.ECHAP;
    }
    
}
