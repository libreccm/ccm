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

import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonBundle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.redhat.persistence.metadata.Link;

/**
 * This class provides some convenient methods for dealing woth the Persons asset/mixin.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsService {

    public static final String PERSONS = "persons";
    public static final String PUBLICATIONS = "publications";
    public static final String RELATION = "relation";
    private static final String PUBLICATIONS_PERSONS = "publicationsPersons";

    public SciPublicationsPersonsPublicationCollection getPublications(final GenericPerson person) {

        final GenericPersonBundle bundle = person.getGenericPersonBundle();

        final DataCollection dataCollection = (DataCollection) bundle.get(PUBLICATIONS);

        return new SciPublicationsPersonsPublicationCollection(dataCollection);
    }

    public void addPublication(final GenericPerson person,
                               final Publication publication,
                               final String relation) {

        final GenericPersonBundle personBundle = person.getGenericPersonBundle();
        final PublicationBundle publicationBundle = publication.getPublicationBundle();

        final DataObject link = personBundle.add(PUBLICATIONS, publicationBundle);
        link.set(RELATION, relation);
        link.save();

        final DataObject asset1 = (DataObject) publication.get(PUBLICATIONS_PERSONS);
        if (asset1 == null) {
            SciPublicationsPersons.create(publication);
        } else {
            final SciPublicationsPersons persons = new SciPublicationsPersons(asset1);
            persons.update();
        }

        final DataObject asset2 = (DataObject) person.get(PUBLICATIONS_PERSONS);
        if (asset2 == null) {
            SciPublicationsPersons.create(person);
        } else {
            final SciPublicationsPersons persons = new SciPublicationsPersons(asset2);
            persons.update();
        }
    }

    public void removePublication(final GenericPerson person,
                                  final Publication publication) {
        final GenericPersonBundle personBundle = person.getGenericPersonBundle();
        final PublicationBundle publicationBundle = publication.getPublicationBundle();
        
        personBundle.remove(PUBLICATIONS, publicationBundle);
        final DataObject dobj1 = (DataObject) person.get(PUBLICATIONS_PERSONS);
        final SciPublicationsPersons persons1 = new SciPublicationsPersons(dobj1);
        persons1.update();
        final DataObject dobj2 = (DataObject) publication.get(PUBLICATIONS_PERSONS);
        final SciPublicationsPersons persons2 = new SciPublicationsPersons(dobj2);
        persons2.update();
         
    }
    
    public SciPublicationsPersonsPersonCollection getPersons(final Publication publication) {

        final PublicationBundle bundle = publication.getPublicationBundle();

        final DataCollection dataCollection = (DataCollection) bundle.get(PERSONS);

        return new SciPublicationsPersonsPersonCollection(dataCollection);
    }
    
   
}
