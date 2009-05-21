/**
 * OrganizationFunction
 *
 * Part of GenericOrganization
 *
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.OID;
import org.apache.log4j.Logger;


public class OrganizationFunction extends DomainObject {
    
    private static final Logger s_log = Logger.getLogger(OrganizationFunction.class);

    public static final String FUNCTIONNAME = "functionname";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationFunction";

    public OrganizationFunction(String typeName) {
	super(typeName);
    }

    public OrganizationFunction(ObjectType type) {
	super(type);
    }

    public OrganizationFunction(OID oid) {
	super(oid);
    }

    public OrganizationFunction(DataObject dataObject) {
	super(dataObject);
    }

    public String getFunctionName() {
	return (String)get(FUNCTIONNAME);
    }

    public void setFunctioName(String functionName) {
	set(FUNCTIONNAME, functionName);
    }
}
