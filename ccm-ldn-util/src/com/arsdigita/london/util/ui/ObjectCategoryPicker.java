/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.london.util.Categorization;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class ObjectCategoryPicker extends AbstractCategoryPicker {
    private static final Logger s_log =
        Logger.getLogger(ObjectCategoryPicker.class);
    
    public ObjectCategoryPicker(String name) {
        super(name);
    }
    
    protected void addOptions(PageState state,
                              SingleSelect target) {
        ACSObject object = getObject(state);
        String context = getContext(state);
        Category root = Category.getRootForObject(object, context);

        if (null == root) {
            s_log.error("No category root for object " + object.getOID() +
                        " in context " + context);
            return;
        }
        
        Map cats = Categorization.categorySubtreePath(root, " > ");
        Iterator i = cats.keySet().iterator();
        target.addOption(new Option(null, "-- pick one --"));
        while (i.hasNext()) {
            String path = (String)i.next();
            Category cat = (Category)cats.get(path);
            
            target.addOption(new Option(cat.getOID().toString(), 
                                        path));
        }
    }

    protected abstract ACSObject getObject(PageState state);
    protected abstract String getContext(PageState state);

}
