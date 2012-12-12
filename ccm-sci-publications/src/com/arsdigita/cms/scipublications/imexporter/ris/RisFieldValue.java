package com.arsdigita.cms.scipublications.imexporter.ris;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class RisFieldValue {

    private RisField name;
    private String value;

    public RisFieldValue() {
    }

    public RisFieldValue(final RisField name, final String value) {
        this.name = name;
        this.value = value;
    }

    public RisField getName() {
        return name;
    }

    public void setName(final RisField name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
