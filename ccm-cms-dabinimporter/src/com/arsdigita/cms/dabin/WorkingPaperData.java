package com.arsdigita.cms.dabin;

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
        this.descDe = descDe;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
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

    public void setAuthors(List<Authorship> authors) {
        this.authors = authors;
    }

    public void addAuthor(final Authorship author) {
        authors.add(author);
    }
}
