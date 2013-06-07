/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ui.ProceedingsExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Content type of proceedings. Provides attributes for storing the data of the conference (name, date, place,
 * organizer) and for linking the papers (objects of the content type {@link InProceedings} to a proceedings object.
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
    private static final ProceedingsConfig PROCEEDINGS_CONFIG = new ProceedingsConfig();

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

    public static ProceedingsConfig getProceedingsConfig() {
        return PROCEEDINGS_CONFIG;
    }

    public ProceedingsBundle getProceedingsBundle() {
        return (ProceedingsBundle) getContentBundle();
    }

    public GenericOrganizationalUnit getOrganizerOfConference() {
        /*DataCollection collection;

         collection = (DataCollection) get(ORGANIZER_OF_CONFERENCE);

         if (0 == collection.size()) {
         return null;
         } else {
         DataObject dobj;

         collection.next();
         dobj = collection.getDataObject();
         collection.close();

         return (GenericOrganizationalUnit) DomainObjectFactory.newInstance(
         dobj);
         }*/

        final GenericOrganizationalUnitBundle bundle = getProceedingsBundle().getOrganizerOfConference();

        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
        }
    }

    public GenericOrganizationalUnit getOrganizerOfConference(
            final String language) {
        final GenericOrganizationalUnitBundle bundle = getProceedingsBundle().getOrganizerOfConference();

        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getInstance(language);
        }
    }

    public void setOrganizerOfConference(GenericOrganizationalUnit organizer) {
        /*GenericOrganizationalUnit oldOrga;

         oldOrga = getOrganizerOfConference();
         if (oldOrga != null) {
         remove(ORGANIZER_OF_CONFERENCE, oldOrga);
         }

         if (null != organizer) {
         Assert.exists(organizer, GenericOrganizationalUnit.class);
         DataObject link = add(ORGANIZER_OF_CONFERENCE, organizer);
         link.set("organizerOrder", 1);
         link.save();
         } */

        getProceedingsBundle().setOrganizerOfConference(organizer);
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
        return (Date) get(DATE_TO_OF_CONFERENCE);
    }

    public void setDateToOfConference(Date dateTo) {
        set(DATE_TO_OF_CONFERENCE, dateTo);
    }

    public InProceedingsCollection getPapers() {
        //return new InProceedingsCollection((DataCollection) get(PAPERS));

        return getProceedingsBundle().getPapers();
    }

    public void addPaper(InProceedings paper) {
        /*Assert.exists(paper, InProceedings.class);

         DataObject link = add(PAPERS, paper);

         link.set(PAPER_ORDER, Integer.valueOf((int) getPapers().size()));*/

        getProceedingsBundle().addPaper(paper);
    }

    public void removePaper(InProceedings paper) {
        /*Assert.exists(paper, InProceedings.class);
         remove(PAPERS, paper);*/

        getProceedingsBundle().removePaper(paper);
    }

    public boolean hasPapers() {
        return !this.getPapers().isEmpty();
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new ProceedingsExtraXmlGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new ProceedingsExtraXmlGenerator());
        return generators;
    }
}
