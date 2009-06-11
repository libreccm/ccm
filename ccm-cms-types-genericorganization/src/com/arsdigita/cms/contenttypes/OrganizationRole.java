package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
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
public class OrganizationRole extends ACSObject {

    private static final Logger logger = Logger.getLogger(OrganizationRole.class);
    public static final String ROLENAME = "roleName";
    public static final String TARGETITEM = "targetItem";
    public static final String ROLEOWNER = "roleOwner";
    public static final String ORDER = "roleOrder";
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationRole";

    public OrganizationRole() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public OrganizationRole(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OrganizationRole(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public OrganizationRole(DataObject obj) {
        super(obj);
    }

    public OrganizationRole(String type) {
        super(type);
    }

    public String getRolename() {
        return (String) get(ROLENAME);
    }

    public void setRolename(String rolename) {
        set(ROLENAME, rolename);
    }

    public GenericOrganization getRoleOwner() {
        DataObject obj = (DataObject) get(ROLEOWNER);
        if (obj == null) {
            return null;
        } else {
            return (GenericOrganization) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setRoleOwner(GenericOrganization orga) {
        Assert.exists(orga, GenericOrganization.class);
        logger.debug(String.format("Setting role owner to %s", orga.getOrganizationName()));
        setAssociation(ROLEOWNER, orga);
    }

    public Person getTargetItem() {
        DataObject object = (DataObject) get(TARGETITEM);
        return (Person) DomainObjectFactory.newInstance(object);
    }

    public void setTargetItem(Person item) {
        setAssociation(TARGETITEM, item);
    }

    public Integer getOrder() {
        return (Integer) get(ORDER);
    }

    public void setOrder(Integer order) {
        Assert.exists(order);
        set(ORDER, order);
    }

    public String getURI(PageState s) {
        Person item = getTargetItem();

        if (item == null) {
            logger.error(getOID() + " is a link between an organization and a person, but the associated person is null");
            return "";
        }

        ContentSection section = item.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(s, item, section, item.getVersion());

        return URL.there(s.getRequest(), url).toString();
    }

    public static DataCollection getReferingRoles(Person person) {
        Session session = SessionManager.getSession();
        DataCollection roles = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = roles.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getRefferingRoles");
        filter.set("itemID", person.getID());

        return roles;
    }

    public static DataCollection getRoles(GenericOrganization orga) {
        logger.debug("Getting roles for an organization...");
        Session session = SessionManager.getSession();
        DataCollection roles = session.retrieve(BASE_DATA_OBJECT_TYPE);
        roles.addEqualsFilter(ROLEOWNER + ".id", orga.getID());
        roles.addOrder(ORDER);
        return roles;
    }

    public void swapWithNext() throws UnsupportedOperationException {
        swapWithNext("com.arsdigita.cms.contenttypes.allRoleOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrganizationRoleWithNextInGroup");
    }

    public void swapWithPrevious() throws UnsupportedOperationException {
        swapWithNext("com.arsdigita.cms.contenttypes.allRoleOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrganizationRoleWithNextInGroup");
    }

    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName);
    }

    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName);
    }

    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("ownerID", getRoleOwner().getID());
        return query;
    }

    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().retrieveDataOperation(operationName);
        operation.setParameter("ownerID", getRoleOwner().getID());
        return operation;
    }

    protected void swapKeys(boolean swapNext, String queryName, String operationName) {
        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + "cannot be called on an object that is new");

        Integer currentKey = (Integer) get(ORDER);

        if (currentKey == null) {
            alphabetize();
            return;
        }

        Assert.isTrue(currentKey != null, methodName + " cannot be " +
                "called on an object that is not currently in the " +
                "list");

        int key = currentKey.intValue();

        DataQuery query = getSwapQuery(queryName);

        int otherKey = key;

        if (swapNext) {
            otherKey = key + 1;
            query.addOrder("roleOrder ASC");
            query.addFilter(query.getFilterFactory().greaterThan("roleOrder", currentKey, true));
        } else {
            otherKey = key - 1;
            query.addOrder("roleOrder DESC");
            query.addFilter(query.getFilterFactory().lessThan("roleOrder", currentKey, true));
        }

        if (query.next()) {
            otherKey = ((Integer) query.get("roleOrder")).intValue();
            query.close();
        }

        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("linkOrder", new Integer(key));
        operation.setParameter("nextLinkOrder", new Integer(otherKey));
        operation.execute();
    }

    protected void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection roles = session.retrieve(BASE_DATA_OBJECT_TYPE);
        roles.addEqualsFilter(ROLEOWNER + ".id", getRoleOwner().getID());
        roles.addOrder(ROLENAME);
        int sortkey = 0;
        while (roles.next()) {
            sortkey++;
            OrganizationRole role = new OrganizationRole(roles.getDataObject());
            role.setOrder(sortkey);
            role.save();
        }
    }

    public int maxOrder() {
        GenericOrganization roleOwner = getRoleOwner();
        if (roleOwner == null) {
            return 0;
        }
        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allRoleOrderForItem");
        query.setParameter("ownerID", getRoleOwner().getID());
        query.addOrder("linkOrder DESC");
        if (query.next()) {
            Integer roleOrder = ((Integer) query.get("roleOrder"));
            query.close();
            if (roleOrder != null) {
                returnOrder = roleOrder.intValue();
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
