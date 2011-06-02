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
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * <p>
 * The class represents a (scientific) project. It extends 
 * {@link GenericOrganizationalUnit} and adds a relation for subprojects and 
 * some fields for additional information: 
 * </p>
 * <dl>
 * <dt><code>projectBegin</code></dt>
 * <dd>The begin of the project</dd>
 * <dt><code>projectEnd</code></dt>
 * <dd>The end of the project</dd>
 * <dt><code>shortDescription</code></dt>
 * <dd>A short description (500 characters) of the project</dd>
 * <dt><code>description</code></dt>
 * <dd>A description of the project/<dd>
 * <dt><code>funding</code><dt>
 * <dd>A text about the funding of the project</dd>
 * <dt><code>fundingVolume</code></dt>
 * <dd><code>Volume of the funding</code></dt>
 * </dl>
 * <p>
 * Also, the class has some methods the access the associations defined by
 * {@link SciOrganization} and {@link SciDepartment} with
 * <code>SciProject</code>.
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
 * @see SciDepartment
 */
public class SciProject extends GenericOrganizationalUnit {

    public static final String BEGIN = "projectbegin";
    public static final String END = "projectend";
    public static final String PROJECT_SHORT_DESCRIPTION = "projectShortDesc";
    public static final String PROJECT_DESCRIPTION = "projectDescription";
    public static final String FUNDING = "funding";
    public static final String FUNDING_VOLUME = "fundingVolume";
    public static final String SUBPROJECTS = "subProjects";
    public static final String SUBPROJECT_ORDER = "subProjectOrder";
    public static final String ORGANIZATIONS = "organization";
    public static final String ORGANIZATIONS_ORDER = "organizationOrder";
    public static final String SUPER_PROJECT = "superProject";
    public static final String DEPARTMENTS = "department";
    public static final String DEPARTMENTS_ORDER = "departmentOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciProject";
    private static final SciOrganizationConfig s_config =
                                               new SciOrganizationConfig();
    private static final Logger logger = Logger.getLogger(SciProject.class);

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

    public SciProject() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciProject(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciProject(OID oid) {
        super(oid);
    }

    public SciProject(DataObject obj) {
        super(obj);
    }

    public SciProject(String type) {
        super(type);
    }

    public static SciOrganizationConfig getConfig() {
        return s_config;
    }

    public Date getBegin() {
        return (Date) get(BEGIN);
    }

    public void setBegin(Date begin) {
        set(BEGIN, begin);
    }

    public Date getEnd() {
        return (Date) get(END);
    }

    public void setEnd(Date end) {
        set(END, end);
    }

    public String getProjectShortDescription() {
        return (String) get(PROJECT_SHORT_DESCRIPTION);
    }

    public void setProjectShortDescription(String shortDesc) {
        set(PROJECT_SHORT_DESCRIPTION, shortDesc);
    }

    public String getProjectDescription() {
        return (String) get(PROJECT_DESCRIPTION);
    }

    public void setProjectDescription(String description) {
        set(PROJECT_DESCRIPTION, description);
    }

    public String getFunding() {
        return (String) get(FUNDING);
    }

    public void setFunding(String funding) {
        set(FUNDING, funding);
    }

    public String getFundingVolume() {
        return (String) get(FUNDING_VOLUME);
    }

    public void setFundingVolume(String fundingVolume) {
        set(FUNDING_VOLUME, fundingVolume);
    }

    public SciProjectSubProjectsCollection getSubProjects() {
        return new SciProjectSubProjectsCollection(
                (DataCollection) get(SUBPROJECTS));
    }

    public void addSubProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        DataObject link = add(SUBPROJECTS, project);

        link.set(SUBPROJECT_ORDER,
                 Integer.valueOf((int) getSubProjects().size()));
        link.save();
    }

