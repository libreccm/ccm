package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
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

    public static final String ALL = "--ALL--";
    private final String property;
    private final String label;
    private final CustomizableObjectList objectList;
    private final boolean allOption;
    private final boolean allOptionIsDefault;
    private final boolean reverseOptions;
    private final boolean propertyIsNumeric;
    //private Map<String, String> options = new HashMap<String, String>();
    private String value;

    protected SelectFilter(final String property,
                           final String label,
                           final CustomizableObjectList objectList,
                           final boolean reverseOptions,
                           final boolean allOption,
                           final boolean allOptionIsDefault,
                           final boolean propertyIsNumeric) {
        this.property = property;
        this.label = label;
        this.objectList = objectList;
        this.reverseOptions = reverseOptions;
        this.allOption = allOption;
        this.allOptionIsDefault = allOptionIsDefault;
        this.propertyIsNumeric = propertyIsNumeric;
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String getFilter() {
        List<String> options;

        options = getOptions();
        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                value = ALL;
            } else {
                value = options.get(0);
            }
        }

        if (ALL.equals(value)) {
            return null;
        }

        if (propertyIsNumeric) {
            return String.format("%s = %s", property, value);

        } else {
            return String.format("%s = '%s'", property, value);
        }
    }

    @Override
    public Element getXml() {
        Element filter;
        Element optionElem;
        String selected;    
        List<String> options;

        options = getOptions();

        filter = new Element("selectFilter");

        if ((value == null) || value.isEmpty()) {
            if (allOptionIsDefault) {
                selected = ALL;
            } else {
                selected = options.get(0);
            }
        } else {
            selected = value;
        }

        filter.addAttribute("label", label);
        filter.addAttribute("selected", selected);

        if (allOption) {
            optionElem = filter.newChildElement("option");
            optionElem.addAttribute(label, ALL);
        }

        for (String optionStr : options) {
            optionElem = filter.newChildElement("option");
            optionElem.addAttribute("label", optionStr);
        }

        return filter;
    }

    private List<String> getOptions() {
        DataCollection objects;
        DataObject dobj;
        String option;
        Set<String> optionsSet;
        List<String> options;

        objects = objectList.getDefinition().getDataCollection(objectList.
                getModel());

        optionsSet = new HashSet<String>();

        while (objects.next()) {
            dobj = objects.getDataObject();

            option = (dobj.get(property)).toString();
            optionsSet.add(option);
        }

        options = new ArrayList<String>(optionsSet);
        Collections.sort(options);
        if (reverseOptions) {
            Collections.reverse(options);
        }

        return options;
    }
}
