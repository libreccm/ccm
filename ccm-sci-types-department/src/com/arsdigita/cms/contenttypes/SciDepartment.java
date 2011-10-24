package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.SciDepartmentExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartment extends GenericOrganizationalUnit {

    public static final String DEPARTMENT_SHORT_DESCRIPTION =
                               "departmentShortDescription";
    public static final String DEPARTMENT_DESCRIPTION = "departmentDescription";
    public static final String ROLE_ENUM_NAME = "SciDepartmentRole";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciDepartment";
    private static final SciDepartmentConfig config = new SciDepartmentConfig();

    static {
        config.load();
    }

    public SciDepartment() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciDepartment(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciDepartment(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciDepartment(final DataObject dataObject) {
        super(dataObject);
    }

    public SciDepartment(final String type) {
        super(type);
    }

    public static SciDepartmentConfig getConfig() {
        return config;
    }

    public String getDepartmentShortDescription() {
        return (String) get(DEPARTMENT_SHORT_DESCRIPTION);
    }

    public void setDepartmentShortDescription(final String shortDesc) {
        set(DEPARTMENT_SHORT_DESCRIPTION, shortDesc);
    }

    public String getDepartmentDescription() {
        return (String) get(DEPARTMENT_DESCRIPTION);
    }

    public void setDepartmentDescription(final String description) {
        set(DEPARTMENT_DESCRIPTION, description);
    }

    @Override
    public boolean hasContacts() {
        boolean result = false;

        final DataQuery query =
                  SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfContactsOfSciDepartment");
        query.setParameter("project", getID());

        if (query.size() > 0) {
            result = true;
        } else {
            result = false;
        }

        query.close();

        return result;
    }

    public boolean hasMembers(final boolean merge,
                              final SciDepartmentMemberStatus status) {
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

        final DataQuery query = SessionManager.getSession().retrieveQuery(
                queryName);
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
                               final SciDepartmentMemberStatus status) {
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

        final DataQuery query = SessionManager.getSession().retrieveQuery(
                queryName);
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
                    BigDecimal subdepartmentId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subdepartmentId = (BigDecimal) subDepartmentsQuery.get(
                                "departmentId");
                        result = hasMembers(subdepartmentId, merge, status);

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

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new SciDepartmentExtraXmlGenerator());
        return generators;
    }
}
