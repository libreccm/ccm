/*
 * Copyright (c) 2010 Jens Pelzetter,
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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 * <p>
 * A class for representing departments of an organization. Adds two 
 * associations to {@link GenericOrganizationalUnit}:
 * </p>
 * <ul>
 * <li>departments</li>
 * <li>projects</li>
 * </ul>
 * <p>
 * The <em>departments</em> association is used to link an organization with
 * its departments. The <em>project</em> association can be used to link an 
 * organization with projects.
 * </p>
 * <p>
 * Also, two fields are added. The <code>shortDescription</code> takes a string
 * of 500 characters for a short description of the organization. An detailed 
 * description of the organization can be put into the <code>description</code>
 * field.
 * </p>
 * <p>
 * There is an
 * <a href="doc-files/ccm-sci-types-organization_entities.png">UML digram</a>
 * with an overview of the content types and their associations. Please not that
 * the diagram show a design sketch and not the actually implemented classes.
 * </p>
 *
 * @author Jens Pelzetter
 * @see GenericOrganizationalUnit
 * @see SciDepartment
 * @see SciProject
 */
public class SciOrganization extends GenericOrganizationalUnit {

    private static final Logger logger = Logger.getLogger(SciOrganization.class);
    public static final String ORGANIZATION_SHORT_DESCRIPTION =
                               "organizationShortDescription";
    public static final String ORGANIZATION_DESCRIPTION =
                               "organizationDescription";
    public static final String DEPARTMENTS = "departments";
    public static final String DEPARTMENT_ORDER = "departmentOrder";
    public static final String PROJECTS = "projects";
    public static final String PROJECT_ORDER = "projectOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciOrganization";
    private static final SciOrganizationConfig s_config =
                                               new SciOrganizationConfig();

    static {
        logger.debug("Static initalizer starting...");
        s_config.load();
        logger.debug("Static initalizer finished.");
    }

    public enum MemberStatus {

        ALL,
        ACTIVE,
        ASSOCIATED,
        FORMER
    }

    public enum ProjectStatus {

        ALL,
        ONGOING,
        FINISHED
    }

    public SciOrganization() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciOrganization(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciOrganization(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciOrganization(DataObject obj) {
        super(obj);
    }

    public SciOrganization(String type) {
        super(type);
    }

    public static SciOrganizationConfig getConfig() {
        return s_config;
    }

    public String getOrganizationShortDescription() {
        return (String) get(ORGANIZATION_SHORT_DESCRIPTION);
    }

    public void setOrganizationShortDescription(String description) {
        set(ORGANIZATION_SHORT_DESCRIPTION, description);
    }

    public String getOrganizationDescription() {
        return (String) get(ORGANIZATION_DESCRIPTION);
    }

    @Override
    public boolean hasContacts() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfContactsOfSciOrganization");
        query.setParameter("organization", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }

    public boolean hasDepartments() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfDepartmentsOfSciOrganization");
        query.setParameter("organization", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }

    /**
     * 
     * @param merge Should I also look into the departments and return true
     * if the organization or at least one of the departments has members?
     * @param status 
     * @return 
     */
    public boolean hasMembers(final boolean merge, final MemberStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfSciOrganization";
                break;
            case ACTIVE:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfActiveMembersOfSciOrganization";
                break;
            case ASSOCIATED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfSciOrganization";
                break;
            case FORMER:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFormerMembersOfSciOrganization";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("organization", getID());

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery departmentsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfDepartmentsOfSciOrganization");
                departmentsQuery.setParameter("organization", getID());
                                
                if (departmentsQuery.size() > 0) {
                    BigDecimal departmentId;
                    boolean result = false;
                    while (departmentsQuery.next()) {
                        departmentId = (BigDecimal) departmentsQuery.get(
                                "departmentId");
                        result = hasMembers(departmentId, merge, status);

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

    private boolean hasMembers(final BigDecimal departmentId,
                               final boolean merge,
                               final MemberStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfSciDepartment";
                break;
            case ACTIVE:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfActiveMembersOfSciDepartment";
                break;
            case ASSOCIATED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfDepartment";
                break;
            case FORMER:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFormerMembersOfSciDepartment";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
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

                if (query.size() > 0) {
                    BigDecimal subDepartmentId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subDepartmentId = (BigDecimal) subDepartmentsQuery.get(
                                "departmentId");
                        result = hasMembers(subDepartmentId, merge, status);

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

    public boolean hasProjects(final boolean merge,
                               final ProjectStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfProjectsOfSciOrganization";
                break;
            case ONGOING:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfOngoingProjectsOfSciOrganization";
                break;
            case FINISHED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFinishedProjectsOfSciOrganization";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("organization", getID());
        if (status != ProjectStatus.ALL) {
            Calendar today = new GregorianCalendar();
            query.setParameter("today",
                               String.format("%d-%02d-%02d",
                                             today.get(Calendar.YEAR),
                                             today.get(Calendar.MONTH) + 1,
                                             today.get(Calendar.DAY_OF_MONTH)));
        }

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery departmentsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfDepartmentsOfSciOrganization");
                departmentsQuery.setParameter("organization", getID());

                if (departmentsQuery.size() > 0) {
                    BigDecimal departmentId;
                    boolean result = false;
                    while (departmentsQuery.next()) {
                        departmentId = (BigDecimal) departmentsQuery.get(
                                "departmentId");
                        result = hasProjects(departmentId, merge, status);

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

    private boolean hasProjects(final BigDecimal departmentId,
                               final boolean merge,
                               final ProjectStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfProjectsOfSciDepartment";
                break;
            case ONGOING:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfOngoingProjectsOfSciDepartment";
                break;
            case FINISHED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFinishedProjectsOfSciDepartment";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("department", departmentId);
        if (status != ProjectStatus.ALL) {
            Calendar today = new GregorianCalendar();
            query.setParameter("today",
                               String.format("%d-%02d-%02d",
                                             today.get(Calendar.YEAR),
                                             today.get(Calendar.MONTH) + 1,
                                             today.get(Calendar.DAY_OF_MONTH)));

        }

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
                                "departmentId");
                        result = hasProjects(subDepartmentId, merge, status);

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

    /*public boolean hasPublications() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationLinksOfSciOrganization");
        query.setParameter("organization", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }*/

    public void setOrganizationDescription(String description) {
        set(ORGANIZATION_DESCRIPTION, description);
    }

    public SciOrganizationDepartmentsCollection getDepartments() {
        return new SciOrganizationDepartmentsCollection(
                (DataCollection) get(DEPARTMENTS));
    }

    public void addDepartment(SciDepartment department) {
        Assert.exists(department, SciDepartment.class);

        DataObject link = add(DEPARTMENTS, department);

        link.set(DEPARTMENT_ORDER,
                 Integer.valueOf((int) getDepartments().size()));
        link.save();
    }

    public void removeDepartment(SciDepartment department) {
        Assert.exists(department, SciDepartment.class);

        remove(DEPARTMENTS, department);
    }

    public SciOrganizationProjectsCollection getProjects() {
        return new SciOrganizationProjectsCollection(
                (DataCollection) get(PROJECTS));
    }

    public void addProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        DataObject link = add(PROJECTS, project);
        link.set(PROJECT_ORDER, Integer.valueOf((int) getProjects().size()));
        link.set(SciProject.ORGANIZATIONS_ORDER, Integer.valueOf((int) project.
                getOrganizations().size()));
        link.save();
    }

    public void removeProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        remove(PROJECTS, project);
    }
   
}
