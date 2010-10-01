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
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProject extends GenericOrganizationalUnit {

    public static final String BEGIN = "projectbegin";
    public static final String END = "projectend";
    public static final String PROJECT_DESCRIPTION = "projectDescription";
    public static final String FUNDING = "funding";
    public static final String SUBPROJECTS = "subProjects";
    public static final String SUBPROJECT_ORDER = "subProjectOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.SciProject";

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

    public SciProjectSubProjectsCollection getSubProjects() {
        return new SciProjectSubProjectsCollection(
                (DataCollection) get(SUBPROJECTS));
    }

    public void addSubProject(SciProject project) {
        Assert.exists((project), SciProject.class);

        DataObject link = add(SUBPROJECTS, project);

        link.set(SUBPROJECT_ORDER,
                 Integer.valueOf((int) getSubProjects().size()));
    }

    public void removeSubProject(SciProject project) {
        Assert.exists((project), SciProject.class);

        remove(SUBPROJECTS, project);
    }

    public boolean hasSubProjects() {
        return !this.getSearchSummary().isEmpty();
    }
}
