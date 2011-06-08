package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.xml.Element;

/**
 * This filter is usually 
 * rendered as a input box. The SQL filter created by this filter looks like 
 * this:
 * </p>
 * <p>
 * {@code property LIKE 'value'}
 * </p>
 * 
 * @author Jens Pelzetter 
 */
public class TextFilter implements Filter {

    private final String property;
    private final String label;
    private String value;

    @Override
    public String getProperty() {
        return property;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    /**
     * Creates a new text filter.
     * 
     * @param property The property which is used by this filter.
     * @param label The label for the input component of the filter.
     */
    public TextFilter(final String label, final String property) {
        this.property = property;
        this.label = label;
    }

    @Override
    public String getFilter() {
        if ((value == null) || value.isEmpty()) {
            return null;
        } else {
            return String.format("(lower(%s) LIKE lower('%%%s%%'))",
                                 property, value);
        }
    }

    @Override
    public void generateXml(final Element parent) {
        Element textFilter;
        
        textFilter = parent.newChildElement("filter");
        textFilter.addAttribute("type", "text");
        
        textFilter.addAttribute("label", label);
        if ((value != null) && !(value.isEmpty())) {
            textFilter.addAttribute("value", value);
        }                
    }

    @Override
    public void setValue(final String value) {
          this.value = value;
    }
}
