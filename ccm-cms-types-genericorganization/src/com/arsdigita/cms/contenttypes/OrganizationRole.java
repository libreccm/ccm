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
 * This class represents an role in a organization. Examples for roles 
 * are CEO, chairmen, mayor, speaker etc.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class OrganizationRole extends ACSObject {

    private static final Logger logger = Logger.getLogger(OrganizationRole.class);
    /**
     * PDL identifier of the name property of the role
     */
    public static final String ROLENAME = "roleName";
    /**
     * PDL identifier for the targetItem - the person assoicated with the role.
     */
    public static final String TARGETITEM = "targetItem";
    /**
     * PDL for identifier for the organization associated with this role.
     */
    public static final String ROLEOWNER = "roleOwner";
    /**
     * PDL id for the property for ordering the roles of an organization.
     */
    public static final String ORDER = "roleOrder";
    /**
     * Type of this object.
     */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrganizationRole";

    /**
     * Empty construtor creating a new role
     */
    public OrganizationRole() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Tries to get the role with @id from the database.
     *
     * @param id
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     */
    public OrganizationRole(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Tries to get the role with @id from the database.
     *
     * @param id
     * @throws com.arsdigita.domain.DataObjectNotFoundException
     */
    public OrganizationRole(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Tries to find obj in the database.
     *
     * @param obj
     */
    public OrganizationRole(DataObject obj) {
        super(obj);
    }

    /**
     * Creates a new role.
     *
     * @param type
     */
    public OrganizationRole(String type) {
        super(type);
    }

    /**
     *
     * @return The name of the role.
     */
    public String getRolename() {
        return (String) get(ROLENAME);
    }

    /**
     * Sets the name of the role
     *
     * @param rolename
     */
    public void setRolename(String rolename) {
        set(ROLENAME, rolename);
    }

    /**
     *
     * @return The owning organization.
     */
    public GenericOrganization getRoleOwner() {
        DataObject obj = (DataObject) get(ROLEOWNER);
        if (obj == null) {
            return null;
        } else {
            return (GenericOrganization) DomainObjectFactory.newInstance(obj);
        }
    }

    /**
     * Sets the owing organization.
     *
     * @param orga
     */
    public void setRoleOwner(GenericOrganization orga) {
        Assert.exists(orga, GenericOrganization.class);
        logger.debug(String.format("Setting role owner to %s", orga.getOrganizationName()));
        setAssociation(ROLEOWNER, orga);
    }

    /**
     *
     * @return The person associated with the role.
     */
    public Person getTargetItem() {
        DataObject object = (DataObject) get(TARGETITEM);
        return (Person) DomainObjectFactory.newInstance(object);
    }

    /**
     * Sets the person associated with the role
     *
     * @param item
     */
    public void setTargetItem(Person item) {
        setAssociation(TARGETITEM, item);
    }

    /**
     *
     * @return The order of the role.
     */
    public Integer getOrder() {
        return (Integer) get(ORDER);
    }

    /**
     * Sets the order of the role
     *
     * @param order
     */
    public void setOrder(Integer order) {
        Assert.exists(order);
        set(ORDER, order);
    }

    /*
     *
     * @param s Current PageState
     * @return The URI of the target item.
     */
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

    /**
     *
     * @param person
     * @return All roles a person is associated with.
     */
    public static DataCollection getReferingRoles(Person person) {
        Session session = SessionManager.getSession();
        DataCollection roles = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = roles.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getRefferingRoles");
        filter.set("itemID", person.getID());

        return roles;
    }

    /**
     *
     * @param orga
     * @return all roles an organization is associated with.
     */
    public static DataCollection getRoles(GenericOrganization orga) {
        logger.debug("Getting roles for an organization...");
        Session session = SessionManager.getSession();
        DataCollection roles = session.retrieve(BASE_DATA_OBJECT_TYPE);
        roles.addEqualsFilter(ROLEOWNER + ".id", orga.getID());
        roles.addOrder(ORDER);
        return roles;
    }

    /**
     * Swaps an role with the next in the order.
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public void swapWithNext() throws UnsupportedOperationException {
        swapWithNext("com.arsdigita.cms.contenttypes.allRoleOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrganizationRoleWithNextInGroup");
    }

    /**
     * Swaps a role with the previous in the order.
     *
     * @throws java.lang.UnsupportedOperationException
     */
    public void swapWithPrevious() throws UnsupportedOperationException {
        swapWithNext("com.arsdigita.cms.contenttypes.allRoleOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrganizationRoleWithNextInGroup");
    }

    /**
     * Swaps an role with the next in the order.
     * 
     * @param queryName
     * @param operationName
     */
    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName);
    }

    /**
     * Swaps a role with the previous in the order.
     *
     * @param queryName
     * @param operationName
     */
    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName);
    }

    /**
     * Returns the query for swaping roles definied in the PDL.
     *
     * @param queryName
     * @return
     */
    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("ownerID", getRoleOwner().getID());
        return query;
    }

    /**
     * Gets the PDL operation for swaping.
     *
     * @param operationName
     * @return
     */
    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().retrieveDataOperation(operationName);
        operation.setParameter("ownerID", getRoleOwner().getID());
        return operation;
    }

    /**
     * Swaps the keys of two roles.
     *
     * @param swapNext
     * @param queryName
     * @param operationName
     */
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
        operation.setParameter("roleOrder", new Integer(key));
        operation.setParameter("nextRoleOrder", new Integer(otherKey));
        operation.execute();
    }

    /**
     *
     */
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

    /**
     *
     * @return
     */
    public int maxOrder() {
        GenericOrganization roleOwner = getRoleOwner();
        if (roleOwner == null) {
            return 0;
        }
        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allRoleOrderForOrganization");
        query.setParameter("ownerID", getRoleOwner().getID());
        query.addOrder("roleOrder DESC");
        if (query.next()) {
            Integer roleOrder = ((Integer) query.get("roleOrder"));
            query.close();
            if (roleOrder != null) {
                returnOrder = roleOrder.intValue();
            }
        }

        return returnOrder;
    }

    /**
     * Called before the object is saved to the database.
     */
    @Override
    public void beforeSave() {
        super.beforeSave();
        if (getOrder() == null) {
            setOrder(maxOrder() + 1);
        }
    }
}
