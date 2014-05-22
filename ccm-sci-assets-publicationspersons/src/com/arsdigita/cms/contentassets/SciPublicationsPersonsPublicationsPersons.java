/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;

/**
 * Helper class for easy and efficient filtering
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsPublicationsPersons extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contentassets.SciPublicationsPersonsPublicationsPersons";

    public static final String PUBLICATIONS_PERSONS = "publicationsPersons";
    public static final String PERSONS = "persons";
    public static final String OWNER = "owner";

    public SciPublicationsPersonsPublicationsPersons() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciPublicationsPersonsPublicationsPersons(final String type) {
        super(type);
    }

    public SciPublicationsPersonsPublicationsPersons(final DataObject dataObject) {
        super(dataObject);
    }

    public static SciPublicationsPersonsPublicationsPersons create(final Publication owner) {
        final PublicationBundle bundle = owner.getPublicationBundle();
        final ItemCollection instances = bundle.getInstances();

        Publication instance;
        while (instances.next()) {
            instance = (Publication) instances.getContentItem();
            createForInstance(instance);
        }

        return new SciPublicationsPersonsPublicationsPersons((DataObject) owner.get(
            PUBLICATIONS_PERSONS));
    }

    private static void createForInstance(final Publication instance) {
        final SciPublicationsPersonsPublicationsPersons persons
                                                        = new SciPublicationsPersonsPublicationsPersons();
        persons.set(OWNER, instance);
        persons.update();

        persons.save();
    }

    public String getPersons() {
        return (String) get(PERSONS);
    }

    protected void update() {

        final Publication owner = (Publication) DomainObjectFactory.newInstance((DataObject) get(
            OWNER));
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();

        final SciPublicationsPersonsPersonCollection persons = service.getPersons(
            (Publication) owner);
        final StringBuilder builder = new StringBuilder();
        while (persons.next()) {
            builder.append(persons.getPerson().getTitle());
            builder.append(';');
        }

        final PublicationBundle bundle = ((Publication) owner).getPublicationBundle();
        final ItemCollection instances = bundle.getInstances();
        Publication instance;
        SciPublicationsPersonsPublicationsPersons asset;
        while (instances.next()) {
            instance = (Publication) instances.getContentItem();
            asset = new SciPublicationsPersonsPublicationsPersons((DataObject) instance.get(
                PUBLICATIONS_PERSONS));

            asset.set(PERSONS, builder.toString());
        }

    }

}


