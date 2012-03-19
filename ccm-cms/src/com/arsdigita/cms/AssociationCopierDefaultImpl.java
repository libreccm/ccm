package com.arsdigita.cms;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.metadata.Property;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class AssociationCopierDefaultImpl implements AssociationCopier {

    final Map<String, HandledProperty> handledProperties =
                                       new HashMap<String, HandledProperty>();

    public void addHandledProperty(final String name,
                                   final String type,
                                   final String reverse,
                                   final String reverseType) {
        final HandledProperty prop = new HandledProperty();
        prop.setName(name);
        prop.setType(type);
        prop.setReverse(reverse);
        prop.setReverseType(reverseType);
        handledProperties.put(name, prop);
    }

    public boolean handlesProperty(final Property property) {
        return handledProperties.containsKey(property.getName());
    }

    public void copy(final DomainObject source,
                     final DomainObject target,
                     final DomainObject value,
                     final Property property) {
        final HandledProperty propData = handledProperties.get(
                property.getName());
        if (propData == null) {
            throw new IllegalArgumentException(String.format(
                    "Illegal call for this method. Property '%s' is not handled"
                    + "by this AssociationCopier.", property.getName()));
        }

        //Create PublishedAssociation here.
        final HandledProperty handledProperty = handledProperties.get(property.
                getName());
        if (handledProperty == null) {
            throw new IllegalArgumentException(String.format(
                    "Property '%s' "
                    + "is not handled by this AssociationCopier.",
                    property.getName()));
        }

        final ContentItem sourceItem = (ContentItem) source;
        final ContentItem valueItem = (ContentItem) value;

        PublishedAssociation.create(sourceItem,
                                    valueItem,
                                    property.getName(),
                                    handledProperty.getReverse());
    }

    private class HandledProperty {

        private String name;
        private String type;
        private String reverse;
        private String reverseType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReverse() {
            return reverse;
        }

        public void setReverse(String reverse) {
            this.reverse = reverse;
        }

        public String getReverseType() {
            return reverseType;
        }

        public void setReverseType(String reverseType) {
            this.reverseType = reverseType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
