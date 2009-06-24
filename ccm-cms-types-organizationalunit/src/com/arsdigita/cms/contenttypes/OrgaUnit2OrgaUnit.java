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
public class OrgaUnit2OrgaUnit extends ACSObject {

    private final static Logger logger = Logger.getLogger(OrgaUnit2OrgaUnit.class);
    public final static String TARGETITEM = "targetItem";
    public final static String UNITOWNER = "unitOwner";
    public final static String UNITORDER = "unitOrder";
    public final static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.OrgaUnit2OrgaUnit";

    public OrgaUnit2OrgaUnit() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public OrgaUnit2OrgaUnit(DataObject obj) {
        super(obj);
    }

    public OrgaUnit2OrgaUnit(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public OrgaUnit2OrgaUnit(OID id) {
        super(id);
    }

    public OrgaUnit2OrgaUnit(String type) {
        super(type);
    }

    public OrganizationalUnit getUnitOwner() {
        DataObject obj = (DataObject) get(UNITOWNER);
        if (obj == null) {
            return null;
        } else {
            return (OrganizationalUnit) DomainObjectFactory.newInstance(obj);
        }
    }

    public void setUnitOwner(OrganizationalUnit ou) {
        Assert.exists(ou, OrganizationalUnit.class);
        setAssociation(UNITOWNER, ou);
    }

    public OrganizationalUnit getTargetItem() {
        DataObject obj = (DataObject) get(TARGETITEM);
        return (OrganizationalUnit) DomainObjectFactory.newInstance(obj);
    }

    public void setTargetItem(OrganizationalUnit ou) {
        setAssociation(TARGETITEM, ou);
    }

    public Integer getOrder() {
        return (Integer) get(UNITORDER);
    }

    public void setOrder(Integer order) {
        Assert.exists(order);
        set(UNITORDER, order);
    }

    public String getURI(PageState state) {
        OrganizationalUnit ou = this.getTargetItem();

        if (ou == null) {
            logger.error(getOID() + " is a link between two organizational units, but the associated organizational unit is null");
            return "";
        }

        ContentSection section = ou.getContentSection();
        ItemResolver resolver = section.getItemResolver();
        String url = resolver.generateItemURL(state, ou, section, ou.getVersion());

        return URL.there(state.getRequest(), url).toString();
    }

    public static DataCollection getReferingUnits(OrganizationalUnit unit) {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = units.addInSubqueryFilter("id", "com.arsdigita.cms.contenttypes.getReferingOrgaUnits");
        filter.set("itemID", unit.getID());

        return units;
    }

    public static DataCollection getUnits(OrganizationalUnit ou) {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        units.addEqualsFilter(UNITOWNER + ".id", ou.getID());
        units.addOrder(UNITORDER);
        return units;
    }

    public void swapWithNext() {
        swapWithNext("com.arsdigita.cms.contenttypes.allUnitsOrderForUnit", "com.arsdigita.cms.contenttypes.swapOrgaUnit2OrgaUnitWithNextInGroup");
    }

    public void swapWithPrevious() {
        swapWithPrevious("com.arsdigita.cms.contenttypes.allUnitsOrderForUnit", "com.arsdigita.cms.contenttypes.swapOrgaUnit2OrgaUnitWithNextInGroup");
    }

    public void swapWithNext(String queryName, String operationName) {
        swapKeys(true, queryName, operationName);
    }

    public void swapWithPrevious(String queryName, String operationName) {
        swapKeys(false, queryName, operationName);
    }

    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = SessionManager.getSession().retrieveQuery(queryName);
        query.setParameter("ownerID", getUnitOwner().getID());
        return query;
    }

    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = SessionManager.getSession().retrieveDataOperation(operationName);
        operation.setParameter("ownerID", getUnitOwner().getID());
        return operation;
    }

    protected void swapKeys(boolean swapNext, String queryName, String operationName) {
        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + " cannot be called on an object that is new");

        Integer currentKey = (Integer) get(UNITORDER);

        if (currentKey == null) {
            alphabetize();
            return;
        }

        Assert.isTrue(currentKey != null, methodName + " cannot be " +
                "called on an object that is not currently in the " +
                "list");

        int key = currentKey.intValue();

        DataQuery query = getSwapQuery(queryName);

        int otherKey;
        if (swapNext) {
            otherKey = key + 1;
            query.addOrder("unitOrder ASC");
            query.addFilter(query.getFilterFactory().greaterThan("unitOrder", currentKey, true));
        } else {
            otherKey = key - 1;
            query.addOrder("unitOrder DESC");
            query.addFilter(query.getFilterFactory().lessThan("unitOrder", currentKey, true));
        }

        if (query.next()) {
            otherKey = ((Integer) query.get("unitOrder")).intValue();
            query.close();
        }

        DataOperation operation = getSwapOperation(operationName);
        operation.setParameter("unitOrder", new Integer(key));
        operation.setParameter("nextUnitOrder", new Integer(otherKey));
        operation.execute();
    }

    protected void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection units = session.retrieve(BASE_DATA_OBJECT_TYPE);
        units.addEqualsFilter(UNITOWNER + ".id", getUnitOwner().getID());
        int sortkey = 0;
        while (units.next()) {
            sortkey++;
            OrgaUnit2OrgaUnit ou2ou = new OrgaUnit2OrgaUnit(units.getDataObject());
            ou2ou.setOrder(sortkey);
            ou2ou.save();
        }
    }

    public int maxOrder() {
        OrganizationalUnit unitOwner = getUnitOwner();
        if (unitOwner == null) {
            return 0;
        }

        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery("com.arsdigita.cms.contenttypes.allUnitsOrderForUnit");
        query.setParameter("ownerID", getUnitOwner().getID());
        query.addOrder("unitOrder DESC");
        if (query.next()) {
            Integer unitOrder = ((Integer) query.get("unitOrder"));
            query.close();
            if (unitOrder != null) {
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