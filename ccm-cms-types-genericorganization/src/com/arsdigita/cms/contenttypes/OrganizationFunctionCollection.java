/**
 * OrganizationFunctionCollection
 */ 

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import org.apache.log4j.Logger;

/**
 * @author Jens Pelzetter
 */
public class OrganizationFunctionCollection extends DomainCollection {

    public OrganizationFunctionCollection(DataCollection collection) {
	super(collection);
    }

    public final String getFunctionName() {
	return (String)getOrganizationFunction().getFunctionName();
    }

    public OrganizationFunction getOrganizationFunction() {
	//return (OrganizationFunction)getDomainObject();
	return new OrganizationFunction(m_dataCollection.getDataObject());
    }

}