package com.arsdigita.cms.scipublications.ui;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.navigation.ui.object.CustomizableObjectList;
import com.arsdigita.xml.Element;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This component creates export links for all publications in a category. The export works with the descendCategories
 * parameter set to true and false. Filters from a {@link CustomizableObjectList} are also supported. To add the 
 * component add the following to your JSP template:
 * 
 * <pre>
 * <define:component name="publicationExportLinks"
 *                        classname="com.arsdigita.cms.scipublications.ui.PublicationExportLinks"/>     
 *  <jsp:scriptlet>     
 *  ((com.arsdigita.cms.scipublications.ui.PublicationExportLinks)publicationExportLinks).setObjList(objList);
 *  </jsp:scriptlet>
 * </pre>
 * 
 * {@code objList} is to be supposed the name of variable for the object list in the template.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationExportLinks extends AbstractComponent {

    private CustomizableObjectList objList;

    @Override
    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {

        final Element parent = new Element("publicationExportLinks");

        final List<PublicationFormat> formats = SciPublicationsExporters.getInstance().getSupportedFormats();

        for (PublicationFormat format : formats) {
            createExportLink(format, parent, getCategory());
        }

        return parent;

    }

    public void setObjList(final CustomizableObjectList objList) {
        this.objList = objList;
    }

    private void createExportLink(final PublicationFormat format,
                                  final Element parent,
                                  final Category category) {
        final Element exportLinkElem = parent.newChildElement("publicationExportLink");
        final Element formatKeyElem = exportLinkElem.newChildElement("formatKey");
        formatKeyElem.setText(format.getName().toLowerCase());
        final Element formatNameElem = exportLinkElem.newChildElement("formatName");
        formatNameElem.setText(format.getName());
        final Element categoryIdElem = exportLinkElem.newChildElement("categoryId");
        categoryIdElem.setText(category.getID().toString());
        final Element filterSqlElem = exportLinkElem.newChildElement("filterSql");
        try {
            filterSqlElem.setText(URLEncoder.encode(objList.getFilterSql(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            filterSqlElem.setText("");
        }
        final Element desCatsElem = exportLinkElem.newChildElement("descendCategories");
        if (objList.getDefinition().getDescendCategories()) {
            desCatsElem.setText("true");
        } else {
            desCatsElem.setText("false");
        }
    }

}
