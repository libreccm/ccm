/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.navigation.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentItemComponent extends GreetingItem {

    private String contentItemOID;

    @Override
    public ACSObject getObject() {

        final DataObject dataObject = SessionManager
            .getSession()
            .retrieve(OID.valueOf(contentItemOID));

        final ContentItem item = new ContentItem(dataObject);
        if (item.isDraftVersion()) {
            return (ContentItem) item.getLiveVersion().getParent();
        } else {
            return (ContentItem) item.getParent();
        }
    }

    @Override
    protected String getElementName() {
        return "contentItem";
    }
    
    public String getContentItemOID() {
        return contentItemOID;
    }

    public void setContentItemOID(final String contentItemOID) {
        this.contentItemOID = contentItemOID;
    }

}
