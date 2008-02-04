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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.dispatcher.StreamAsset;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

/**
 *
 * Builds a table model of contacts
 *
 * @author Richard Su (richard.su@alum.mit.edu)
 * @version $Id: FileAttachmentModelBuilder.java 1592 2007-06-21 16:48:55Z lbcfrancois $
 *
 **/
public class FileAttachmentModelBuilder extends AbstractTableModelBuilder {

    private Table m_table;
    private ItemSelectionModel m_model;
    /**
     * Constructor
     **/
    public FileAttachmentModelBuilder(ItemSelectionModel model) {
        super();
        m_model = model;
    }

    /**
     * @return a TableModel represented by the PageState
     **/
    public TableModel makeModel(Table table, PageState state) {
        m_table = table;
        return new FileAttachmentTableModel(getDataCollection(state));
    }

    /**
     * @return a data collection of Contacts
     **/
    public DataCollection getDataCollection(PageState state) {
        ContentItem item = (ContentItem) m_model.getSelectedObject(state);

        return FileAttachment.getAttachments(item);
    }

    public boolean isVisible(PageState state) {
        return m_table.isVisible(state);
    }

    static class FileAttachmentTableModel implements TableModel {

        private DataCollection _collection;
        private FileAttachment _file;

        /**
         * Constructor
         **/
        public FileAttachmentTableModel(DataCollection collection) {
            _collection = collection;
        }

        /**
         * @return the number of columns in the table model
         **/
        public int getColumnCount() {
            return 1;
        }

        /**
         * @return true if there is more rows in this table model and
         * false otherwise
         **/
        public boolean nextRow() {
            if (_collection == null) {
                return false;
            }

            boolean b = _collection.next();
            if ( b == true ) {
                // there is more rows in this collection
                DataObject _object = _collection.getDataObject();
                _file = new FileAttachment(_object);
            } else {
                // there are no more rows, close the collection
                _collection.close();
            }
            return b;
        }

        /**
         * @return the Object for the specified column for the
         * the current row
         **/
        public Object getElementAt(int columnIndex) {
            if (columnIndex == 4) {
            	if(FileAttachment.getConfig().isShowAssetIDEnabled()){
            		// TODO provide API for asset URL
            		ParameterMap params = new ParameterMap();
            		params.setParameter(StreamAsset.ASSET_ID, _file.getID());
            		return URL.there( Utilities.getServiceURL()+"download/asset",
                                  params ).getURL();
            	}else{
            		return _file.getDescription();
            	}
            } else {
                return null;
            }
        }

        /**
         * @returns the Object key for the specified column
         **/
        public Object getKeyAt(int columnIndex) {
            return _file.getID().toString();
        }

        public long size() {
            return _collection.size();
        }

    }

}
