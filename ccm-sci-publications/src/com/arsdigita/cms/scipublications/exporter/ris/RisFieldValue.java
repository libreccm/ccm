package com.arsdigita.cms.scipublications.exporter.ris;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class RisFieldValue {

    private RisFields name;
    private String value;

    public RisFieldValue() {
    }

    public RisFieldValue(final RisFields name, final String value) {
        this.name = name;
        this.value = value;
    }

    public RisFields getName() {
        return name;
    }

    public void setName(final RisFields name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
