/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.navigation.ui.object;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.DataCollectionRenderer;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoriesObjectList extends AbstractComponent {

    private String objectType;
    private DataCollectionRenderer renderer;

    public CategoriesObjectList() {
        super();
        objectType = ContentItem.BASE_DATA_OBJECT_TYPE;
    }
    
    public DataCollectionRenderer getRenderer() {
        return renderer;
    }
    
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }
    
    public void setRenderer(final DataCollectionRenderer renderer) {
        this.renderer = renderer;
        renderer.setPageSize(100);
    }
    
    @Override
    public void lock() {
        super.lock();
        renderer.lock();
    }

    @Override
    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {

        final Element listRootElem = Navigation
            .newElement("categoriesObjectList");

        final Category category = getCategory();
        final CategoryCollection subCategories = category.getChildren();

        while (subCategories.next()) {
            generateCategoryListXml(listRootElem, subCategories.getCategory());
        }

        return listRootElem;
    }

    private void generateCategoryListXml(final Element rootListElem,
                                         final Category category) {

        final String lang = GlobalizationHelper
            .getNegotiatedLocale()
            .getLanguage();

        final Element catListElem = rootListElem
            .newChildElement("categoryObjectList");
        final Element titleElem = catListElem.newChildElement("title");
        titleElem.setText(category.getName(lang));

        final Element objListElem = catListElem
            .newChildElement("simpleObjectList");

        final DataCollection objects = SessionManager
            .getSession()
            .retrieve(objectType);
        objects.addPath("masterVersion.id");
        objects.addPath("masterVersion.objectType");
        objects.addEqualsFilter("parent.categories.id",
                                category.getID());
        final com.arsdigita.persistence.Filter moreChildren = objects
            .addNotInSubqueryFilter(
                "parent.id",
                "com.arsdigita.categorization.liveIndexItemsInCategory");
        moreChildren.set("categoryID", category.getID());
        
        objects.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
        
        System.err.printf("Found %s objects%n", objects.size());
        
        final Element listElem = renderer.generateXML(objects, 0);
        objListElem.addContent(listElem);
        
        

//        final CategorizedCollection items = category
//            .getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
//        
//        while (items.next()) {
//            final items.getACSObject()
//        }
    }

}
