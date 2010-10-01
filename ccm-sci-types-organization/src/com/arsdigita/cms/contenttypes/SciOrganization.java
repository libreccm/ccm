package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class SciOrganization extends GenericOrganizationalUnit {

    public static final String ORGANIZATION_DESCRIPTION =
                               "organizationDescription";
    public static final String DEPARTMENTS = "departments";
    public static final String DEPARTMENT_ORDER = "departmentOrder";
    public static final String PROJECTS  ="projects";
    public static final String PROJECT_ORDER = "projectOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciOrganization";

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

    public String getOrganizationDescription() {
        return (String) get(ORGANIZATION_DESCRIPTION);
    }

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
    }

    public void removeDepartment(SciDepartment department) {
        Assert.exists(department, SciDepartment.class);

        remove(DEPARTMENTS, department);
    }

    public boolean hasDepartments() {
        return !this.getDepartments().isEmpty();
    }

    public SciOrganizationProjectsCollection getProjects() {
        return new SciOrganizationProjectsCollection(
                (DataCollection) get(PROJECTS));
    }

    public void addProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        DataObject link = add(PROJECTS, project);

        link.set(PROJECT_ORDER, Integer.valueOf((int) getProjects().size()));
    }

    public void removeProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        remove(PROJECTS, project);
    }

    public boolean hasProjects() {
        return !this.getProjects().isEmpty();
    }
}
