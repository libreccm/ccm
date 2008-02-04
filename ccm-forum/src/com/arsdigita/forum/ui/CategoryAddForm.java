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
package com.arsdigita.forum.ui;


import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.security.UserContext;

import org.apache.log4j.Logger;


/**
 * <b><font color="red">Experimental</font></b>
 * class to create form to add categories and map them to the forum
 * parent category. temporary hack for testing purposes
 *
 * @author <a href=mailto:sarah@arsdigita.com>Sarah Barwig</a>
 * @version $Revision: #9 $ $Author: sskracic $ $DateTime: 2004/08/17 23:26:27 $
 */
public class CategoryAddForm extends Form {
    public static final String versionId =
        "$Id: CategoryAddForm.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger
        (CategoryAddForm.class);

    private TextField m_name;
    private TextArea m_description;

    /**
     * Builds a form to add a category.
     */
    public CategoryAddForm() {
        super("categoryAdd");
        setRedirecting(true);

        add(new Label(Text.gz("forum.ui.name")));
        m_name = new TextField("name");
        m_name.addValidationListener(new NotNullValidationListener());
        add(m_name);

        add(new Label(Text.gz("forum.ui.description")));
        m_description = new TextArea("description");
        m_description.setRows(5);
        m_description.setCols(60);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

        Submit submit = new Submit("Create topic");
        add(submit);

        /*
         * Listener to process form data.  Just adds the categories, then
         * adds mappings.
         */
        addProcessListener
            (new FormProcessListener() {
                    public void process( FormSectionEvent e ) {
                        PageState state = e.getPageState();
                        
                        Forum forum = ForumContext.getContext(state).getForum();

                        String name = (String)m_name.getValue(state);
                        String description = (String)m_description.getValue(state);

                        Category topic = new Category();
                        topic.setName(name);
                        topic.setDescription(description);
                        topic.save();

                        Category parent = forum.getRootCategory();
                        parent.addChild(topic);
                        parent.save();
                        topic.setDefaultParentCategory(parent);
                        topic.save();

                        fireCompletionEvent(state);
                    }
                });

        /*
         * Generates the object id for the new category
         */
        addInitListener
            (new FormInitListener() {
                    public void init( FormSectionEvent e ) {
                        PageState state = e.getPageState();
                        FormData data = e.getFormData();

                        if ( Kernel.getContext().getParty() == null ) {
                            UserContext.redirectToLoginPage(state.getRequest());
                        }
                    }
                }
             );
    }
}
