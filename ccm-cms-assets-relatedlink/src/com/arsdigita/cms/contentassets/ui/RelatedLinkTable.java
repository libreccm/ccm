/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.contentassets.RelatedLink;
import com.arsdigita.cms.contenttypes.ui.LinkTable;
import com.arsdigita.cms.contenttypes.ui.LinkSelectionModel;
import com.arsdigita.cms.ItemSelectionModel;

/**
 * Bebop table to display a list of RelatedLinks associated with a ContentItem
 *
 * @version $Revision: #3 $ $Date: 2004/03/30 $
 * @author Scott Seago (sseago@redhat.com)
 */

public class RelatedLinkTable extends LinkTable {
    
    private TableColumn m_typeCol;
    private TableColumn m_sizeCol;

    /**
     * Constructor. Creates a <code>RelatedLinkTable</code> given an
     * <code>ItemSelectionModel</code>  and a
     * <code>LinkSelectionModel</code>, which track the current item
     * and link.
     *
     * @param item The <code>ItemSelectionModel</code> for the current page.
     * @param link The <code>LinkSelectionModel</code> to track the
     * current link
     */
    public RelatedLinkTable(ItemSelectionModel item, 
                            LinkSelectionModel link, 
                            String linkListName) {
        super(item, link);

        setModelBuilder(new RelatedLinkTableModelBuilder(item, linkListName));

        RelatedLinkRenderer rlcr = new RelatedLinkRenderer();
        m_typeCol.setCellRenderer(rlcr);
        m_sizeCol.setCellRenderer(rlcr);

    }
    
    
  /**
   * @see com.arsdigita.cms.contenttypes.ui.LinkTable#addColumns()
   */
  protected void addColumns() {

    super.addColumns();
    
    TableColumnModel model = getColumnModel();
    m_typeCol = new TableColumn(model.size() , "Resource Type");
    model.add(m_typeCol);
    m_sizeCol = new TableColumn(model.size() , "Size");
    model.add(m_sizeCol);
  }
  
  /**
   * TableCellRenderer class for LinkTable
   */
  private class RelatedLinkRenderer implements TableCellRenderer {

      /**
       * 
       * @param table
       * @param state
       * @param value
       * @param isSelected
       * @param key
       * @param row
       * @param column
       * @return 
       */
      public Component getComponent(Table table,
                                    PageState state,
                                    Object value,
                                    boolean isSelected,
                                    Object key,
                                    int row,
                                    int column) {
          RelatedLink link = (RelatedLink) value;
          if (column == m_sizeCol.getModelIndex()) {
              return new Label(link.getResourceSize());
          } else if(column == m_typeCol.getModelIndex()){
              Label l;
              //Check before returning to avoid NPE.
              if(link.getResourceType() != null){
                l = new Label(link.getResourceType().getMimeType());
              } else{
                l = new Label("Unknown");
              }
              return l;
          } else {
              return new Label("What's this , How did i come here ?");
          }
      }
  }

}
