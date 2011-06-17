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

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentWithPublications extends SciDepartment {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciDepartmentWithPublications";
    public static final String PUBLICATIONS = "publications";

    public SciDepartmentWithPublications() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciDepartmentWithPublications(final BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciDepartmentWithPublications(final OID oid) {
        super(oid);
    }

    public SciDepartmentWithPublications(final DataObject dobj) {
        super(dobj);
    }

    public SciDepartmentWithPublications(final String type) {
        super(type);

    }

    public boolean hasPublications(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsOfSciDepartment");
        query.setParameter("department", getID());

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
                                "subDepartmentId");
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
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationsOfSciDepartment");
        query.setParameter("department", departmentId);

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

                if (subDepartmentsQuery.size() > 0) {
                    BigDecimal subDepartmentId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subDepartmentId = (BigDecimal) subDepartmentsQuery.get(
                                "subDepartmentId");
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

    public boolean hasWorkingPapers(final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfWorkingPapersOfSciDepartment");
        query.setParameter("department", getID());

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
                                "subDepartmentId");
                        result = hasWorkingPapers(departmentId, merge);

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
    
    private boolean hasWorkingPapers(final BigDecimal departmentId,
                                     final boolean merge) {
        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfWorkingPapersOfSciDepartment");
        query.setParameter("department", departmentId);

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

                if (subDepartmentsQuery.size() > 0) {
                    BigDecimal subDepartmentId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subDepartmentId = (BigDecimal) subDepartmentsQuery.get(
                                "subDepartmentId");
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

    public SciDepartmentPublicationsCollection getPublications() {
        return new SciDepartmentPublicationsCollection((DataCollection) get(
                PUBLICATIONS));
    }

    public void addPublication(final Publication publication) {
        Assert.exists(publication, Publication.class);

        DataObject link = add(PUBLICATIONS, publication);
        link.set("publicationOrder", Integer.valueOf((int) getPublications().
                size()));
        link.save();
    }

    public void removePublication(final Publication publication) {
        Assert.exists(publication, Publication.class);

        remove(PUBLICATIONS, publication);
    }
}
