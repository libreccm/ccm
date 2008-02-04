package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.london.cms.freeform.asset.FreeformBinaryAsset;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.Label;

import com.arsdigita.cms.ItemSelectionModel;



/**
 * A Bebop component for editing a {@link FreeformBinaryAsset}.
 *
 * @author <a href="phong@arsdigita.com">Phong Nguyen</a>
 **/
public class FreeformBinaryAssetEdit extends FileUploadForm {

    private static final String NAME = "name";
    private static final String DESC = "desc";
    
    private ItemSelectionModel m_itemModel;
    private ItemSelectionModel m_assetModel;
    private ViewAssets m_view;

    private SaveCancelSection m_saveCancel;

    private TextField m_name;
    private TextField m_desc;


    /**
     * Constructor.
     *
     * @param itemModel  The item model that represents the {@link FreeformContentItem}.
     * @param assetModel The model that represents the binary asset we are editing.
     **/
    public  FreeformBinaryAssetEdit(SingleSelectionModel assetModel, 
                                    ItemSelectionModel itemModel, ViewAssets view) {
        super(new ColumnPanel(2));

        m_itemModel = itemModel;
        m_assetModel = new ItemSelectionModel(assetModel);
        m_view = view;

        addInitListener(new FormInitListener() {
            public void init(FormSectionEvent event) {
                PageState state = event.getPageState();

                FreeformBinaryAsset asset = 
                    (FreeformBinaryAsset)m_assetModel.getSelectedObject(state);
                if (asset != null) {
                    m_name.setValue(state, asset.getName());
                    m_desc.setValue(state, asset.getDescription());                    
                }
            }
        });
    }

    /**
     * Returns the cancel button.
     **/
    public Submit getCancelButton() {
        return m_saveCancel.getCancelButton();
    }

    /**
     * Adds a component which displays the properties of the asset at
     * the top of the form.
     **/
    protected void addWidgets() {

        m_name = new TextField(new StringParameter("name"));
        add(new Label("Name: "));
        add(m_name);
        
        m_desc = new TextField(new StringParameter(DESC));
        add(new Label("Description: "));
        add(m_desc);        
        
        super.addWidgets();
    }
    
    /**
     * Adds the submit button to this form.
     **/
    protected void addSubmitButton() {
        m_saveCancel = new SaveCancelSection();
        add(m_saveCancel, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);
    }
        
    /**
     * Returns the modified or newly created FreeformBinaryAsset.
     *
     * @param state The state of the current request.
     **/
    protected FreeformBinaryAsset getBinaryAsset(FormSectionEvent event) {
        PageState state = event.getPageState();

        FreeformBinaryAsset asset = (FreeformBinaryAsset)m_assetModel.getSelectedObject(state);
        if (asset == null) {
            asset = new FreeformBinaryAsset();
        }

        java.io.File file = getFileUploadSection().getFile(event);
        if (file != null) {
            try {
                asset.readBytes(new java.io.FileInputStream(file));
            } catch (java.io.FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not find file: " + e.getMessage());
            } catch (java.io.IOException e) {
                e.printStackTrace();
                throw new RuntimeException("An error occured: " + e.getMessage());
            }
        }
        

        String name = (String)m_name.getValue(state);
        String desc = (String)m_desc.getValue(state);
        if (name == null) {
            name = getFileUploadSection().getFileName(event);
        }

        asset.setName(name);
        asset.setDescription(desc);


        return asset;
    }


}
