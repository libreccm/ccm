package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.SimpleOrganizationExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SimpleOrganization extends GenericOrganizationalUnit {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.SimpleOrganization";
    
    public SimpleOrganization() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public SimpleOrganization(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public SimpleOrganization(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
    
    public SimpleOrganization(final DataObject dobj) {
        super(dobj);
    }
    
    public SimpleOrganization(final String type) {
        super(type);
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new SimpleOrganizationExtraXmlGenerator());
        return generators;
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new SimpleOrganizationExtraXmlGenerator());
        return generators;
    }
}
