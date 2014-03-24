package com.arsdigita.navigation.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.navigation.DataCollectionPropertyRenderer;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * <p>
 * An extended {@code DataCollectionRenderer} which displays a object list split into several
 * sections. The sections are created using a second category system/term domain set using the
 * {@link #setTermDomain(java.lang.String)} method in the JSP template. This renderer is designed to
 * be used together with the {@link CategorisedDataCollectionRenderer}. To use them, a special JSP
 * template is required. More specificly a JSP template using these two class would look like this
 * (only relevant parts shown):
 * </p>
 * <pre>
 * ...
 * <define:component name="itemList"
 *                   classname="com.arsdigita.navigation.ui.object.SimpleObjectList"/>
 * <jsp:scriptlet>
 *   CategorisedDataCollectionDefinition definition = new CategorisedDataCollectionDefinition();
 *   CategorisedDataCollectionRenderer renderer = new CategorisedDataCollectionRenderer();
 *
 *   definition.setObjectType("com.arsdigita.cms.contenttypes.GenericPerson");
 *   definition.setDescendCategories(false);
 *   definition.setTermDomain("memberTypes");
 *
 *   renderer.setTermDomain("memberTypes");
 *
 *   ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setDefinition(definition);
 *   ((com.arsdigita.navigation.ui.object.SimpleObjectList) itemList).setRenderer(renderer);
 *
 *   ...
 * </jsp:scriplet>
 * </pre>
 * <p>
 * This example will create a list of objects of the type {@link GenericPerson}, split into sections
 * definied by the terms domain identified by the key {@code memberTypes}. Only the root terms of
 * the domain are used for creating the sections.
 * </p>
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CategorisedDataCollectionRenderer extends CMSDataCollectionRenderer {

    private static final Logger s_log = Logger.getLogger(CategorisedDataCollectionRenderer.class);
    private String termDomain;

    public CategorisedDataCollectionRenderer() {
        super();
    }

    public String getTermDomain() {
        return termDomain;
    }

    public void setTermDomain(final String termDomain) {
        this.termDomain = termDomain;
    }

    @Override
    public Element generateXML(final DataCollection objects, int pageNum) {
        Assert.isLocked(this);

        int pageNumber = pageNum;

        // Quasimodo: Begin
        // If objects is null or empty, do not insert objectList-element
        // but do insert noContent-element and return immediately
        if (objects == null || objects.isEmpty()) {
            return Navigation.newElement("noContent");
        }
        // Quasimodo: End

        final Element content = Navigation.newElement("objectList");

        //Return the empty nav:item & nav:paginator tags.
        // Quasimodo: Why should I??? There is no need for a paginator if there aren't any elements
        if (!getNavItems()) {
            final Element paginator = Navigation.newElement("paginator");
            content.addContent(paginator);
            return content;
        }

        final long objectCount = objects.size();
        final int pageCount = (int) Math.ceil((double) objectCount / (double) getPageSize());

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }

        final long begin = ((pageNumber - 1) * getPageSize());
        final int count = (int) Math.min(getPageSize(), (objectCount - begin));
        final long end = begin + count;

        if (count != 0) {
            objects.setRange((int) begin + 1, (int) end + 1);
        }

        final Element paginator = Navigation.newElement("paginator");

        // Quasimodo: Begin
        // Copied from com.arsdigita.search.ui.ResultPane
        final String pageParam = "pageNumber";

        final URL url = Web.getWebContext().getRequestURL();
        final ParameterMap map = new ParameterMap();

        if (url.getParameterMap() != null) {
            final Iterator current = url.getParameterMap().keySet().iterator();
            while (current.hasNext()) {
                final String key = (String) current.next();
                if (key.equals(pageParam)) {
                    continue;
                }
                map.setParameterValues(key, url.getParameterValues(key));
            }
        }

        paginator.addAttribute("pageParam", pageParam);
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(), map).toString());
        // Quasimodo: End

        paginator.addAttribute("pageNumber", Long.toString(pageNumber));
        paginator.addAttribute("pageCount", Long.toString(pageCount));
        paginator.addAttribute("pageSize", Long.toString(getPageSize()));
        paginator.addAttribute("objectBegin", Long.toString(begin + 1));
        paginator.addAttribute("objectEnd", Long.toString(end));
        paginator.addAttribute("objectCount", Long.toString(objectCount));

        content.addContent(paginator);

        int index = 0;
        final Domain domain = Domain.retrieve(termDomain);
        Category currentCat = null;
        final Map<String, Element> sections = new HashMap<String, Element>();
        Element currentSection = null;
        while (objects.next()) {
            final DataObject dobj = objects.getDataObject();
            ACSObject object = null;
            //if (m_specializeObjects) {
            object = (ACSObject) DomainObjectFactory.newInstance(dobj);
            if (object == null) {
                s_log.error(String.format(
                        "Failed to specialize object with with id %s. Skiping object.",
                        dobj.getOID().toString()));
                continue;
            } else {
                s_log.debug("Specializing successful.");
            }
            //}

            // Get the content bundle to retrieve the terms/categories. This is necessary 
            //because the bundle is object which is categorised not the item itself.
            final ACSObject categorisedObj;
            if (object instanceof ContentPage) {
                final ContentPage item = (ContentPage) object;
                categorisedObj = item.getContentBundle();
            } else {
                categorisedObj = object;
            }

            // Get the term from the term domain used to separate the list which are associated the 
            // current object.
            final DomainCollection terms = domain.getDirectTerms(categorisedObj);
            while (terms.next()) {
                //Get the category
                final Category cat = ((Term) terms.getDomainObject()).getModel();
                // If a new section starts create a new section element. Ordering has to be done 
                // the theme using the sortKey attribute added to the section
                if (currentCat == null) {
                    currentCat = cat;
                    final Element section = Navigation.newElement(content, "section");
                    section.addAttribute("id", cat.getID().toString());
                    section.addAttribute("url", cat.getURL());
                    section.addAttribute("title", cat.getName(GlobalizationHelper.
                            getNegotiatedLocale().getLanguage()));
                    final DataAssociationCursor childCats = domain.getModel().getRelatedCategories(
                            Category.CHILD);
                    childCats.addEqualsFilter("id", cat.getID());
                    if (childCats.next()) {
                        section.addAttribute("sortKey", childCats.get("link.sortKey").toString());
                    }
                    childCats.close();
                    currentSection = section;
                    sections.put(cat.getURL(), section);
                } else if (!cat.getID().equals(currentCat.getID())) {
                    currentCat = cat;
                    //If the section has been already created use the existing element.
                    if (sections.containsKey(cat.getURL())) {
                        currentSection = sections.get(cat.getURL());
                    } else {
                        final Element section = Navigation.newElement(content, "section");
                        section.addAttribute("id", cat.getID().toString());
                        section.addAttribute("url", cat.getURL());
                        section.addAttribute("title", cat.getName(GlobalizationHelper.
                                getNegotiatedLocale().
                                getLanguage()));
                        final DataAssociationCursor childCats = domain.getModel().
                                getRelatedCategories(Category.CHILD);
                        childCats.addEqualsFilter("id", cat.getID());
                        if (childCats.next()) {
                            section.
                                    addAttribute("sortKey", childCats.get("link.sortKey").toString());
                        }
                        childCats.close();
                        currentSection = section;
                        sections.put(cat.getURL(), section);
                    }
                }
            }
            terms.close();

            final Element item;
            if (currentSection == null) {
                item = Navigation.newElement(content, "item");
            } else {
                item = Navigation.newElement(currentSection, "item");
            }

            final Iterator attributes = getAttributes().iterator();
            while (attributes.hasNext()) {
                final String name = (String) attributes.next();
                final String[] paths = StringUtils.split(name, '.');
                outputValue(item, dobj, name, paths, 0);
            }

            final Iterator properties = getProperties().iterator();
            while (properties.hasNext()) {
                final DataCollectionPropertyRenderer property = (DataCollectionPropertyRenderer) properties.
                        next();
                property.render(objects, item);
            }

            final Element path = Navigation.newElement(item, "path");
            path.setText(getStableURL(dobj, object));
            //item.addContent(path);

            generateItemXML(item, dobj, object, index);

            index++;
            //content.addContent(item);
        }

        return content;
    }

}
