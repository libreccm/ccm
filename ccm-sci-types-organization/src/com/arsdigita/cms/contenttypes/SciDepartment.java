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
public class SciDepartment extends GenericOrganizationalUnit {

    public static final String DEPARTMENT_DESCRIPTION = "departmentDescription";
    public static final String SUBDEPARTMENTS = "subDepartments";
    public static final String SUBDEPARTMENT_ORDER = "subDepartmentOrder";
    public static final String PROJECTS = "projects";
    public static final String PROJECT_ORDER = "projectOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciDepartment";

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

    public String getDepartmentDescription() {
        return (String) get(DEPARTMENT_DESCRIPTION);
    }

    public void setDepartmentDescription(String description) {
        set(DEPARTMENT_DESCRIPTION, description);
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
    }

    public void removeSubDepartment(SciDepartment subDepartment) {
        Assert.exists(subDepartment, SciDepartment.class);

        remove(SUBDEPARTMENTS, subDepartment);
    }

    public boolean hasSubDepartments() {
        return !this.getSubDepartments().isEmpty();
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
    }

    public void removeProject(SciProject project) {
        Assert.exists(project, SciProject.class);

        remove(PROJECTS, project);
    }

    public boolean hasProjects() {
        return !this.getProjects().isEmpty();
    }
}
