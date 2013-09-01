/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.cms.contenttypes.ContentItemTraversalAdapter;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.persistence.metadata.Property;

/**
 * 
 * 
 */
public class DecisionTreeTraversalAdapter extends ContentItemTraversalAdapter {

    private static final Logger s_log = Logger.getLogger(DecisionTreeTraversalAdapter.class);

    /**
     * 
     */
    public DecisionTreeTraversalAdapter() {
    }

    /**
     * 
     * @param adapter 
     */
    public DecisionTreeTraversalAdapter(SimpleDomainObjectTraversalAdapter adapter) {
        super(adapter);
    }

    /**
     * 
     * @param obj
     * @param path
     * @param prop
     * @param context
     * @return 
     */
    @Override
    public boolean processProperty(DomainObject obj, String path,
                                   Property prop, String context) {

        HttpServletRequest request = DispatcherHelper.getRequest();

        if ("/object/sections/title".equals(path) || "/object/sections/instructions".equals(path)
            || "/object/sections/sectionOptions".equals(path)) {
            // Only include one TreeSection in the output.
            // Which one depends on the section_id parameter.
            DecisionTreeSection section = (DecisionTreeSection) obj;
            DecisionTreeSection selectedSection = null;
            String sectionID = request.getParameter("section_id");
            if (sectionID == null || "".equals(sectionID)) {
                DecisionTree tree = section.getTree();
                selectedSection = tree.getFirstSection();

                if (selectedSection == null) {
                    throw new RuntimeException("The first section has not been set for tree " + tree);
                }
            } else {
                try {
                    selectedSection = new DecisionTreeSection(new BigDecimal(sectionID));
                } catch (DataObjectNotFoundException e) {
                    throw new RuntimeException("Cannot find section for section_id parameter "
                                               + sectionID);
                }
            }

            return section.equals(selectedSection);
        } else {
            return super.processProperty(obj, path, prop, context);
        }
    }

}
