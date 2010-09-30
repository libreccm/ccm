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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Content type of proceedings. Provides attributes for storing the data
 * of the conference (name, date, place, organizer) and for linking the papers
 * (objects of the content type {@link InProceedings} to a proceedings object.
 *
 *
 * @author Jens Pelzetter
 */
public class Proceedings extends PublicationWithPublisher {

    public static final String ORGANIZER_OF_CONFERENCE = "organizerOfConference";
    public static final String NAME_OF_CONFERENCE = "nameOfConference";
    public static final String PLACE_OF_CONFERENCE = "placeOfConference";
    public static final String DATE_FROM_OF_CONFERENCE = "dateFromOfConference";
    public static final String DATE_TO_OF_CONFERENCE = "dateToOfConference";
    public static final String PAPERS = "papers";
    public static final String PAPER_ORDER = "paperOrder";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Proceedings";

    public Proceedings() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public Proceedings(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Proceedings(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Proceedings(DataObject dataObject) {
        super(dataObject);
    }

    public Proceedings(String type) {
        super(type);
    }

    public GenericOrganizationalUnit getOrganizerOfConference() {
        DataObject dataObj;

        dataObj = (DataObject) get(ORGANIZER_OF_CONFERENCE);

        if (dataObj == null) {
            return null;
        } else {
            return new GenericOrganizationalUnit(dataObj);
        }
    }

    public void setOrganizerOfConference(GenericOrganizationalUnit organizer) {
        set(ORGANIZER_OF_CONFERENCE, organizer);
    }

    public String getNameOfConference() {
        return (String) get(NAME_OF_CONFERENCE);
    }

    public void setNameOfConference(String nameOfConference) {
        set(NAME_OF_CONFERENCE, nameOfConference);
    }

    public String getPlaceOfConference() {
        return (String) get(PLACE_OF_CONFERENCE);
    }

    public void setPlaceOfConference(String place) {
        set(PLACE_OF_CONFERENCE, place);
    }

    public Date getDateFromOfConference() {
        return (Date) get(DATE_FROM_OF_CONFERENCE);
    }

    public void setDateFromOfConference(Date dateFrom) {
        set(DATE_FROM_OF_CONFERENCE, dateFrom);
    }

    public Date getDateToOfConference() {
        return (Date) get(DATE_FROM_OF_CONFERENCE);
    }

    public void setDateToOfConference(Date dateTo) {
        set(DATE_TO_OF_CONFERENCE, dateTo);
    }

    public InProceedingsCollection getPapers() {
        return new InProceedingsCollection((DataCollection) get(PAPERS));
    }

    public void addPaper(InProceedings paper) {
        Assert.exists(paper, InProceedings.class);

        DataObject link = add(PAPERS, paper);

        link.set(PAPER_ORDER, Integer.valueOf((int) getPapers().size()));
    }

    public void removePaper(InProceedings paper) {
        Assert.exists(paper, InProceedings.class);
        remove(PAPERS, paper);
    }

    public boolean hasPapers() {
        return !this.getPapers().isEmpty();
    }
}
