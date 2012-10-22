package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class OrganizationalUnitImportReport {

    private String name;
    private String type;
    private boolean created;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(final boolean created) {
        this.created = created;
    }

    @Override
    public String toString() {
        if (created) {
            return String.format("Created organizationalunit '%s' of type '%s' and linked it with publication.",
                                 name,
                                 type);
        } else {
            return String.format("Found organizational unit '%s' of type '%s' in database and linked it "
                                 + "with publication.",
                                 name, type);
        }
    }

}
