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
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Project2Person extends ACSObject {

    private final static Logger s_log = Logger.getLogger(Project2Person.class);
    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Project2Person";
    public final static String TASK = "task";
    public final static String TARGET_ITEM = "targetItem";
    public static final String PROJECT = "project";
    public final static String ROLE_ORDER = "personOrder";
    public final static String PROJECT2PERSON = "project2Person";

    public Project2Person() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Project2Person(DataObject obj) {
        super(obj);
    }

    public Project2Person(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Project2Person(OID oid) {
        super(oid);
    }

    public Project2Person(String type) {
        super(type);
    }

    public String getTask() {
        return (String) get(TASK);
    }

    public void setTask(String task) {
        set(TASK, task);
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
        setAssociation(PROJECT, project);
    }

    public Person getTargetItem() {
        DataObject obj = (DataObject) get(TARGET_ITEM);
        if (obj == null) {
            return null;
        } else {
            return (Person) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setTargetItem(Person person) {
        Assert.exists(person, Person.class);
        setAssociation(TARGET_ITEM, person);
    }

     public Integer getOrder() {
        return (Integer) get(ROLE_ORDER);
    }

    public void setOrder(Integer order) {
        Assert.exists(order);
        set(ROLE_ORDER, order);
    }


    public String getURI(PageState state) {
        Person person = this.getTargetItem();

        if (person == null) {
            s_log.error(getOID() + " is a link between a Project and a Person, but the associated Person is null");
            return "";
        }

        ContentSection section = person.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(state, person, section, person.getVersion());

        return URL.there(state.getRequest(), url).toString();
    }

    public static DataCollection getReferingProject2Persons(Person person) {
        Session session = SessionManager.getSession();
        DataCollection project2Persons = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = project2Persons.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getReferingProject2Persons");
        filter.set("itemID", person.getID());

        return project2Persons;
    }

    public static DataCollection getProject2Persons(Project project) {
        Session session = SessionManager.getSession();
        DataCollection dc = session.retrieve(BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(PROJECT + ".id", project.getID());
        return dc;
    }

    public void swapWithNext() {
        swapWithNext("com.arsdigita.cms.contenttypes.allPersonsOrderForProject", "com.arsdigita.cms.contenttypes.swapProject2OrgaPersonWithNextInGroup");
    }

    public void swapWithPrevious() {
        swapWithPrevious("com.arsdigita.cms.contenttypes.allPersonsOrderForProject", "com.arsdigita.cms.contenttypes.swapProject2OrgaPersonWithNextInGroup");
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

        Integer currentKey = (Integer) get(ROLE_ORDER);

        if (currentKey == null) {
            alphabetize();
            return;
        }

        Assert.isTrue(currentKey != null, methodName + "cannot be called on a object htat is not currently in the list");

        int key = currentKey.intValue();

        DataQuery query = getSwapQuery(queryName);

        int otherKey;
        if (swapNext) {
            otherKey = key + 1;
            query.addOrder("personOrder ASC");
            query.addFilter(query.getFilterFactory().greaterThan("personOrder", currentKey, true));
        } else {
            otherKey = key - 1;
            query.addOrder("personOrder DESC");
            query.addFilter(query.getFilterFactory().lessThan("personOrder", currentKey, true));
        }

        if (query.next()) {
            otherKey = ((Integer) query.get("personOrder")).intValue();
            query.close();
        }

        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("personOrder", new Integer(key));
        operation.setParameter("nextPersonOrder", new Integer(otherKey));
        operation.execute();
    }

    public void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection projects = session.retrieve(BASE_DATA_OBJECT_TYPE);
        projects.addEqualsFilter(PROJECT + ".id", getProject().getID());
        int sortKey = 0;
        while (projects.next()) {
            sortKey++;
            Project2Person p2ou = new Project2Person(projects.getDataObject());
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
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allRoleOrderForProject2Person");
        query.setParameter("projectID", getProject().getID());
        query.addOrder("personOrder DESC");
        if (query.next()) {
            Integer personOrder = ((Integer) query.get("personOrder"));
            query.close();
            if (personOrder != null) {
                returnOrder = personOrder.intValue();
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
