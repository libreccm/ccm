package com.arsdigita.london.cms.freeform;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentPage;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;


/**
 * The content type which allows arbitrary assets to be associated to it as
 * kind of 'body text'.
 * The associations will be 'compositions'
 *
 * @author <a href="mailto:slater@arsdigita.com">Michael Slater</a>
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @version $Id: FreeformContentItem.java 753 2005-09-02 13:22:34Z sskracic $
 **/
public class FreeformContentItem extends ContentPage {

    /** 
     * Examples of the different MimeTypes this FreeformContentItem 
     * would handle as alternative body assets.  Developers would hav
     * to supply support for any news mimetype added. This would include:
     * TODO: what are all the tasks a developer needs to do to
     * add a new mimetype support?
     *
     *  TODO:  this information could be persisted in a DB table
     */
    public static final String HTML = "Text/Html";
    public static final String WML = "Text/WirelessHtml";
    public static final String ADOBE = "Adobe/pdf";


    public static final String ASSETS = "assets";
    public static final String MIMETYPE = "mimetype";
    public static final String RANK = "rank";

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.cms.freeform.FreeformContentItem";

    private static final String ID_NAME = "itemId";
    private static final String QUERY_NAME = 
        "com.arsdigita.london.cms.freeform.FreeformGetAssetAttributes";
    private static final String ASSET_ID = "id";
    private static final String ASSET_NAME = "name";
    private static final String ASSET_DESC = "description";
    private static final String ASSET_RANK = "rank";

    
    /**
     * Default constructor. This creates a new content page.
     */
    public FreeformContentItem() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FreeformContentItem(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>ContentPage.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FreeformContentItem(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
  
    public FreeformContentItem(DataObject obj) {
        super(obj);
    }

    public FreeformContentItem(String type) {
        super(type);
    }    

    /**
     * Retrieves all assets with the specified mime type and returns
     * the one with the specified ranking.
     **/
    public Asset getOneAsset(String mimeType, String rank) {
        Asset asset = null;
        int count = Integer.parseInt(rank);
        
        DataAssociationCursor dac = ((DataAssociation)get(ASSETS)).cursor();
        dac.addEqualsFilter("assets.asset.mimeType.mimeType", mimeType);

        while (dac.next()) {
            if (count == 0) {
                Link link = new Link(dac.getDataObject());
                asset = link.getAsset();
                break;
            }
            count--;
        }

        dac.close();
        return asset;
    }

    /** 
     * Associate a Freeform[Text|Binary]Asset with this item and set its rank. 
     * Don't handle rank conflicts, holes here. Handle them in a higher-order 
     * method.
     *
     * @param oneAsset The asset to add
     * @param rank The rank for the asset. Setting this to 0 will cause
     * the rank to be generated from max of ranks + 1.
     **/
    public void addAsset( Asset oneAsset, Integer rank ) {
        
        if (rank.intValue() == 0) {
            DataQuery query = SessionManager.getSession().retrieveQuery(QUERY_NAME);
            query.setParameter(ID_NAME, getID());
            query.addOrder(ASSET_RANK + " desc");
            
            if (!query.next()) {
                rank = new Integer(1);
            } else {
                rank = new Integer(((BigDecimal)query.
                                    get(ASSET_RANK)).intValue() + 1);
            }
            query.close();
        }

        Link link = Link.create(oneAsset, rank);

        DataAssociation da = (DataAssociation)get(ASSETS);
        link.addToAssociation(da);
        da.close();
    }        

    /** 
     * remove an asset from associationw with the FreeformContentItem 
     *
     *  TODO: what to do if the asset isn't already associated ?  
     **/
    public void removeAsset( Asset oneAsset ) {                
        DataAssociationCursor da = ((DataAssociation)get(ASSETS)).cursor();
        da.addEqualsFilter("assets.asset.id", oneAsset.getID());
        if (da.next()) {
            da.remove();
        }
        da.close();
    }
        
    /**
     * Returns the rank of an asset.
     * 
     * @param oneAsset The asset to retrieve the rank for.
     * @return The rank of the asset.
     **/
    public String getRank( Asset oneAsset ) {
        DataAssociationCursor da = ((DataAssociation)get(ASSETS)).cursor();
        da.addEqualsFilter("assets.asset.id", oneAsset.getID());

        Integer rank = null;
        if (da.next()) {
            Link link = new Link(da.getDataObject());
            rank = link.getRank();
        }
        return rank == null ? null : rank.toString();
    }
        
    /**
     * Changes the rank of an asset by swapping the rank value with an
     * adjacent asset.
     *
     * @param assetId The id of the asset.
     * @param moveUp Increase the ranking if this is
     * <code>true</code>. A value of <code>false</code> will decrease
     * the ranking.
     **/
    public void changeAssetRank(BigDecimal assetId, boolean moveUp) {

        BigDecimal prevID = null;
        BigDecimal currID = null;
        BigDecimal prevRank = null;
        BigDecimal currRank = null;

        DataQuery query = SessionManager.getSession().retrieveQuery(QUERY_NAME);
        query.setParameter(ID_NAME, getID());
        query.addOrder(ASSET_RANK + " asc");
        while (query.next()) {
            prevID = currID;
            prevRank = currRank;

            currID = (BigDecimal)query.get(ASSET_ID);
            currRank = (BigDecimal)query.get(ASSET_RANK);

            if ((assetId.equals(currID) && moveUp) ||
                (assetId.equals(prevID) && !moveUp)) {
            
                query.close();

                Session ssn = SessionManager.getSession();
                DataOperation op = ssn.retrieveDataOperation
                    ("com.arsdigita.london.cms.freeform.updateAssetRank");
                op.setParameter("itemId", getID());

                op.setParameter("assetId", prevID);
                op.setParameter("rank", currRank);
                op.execute();

                op.setParameter("assetId", currID);
                op.setParameter("rank", prevRank);
                op.execute();

                break;
            }
        }
    }
    
    private static class Link extends DomainObject {
        private static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.london.cms.freeform.FreeformContentItemAsset";

        private static final String ITEM_ID = "itemID";
        private static final String ASSET_ID = "assetID";
        private static final String ASSET = "asset";
        private static final String RANK = "rank";

        private Link() {
            super(BASE_DATA_OBJECT_TYPE);
        }

        public Link(DataObject obj) {
            super(obj);
        }
        
        public static Link create(/*FreeformContentItem item,*/
                           Asset asset,
                           Integer rank) {
            Link link = new Link();
            //link.set(ITEM_ID, item.getID());
            //set(ASSET_ID, asset.getID());
            link.set(ASSET, asset);
            link.set(RANK, rank);
            return link;
        }
        
        public Asset getAsset() {
            return (Asset)DomainObjectFactory
                .newInstance((DataObject)get(ASSET));
        }

        public Integer getRank() {
            return (Integer)get(RANK);
        }
    }
}
  
  










