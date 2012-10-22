package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class FieldImportReport {
    
    private String name;
    private String value;

    public FieldImportReport() {
        //Nothing
    }
    
    public FieldImportReport(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("%24s: %s", name, value);
    }
    
    
}
