package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * The class represents a (scientific) project. It extends 
 * {@link GenericOrganizationalUnit} and adds
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
 * 
 * Also this module provides two authoring steps for defining hierarchies of
 * projects. Two enable these authoring steps activate them in the 
 * configuration.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProject extends GenericOrganizationalUnit {

    public static final String BEGIN = "projectbegin";
    public static final String END = "projectend";
    public static final String PROJECT_SHORT_DESCRIPTION = "projectShortDesc";
    public static final String PROJECT_DESCRIPTION = "projectDescription";
    public static final String FUNDING = "funding";
    public static final String FUNDING_VOLUME = "fundingVolume";
    public static final String ROLE_ENUM_NAME = "SciProjectRole";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciProject";
    private static final SciProjectConfig config = new SciProjectConfig();

    static {
        config.load();
    }

    public SciProject() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciProject(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciProject(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciProject(final DataObject dataObject) {
        super(dataObject);
    }

    public SciProject(final String type) {
        super(type);
    }

    public static SciProjectConfig getConfig() {
        return config;
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

    /**
     * 
     * @param merge Should I also look into the projects and return true
     * if the organization or at least one of the projects has members?
     * @param status 
     * @return 
     */
    public boolean hasMembers(final boolean merge,
                              final SciProjectMemberStatus status) {
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
                               final SciProjectMemberStatus status) {
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
}