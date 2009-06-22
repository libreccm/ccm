package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.contenttypes.*;
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
 * This class represents all relationship between an organization and an
 * organizational unit. 
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class Orga2OrgaUnit extends ACSObject {

    private final static Logger logger = Logger.getLogger(Orga2OrgaUnit.class);

    /**
     * Id for the targetItem property.
     */
    public final static String TARGETITEM = "targetItem";

    /**
     * Id for the unitOwner property.
     */
    public final static String UNITOWNER = "unitOwner";

    /**
     * Id for the unitOrder property.
     */
    public final static String UNITORDER = "unitOrder";

    /**
     * Object type
     */
    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.Orga2OrgaUnit";

    /**
     * Constrcutor without parameters for creating an new Orga2OrgaUnit relation.
     */
    public Orga2OrgaUnit() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Tries to find the Orga2OrgaUnit relation with the given {@code id} in the database.
     *
     * @param id Of an existing organization to organizational unit relation.
     */
    public Orga2OrgaUnit(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Tries to find the Orga2OrgaUnit relation with the {@code id} in the database.
     *
     * @param id OID Of an existing organization to organizational unit relation.
     */
    public Orga2OrgaUnit(OID id) {
        super(id);
    }

    /**
     * Creates an new instance of this class from {@code obj}
     *
     * @param obj 
     */
    public Orga2OrgaUnit(DataObject obj) {
        super(obj);
    }

    /**
     *
     * @param type Type of the object to create.
     */
    public Orga2OrgaUnit(String type) {
        super(type);
    }

    /**
     *
     * @return The GenericOrganization which is part of the relation.
     */
    public GenericOrganization getUnitOwner() {
        DataObject obj = (DataObject) get(UNITOWNER);
        if (obj == null) {
            return null;
        } else {
            return (GenericOrganization) DomainObjectFactory.newInstance(obj);
        }
    }

    /**
     * Sets the owing organization.
     *
     * @param orga The owning organization.
     */
    public void setUnitOwner(GenericOrganization orga) {
        Assert.exists(orga, GenericOrganization.class);
        logger.debug(String.format("Setting unit owner to %s", orga.getOrganizationName()));
        setAssociation(UNITOWNER, orga);
    }

    /**
     *
     * @return The OrganizationalUnit which is part of the relation.
     */
    public OrganizationalUnit getTargetItem() {
        DataObject object = (DataObject) get(TARGETITEM);
        return (OrganizationalUnit) DomainObjectFactory.newInstance(object);
    }

    /**
     * Sets the OrganizationalUnit
     *
     * @param unit
     */
    public void setTargetItem(OrganizationalUnit unit) {
        setAssociation(TARGETITEM, unit);
    }

    /**
     *
     * @return The order of the the relations.
     */
    public Integer getOrder() {
        return (Integer) get(UNITORDER);
    }

    /**
     * Sets the order.
     *
     * @param order
     */
    public void setOrder(Integer order) {
        Assert.exists(order);
        set(UNITORDER, order);
    }

    /**
     *
     * @param s Current PageState.
     * @return The URI of the targetItem.
     */
    public String getURI(PageState s) {
        OrganizationalUnit unit = getTargetItem();

        if(unit == null) {
            logger.error(getOID() + "is a link between an organization and an organizational unit, but the associated organizational unit is null");
            return "";
        }

        ContentSection section = unit.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(s, unit, section, unit.getVersion());

        return URL.there(s.getRequest(), url).toString();
    }

    /**
     *
     * @param unit
     * @return All Orga2OrgaUnit relations for a specific OrganizationalUnit
     */
    public static DataCollection getReferingUnits(OrganizationalUnit unit) {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = units.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getReferingUnits");
        filter.set("itemID", unit.getID());

        return units;
    }

    /**
     *
     * @param orga
     * @return All units associated with an GenericOrganization.
     */
    public static DataCollection getUnits(GenericOrganization orga) {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        units.addEqualsFilter(UNITOWNER + ".id", orga.getID());
        units.addOrder(UNITORDER);
        return units;
    }

    /**
     * Swaps an Orga2OrgaUnit relation with the next one in the list.
     */
    public void swapWithNext() {
        swapWithNext("com.arsdigita.cms.contenttypes.allUnitsOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrga2OrgaUnitWithNextInGroup");
    }

    /**
     * Swaps an Orga2OrgaUnit relation with the previous one in the list.
     */
    public void swapWithPrevious() {
        swapWithPrevious("com.arsdigita.cms.contenttypes.allUnitsOrderForOrganization", "com.arsdigita.cms.contenttypes.swapOrga2OrgaUnitWithNextInGroup");
    }

    /**
     * Swaps an Orga2OrgaUnit relation with the next one in the list.
     *
     * @param queryName Query to use.
     * @param operationName Operation to use.
     */
    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName);
    }

    /**
     * Swaps an Orga2OrgaUnit relation with the previous one in the list.
     *
     * @param queryName Query to use.
     * @param operationName Operation to use.
     */
    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName);
    }

    /**
     *
     * @param queryName Name of the SwapQuery.
     * @return The query named with @queryName
     */
    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("ownerID", getUnitOwner().getID());
        return query;
    }

    /**
     *
     * @param operationName Name of the operation.
     * @return Returns the DataOperation for swapping.
     */
    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().retrieveDataOperation(operationName);
        operation.setParameter("ownerID", getUnitOwner().getID());
        return operation;
    }

    /**
     * The method which does the real swapping.
     *
     * @param swapNext If true, swap with next, if false, swap with previous
     * @param queryName Name of the DataQuery to use.
     * @param operationName Name of the DataOperation to use.
     */
    protected void swapKeys(boolean swapNext, String queryName, String operationName) {

        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + " cannot be called on an object that is new");

        Integer currentKey = (Integer) get(UNITORDER);

        if(currentKey == null) {
            alphabetize();
            return;
        }

        Assert.isTrue(currentKey != null, methodName + " cannot be " +
                "called on an object that is not currently in the " +
                "list");

        int key = currentKey.intValue();

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

        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("unitOrder", new Integer(key));
        operation.setParameter("nextUnitOrder", new Integer(otherKey));
        operation.execute();
    }

    /**
     * Don't kwow what this is for, but it is needed for the swapping
     */
    protected void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        units.addEqualsFilter(UNITOWNER + ".id", getUnitOwner().getID());
        int sortkey = 0;
        while (units.next()) {
            sortkey++;
            Orga2OrgaUnit o2ou = new Orga2OrgaUnit(units.getDataObject());
            o2ou.setOrder(sortkey);
            o2ou.save();
        }
    }

    /**
     *
     * @return Maximum order index.
     */
    public int maxOrder() {
        GenericOrganization unitOwner = getUnitOwner();
        if (unitOwner == null) {
            return 0;
        }

        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allUnitsOrderForOrganization");
        query.setParameter("ownerID", getUnitOwner().getID());
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

    /**
     * Invoked before saving to the database.
     */
    @Override
    public void beforeSave() {
        super.beforeSave();
        if (getOrder() == null) {
            setOrder(maxOrder() + 1);
        }
    }
}
