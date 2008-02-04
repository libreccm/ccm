package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.london.cms.freeform.FreeformContentItem;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.DomainObjectSelectionModel;

import java.math.BigDecimal;

/**
 * A selection model which returns an asset for a {@link
 * FreeformContentItem}. This class is used to provide greater
 * flexibility in the placemnt of the {@link FreeformAssetView}
 * component inside a JSP page.
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 **/
public class AssetSelectionModel implements DomainObjectSelectionModel {

    // The default parameter names used in the selection model.
    public static final String ITEM_PARAM_NAME = "item_id";
    public static final String ASSET_PARAM_NAME = "asset_id";
    public static final String RANK_PARAM_NAME = "rank";
    public static final String MIME_TYPE_PARAM_NAME = "mime_type";

    // The selection models used to retrieve the asset.
    private ItemSelectionModel m_itemModel;
    private SingleSelectionModel m_assetModel;
    private SingleSelectionModel m_rankModel;
    private SingleSelectionModel m_mimeTypeModel;

    // Provides caching for the retrieved asset and item.
    private RequestLocal m_asset = new RequestLocal();
    private RequestLocal m_item = new RequestLocal();

    /**
     * Constructor. Creates selection models using the default
     * parameter names.
     **/
    public AssetSelectionModel() {
        m_itemModel = new ItemSelectionModel(ITEM_PARAM_NAME);
        m_assetModel = new ParameterSingleSelectionModel
            (new StringParameter(ASSET_PARAM_NAME));
        m_rankModel = new ParameterSingleSelectionModel
            (new StringParameter(RANK_PARAM_NAME));
        m_mimeTypeModel = new ParameterSingleSelectionModel
            (new StringParameter(MIME_TYPE_PARAM_NAME));
    }

    /**
     * Registers the parameter models used for selecting the asset.
     *
     * @param p The page to register with.
     * @param c The component the parameters will be registered to.
     **/
    public void register(Page p, Component c) {
        p.addComponentStateParam(c, m_itemModel.getStateParameter());
        p.addComponentStateParam(c, m_assetModel.getStateParameter());
        p.addComponentStateParam(c, m_rankModel.getStateParameter());
        p.addComponentStateParam(c, m_mimeTypeModel.getStateParameter());
    }