    public void removeSubProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        logger.debug(String.format("Removing subproject %s",
                                   project.getTitle()));
        remove(SUBPROJECTS, project);
    }

    public SciProjectOrganizationsCollection getOrganizations() {
        return new SciProjectOrganizationsCollection((DataCollection) get(
                ORGANIZATIONS));
    }

    public void addOrganization(SciOrganization orga) {
        Assert.exists(orga, SciOrganization.class);

        DataObject link = add(ORGANIZATIONS, orga);

        link.set(ORGANIZATIONS_ORDER,
                 Integer.valueOf((int) getOrganizations().size()));
        link.set(SciOrganization.PROJECT_ORDER,
                 Integer.valueOf((int) orga.getProjects().size()));
        link.save();
    }

    public void removeOrganization(SciOrganization orga) {
        Assert.exists(orga, SciOrganization.class);

        remove(ORGANIZATIONS, orga);
    }

    public boolean hasOrganizations() {
        return !this.getOrganizations().isEmpty();
    }

    public SciProjectDepartmentsCollection getDepartments() {
        return new SciProjectDepartmentsCollection((DataCollection) get(
                DEPARTMENTS));
    }

    public void addDepartment(SciDepartment department) {
        Assert.exists(department, SciDepartment.class);

        DataObject link = add(DEPARTMENTS, department);

        link.set(DEPARTMENTS_ORDER,
                 Integer.valueOf((int) getDepartments().size()));
        link.save();
    }

    public void removeDepartment(SciDepartment department) {
        Assert.exists(department, SciDepartment.class);

        remove(DEPARTMENTS, department);
    }

    public boolean hasDepartments() {
        return !this.getDepartments().isEmpty();
    }

    public SciProject getSuperProject() {
        DataCollection collection;

        collection = (DataCollection) get(SUPER_PROJECT);

        if (0 == collection.size()) {
            logger.debug(String.format("Project %s has NO super project...",
                                       getTitle()));
            return null;
        } else {
            DataObject dobj;

            logger.debug(String.format("Project %s has a super project...",
                                       getTitle()));
            collection.next();
            dobj = collection.getDataObject();
            collection.close();

            return new SciProject(dobj);
        }
    }

    public void setSuperProject(SciProject superProject) {
        SciProject oldSuperProject;

        oldSuperProject = getSuperProject();
        if (oldSuperProject != null) {
            logger.debug("Removing old super project...");
            remove(SUPER_PROJECT, oldSuperProject);
        }

        if (superProject != null) {
            Assert.exists(superProject, SciProject.class);
            logger.debug("Setting new super project...");
            DataObject link = add(SUPER_PROJECT, superProject);

            link.set(SUBPROJECT_ORDER,
                     Integer.valueOf((int) superProject.getSubProjects().size()));
            link.save();
        }
    }

    @Override
    public boolean hasContacts() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfContactsOfSciProject");
        query.setParameter("project", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }

    public boolean hasSubProjects() {
        boolean result = false;

        DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfSubProjectsOfSciProject");
        query.setParameter("project", getID());

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
     * @param merge Should I also look into the projects and return true
     * if the organization or at least one of the projects has members?
     * @return 
     */
    public boolean hasMembers(final boolean merge, final MemberStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfSciProject";
                break;
            case ACTIVE:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfActiveMembersOfSciProject";
                break;
            case ASSOCIATED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfSciProject";
                break;
            case FORMER:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFormerMembersOfSciProject";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("project", getID());

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery projectsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubProjectsOfSciProject");
                projectsQuery.setParameter("project", getID());

                if (query.size() > 0) {
                    BigDecimal projectId;
                    boolean result = false;
                    while (projectsQuery.next()) {
                        projectId = (BigDecimal) projectsQuery.get(
                                "projectId");
                        result = hasMembers(projectId, merge, status);

                        if (result) {
                            break;
                        }
                    }

                    projectsQuery.close();
                    return result;
                } else {
                    projectsQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }

    private boolean hasMembers(final BigDecimal projectId,
                               final boolean merge,
                               final MemberStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfSciProject";
                break;
            case ACTIVE:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfActiveMembersOfSciProject";
                break;
            case ASSOCIATED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfProject";
                break;
            case FORMER:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFormerMembersOfSciProject";
                break;
            default:
                queryName = "";
                break;
        }

        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("project", projectId);

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery subProjectsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubProjectsOfSciProject");
                subProjectsQuery.setParameter("project", projectId);

                if (query.size() > 0) {
                    BigDecimal subprojectId;
                    boolean result = false;
                    while (subProjectsQuery.next()) {
                        subprojectId = (BigDecimal) subProjectsQuery.get(
                                "projectId");
                        result = hasMembers(subprojectId, merge, status);

                        if (result) {
                            break;
                        }
                    }

                    subProjectsQuery.close();
                    return result;
                } else {
                    subProjectsQuery.close();
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
                "com.arsdigita.cms.contenttypes.getIdsOfPublicationLinksOfSciProject");
        query.setParameter("project", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }*/
}
