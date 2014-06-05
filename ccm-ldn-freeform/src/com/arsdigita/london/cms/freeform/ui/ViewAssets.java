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

import com.arsdigita.london.cms.freeform.FreeformContentItem;
import com.arsdigita.london.cms.freeform.asset.FreeformBinaryAsset;

import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableColumn;


/**
 * An authoring kit step to view the assets of a Freeform content
 * type.
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @version $Revision: #2 $
 */
public class ViewAssets extends ResettableContainer {

    // $Source: /cvsroot/content-types/apps/freeform/src/ui/ViewAssets.java,v $
    // $Revision: #2 $
    // $Date: 2003/08/28 $
    // $Author: sskracic $

    // Keys used for the containers that hold the actual component and
    // associated links.
    public static final String ASSET_TABLE_KEY = "atk";
    public static final String ASSET_VIEW_KEY = "avk";
    public static final String TEXT_ASSET_EDIT_KEY = "taek";
    public static final String BINARY_ASSET_EDIT_KEY = "baek";
        
    private final AuthoringKitWizard m_kitWizard;
    private final ItemSelectionModel m_itemModel;
    private ItemSelectionModel m_assetModel;

    private FreeformAssetTable m_assetTable;
    private FreeformAssetView m_assetView;
    private FreeformTextAssetEdit m_textAssetEdit;
    private FreeformBinaryAssetEdit m_binaryAssetEdit;

    /**
     * Constructor.
     *
     * @param itemModel The {@link ItemSelectionModel} which returns the
     * {@link FreeformContentItem} that is being authored.
     * @param parent The {@link AuthoringKitWizard} handling this step.
     **/
    public ViewAssets(ItemSelectionModel itemModel,
                      AuthoringKitWizard parent) {
        super();
        m_itemModel = itemModel;
        m_kitWizard = parent;
        
        add(buildTablePanel(), true);
        add(buildViewPanel(), false);
        add(buildTextEditPanel(), false);
        add(buildBinaryEditPanel(), false);
    }


    /**
     * Builds a container which holds a {@link AssetTable} and a link
     * to add a new {@link Asset}.
     * 
     * @return 
     */
    protected Container buildTablePanel() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(ASSET_TABLE_KEY);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");

        // for testing
        c.add(new Label("Assets", false));
        c.add(new Label("<p>", false));
        
        // A table to display all assets 
        m_assetTable = new FreeformAssetTable(m_itemModel);
        m_assetTable.setClassAttr("dataTable");
        m_assetModel = new ItemSelectionModel(m_assetTable.getRowSelectionModel());
        Label emptyView = new Label("No assets");
        emptyView.setFontWeight(Label.ITALIC);
        m_assetTable.setEmptyView(emptyView);
        m_assetTable.addTableActionListener(new TableActionListener() {
            @Override
            public void cellSelected(TableActionEvent event) {
                TableColumn tc = m_assetTable.getColumnModel()
                    .get(event.getColumn().intValue());
                String columnName = (String) tc.getHeaderValue();
                if (FreeformAssetTable.COLUMN_NAME.equals(columnName)) {
                    onlyShowComponent(event.getPageState(), ASSET_VIEW_KEY);
                }
            }
            @Override
            public void headSelected(TableActionEvent event) {}
        });

        // The pagination component for the table
        Paginator pgntr = new Paginator
            (m_assetTable.getPaginationModelBuilder(), 5);
        m_assetTable.setPaginator(pgntr);
        c.add(pgntr);
        c.add(m_assetTable);

        c.add(new Label("<p>", false));

        // A link to add a new text asset 
        ActionLink addLink = new ActionLink("Add text asset");
        addLink.setClassAttr("actionLink");
        addLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();

