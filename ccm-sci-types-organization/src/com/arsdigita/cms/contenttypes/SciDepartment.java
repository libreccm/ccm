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

            return new SciDepartment(dobj);
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
