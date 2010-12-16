package com.arsdigita.cms.dabin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jensp
 */
public class WorkingPaperData {

    private String dabinId;
    private String titleDe;
    private String titleEn;
    private String year;
    private String descDe;
    private String descEn;
    private InputStream file;
    private List<Authorship> authors;

    public WorkingPaperData() {
        authors = new ArrayList<Authorship>();
    }

    public String getDabinId() {
        return dabinId;
    }

    public void setDabinId(String dabinId) {
        this.dabinId = dabinId;
    }

    public String getDescDe() {
        return descDe;
    }

    public void setDescDe(String descDe) {
        if (descDe.length() < 4096) {
            this.descDe = descDe;
        } else {
            System.out.println(
                    "Value of DaBIn field is longer than maximum length for abstract (4096 characters). Truncating");
            this.descDe = descDe.substring(0, 4096);
        }
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        if (descEn.length() < 4096) {
            this.descEn = descEn;
        } else {
            System.out.println(
                    "Value of DaBIn field is longer than maximum length for abstract (4096 characters). Truncating");
            this.descEn = descEn.substring(0, 4096);
        }
    }

    public String getTitleDe() {
        return titleDe;
    }

    public void setTitleDe(String titleDe) {
        this.titleDe = titleDe;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Authorship> getAuthors() {
        return authors;
    }

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public void setAuthors(List<Authorship> authors) {
        this.authors = authors;
    }

    public void addAuthor(final Authorship author) {
        authors.add(author);
    }
}
