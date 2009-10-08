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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.URL;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Represents the relationship between a organizational unit and a project.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2OrgaUnit extends ACSObject {

    private static final Logger s_log = Logger.getLogger(Project2OrgaUnit.class);

    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Project2OrgaUnit";

    public static final String TARGET_ITEM = "targetItem";
    public static final String PROJECT = "project";
    public static final String UNIT_ORDER = "unitOrder";

    public Project2OrgaUnit() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Project2OrgaUnit(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Project2OrgaUnit(OID oid) {
        super(oid);
    }

    public Project2OrgaUnit(DataObject obj) {
        super(obj);
    }

    public Project2OrgaUnit(String type) {
        super(type);
    }

    public Project getProject() {
        DataObject obj = (DataObject) get(PROJECT);
        if (obj == null) {
            return null;
        } else {
            return (Project) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setProject(Project project) {
        Assert.exists(project, Project.class);
        s_log.debug(String.format("Setting project to %s", project.getProjectName()));
        setAssociation(PROJECT, project);
    }

    public OrganizationalUnit getTargetItem() {
        DataObject object = (DataObject) get(TARGET_ITEM);
        if (object == null) {
            return null;
        } else {
            return (OrganizationalUnit) DomainObjectFactory.newInstance(object);
        }
    }

    public void setTargetItem(OrganizationalUnit orgaUnit) {
        Assert.exists(orgaUnit, OrganizationalUnit.class);
        setAssociation(TARGET_ITEM, orgaUnit);
    }

    public Integer getOrder() {
        return (Integer) get(UNIT_ORDER);
    }

    public void setOrder(Integer order) {
        Assert.exists(order);
        set(UNIT_ORDER, order);
    }

    public String getURI(PageState state) {
        OrganizationalUnit orgaUnit = getTargetItem();

        if (orgaUnit == null) {
            s_log.error(getOID() + "is a link between a project and a organizational unit, but the associated organizational unit is null");
            return "";
        }

        ContentSection section = orgaUnit.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(state, orgaUnit, section, orgaUnit.getVersion());

        return URL.there(state.getRequest(), url).toString();
    }

    public static DataCollection getReferingProjects(OrganizationalUnit orgaUnit) {
        Session session = SessionManager.getSession();
        DataCollection projects = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = projects.addInSubqueryFilter("id", "com.arsdigita.cmscontenttypes.getReferingProjects");
        filter.set("itemID", orgaUnit.getID());

        return projects;
    }

    public static DataCollection getUnits(Project project) {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        units.addEqualsFilter(PROJECT + ".id", project.getID());
        units.addOrder(UNIT_ORDER);
        
        return units;
    }

    public void swapWithNext() {
        swapWithNext("com.arsdigita.cms.contenttypes.allUnitsOrderForProject", "com.arsdigita.cms.contenttypes.swapProject2OrgaUnitWithNextInGroup");
    }

    public void swapWithPrevious() {
        swapWithPrevious("com.arsdigita.cms.contenttypes.allUnitsOrderForProject", "com.arsdigita.cms.contenttypes.swapProject2OrgaUnitWithNextInGroup");
    }

    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName);
    }

    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName);
    }

    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("projectID", getProject().getID());
        return query;
    }

    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().retrieveDataOperation(operationName);
        operation.setParameter("projectID", getProject().getID());
        return operation;
    }

    protected void swapKeys(boolean swapNext, String queryName, String operationName) {

        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + " cannon be called on an object that is new");

        Integer currentKey = (Integer) get(UNIT_ORDER);

        if (currentKey == null) {
            alphabetize();
            return;
        }

        Assert.isTrue(currentKey != null, methodName + "cannot be called on a object htat is not currently in the list");

        int key = currentKey.intValue();
        s_log.debug("current key: " + currentKey);
        s_log.debug("key = " + key);

        DataQuery query = getSwapQuery(queryName);

        int otherKey;
        if(swapNext) {
            otherKey = key + 1;
            query.addOrder("unitOrder ASC");
            query.addFilter(query.getFilterFactory().greaterThan("unitOrder", currentKey, true));
        } else {
            otherKey = key - 1;
            query.addOrder("unitOrder DESC");
            query.addFilter(query.getFilterFactory().lessThan("unitOrder", currentKey, true));
        }

        if(query.next()) {
            otherKey = ((Integer) query.get("unitOrder")).intValue();
            query.close();
        }

        s_log.debug("otherKey = " + otherKey);
        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("unitOrder", new Integer(key));
        operation.setParameter("nextUnitOrder", new Integer(otherKey));
        operation.execute();
    }

    public void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection projects = session.retrieve(BASE_DATA_OBJECT_TYPE);
        projects.addEqualsFilter(PROJECT + ".id", getProject().getID());
        int sortKey = 0;
        while(projects.next()) {
            sortKey++;
            Project2OrgaUnit p2ou = new Project2OrgaUnit(projects.getDataObject());
            p2ou.setOrder(sortKey);
            p2ou.save();
        }
    }

    public int maxOrder() {
        Project project = getProject();
        if (project == null) {
            return 0;
        }

        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allUnitsOrderForProject");
        query.setParameter("projectID", getProject().getID());
        query.addOrder("unitOrder DESC");
        if(query.next()) {
            Integer unitOrder = ((Integer) query.get("unitOrder"));
            query.close();
            if(unitOrder != null) {
                returnOrder = unitOrder.intValue();
            }
        }

        return returnOrder;
    }

    @Override
    public void beforeSave() {
        super.beforeSave();
        if (getOrder() == null) {
            setOrder(maxOrder() + 1);
        }
    }
}
