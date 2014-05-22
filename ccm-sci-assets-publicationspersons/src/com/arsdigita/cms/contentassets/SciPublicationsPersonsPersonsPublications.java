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
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsPersonsPublications extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contentassets.SciPublicationsPersonsPersonsPublications";

    public static final String PUBLICATIONS = "publications";
    public static final String PERSONS_PUBLICATIONS = "personsPublications";
    public static final String OWNER = "owner";

    public SciPublicationsPersonsPersonsPublications() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public SciPublicationsPersonsPersonsPublications(final String type) {
        super(type);
    }

    public SciPublicationsPersonsPersonsPublications(final DataObject dataObject) {
        super(dataObject);
    }

    public static SciPublicationsPersonsPersonsPublications create(final GenericPerson owner) {
        final GenericPersonBundle bundle = owner.getGenericPersonBundle();
        final ItemCollection instances = bundle.getInstances();

        GenericPerson instance;
        while (instances.next()) {
            instance = (GenericPerson) instances.getContentItem();
            createForInstance(instance);
        }

        return new SciPublicationsPersonsPersonsPublications((DataObject) owner.get(
            PERSONS_PUBLICATIONS));
    }

    private static void createForInstance(final GenericPerson instance) {

        final SciPublicationsPersonsPersonsPublications publications
                                                        = new SciPublicationsPersonsPersonsPublications();
        publications.set(OWNER, instance);
        publications.update();

        publications.save();
    }

    public String getPublications() {
        return (String) get(PUBLICATIONS);
    }
    
    protected void update() {
        
        final GenericPerson owner = (GenericPerson) DomainObjectFactory .newInstance((DataObject) get(OWNER));
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();
        
        final SciPublicationsPersonsPublicationCollection publications = service.getPublications(
            owner);
        final StringBuilder builder = new StringBuilder();
        while(publications.next()) {
            builder.append(publications.getPublication().getTitle());
            builder.append(';');
        }
        
        final GenericPersonBundle bundle = owner.getGenericPersonBundle();
        final ItemCollection instances = bundle.getInstances();
        GenericPerson instance;
        SciPublicationsPersonsPersonsPublications asset;
        while(instances.next()) {
            instance = (GenericPerson) instances.getContentItem();
            asset = new SciPublicationsPersonsPersonsPublications((DataObject) instance.get(PERSONS_PUBLICATIONS));
            
            asset.set(PUBLICATIONS, builder.toString());
            asset.save();
        }
        
    }

}
