package com.arsdigita.cms.dabin;

/**
 *
 * @author jensp
 */
public class PublisherData {

    private String name;
    private String place;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            if ("null".equals(name.toLowerCase())) {
                this.name = "";
            } else {
                this.name = name.trim();
            }
        }
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        if (place == null) {
            this.place = "";
        } else {
            if ("null".equals(place.toLowerCase())) {
                this.place = "";
            } else {
                this.place = place.trim();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublisherData other = (PublisherData) obj;
        if ((this.name == null) ? (other.name != null)
            : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.place == null) ? (other.place != null)
            : !this.place.equals(other.place)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.place != null ? this.place.hashCode() : 0);
        return hash;
    }
}
