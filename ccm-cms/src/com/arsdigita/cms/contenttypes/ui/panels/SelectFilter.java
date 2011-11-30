package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This filter allows the user to filter the object list for a specific value
 * of a property. The selectable values are determined by traversing
 * through all objects of the list (before the list is processed by the
 * paginator) and using each distinct value of the property as option.
 * 
 * @author Jens Pelzetter 
 */
public class SelectFilter implements Filter {

    public static final String NONE = "--NONE--";
    public static final String ALL = "--ALL--";
    private final String property;
    private final String label;
    private DataQuery dataQuery;
    private String queryProperty;
    private final boolean allOption;
    private final boolean allOptionIsDefault;
    private final boolean reverseOptions;
    private final boolean propertyIsNumeric;
    private boolean emptyDefaultOption = false;
    private String value;

    public SelectFilter(final String label,
                        final String property,
                        final boolean reverseOptions,
                        final boolean allOption,
                        final boolean allOptionIsDefault,
                        final boolean propertyIsNumeric) {
        this.property = property;
        this.label = label;

        this.reverseOptions = reverseOptions;
        this.allOption = allOption;
        this.allOptionIsDefault = allOptionIsDefault;
        this.propertyIsNumeric = propertyIsNumeric;
    }
    
    public SelectFilter(final String label,
                        final String property,
                        final boolean reverseOptions,
                        final boolean allOption,
                        final boolean allOptionIsDefault,
                        final boolean propertyIsNumeric,
                        final boolean emptyDefaultOption) {
        this(label, property, reverseOptions, allOption, allOptionIsDefault,
             propertyIsNumeric);
        this.emptyDefaultOption = emptyDefaultOption;
    }

    @Override
    public String getProperty() {
        return property;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setDataQuery(final DataQuery dataQuery,
                             final String queryProperty) {
        this.dataQuery = dataQuery;
        this.queryProperty = queryProperty;
    }

    public String getFilter() {        
        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                value = ALL;
            } else if(emptyDefaultOption) {
                value = NONE;
            } else {
                value = getOptions().get(0);
            }
        }

        if (ALL.equals(value) || NONE.equals(value)) {
            return null;
        }

        if (propertyIsNumeric) {
            return String.format("%s = %s", property, value);

        } else {
            return String.format("%s = '%s'", property, value);
        }
    }

    public void generateXml(final Element parent) {
        Element filter;
        Element optionElem;
        String selected;
        List<String> options;

        options = getOptions();

        filter = parent.newChildElement("filter");
        filter.addAttribute("type", "select");
        filter.addAttribute("label", label);

        if(options.isEmpty()) {
            return;
        }
        
        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                selected = ALL;
            } else if(emptyDefaultOption) {
                selected = NONE;
            } else {
                selected = options.get(0);
            }
        } else {
            selected = value;
        }
        
        filter.addAttribute("selected", selected);

        if (emptyDefaultOption) {
            optionElem = filter.newChildElement("option");
            optionElem.addAttribute("label", NONE);
        }
        
        if (allOption) {
            optionElem = filter.newChildElement("option");
            optionElem.addAttribute("label", ALL);
        }

        for (String optionStr : options) {
            optionElem = filter.newChildElement("option");
            optionElem.addAttribute("label", optionStr);
            if (propertyIsNumeric) {
                 optionElem.addAttribute("valueType", "number");
            } else {
                optionElem.addAttribute("valueType", "text");
            }
        }
    }

    public void setValue(final String value) {
        this.value = value;
    }

    private List<String> getOptions() {              
        Object obj;
        String option;
        Set<String> optionsSet;
        List<String> options;

        optionsSet = new HashSet<String>();
              
        while(dataQuery.next()) {
            obj = dataQuery.get(queryProperty);
            if (obj == null) {
                continue;
            }
            option = obj.toString();
            optionsSet.add(option);
        }
         
        dataQuery.rewind();
        
        options = new ArrayList<String>(optionsSet);
        Collections.sort(options);
        if (reverseOptions) {
            Collections.reverse(options);
        }
        
        return options;
    }
}
