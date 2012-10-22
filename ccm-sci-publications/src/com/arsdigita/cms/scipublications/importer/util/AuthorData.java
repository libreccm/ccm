/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.scipublications.importer.util;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AuthorData {

    private static final String DR_TITLE = "Dr.";
    private static final String PROF_DR = "Prof. Dr.";
    private String surname;
    private String givenName;
    private boolean editor;

    public AuthorData() {
        //Nothing
    }

    public String getSurname() {
        if (surname == null) {
            return "";
        } else {
            return surname.trim();
        }
    }

    public void setSurname(final String surname) {
        if (surname.startsWith(DR_TITLE)) {
            this.surname = surname.substring(DR_TITLE.length());
        } else if (surname.startsWith(PROF_DR)) {
            this.surname = surname.substring(PROF_DR.length());
        } else {
            this.surname = surname;
        }
    }

    public String getGivenName() {
        if (givenName == null) {
            return "";
        } else {
            return givenName.trim();
        }
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

}