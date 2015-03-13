/*
 * Copyright (C) 2004 Chris Gilbert
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
package com.arsdigita.categorization.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.categorization.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract form for assigning categories to acs_objects. The assigned categories are those
 * specified by the category widget, which is retrieved by the concrete subclass' implementation of
 * getCategoryWidget.
 *
 * The category widget may be an implementation of CategoryWidget, which generates a javascript tree
 * of categories. Implementations need only specify an XML prefix and namespace.
 *
 * The object that is to be assigned to the categories is specified by the concrete subclass'
 * implentation of getObject
 *
 * @author chris.gilbert@westsussex.gov.uk
 *
 *
 */
// this class has been abstracted out from the original cms specific 
// category form in ccm-cms
public abstract class ACSObjectCategoryForm extends Form {

    private Widget m_category;
    private SaveCancelSection m_buttons;

    protected abstract ACSObject getObject(PageState state);

    public ACSObjectCategoryForm(BigDecimalParameter root,
                                 StringParameter mode,
                                 Widget categoryWidget) {
        super("category", new BoxPanel(BoxPanel.VERTICAL));

        m_category = categoryWidget;
        m_category.addValidationListener(new NotNullValidationListener());
        m_buttons = new SaveCancelSection();

        add(m_category);
        add(m_buttons);

        addInitListener(new FormInitListener() {

            @Override
            public void init(FormSectionEvent ev) throws FormProcessException {

                final PageState state = ev.getPageState();
                final ACSObject object = getObject(state);

                final List<BigDecimal> selectedCats = new ArrayList<BigDecimal>();
                final Set<BigDecimal> ancestorCats = new HashSet<BigDecimal>();
                final CategoryCollection categories = new CategorizedObject(object).getParents();
                while (categories.next()) {
                    final Category category = categories.getCategory();
                    selectedCats.add(category.getID());
                    addAncestorCats(ancestorCats, category);
                }
                
                final BigDecimal[][] paramArray = new BigDecimal[2][];
                paramArray[0] = selectedCats.toArray(new BigDecimal[selectedCats.size()]);
                paramArray[1] = ancestorCats.toArray(new BigDecimal[ancestorCats.size()]);

//                m_category.setValue(state,
//                                    selectedCats.toArray(new BigDecimal[selectedCats.size()]));
                m_category.setValue(state, paramArray);
            }
        });

        addProcessListener(new FormProcessListener() {

            @Override
            public void process(FormSectionEvent ev)
                    throws FormProcessException {

                PageState state = ev.getPageState();

                ACSObject object = getObject(state);

                HashSet curSelectedCat = new HashSet();
                CategoryCollection cats = new CategorizedObject(object).getParents();
                while (cats.next()) {
                    curSelectedCat.add(cats.getCategory().getID());
                }

                BigDecimal[] ids = (BigDecimal[]) m_category.getValue(state);
                for (BigDecimal id : ids) {
                    Category cat = (Category) DomainObjectFactory
                            .newInstance(new OID(Category.BASE_DATA_OBJECT_TYPE, id));
                    if (!curSelectedCat.contains(id)) {
                        cat.addChild(object);
                    } else {
                        cat.removeChild(object);
                    }
                }

                fireCompletionEvent(state);
            }
        });
        addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(FormSectionEvent ev)
                    throws FormProcessException {

                PageState state = ev.getPageState();

                if (m_buttons.getCancelButton().isSelected(state)) {
                    fireCompletionEvent(state);
                    throw new FormProcessException("Submission cancelled",
                                                   GlobalizationUtil.globalize(
                                                           "categorization.cancel.msg"));
                }
            }
        });
    }
    
    private void addAncestorCats(final Set<BigDecimal> ancestorCats, final Category category) {
        
        final CategoryCollection ascendants = category.getDefaultAscendants();
        
        while(ascendants.next()) {
            final Category ascendant = ascendants.getCategory();
            ancestorCats.add(ascendant.getID());
            
        }
        
    }
}