                m_assetModel.clearSelection(state);
                onlyShowComponent(state, TEXT_ASSET_EDIT_KEY);                
            }
        });
        c.add(addLink);

        // A link to add a new binary asset 
        addLink = new ActionLink("Add binary asset");
        addLink.setClassAttr("actionLink");
        addLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();

                m_assetModel.clearSelection(state);
                onlyShowComponent(state, BINARY_ASSET_EDIT_KEY);                
            }
        });
        c.add(addLink);

        return c;
    }


    /**
     * Builds a container which holds the {@link FreeformAssetView}
     * and a link to edit the displayed asset.
     * 
     * @return 
     **/
    protected Container buildViewPanel() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(ASSET_VIEW_KEY);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");

        // A component that displays the asset's properties
        m_assetView = new FreeformAssetView(m_assetModel, m_itemModel);
        c.add(m_assetView);

        // A link to edit the asset
        ActionLink editLink = new ActionLink("Edit asset");
        editLink.setClassAttr("actionLink");
        editLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                PageState state = event.getPageState();
                Asset asset = (Asset)m_assetModel.getSelectedObject(state);

                MimeType mimeType = asset.getMimeType();
                String type;
                if (mimeType == null) {
                    type = "unknown";
                } else {
                    type = mimeType.getMimeType();
                }

                if (type.equals("text/html") || type.equals("text/gopher")) {
                    onlyShowComponent(state, TEXT_ASSET_EDIT_KEY); 
                } else {
                    onlyShowComponent(state, BINARY_ASSET_EDIT_KEY);
                }           
            }
        });
        c.add(editLink);
        
        // A link to view all assets
        ActionLink viewAllLink = new ActionLink("View all assets");
        viewAllLink.setClassAttr("actionLink");
        viewAllLink.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent event) {
                onlyShowComponent(event.getPageState(), ASSET_TABLE_KEY);                
            }

        });
        c.add(viewAllLink);

        return c;
    }


    /**
     * Builds a container which holds the {@link FreeformTextAssetEdit}.
     * 
     * @return 
     */
    protected Container buildTextEditPanel() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(TEXT_ASSET_EDIT_KEY);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");
        
        c.add(new Label(new PrintListener() {
            @Override
            public void prepare(PrintEvent event) {
                PageState state = event.getPageState();
                Label label = (Label)event.getTarget();
                
                if (m_assetModel.getSelectedKey(state) == null) {
                    label.setLabel("Add Text Asset");
                } else {
                    label.setLabel("Edit Text Asset");
                }
            }
        }));
              
        // A component to edit an asset's properties
        m_textAssetEdit = new FreeformTextAssetEdit(m_assetModel, m_itemModel, this);
        c.add(m_textAssetEdit);

        // A link to view all assets
        ActionLink viewAllLink = new ActionLink("View all assets");
        viewAllLink.setClassAttr("actionLink");
        viewAllLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                onlyShowComponent(event.getPageState(), ASSET_TABLE_KEY);                
            }
        });
        c.add(viewAllLink);
        
        return c;
    }


    /**
     * Builds a container which holds the {@link FreeformBinaryAssetEdit}.
     * 
     * @return 
     **/
    protected Container buildBinaryEditPanel() {
        ColumnPanel c = new ColumnPanel(1);
        c.setKey(BINARY_ASSET_EDIT_KEY);
        c.setBorderColor("#ffffff");
        c.setPadColor("#ffffff");
        
        c.add(new Label(new PrintListener() {

            /**
             * 
             * @param event 
             */
            @Override
            public void prepare(PrintEvent event) {
                PageState state = event.getPageState();
                Label label = (Label)event.getTarget();
                
                if (m_assetModel.getSelectedKey(state) == null) {
                    label.setLabel("Add Binary Asset");
                } else {
                    label.setLabel("Edit Binary Asset");
                }
            }
        }));
              
        // A component to edit an asset's properties
        m_binaryAssetEdit = new FreeformBinaryAssetEdit(m_assetModel, m_itemModel, this);
        c.add(m_binaryAssetEdit);

        // Retrieves the newly created or editted asset and updates the item.
        m_binaryAssetEdit.addSubmissionListener(new FormSubmissionListener() {

            public void submitted(FormSectionEvent event) throws FormProcessException {
                PageState state = event.getPageState();

                if (!m_binaryAssetEdit.getCancelButton().isSelected(state)) {
                    FreeformBinaryAsset asset = m_binaryAssetEdit.getBinaryAsset(event);
                    if (asset != null) {
                        asset.save();
                        FreeformContentItem item = (FreeformContentItem)m_itemModel.getSelectedObject(state);
                        item.addAsset(asset, new Integer(0));
                        item.save();
                    }
                }
                onlyShowComponent(state, ASSET_TABLE_KEY);
            };
        });

        // A link to view all assets
        ActionLink viewAllLink = new ActionLink("View all assets");
        viewAllLink.setClassAttr("actionLink");
        viewAllLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                onlyShowComponent(event.getPageState(), ASSET_TABLE_KEY);                
            }
        });
        c.add(viewAllLink);
        
        return c;
    }

}

