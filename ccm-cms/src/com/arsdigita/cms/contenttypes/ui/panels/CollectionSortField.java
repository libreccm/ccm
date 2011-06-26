package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.xml.Element;

/**
 *
 * @param <T> 
 * @author Jens Pelzetter 
 */
public class CollectionSortField {

    private String label;
    private String field;

    public CollectionSortField(final String label, final String field) {
        this.label = label;
        this.field = field;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getField() {
        return field;
    }

    public void generateXml(final Element element) {
        final Element sortFieldElem = element.newChildElement("sortField");
        sortFieldElem.addAttribute("label", label);
    }
}
