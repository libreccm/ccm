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
import com.arsdigita.cms.contenttypes.ui.UnPublishedExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public abstract class UnPublished extends Publication {

    public static final String PLACE = "place";
    public static final String ORGANIZATION = "organization";
    public static final String NUMBER = "number";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.UnPublished";

    public UnPublished() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public UnPublished(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public UnPublished(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public UnPublished(DataObject dataObject) {
        super(dataObject);
    }

    public UnPublished(String type) {
        super(type);
    }

    public UnPublishedBundle getUnPublishedBundle() {
        return (UnPublishedBundle) getContentBundle();
    }

    public String getPlace() {
        return (String) get(PLACE);
    }

    public void setPlace(String place) {
        set(PLACE, place);
    }

    public GenericOrganizationalUnit getOrganization() {
        final GenericOrganizationalUnitBundle bundle = getUnPublishedBundle().
                getOrganization();

        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
        }
    }

    public GenericOrganizationalUnit getOrganization(final String language) {
        final GenericOrganizationalUnitBundle bundle = getUnPublishedBundle().
                getOrganization();
        if (bundle == null) {
            return null;
        } else {
            return (GenericOrganizationalUnit) bundle.getInstance(language);
        }
    }

    public void setOrganization(final GenericOrganizationalUnit orga) {
        getUnPublishedBundle().setOrganization(orga);
    }

    public String getNumber() {
        return (String) get(NUMBER);
    }

    public void setNumber(String number) {
        set(NUMBER, number);
    }

    public Integer getNumberOfPages() {
        return (Integer) get(NUMBER_OF_PAGES);
    }

    public void setNumberOfPages(Integer numberOfPages) {
        set(NUMBER_OF_PAGES, numberOfPages);
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new UnPublishedExtraXmlGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.
                getExtraListXMLGenerators();
        generators.add(new UnPublishedExtraXmlGenerator());
        return generators;
    }
}
