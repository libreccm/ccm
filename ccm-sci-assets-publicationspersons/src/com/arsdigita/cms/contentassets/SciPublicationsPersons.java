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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
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
public class SciPublicationsPersons extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contentassets.SciPublicationsPersons";

    public static final String PUBLICATIONS = "publications";
    public static final String PUBLICATIONS_PERSONS = "publicationsPersons";
    public static final String PERSONS = "persons";
    public static final String OWNER = "owner";

    public SciPublicationsPersons() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciPublicationsPersons(final String type) {
        super(type);
    }

    public SciPublicationsPersons(final DataObject dataObject) {
        super(dataObject);
    }

    public static SciPublicationsPersons create(final Publication owner) {
        final PublicationBundle bundle = owner.getPublicationBundle();
        final ItemCollection instances = bundle.getInstances();

        Publication instance;
        while (instances.next()) {
            instance = (Publication) instances.getContentItem();
            createForInstance(instance);
        }

        return new SciPublicationsPersons((DataObject) owner.get(PUBLICATIONS_PERSONS));
    }

    private static void createForInstance(final Publication instance) {
        final SciPublicationsPersons persons = new SciPublicationsPersons();
        persons.set(OWNER, instance);
        persons.update();

        persons.save();
    }

    public static SciPublicationsPersons create(final GenericPerson owner) {
        final GenericPersonBundle bundle = owner.getGenericPersonBundle();
        final ItemCollection instances = bundle.getInstances();

        GenericPerson instance;
        while (instances.next()) {
            instance = (GenericPerson) instances.getContentItem();
            createForInstance(instance);
        }

        return new SciPublicationsPersons((DataObject) owner.get(PUBLICATIONS_PERSONS));
    }

    private static void createForInstance(final GenericPerson instance) {
        final SciPublicationsPersons persons = new SciPublicationsPersons();
        persons.set(OWNER, instance);
        persons.update();

        persons.save();
    }

    public String getPersons() {
        return (String) get(PERSONS);
    }

    public String getPublications() {
        return (String) get(PUBLICATIONS);
    }

    protected void update() {

        final ContentItem owner = (ContentItem) DomainObjectFactory.newInstance((DataObject) get(
            OWNER));
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();
        
        
        if (owner instanceof Publication) {

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
            SciPublicationsPersons asset;
            while (instances.next()) {
                instance = (Publication) instances.getContentItem();
                asset = new SciPublicationsPersons((DataObject) instance.get(PUBLICATIONS_PERSONS));
                
                asset.set(PERSONS, builder.toString());
            }
            
        } else if (owner instanceof GenericPerson) {

            final SciPublicationsPersonsPublicationCollection publications = service
                .getPublications((GenericPerson) owner);
            final StringBuilder builder = new StringBuilder();
            while(publications.next()) {
                builder.append(publications.getPublication().getTitle());
                builder.append(';');
            }
            
            final GenericPersonBundle bundle = ((GenericPerson) owner).getGenericPersonBundle();
            final ItemCollection instances = bundle.getInstances();
            GenericPerson instance;
            SciPublicationsPersons asset;
            while(instances.next()) {
                instance = (GenericPerson) instances.getContentItem();
                asset = new SciPublicationsPersons((DataObject) instance.get(PUBLICATIONS_PERSONS));
                
                asset.set(PUBLICATIONS, builder.toString());
            }

        }
        
    }

}
