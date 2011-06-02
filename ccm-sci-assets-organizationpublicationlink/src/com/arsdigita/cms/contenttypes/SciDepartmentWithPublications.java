/*
 * Copyright (c) 2011 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contentassets.SciOrganizationPublicationCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentWithPublications extends SciDepartment {
    
    private GenericOrganizationalUnitWithPublications departmentWithPublications;
    
    private SciDepartmentWithPublications() {
    }
    
    private SciDepartmentWithPublications(final BigDecimal id) {
    }
    
    private SciDepartmentWithPublications(final OID oid) {
    }
    
    private SciDepartmentWithPublications(final DataObject dobj) {
    }
    
    private SciDepartmentWithPublications(final String type) {
    }
    
    public SciDepartmentWithPublications(final SciDepartment department) {
        departmentWithPublications =
        new GenericOrganizationalUnitWithPublications(department.getID());
    }
    
    public boolean hasPublications(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getIdsOfPublicationsOfSciDepartment");
        query.setParameter("departmentId", getID());
        
        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery departmentsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubDepartmentsOfSciDepartment");
                departmentsQuery.setParameter("department", getID());
                
                if (departmentsQuery.size() > 0) {
                    BigDecimal departmentId;
                    boolean result = false;
                    while (departmentsQuery.next()) {
                        departmentId = (BigDecimal) departmentsQuery.get(
                                "departmentId");
                        result = hasPublications(departmentId, merge);
                        
                        if (result) {
                            break;
                        }
                    }
                    
                    departmentsQuery.close();
                    return result;
                } else {
                    departmentsQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }
    
    private boolean hasPublications(final BigDecimal departmentId,
                                    final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getIdsOfPublicationsOfSciDepartment");
        query.setParameter("departmentId", departmentId);
        
        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery subDepartmentsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubDepartmentsOfSciDepartment");
                subDepartmentsQuery.setParameter("department", departmentId);
                
                if (query.size() > 0) {
                    BigDecimal subDepartmentId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subDepartmentId = (BigDecimal) subDepartmentsQuery.get(
                                "departmentId");
                        result = hasPublications(subDepartmentId, merge);
                        
                        if (result) {
                            break;
                        }
                    }
                    
                    subDepartmentsQuery.close();
                    return result;                    
                } else {
                    subDepartmentsQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }
    
    public SciOrganizationPublicationCollection getPublications() {
        return departmentWithPublications.getPublications();
    }
}
