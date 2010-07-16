/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center for Social Policy Research of the University of Bremen
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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Contenttype which represents a (scientific) project.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project extends ContentPage {

    public static final String PROJECTNAME = "projectname";
    public static final String FUNDING = "funding";
    public static final String PROJECT_DESCRIPTION = "projectDescription";
    public static final String BEGINDATE = "beginDate";
    public static final String ENDDATE = "endDate";
    //public static final String FINISHED = "finished";
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Project";
    private static final Logger s_log = Logger.getLogger(Project.class);

    public Project() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Project(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Project(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Project(DataObject obj) {
        super(obj);
    }

    public Project(String type) {
        super(type);
    }

    /* accessors ********************************/
    public String getProjectName() {
        return (String) get(PROJECTNAME);
    }

    public void setProjectName(String projectName) {
        set(PROJECTNAME, projectName);
    }

    public String getFunding() {
        return (String) get(FUNDING);
    }

    public void setFunding(String funding) {
        set(FUNDING, funding);
    }

    public String getProjectDescription() {      
        return (String) get(PROJECT_DESCRIPTION);
    }

    public void setProjectDescription(String projectDescription) {
        set(PROJECT_DESCRIPTION, projectDescription);
    }

    public Date getBegin() {
        return (Date) get(BEGINDATE);
    }

    public void setBegin(Date begin) {
        set(BEGINDATE, begin);
    }

    public Date getEnd() {
        return (Date) get(ENDDATE);
    }

    public void setEnd(Date end) {
        set(ENDDATE, end);
    }

    /*public Boolean getFinished() {
    return (Boolean)get(FINISHED);
    }

    public void setFinished(Boolean finished) {
    set(FINISHED, finished);
    }*/
}
