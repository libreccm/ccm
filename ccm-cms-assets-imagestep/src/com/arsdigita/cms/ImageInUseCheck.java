/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import static com.redhat.persistence.Expression.value;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Koalamann
 */
public class ImageInUseCheck implements ImageInUseChecker {

    @Override
    public boolean isImageInUse(ReusableImageAsset image) {

        DataQuery q = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getAllImageUser");
        q.setParameter("image_id", image.getID());

        if (q.isEmpty()) {
            return false;
        }

        return true;
    }

    /*
     * get all users(content-items) that are using the image.
     *   
     */
    @Override
    public List getImageUsers(ReusableImageAsset image) {
        ArrayList list = new ArrayList();
            DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.cms.contentassets.getAllImageUser");
        query.setParameter("image_id", image.getID());
        
        while (query.next()) {
            Object itemID = query.get("itemID");
            list.add(itemID);
        }

        return list;
    }
}
