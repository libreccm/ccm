package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.xml.Element;
import java.util.Comparator;

/**
 *
 * @param <T> 
 * @author Jens Pelzetter 
 */
public class SortField<T extends ContentItem> {

    private String label;
    private Comparator<T> comparator;

    public SortField(final String label, final Comparator<T> comparator) {
        this.label = label;
        this.comparator = comparator;
    }
    
    public String getLabel() {
        return label;
    }
    
    public Comparator<T> getComparator() {
        return comparator;
    }

    public void generateXml(final Element element) {
        final Element sortFieldElem = element.newChildElement("sortField");
        sortFieldElem.addAttribute("label", label);
    }
}
