package com.arsdigita.cms.scipublications.importer.bibtex.util;

/**
 * Represents a Author token from a BibTeX entry.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$ 
 */
public class Author {
    
    /**
     * First name
     */
    private String first = "";
    /**
     * Last name
     */
    private String last = "";
    /**
     * von, van etc.
     */
    private String preLast = "";
    /**
     * Jr., PhD etc.
     */
    private String suffix = "";
    
    /**
     * Can only created by the {@link AuthorListParser} class in this package.
     */
    Author() {
        super();
    }

    public String getFirst() {
        return first;
    }

    protected void setFirst(final String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    protected void setLast(final String last) {
        this.last = last;
    }

    public String getPreLast() {
        return preLast;
    }

    protected void setPreLast(final String preLast) {
        this.preLast = preLast;
    }

    public String getSuffix() {
        return suffix;
    }

    protected void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    
}

