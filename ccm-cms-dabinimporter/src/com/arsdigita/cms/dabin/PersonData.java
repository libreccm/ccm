/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.dabin;

/**
 *
 * @author jensp
 */
public class PersonData {

    private String dabinId;
    private String titlePre;
    private String surname;
    private String givenname;
    private String titlePost;
    private String contactData;


    public String getDabinId() {
        return dabinId;
    }

    public void setDabinId(String dabinId) {
        this.dabinId = dabinId;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitlePost() {
        return titlePost;
    }

    public void setTitlePost(String titlePost) {
        this.titlePost = titlePost;
    }

    public String getTitlePre() {
        return titlePre;
    }

    public void setTitlePre(String titlePre) {
        this.titlePre = titlePre;
    }

    public String getContactData() {
        return contactData;
    }

    public void setContactData(String contactData) {
        this.contactData = contactData;
    }
}
