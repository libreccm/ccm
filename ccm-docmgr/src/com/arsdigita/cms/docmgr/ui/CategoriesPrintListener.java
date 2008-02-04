/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.categorization.CategoryTreeModelLite;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.kernel.ui.DataQueryTreeNode;

/* CategoriesPrintListener displays categories for a given
 * content section.  May be used in conjunction with an
 * item-edit form, so that assigned categories are preselected.
 * 
 * @author Crag Wolfe
 */
public class CategoriesPrintListener implements PrintListener {

    private final ContentSection m_docsContentSection;
    private final BigDecimalParameter m_selectedFileParam;
    private final boolean m_showRoot;

    private static final String SEPARATOR = ">";

    public CategoriesPrintListener(ContentSection docsContentSection) {
        this(docsContentSection, null);
    }

    public CategoriesPrintListener(ContentSection docsContentSection,
                                   BigDecimalParameter selectedFileParam) {
        this(docsContentSection, selectedFileParam, false);
    }
   
    public CategoriesPrintListener(ContentSection docsContentSection,
                                   BigDecimalParameter selectedFileParam, 
                                   boolean showRoot) {
        m_docsContentSection = docsContentSection;
        m_selectedFileParam = selectedFileParam;
        m_showRoot = showRoot;
    }

    public void prepare(PrintEvent e) {
        OptionGroup o = (OptionGroup)e.getTarget();
        PageState state = e.getPageState();
        Category root = 
            m_docsContentSection.getRootCategory();

        // Breadth-first traversal of the teee
        CategoryTreeModelLite model = new CategoryTreeModelLite(root);
        //CategoryMap assigned = getAssignedCategories(state);
        LinkedList queue = new LinkedList(), nameQueue = new LinkedList();

        //if (root.isAbstract()) {
        queue.addLast(model.getRoot(state));
        nameQueue.addLast("");
        //} else {
        // queue.addLast(new DataQueryTreeNode
        //              (root.getID(), root.getName(), true));
        //nameQueue.addLast(root.getName());
        //}

        HashSet assignedIDs = new HashSet();
        if (m_selectedFileParam != null) {
            BigDecimal id = (BigDecimal) state.getValue(m_selectedFileParam);
            if (id != null) {
                ContentItem ci = new ContentItem(id);
	    CategoryCollection cats = ci.getCategoryCollection();
	    Category cat;
	    if (cats.next()) {
		cat = cats.getCategory();
		String catID = cat.getID().toString();
		assignedIDs.add(catID);
                }
	    }
        }
        
        boolean firstLoop = true;
        while(!queue.isEmpty()) {
            DataQueryTreeNode node = (DataQueryTreeNode)queue.removeFirst();
            String name = (String)nameQueue.removeFirst();

            // Process the node
            String id = (String)node.getKey();
                
            if (firstLoop && m_showRoot) {
                // can't assign to root category
                o.addOption(new Option(id, name));
            } else if (!firstLoop) {
                o.addOption(new Option(id, name));
            }

            if (model.hasChildren(node, state)) {
                // Append children
                for(Iterator i = model.getChildren(node, state); i.hasNext(); ) {
                    TreeNode n = (TreeNode)i.next();
                    queue.addLast(n);
                    StringBuffer nameBuf = new StringBuffer(name);
                    if(name.length() > 0) {
                        nameBuf.append(SEPARATOR);
                    }
                    nameBuf.append(n.getElement());
                    nameQueue.addLast(nameBuf.toString());
                }
            }
            firstLoop = false;
        }
            
    }

    //addFillerOption(o);
}
