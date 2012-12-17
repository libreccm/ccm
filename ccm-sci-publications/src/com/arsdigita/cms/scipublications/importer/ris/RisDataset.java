package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisDataset {
    
    private final RisType type;
    private final Map<RisField, List<String>> values = new EnumMap<RisField, List<String>>(RisField.class);
    
    public RisDataset(final RisType type) {
        this.type = type;
    }
    
    public RisType getType() {
        return type;
    }
    
    public Map<RisField, List<String>> getValues() {
        return Collections.unmodifiableMap(values);
    }
    
}
