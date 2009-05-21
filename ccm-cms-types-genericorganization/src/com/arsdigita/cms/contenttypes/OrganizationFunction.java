/**
 * OrganizationFunction
 *
 * Part of GenericOrganization
 *
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import org.apache.log4j.Logger;


public class OrganizationFunction extends ContentPage {
    
    private static final Logger s_log = Logger.getLogger(OrganizationFunction.class);

    public static final String FUNCTIONNAME = "functionname";

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationFunction";

    public OrganizationFunction() {
	super(BASE_DATA_OBJECT_TYPE);
    }

    public OrganizationFunction(BigDecimal id) throws DataObjectNotFoundException {
	super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OrganizationFunction(String typeName) {
	super(typeName);
    }

    public OrganizationFunction(OID oid) throws DataObjectNotFoundException {
	super(oid);
    } 

    public OrganizationFunction(DataObject dataObject) {
	super(dataObject);
    }

    //Accessors
    public String getFunctionName() {
	return (String)get(FUNCTIONNAME);
    }

    public void setFunctionName(String functionName) {
	set(FUNCTIONNAME, functionName);
    }
}
