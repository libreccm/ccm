package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code CompareFilter}
 * filters the object list using a provided value and a operator. Valid 
 * operators defined in the {@link Operators} enumeration.
 *
 * @author Jens Pelzetter 
 */
public class CompareFilter implements Filter {

    public static final String ALL = "--ALL--";
    public static final String NONE = "--NONE--";
    private final String property;
    private final String label;
    private final boolean allOption;
    private final boolean allOptionIsDefault;
    private final boolean propertyIsNumeric;
    private boolean emptyDefaultOption = false;
    private Map<String, Option> options = new LinkedHashMap<String, Option>();
    private String value;

    public CompareFilter(final String label,
                         final String property,
                         final boolean allOption,
                         final boolean allOptionIsDefault,
                         final boolean propertyIsNumeric) {
        this.property = property;
        this.label = label;
        this.allOption = allOption;
        this.allOptionIsDefault = allOptionIsDefault;
        this.propertyIsNumeric = propertyIsNumeric;
    }

    public CompareFilter(final String label,
                         final String property,
                         final boolean allOption,
                         final boolean allOptionIsDefault,
                         final boolean propertyIsNumeric,
                         final boolean emptyDefaultOption) {
        this(label, property, allOption, allOptionIsDefault, propertyIsNumeric);
        this.emptyDefaultOption = true;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public CompareFilter addOption(final String label, final String value) {
        return addOption(label, Operators.EQ, value, false);
    }

    public CompareFilter addOption(final String label,
                                   final Operators operator,
                                   final String value,
                                   final boolean includeNull) {
        Option option;
        option = new Option(label, operator, value, includeNull);
        options.put(label, option);
        return this;
    }

    public String getFilter() {
        Option selectedOption;
        StringBuffer filter;

        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                value = ALL;
            } else if (emptyDefaultOption) {
                return "";
            } else {
                value =
                new ArrayList<Option>(options.values()).get(0).getLabel();
            }
        }

        if (ALL.equals(value) || NONE.equals(value)) {
            return "";
        }

        selectedOption = options.get(value);

        if (selectedOption == null) {
            throw new IllegalArgumentException(String.format(
                    "Unknown option '%s' selected for CompareFilter for property '%s'.",
                    value,
                    property));
        }

        filter = new StringBuffer();
        filter.append(property);

        switch (selectedOption.getOperator()) {
            case EQ:
                filter.append(" = ");
                break;
            case LT:
                filter.append(" < ");
                break;
            case GT:
                filter.append(" > ");
                break;
            case LTEQ:
                filter.append(" <= ");
                break;
            case GTEQ:
                filter.append(" >= ");
                break;
        }

        if (propertyIsNumeric) {
            filter.append(selectedOption.getValue());
        } else {
            filter.append('\'');
            filter.append(selectedOption.getValue());
            filter.append('\'');
        }

        if (selectedOption.getIncludeNull()) {
            filter.append(String.format(" or %s is null", property));
        }

        return filter.toString();
    }

    public void generateXml(final Element parent) {
        Element filter;
        String selected;

        filter = parent.newChildElement("filter");
        filter.addAttribute("type", "compare");

        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                selected = ALL;
            } else if (emptyDefaultOption) {
                selected = NONE;
            } else {
                List<Option> optionsList =
                             new ArrayList<Option>(options.values());
                selected = optionsList.get(0).getLabel();
            }
        } else {
            selected = value;
        }

        filter.addAttribute("label", label);
        filter.addAttribute("selected", selected);

        if (allOption) {
            Element option;

            option = filter.newChildElement("option");
            option.addAttribute("label", ALL);
        }

        if (emptyDefaultOption) {
            final Element emptyOption = filter.newChildElement("option");
            emptyOption.addAttribute("label", NONE);
        }
        Element option;
        for (Map.Entry<String, Option> entry : options.entrySet()) {
            option = filter.newChildElement("option");
            option.addAttribute("label", entry.getValue().getLabel());
        }
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public static enum Operators {

        EQ, //equal, '{@code =}'
        LT, //less than, '{@code <}'
        GT, //greater than, '{@code >}'
        LTEQ, //less than or equal, '{@code <=}'
        GTEQ //greater than or equal, '{@code >=}'
    }

    public static class Option {

        private final String label;
        private final Operators operator;
        private final String value;
        private final boolean includeNull;

        public Option(final String label,
                      final Operators operator,
                      final String value,
                      final boolean includeNull) {
            this.label = label;
            this.operator = operator;
            this.value = value;
            this.includeNull = includeNull;
        }

        public String getLabel() {
            return label;
        }

        public Operators getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }

        public boolean getIncludeNull() {
            return includeNull;
        }
    }
}
