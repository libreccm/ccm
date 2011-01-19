package com.arsdigita.cms.dabin;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jensp
 */
public class PublicationData {

    private String publicationDaBInId;
    private String name;
    private String verlag;
    private String jahr;
    private String link;
    private String beschreibung;
    private String abteilungId;
    private String erschienenIn;
    private int pagesFrom;
    private int pagesTo;
    private PublicationVisibility visiblity;
    private PublicationType type;
    private List<Authorship> authors = new ArrayList<Authorship>();

    public String getAbteilungId() {
        return abteilungId;
    }

    public void setAbteilungId(String abteilungId) {
        this.abteilungId = abteilungId;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        if (beschreibung.length() < 4096) {
            this.beschreibung = beschreibung;
        } else {
            System.out.println(
                    "***Warning: Value of DaBIn field 'Beschreibung' is too long for abstract (max: 4096 characters). Truncating.");
            this.beschreibung = beschreibung.substring(0, 4095);
        }
    }

    public String getErschienenIn() {
        return erschienenIn;
    }

    public void setErschienenIn(String erschienenIn) {
        this.erschienenIn = erschienenIn;
    }

    public String getJahr() {
        return jahr;
    }

    public void setJahr(String jahr) {
        this.jahr = jahr;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        if (link.length() < 200) {
            this.link = link;
        } else {
            System.out.println(
                    "\n***WARNING: Link value too long. Truncating.\n");
            this.link = link.substring(0, 200);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        if (name.length() < 200) {
            return name.replace(",", "").
                    replace("/", "").
                    replaceAll("\\s\\s+", " ").
                    replace(' ', '-').toLowerCase();
        } else {
            System.out.println(
                    "\t***WARNING: Title of publication is too long for URL. Triming to title to a length of 200 characters for URL.");
            return name.substring(0, 200).
                    replace(",", "").
                    replace("/", "").
                    replaceAll("\\s\\s+", " ").
                    replace(' ', '-').toLowerCase();
        }
    }

    public String getPublicationDaBInId() {
        return publicationDaBInId;
    }

    public void setPublicationDaBInId(String publicationDaBInId) {
        this.publicationDaBInId = publicationDaBInId;
    }

    public PublicationType getType() {
        return type;
    }

    public void setType(PublicationType type) {
        this.type = type;
    }

    public String getVerlag() {
        return verlag;
    }

    public void setVerlag(String verlag) {
        this.verlag = verlag;
    }

    public PublicationVisibility getVisiblity() {
        return visiblity;
    }

    public void setVisiblity(PublicationVisibility visiblity) {
        this.visiblity = visiblity;
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

    public int getPagesFrom() {
        return pagesFrom;
    }

    public void setPagesFrom(int pagesFrom) {
        this.pagesFrom = pagesFrom;
    }

    public int getPagesTo() {
        return pagesTo;
    }

    public void setPagesTo(int pagesTo) {
        this.pagesTo = pagesTo;
    }
}
