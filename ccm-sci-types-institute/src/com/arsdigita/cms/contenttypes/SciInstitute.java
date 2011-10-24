package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.SciInstituteExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.List;
import org.omg.PortableInterceptor.ACTIVE;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstitute extends GenericOrganizationalUnit {

    public static final String INSTITUTE_SHORT_DESCRIPTION =
                               "instituteShortDescription";
    public static final String INSTITUTE_DESCRIPTION = "instituteDescription";
    public static final String ROLE_ENUM_NAME = "SciInstituteRole";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciInstitute";
    private static final SciInstituteConfig config = new SciInstituteConfig();

    static {
        config.load();
    }

    public SciInstitute() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public SciInstitute(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public SciInstitute(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciInstitute(final DataObject dataObject) {
        super(dataObject);
    }

    public SciInstitute(final String type) {
        super(type);
    }

    public static SciInstituteConfig getConfig() {
        return config;
    }

    public String getInstituteShortDescription() {
        return (String) get(INSTITUTE_SHORT_DESCRIPTION);
    }

    public void setInstituteShortDescription(final String shortDesc) {
        set(INSTITUTE_SHORT_DESCRIPTION, shortDesc);
    }

    public String getInstituteDescription() {
        return (String) get(INSTITUTE_DESCRIPTION);
    }

    public void setInstituteDescription(final String description) {
        set(INSTITUTE_DESCRIPTION, description);
    }

    @Override
    public boolean hasContacts() {
        boolean result = false;

        final DataQuery query =
                        SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contenttypes.getIdsOfContactsOfSciInstitute");
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
                              final SciInstituteMemberStatus status) {
        String queryName;

        switch (status) {
            case ALL:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfMembersOfSciInstitute";
                break;
            case ACTIVE:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfActiveMembersOfSciInstitute";
                break;
            case ASSOCIATED:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfAssociatedMembersOfSciInstitute";
                break;
            case FORMER:
                queryName =
                "com.arsdigita.cms.contenttypes.getIdsOfFormerMembersOfSciInstitute";
                break;
            default:
                queryName = "";
                break;
        }

        final DataQuery query = SessionManager.getSession().retrieveQuery(
                queryName);
        query.setParameter("institute", getID());

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery institutesQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubInstitutesOfSciInstitute");
                institutesQuery.setParameter("institute", getID());

                if (query.size() > 0) {
                    BigDecimal instituteId;
                    boolean result = false;
                    while (institutesQuery.next()) {
                        instituteId = (BigDecimal) institutesQuery.get(
                                "instituteId");
                        result = hasMembers(instituteId, merge, status);

                        if (result) {
                            break;
                        }
                    }

                    institutesQuery.close();
                    return result;
                } else {
                    institutesQuery.close();
                    return false;
                }
            } else {
                query.close();
                return false;
            }
        }
    }

    private boolean hasMembers(final BigDecimal instituteId,
                               final boolean merge,
                               final SciInstituteMemberStatus status) {
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
        query.setParameter("institute", instituteId);

        if (query.size() > 0) {
            query.close();
            return true;
        } else {
            if (merge) {
                query.close();
                DataQuery subDepartmentsQuery =
                          SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.contenttypes.getIdsOfSubDepartmentsOfSciDepartment");
                subDepartmentsQuery.setParameter("institute", instituteId);

                if (query.size() > 0) {
                    BigDecimal subinstituteId;
                    boolean result = false;
                    while (subDepartmentsQuery.next()) {
                        subinstituteId = (BigDecimal) subDepartmentsQuery.get(
                                "instituteId");
                        result = hasMembers(subinstituteId, merge, status);

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
        generators.add(new SciInstituteExtraXmlGenerator());
        return generators;
    }
}
