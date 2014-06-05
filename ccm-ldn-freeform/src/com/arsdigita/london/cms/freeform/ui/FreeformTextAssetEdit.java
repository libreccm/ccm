/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.cms.Asset;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.TextAssetBody;
import com.arsdigita.london.cms.freeform.FreeformContentItem;
import com.arsdigita.london.cms.freeform.asset.FreeformTextAsset;
import com.arsdigita.db.Sequences;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.Label;
import com.arsdigita.cms.ContentItem;

import java.math.BigDecimal;
import java.sql.SQLException;


/**
 * FreeformTextAssetEdit is a fragment, meant for edit/creating 
 * FreeformTextAssets. 
 * It should ultimately be combined with something that will do the
 * same for FreeformBinaryAssets
 * Modelled after some cms code {@link TextPageBody}
 *
 * @author slater@arsdigita.com
 */
public class FreeformTextAssetEdit extends TextAssetBody {

  //Members
  
  ItemSelectionModel m_FreeformContentItemModel;
  ViewAssets m_viewAssets;

  //Members that are elements of the form extension
  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  //  private TextField m_name;
  private TextField m_description;
  //private static final String NAME_ERROR_MESSAGE = 
  //"Name must be unique and only have letters and numbers";
  

  // Member/Listeners for the extension of the form from TextAssetBody
  private FormInitListener m_ff_InitListener; 
  private FormProcessListener m_ff_ProcessListener; 


  /**
   * @param itemModel  the item model that represents the FreeformContentItem
   * @param assetModel the model that represents the asset we are editing
   **/
  public FreeformTextAssetEdit(SingleSelectionModel assetModel, 
                               ItemSelectionModel itemModel, 
                               ViewAssets viewAssets ) {

    super(new ItemSelectionModel(assetModel));

    m_FreeformContentItemModel = itemModel;
    m_viewAssets = viewAssets;
    
  }


  /**
   * Unlike most other authoring components, this component does not require
   * the asset to exist. If the asset does not exist (i.e., if 
   * <code>!m_assetModel.isSelected(state)</code>), the upload and editing
   * forms will create a new asset and set it in the model by calling
   * <code>setSelectedObject</code> on the asset selection model. Child
   * classes should override the {@link #createTextAsset(PageState)} method
   * in to create a valid text asset.
   * @param s the current page state
   * @return a valid TextAsset
   **/

  protected TextAsset createTextAsset(PageState ps) {
    
    // create the new asset
    FreeformTextAsset t = new FreeformTextAsset();

    // set its particulars
    try {
      
      BigDecimal assetID = Sequences.getNextValue();
      t.setID(assetID);
      t.setName(((ContentItem)m_FreeformContentItemModel.getSelectedObject(ps))
                .getName() + "_freeform_text_" + assetID);

    } catch (SQLException ex) {
      ex.printStackTrace();
      throw new UncheckedWrapperException(ex);
    }


    // TODO: 
    //
    // [ ] add a widget for editing the rank
    // [ ] add a widget for editing the name


    // Add the new asset to the FreeformContentItem and persist the
    // changes before returning
    FreeformContentItem item = (FreeformContentItem)m_FreeformContentItemModel
                                                    .getSelectedObject(ps);
    t.save();
    item.addAsset(t, new Integer(0));
    item.save();

    return t;    
  }



  /**
   * Adds the options for the mime type select widget of
   * <code>TextPageForm</code> and sets the default mime type.
   **/
  protected void setMimeTypeOptions(SingleSelect mimeSelect) {
    mimeSelect.addOption(new Option("text/html", "HTML Text"));
    mimeSelect.addOption(new Option("text/gopher", "Gopher Text"));
    mimeSelect.setOptionSelected("text/html");
  }


  /* set remaining parameters after text asset has been uploaded */
  protected void updateTextAsset(PageState ps, TextAsset a) {

    // assign it to the parent FreeformContentItem
    a.setParent((ContentItem)m_FreeformContentItemModel.getSelectedObject(ps));

    //save it.
    a.save();

    // return visibility
    m_viewAssets.onlyShowComponent(ps, m_viewAssets.ASSET_TABLE_KEY);

    //m_FreeformContentItemModel.add(a);
    //m_FreeformContentItemModel.setSelectedObject(ps,a);
    
  }



  /* overridden to provide the scalar attributes */
  protected void addFileWidgets(PageFileForm f) {
    super.addFileWidgets(f);
  }

  /* overriden to provide the scalar attributes */
  protected void addTextWidgets(PageTextForm t) {

    m_ff_InitListener = new FormInitListener() {

      public void init(FormSectionEvent fse) throws FormProcessException {
        PageState ps = fse.getPageState();

        Asset asset = getTextAsset(ps);


          // set the TextField (if it already exists)
        if (asset != null) {
          //m_name.setValue(ps, asset.getName());
           m_description.setValue(ps, asset.getDescription());
        }

      }

    };

    m_ff_ProcessListener = new FormProcessListener() {

      public void process(FormSectionEvent fse) throws FormProcessException {
        PageState ps = fse.getPageState();
        
        Asset asset = getTextAsset(ps);
        

        //asset.setName((String)m_name.getValue(ps));
        asset.setDescription((String)m_description.getValue(ps));
        
        asset.save();
      }

    };


 
    //m_name = new TextField(new StringParameter(NAME));
    //UniqueItemNameValidationListener un = new UniqueItemNameValidationListener(m_name, NAME_ERROR_MESSAGE);
    //UniqueItemNameValidationListener un = new UniqueItemNameValidationListener(m_name);
    //t.addValidationListener(un);

    m_description = new TextField(new StringParameter(DESCRIPTION));

    //t.add(new Label("Name:&nbsp;&nbsp;  ", false));
    //t.add(m_name);
    t.add(new Label("<p>Description:&nbsp;&nbsp; ", false));
    t.add(m_description);

    super.addTextWidgets(t);

    t.add(new FormErrorDisplay(t), ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
   
    // add the other elements to the PageTextForm

    t.addInitListener(m_ff_InitListener);
    t.addProcessListener(m_ff_ProcessListener);

 }


}



