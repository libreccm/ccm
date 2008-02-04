package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.persistence.DataCollection;


public class DataCollectionListModel implements ListModel {
    private DataCollection m_coll;

    public DataCollectionListModel(DataCollection coll) {
	m_coll = coll;
    }

    public Object getElement() {
	return m_coll.getDataObject();
    }

    public String getKey() {
	return m_coll.get("id").toString();
    }

    public boolean next() {
        return m_coll.next();
    }
}
