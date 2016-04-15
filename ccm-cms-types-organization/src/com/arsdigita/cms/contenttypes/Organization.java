package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.OrganizationExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 */
public class Organization extends GenericOrganizationalUnit {
    
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Organization";
    
    public Organization() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public Organization(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public Organization(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
    
    public Organization(final DataObject dobj) {
        super(dobj);
    }
    
    public Organization(final String type) {
        super(type);
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new OrganizationExtraXmlGenerator());
        return generators;
    }
    
    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new OrganizationExtraXmlGenerator());
        return generators;
    }
}
