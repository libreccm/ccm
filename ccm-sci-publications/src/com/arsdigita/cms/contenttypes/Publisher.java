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

/**
 * This class represents a publisher. The class uses the 
 * {@link GenericOrganizationalUnit} class as base.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class Publisher extends GenericOrganizationalUnit {

    public static final String PUBLISHER_NAME = "publisherName";
    public static final String PLACE = "place";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Publisher";

    public Publisher() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Publisher(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Publisher(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Publisher(DataObject dataObject) {
        super(dataObject);
    }

    public Publisher(String type) {
        super(type);
    }

    public PublisherBundle getPublisherBundle() {
        return (PublisherBundle) getContentBundle();
    }
    
    public String getPublisherName() {
        return (String) get(PUBLISHER_NAME);
    }

    public void setPublisherName(String publisherName) {
        set(PUBLISHER_NAME, publisherName);
    }

    /**
     *
     * @return The place of the publisher.
     */
    public String getPlace() {
        return (String) get(PLACE);
    }

    /**
     *
     * @param place (New) placee of the publisher.
     */
    public void setPlace(String place) {
        set(PLACE, place);
    }
    
    public PublicationBundleCollection getPublications() {
        return getPublisherBundle().getPublications();
    }

     // Create a ulr save version of the full name
    public static String urlSave(String in) {

        if (in != null && !in.isEmpty()) {

            // Replacement map
            String[][] replacements = {{"ä", "ae"}, {"Ä", "Ae"}, {"ö", "oe"}, {
                    "Ö", "Oe"}, {"ü", "ue"}, {"Ü", "Ue"}, {"ß", "ss"}, {".", ""}};

            // Replace all spaces with dash
            String out = in.replace(" ", "-");

            // Replace all special chars defined in replacement map
            for (int i = 0; i < replacements.length; i++) {
                out = out.replace(replacements[i][0], replacements[i][1]);
            }

            // Replace all special chars that are not yet replaced with a dash
            return out.replaceAll("[^A-Za-z0-9-]", "_").toLowerCase();
        }

        return in;

    }

}
