package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class JfullConverter extends JourConverter {

    @Override
    public RisType getRisType() {
        return RisType.JFULL;
    }

}