    /**
     * Returns the selected asset.
     **/
    public Asset getAsset(PageState state) {
        Asset asset = (Asset)m_asset.get(state);

        // Attempt to retrieve the asset directly by its id
        if (asset == null) { 
            String assetId = (String) m_assetModel.getSelectedKey(state);
            if (assetId != null && assetId.length() > 0) {
                try {
                    asset = (Asset)DomainObjectFactory.newInstance
                        (new OID("com.arsdigita.cms.Asset", new BigDecimal(assetId)));
                    m_asset.set(state, asset);

                    FreeformContentItem item = getFreeformContentItem(state);
                    if (item != null && asset != null) {
                        String rank = item.getRank(asset);
                        setRank(state, rank);
                    }

                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Attempt to retrieve an asset with the selected rank and mimetype
        if (asset == null) {
            FreeformContentItem item = getFreeformContentItem(state);
            String rank = getRank(state);
            String mimeType = getMimeType(state);
            if (item != null && rank != null && mimeType != null) {
                asset = item.getOneAsset(mimeType, rank);
            }            
        }

        // Try to get the id from the request url
        if (asset == null) {
            javax.servlet.http.HttpServletRequest request = state.getRequest();
            String assetId = request.getParameter(ASSET_PARAM_NAME);
            if (assetId != null && assetId.length() > 0) {
                try {
                    asset = (Asset)DomainObjectFactory.newInstance
                        (new OID("com.arsdigita.cms.Asset", new BigDecimal(assetId)));
                    m_asset.set(state, asset);

                    FreeformContentItem item = getFreeformContentItem(state);
                    if (item != null && asset != null) {
                        String rank = item.getRank(asset);
                        setRank(state, rank);
                    }

                } catch (DataObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return asset;
    }

    /**
     * Selects the asset.
     * 
     * @param state The state of the current request.
     * @param asset The asset to select.
     **/
    public void setAsset(PageState state, Asset asset) {
        m_asset.set(state, asset);
        m_assetModel.setSelectedKey(state, asset.getID().toString());
    }

    /**
     * Return the {@link FreeformContentItem} that the asset belongs
     * to.
     * 
     * @param state The state of the current request.
     * @return The FreeformContentItem that the asset belongs to.
     **/
    public FreeformContentItem getFreeformContentItem(PageState state) {
        FreeformContentItem item = (FreeformContentItem)m_item.get(state);
        
        if (item == null) {
            item = (FreeformContentItem)m_itemModel.getSelectedObject(state);
            if (item == null) {
                javax.servlet.http.HttpServletRequest request = state.getRequest();
                String itemId = request.getParameter(ITEM_PARAM_NAME);
                if (itemId != null && itemId.length() > 0) {
                    try {
                        item = (FreeformContentItem)DomainObjectFactory.newInstance
                            (new OID("com.arsdigita.london.cms.freeform.FreeformContentItem",
                                     new BigDecimal(itemId)));
                    } catch (DataObjectNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (item != null) {
                m_item.set(state, item);
            }
        }

        return item;
    }

    /**
     * Selects the asset's item.
     *
     * @param state The state of the current request.
     * @param asset The item to select.
     **/
    public void setFreeformContentItem(PageState state, FreeformContentItem item) {
        m_item.set(state, item);
        m_itemModel.setSelectedObject(state, item);
    }


    /**
     * Return the rank of the asset.
     * 
     * @param state The state of the current request.
     * @pre #getAsset(PageState) != null.
     * @return The rank of the asset.
     **/
    public String getRank(PageState state) {
        String rank = (String)m_rankModel.getSelectedKey(state);
        if (rank == null || rank.trim().length() == 0) {
            javax.servlet.http.HttpServletRequest request = state.getRequest();
            rank = request.getParameter(RANK_PARAM_NAME);
        }

        return rank;
    }

    /**
     * Sets the rank of the asset.
     *
     * @param state The state of the current request.
     * @param rank The rank to set.
     **/
    public void setRank(PageState state, String rank) {
        m_rankModel.setSelectedKey(state, rank);
    }

    /**
     * Return the mime type of the asset.
     * 
     * @param state The state of the current request.
     * @return The mime type of the asset.
     **/
    public String getMimeType(PageState state) {
        String mimeType = (String)m_mimeTypeModel.getSelectedKey(state);
        if (mimeType == null || mimeType.trim().length() == 0) {
            javax.servlet.http.HttpServletRequest request = state.getRequest();
            mimeType = request.getParameter(MIME_TYPE_PARAM_NAME);
        }

        return mimeType;
    }

    /**
     * Sets the mime type of the asset.
     *
     * @param state The state of the current request.
     * @param mimeType The mime type to set.
     **/
    public void setMimeType(PageState state, String mimeType) {
        m_mimeTypeModel.setSelectedKey(state, mimeType);
    }


    //-----------------------------------------------------------//
    //              Parameter Model get/set methods              //
    //-----------------------------------------------------------//

    /**
     * Returns the selection model for the item the asset belongs to.
     *
     * @return The selection model for the item the asset belongs to.
     **/
    public ItemSelectionModel getItemModel() {
        return m_itemModel;
    }

    /**
     * Sets the selection model for the item the asset belongs to.
     *
     * @param itemModel The selection model for the item the asset
     * belongs to.
     **/
    public void setItemModel(ItemSelectionModel itemModel) {
        m_itemModel = itemModel;
    }

    /**
     * Returns the selection model for the asset.
     * 
     * @return the selection model for the asset.
     **/
    public SingleSelectionModel getAssetModel() {
        return m_assetModel;
    }

    /**
     * Sets the selection model for the asset.
     *
     * @param assetModell The selection model for the asset.
     **/
    public void setAssetModel(SingleSelectionModel assetModel) {
        m_assetModel = assetModel;
    }

    /**
     * Returns the selection model for the rank of the asset.
     *
     * @return The selection model for the rank of the asset.
     **/
    public SingleSelectionModel getRankModel() {
        return m_rankModel;
    }
    
    /**
     * Sets the selctiom model for the rank of the asset.
     * 
     * @param rankModel The selection model for the rank of the asset.
     **/
    public void setRankModel(SingleSelectionModel rankModel) {
        m_rankModel = rankModel;
    }

    /**
     * Returns the selection model for the mime type of the asset.
     *
     * @return The selection model for the mime type of the asset.
     **/
    public SingleSelectionModel getMimeTypeModel() {
        return m_mimeTypeModel;
    }
    
    /**
     * Sets the selectio model for the mime type of the asset.
     *
     * @param mimeTypeModel The selection Model for the mime type of
     * the asset.
     **/
    public void setMimeTypeModel(SingleSelectionModel mimeTypeModel) {
        m_mimeTypeModel = mimeTypeModel;
    }

    //---------------------------------------------------------------------// 
    //    Interface methods                                                //
    //    - Most of these methods delegate to the asset selection model    //
    //---------------------------------------------------------------------//

    /**
     * Return the item which was selected and loaded from the
     * database, using the values supplied in <code>state</code>.
     *
     * @param state The state of the the current request.
     * @return The selected domain object.
     **/
    public DomainObject getSelectedObject(PageState state) {
        return getAsset(state);
    }

    /**
     * Select the given domain object.
     *
     * @param state The state of the current request.
     * @param object The domain object to select.
     * @pre (object instance of Asset)
     **/
    public void setSelectedObject(PageState state, DomainObject object) {
        m_assetModel.setSelectedKey(state, ((Asset)object).getID().toString());
    }



    /**
     * Return the state parameter which will be used to keep track of
     * the currently selected key.
     **/
    public ParameterModel getStateParameter() {
        return m_assetModel.getStateParameter();
    }

    /**
     * Return the key that identifies the selected element.
     * 
     * @param state The state of the current request.
     * @return A <code>String</code> value.
     **/
    public Object getSelectedKey(PageState state) {
        return m_assetModel.getSelectedKey(state);
    }

    /**
     * Set the selected key. If the key is not in the collection of
     * objects underlying this model, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param state The state of the current request.
     * @param key The key to select.  @throws IllegalArgumentException
     * The supplied key can not be selected in the context of the
     * current request.
     **/
    public void setSelectedKey(PageState state, Object key) {
        m_assetModel.setSelectedKey(state, key);
    }

    /**
     * Clear the selection.
     *
     * @param state The state of the current request.
     **/
    public void clearSelection(PageState state) {
        m_assetModel.clearSelection(state);
        m_rankModel.clearSelection(state);
        m_mimeTypeModel.clearSelection(state);
    }

    /**
     * Return <code>true</code> if there is a selected element.
     *
     * @param state The state of the current request.
     **/
    public boolean isSelected(PageState state) {
        return m_assetModel.isSelected(state);
    }


    /**
     * Add a change listener to the model. The listener's
     * <code>stateChanged</code> is called whenever the selected key
     * changes.
     *
     * @param l A listener to notify when the selected key changes.
     **/
    public void addChangeListener(ChangeListener l) {
        m_assetModel.addChangeListener(l);
    }

    /**
     * Remove a change listener from the model.
     * 
     * @param l The listner to remove.
     **/
    public void removeChangeListener(ChangeListener l) {
        m_assetModel.removeChangeListener(l);
    }

}



