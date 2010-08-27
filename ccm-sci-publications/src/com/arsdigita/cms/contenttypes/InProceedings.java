/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedings extends PublicationWithPublisher {

    public static final String ORGANIZER_OF_CONFERENCE =
                               "organizerOfConference";
    public static final String NAME_OF_CONFERENCE = "nameOfConference";
    public static final String DATE_FROM_OF_CONFERENCE = "dateFromOfConference";
    public static final String DATE_TO_OF_CONFERENCE = "dateToOfConference";
    public static final String PLACE_OF_CONFERENCE = "placeOfConference";
    public static final String VOLUME = "volume";
    public static final String NUMBER_OF_VOLUMES = "numberOfVolumes";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String PAGES_FROM = "pagesFrom";
    public static final String PAGES_TO = "pagesFrom";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.InProceedings";

    public InProceedings() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public InProceedings(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public InProceedings(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public InProceedings(DataObject dataObject) {
        super(dataObject);
    }

    public InProceedings(String type) {
        super(type);
    }

    public String getOrganizerOfConference() {
        return (String) get(ORGANIZER_OF_CONFERENCE);
    }

    public void setOrganizerOfConference(String organizerOfConference) {
        set(ORGANIZER_OF_CONFERENCE, organizerOfConference);
    }

    public String getNameOfConference() {
        return (String) get(NAME_OF_CONFERENCE);
    }

    public void setNameOfConference(String nameOfConference) {
        set(NAME_OF_CONFERENCE, nameOfConference);
    }

    public Date getDateFromOfConference() {
        return (Date) get(DATE_FROM_OF_CONFERENCE);
    }

    public void setDateFromOfConference(Date dateFromOfConference) {
        set(DATE_FROM_OF_CONFERENCE, dateFromOfConference);
    }

    public Date getDateToOfConference() {
        return (Date) get(DATE_TO_OF_CONFERENCE);
    }

    public void setDateToOfConference(Date dateToOfConference) {
        set(DATE_TO_OF_CONFERENCE, dateToOfConference);
    }

    public String getPlaceOfConference() {
        return (String) get(PLACE_OF_CONFERENCE);
    }

    public void setPlaceOfConference(String place) {
        set(PLACE_OF_CONFERENCE, place);
    }

    public Integer getVolume() {
        return (Integer) get(VOLUME);
    }

    public void setVolume(Integer volume) {
        set(VOLUME, volume);
    }

    public Integer getNumberOfVolumes() {
        return (Integer) get(NUMBER_OF_VOLUMES);
    }

    public void setNumberOfVolumes(Integer numberOfVolumes) {
        set(NUMBER_OF_VOLUMES, numberOfVolumes);
    }

    public Integer getNumberOfPages() {
        return (Integer) get(NUMBER_OF_PAGES);
    }

    public void setNumberOfPages(Integer numberOfPages) {
        set(NUMBER_OF_PAGES, numberOfPages);
    }

    public Integer getPagesFrom() {
        return (Integer) get(PAGES_FROM);
    }

    public void setPagesFrom(Integer pagesFrom) {
        set(PAGES_FROM, pagesFrom);
    }

    public Integer getPagesTo() {
        return (Integer) get(PAGES_TO);
    }

    public void setPagesTo(Integer pagesTo) {
        set(PAGES_TO, pagesTo);
    }
}
