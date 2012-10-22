package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AuthorImportReport {

    private String surname;
    private String givenName;
    private boolean editor;
    private boolean created;

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(final boolean editor) {
        this.editor = editor;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(final boolean created) {
        this.created = created;
    }

    @Override
    public String toString() {
        final String authorType;
        if (editor) {
            authorType = "editor";
        } else {
            authorType = "author";
        }

        if (created) {
            return String.format("Created author '%s, %s'. Linked to publication as %s.",
                                 surname, givenName, authorType);
        } else {
            return String.format("Found author '%s, %s' in database. Linked to publication as %s.",
                                 surname, givenName, authorType);
        }
    }

}
