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
import com.arsdigita.domain.DomainObjectFactory;
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
 * <li>Subdepartments</li>
 * <li>Projects</li>
 * </ul>
 * <p>
 * The <em>subdepartments</em> association links one instance of this class 
 * with another.
 * The <em>projects</em> association links an department with projects.
 * </p>
 * <p>
 * Also, there are two additional fields:
 * </p>
 * <ul>
 * <li>shortDescription</li>
 * <li>description</li>
 * </ul>
 * <p>
 * <em>shortDescription</em> takes a short text (maximum length 500 characters)
 * about the department. For more detailed description, the <em>description</em>
 * field can be used.
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
 * @see SciOrganization
 * @see SciProject
 */
public class SciDepartment extends GenericOrganizationalUnit {

    private static final Logger logger = Logger.getLogger(SciDepartment.class);
    public static final String DEPARTMENT_SHORT_DESCRIPTION =
                               "departmentShortDescription";
    public static final String DEPARTMENT_DESCRIPTION = "departmentDescription";
    public static final String ORGANIZATION = "organization";
    public static final String SUPER_DEPARTMENT = "superDepartment";
    public static final String SUBDEPARTMENTS = "subDepartments";
    public static final String SUBDEPARTMENT_ORDER = "subDepartmentOrder";
    public static final String PROJECTS = "projects";
    public static final String PROJECT_ORDER = "projectOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciDepartment";
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

    public SciDepartment() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciDepartment(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciDepartment(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciDepartment(DataObject obj) {
        super(obj);
    }

    public SciDepartment(String type) {
        super(type);
    }

    public static final SciOrganizationConfig getConfig() {
        return s_config;
    }

    public String getDepartmentShortDescription() {
        return (String) get(DEPARTMENT_SHORT_DESCRIPTION);
    }

    public void setDepartmentShortDescription(String description) {
        set(DEPARTMENT_SHORT_DESCRIPTION, description);
    }

    public String getDepartmentDescription() {
        return (String) get(DEPARTMENT_DESCRIPTION);
    }

    public void setDepartmentDescription(String description) {
        set(DEPARTMENT_DESCRIPTION, description);
    }

    public SciDepartment getSuperDepartment() {
        /*This is some sort of hack because if we define the
         * relation in PDL with a multiplicity of 0..1 for the super department
         * part, CCM crashes when trying to publish the item. So we have to
         * define both parts of the association with 0..n in PDL. But we don't
         * want more than one superior department per department. So we are
         * retrieving the data collection and get the first element of it, and
         * return this.
         */
        DataCollection collection;

        collection = (DataCollection) get(SUPER_DEPARTMENT);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (SciDepartment) DomainObjectFactory.newInstance(dobj);
        }
    }

    public void setSuperDepartment(SciDepartment department) {
        /* Similar hack as in getSuperDepartment() above. Here, we are
         * retrieving the data collection, removing the first (and only item)
         * and put the new one in it.
         *
         */
        SciDepartment oldSuperDepartment;

        oldSuperDepartment = getSuperDepartment();
        if (oldSuperDepartment != null) {
            remove(SUPER_DEPARTMENT, oldSuperDepartment);
        }

        if (null != department) {
            Assert.exists(department, SciDepartment.class);
            DataObject link = add(SUPER_DEPARTMENT, department);
            link.set(SUBDEPARTMENT_ORDER,
                     Integer.valueOf((int) department.getSubDepartments().size()));
            link.save();
        }
    }

    /*public boolean hasSuperDepartment() {
    
    
    
    }*/
    public SciOrganization getOrganization() {
        DataCollection collection;

        collection = (DataCollection) get(ORGANIZATION);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return (SciOrganization) DomainObjectFactory.newInstance(dobj);
        }
    }

    public void setOrganization(SciOrganization orga) {
        SciOrganization oldOrganization;

        oldOrganization = getOrganization();
        if (oldOrganization != null) {
            remove(ORGANIZATION, oldOrganization);
        }

        if (null != orga) {
            Assert.exists(orga, SciOrganization.class);
            DataObject link = add(ORGANIZATION, orga);
            link.set(SciOrganization.DEPARTMENT_ORDER,
                     Integer.valueOf((int) orga.getDepartments().size()));
            link.save();
        }
    }

    public SciDepartmentSubDepartmentsCollection getSubDepartments() {
        return new SciDepartmentSubDepartmentsCollection(
                (DataCollection) get(SUBDEPARTMENTS));
    }

    public void addSubDepartment(SciDepartment subDepartment) {
        Assert.exists(subDepartment, SciDepartment.class);

        DataObject link = add(SUBDEPARTMENTS, subDepartment);

        link.set(SUBDEPARTMENT_ORDER, Integer.valueOf((int) getSubDepartments().
                size()));
        link.save();
    }

    public void removeSubDepartment(SciDepartment subDepartment) {
        Assert.exists(subDepartment, SciDepartment.class);

        remove(SUBDEPARTMENTS, subDepartment);
    }

    public SciDepartmentProjectsCollection getProjects() {
        return new SciDepartmentProjectsCollection(
                (DataCollection) get(PROJECTS));
    }

    public void addProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        DataObject link = add(PROJECTS, project);

        link.set(PROJECT_ORDER,
                 Integer.valueOf((int) getProjects().size()));
        link.set(SciProject.DEPARTMENTS_ORDER,
                 Integer.valueOf((int) project.getDepartments().size()));
        link.save();
    }

    public void removeProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        remove(PROJECTS, project);
    }

    @Override
    public boolean hasContacts() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfContactsOfSciDepartment");
        query.setParameter("department", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }

    public boolean hasSubDepartments() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfSubDepartmentsOfSciDepartment");
        query.setParameter("department", getID());

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
     * @return 
     */
    public boolean hasMembers(final boolean merge, final MemberStatus status) {
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
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfSciDepartment";
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

                if (query.size() > 0) {
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
        query.setParameter("department", getID());
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
                        "com.arsdigita.cms.contenttypes.getIdsOfSubDepartmentsOfSciDepartment");
                departmentsQuery.setParameter("department", getID());

                if (query.size() > 0) {
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
        query.setParameter("department", getID());
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

                if (query.size() > 0) {
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

    public boolean hasPublications() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationLinksOfSciDepartment");
        query.setParameter("department", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }
}
