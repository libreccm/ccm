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
        this.beschreibung = beschreibung;
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
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
