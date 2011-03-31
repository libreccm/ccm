package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.xml.Element;

/**
 * <p>
 * Filter used by the {@link CustomizableObjectList}. These filter is usually 
 * rendered as a input box. The SQL filter created by this filter looks like 
 * this:
 * </p>
 * <p>
 * {@code property LIKE 'value'}
 * </p>
 * @author Jens Pelzetter
 */
public class TextFilter implements Filter {

    private final String property;
    private final String label;
    private String value;

    /**
     * Creates a new text filter. The constructor should only be invoked by the
     * {@link CustomizableObjectList}.
     * 
     * @param property The property which is used by this filter.
     * @param label The label for the input component of the filter.
     */
    protected TextFilter(final String property, final String label) {
        this.property = property;
        this.label = label;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
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

    private String firstToUpper(final String str) {
        char[] chars;

        chars = str.toCharArray();

        chars[0] = Character.toUpperCase(chars[0]);

        return new String(chars);
    }

    @Override
    public Element getXml() {
        Element textFilter;

        textFilter = new Element("filter");
        textFilter.addAttribute("type", "text");

        textFilter.addAttribute("label", label);
        if ((value != null) && !value.isEmpty()) {
            textFilter.addAttribute("value", value);
        }

        return textFilter;
    }
}
