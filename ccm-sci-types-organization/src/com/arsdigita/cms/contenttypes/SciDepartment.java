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
        s_config.load();
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
        DataCollection collection;

        collection = (DataCollection) get(SUPER_DEPARTMENT);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();

            return new SciDepartment(dobj);
        }
    }

    public void setSuperDepartment(SciDepartment department) {
        SciDepartment oldSuperDepartment;

        oldSuperDepartment = getSuperDepartment();
        remove(SUPER_DEPARTMENT, oldSuperDepartment);

        if (null != department) {
            Assert.exists(department, SciDepartment.class);
            add(SUPER_DEPARTMENT, department);
        }
    }

    public SciOrganization getOrganization() {
        DataCollection collection;

        collection = (DataCollection) get(ORGANIZATION);

        if (0 == collection.size()) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();

            return new SciOrganization(dobj);
        }
    }

    public void setOrganization(SciOrganization orga) {
        SciOrganization oldOrganization;

        oldOrganization = getOrganization();
        remove(ORGANIZATION, oldOrganization);

        if (null != orga) {
            Assert.exists(orga, SciOrganization.class);
            add(ORGANIZATION, orga);
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
