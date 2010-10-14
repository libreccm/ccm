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
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

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
 * @see SciProject^
 */
public class SciOrganization extends GenericOrganizationalUnit {

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
        s_config.load();
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
