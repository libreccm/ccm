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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsBundle extends PublicationWithPublisherBundle {

    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.ProceedingsBundle";
    public static final String ORGANIZER_OF_CONFERENCE = "organizerOfConference";
    public static final String PAPERS = "papers";
    public static final String PAPER_ORDER = "paperOrder";

    public ProceedingsBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public ProceedingsBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ProceedingsBundle(final BigDecimal id)
        throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ProceedingsBundle(final DataObject dobj) {
        super(dobj);
    }

    public ProceedingsBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();

        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final ProceedingsBundle proceedingsBundle
                                        = (ProceedingsBundle) source;

            if (PAPERS.equals(attribute)) {
                final DataCollection papers = (DataCollection) proceedingsBundle
                    .get(
                        PAPERS);

                while (papers.next()) {
                    createPaperAssoc(papers);
                }

                return true;
            } else if (ORGANIZER_OF_CONFERENCE.equals(attribute)) {
                final DataCollection organizers
                                         = (DataCollection) proceedingsBundle
                    .get(
                        ORGANIZER_OF_CONFERENCE);

                while (organizers.next()) {
                    createOrganizerAssoc(organizers);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createPaperAssoc(final DataCollection papers) {
        final InProceedingsBundle draftPaper
                                      = (InProceedingsBundle) DomainObjectFactory
            .newInstance(papers.getDataObject());
        final InProceedingsBundle livePaper = (InProceedingsBundle) draftPaper.
            getLiveVersion();

        if (livePaper != null) {
            final DataObject link = add(PAPERS, livePaper);

            link.set(PAPER_ORDER, papers.get(InProceedingsCollection.LINKORDER));

            link.save();
        }
    }

    private void createOrganizerAssoc(final DataCollection organizers) {
        final GenericOrganizationalUnitBundle draftOrganizer
                                                  = (GenericOrganizationalUnitBundle) DomainObjectFactory
            .newInstance(organizers.getDataObject());
        final GenericOrganizationalUnitBundle liveOrganizer
                                                  = (GenericOrganizationalUnitBundle) draftOrganizer
            .getLiveVersion();

        if (liveOrganizer != null) {
            final DataObject link = add(ORGANIZER_OF_CONFERENCE, liveOrganizer);

            link.set("organizerOrder", 1);

            link.save();
        }
    }

    @Override
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if (("proceedingsOfConference".equals(attribute))
                    && (source instanceof GenericOrganizationalUnitBundle)) {
                final GenericOrganizationalUnitBundle orgaunitBundle
                                                          = (GenericOrganizationalUnitBundle) source;
                final DataCollection proceedings
                                         = (DataCollection) orgaunitBundle.get(
                        "proceedingsOfConference");

                while (proceedings.next()) {
                    createProceedingsAssoc(proceedings,
                                           (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;
            } else {
                return super.copyReverseProperty(source,
                                                 liveItem,
                                                 property,
                                                 copier);
            }
        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }
    }

    private void createProceedingsAssoc(
        final DataCollection proceedings,
        final GenericOrganizationalUnitBundle orgaunit) {
        final ProceedingsBundle draftProceedigns
                                    = (ProceedingsBundle) DomainObjectFactory
            .newInstance(proceedings.getDataObject());
        final ProceedingsBundle liveProceedings
                                    = (ProceedingsBundle) draftProceedigns
            .getLiveVersion();

        if (liveProceedings != null) {
            final DataObject link = orgaunit.add("proceedingsOfConference",
                                                 liveProceedings);

            link.set("organizerOrder", 1);

            link.save();

            XMLDeliveryCache.getInstance().removeFromCache(liveProceedings
                .getOID());
        }
    }

    public InProceedingsCollection getPapers() {
        return new InProceedingsCollection((DataCollection) get(PAPERS));
    }

    public void addPaper(final InProceedings paper) {
        Assert.exists(paper, InProceedings.class);

        final DataObject link = add(PAPERS, paper.getInProceedingsBundle());

        link.set(PAPER_ORDER, Integer.valueOf((int) getPapers().size()));

        link.save();
    }

    public void removePaper(final InProceedings paper) {
        Assert.exists(paper, InProceedings.class);

        remove(PAPERS, paper.getInProceedingsBundle());
    }

    public ProceedingsOrganizerCollection getOrganizersOfConference() {
        return new ProceedingsOrganizerCollection((DataCollection) get(
            ORGANIZER_OF_CONFERENCE));
    }

    public void addOrganizerOfConference(
        final GenericOrganizationalUnit organizer) {
        Assert.exists(organizer, GenericOrganizationalUnit.class);

        final DataObject link = add(ORGANIZER_OF_CONFERENCE,
                                    organizer
                                    .getGenericOrganizationalUnitBundle());
        link.set("organizerOrder",
                 Integer.valueOf((int) getOrganizersOfConference().size()));

        link.save();
    }

    public void removeOrganizer(final GenericOrganizationalUnit organizer) {
        Assert.exists(organizer, GenericOrganizationalUnit.class);

        remove(ORGANIZER_OF_CONFERENCE, organizer.getGenericOrganizationalUnitBundle());
    }

//    public GenericOrganizationalUnitBundle getOrganizerOfConference() {
//        DataCollection collection;
//
//        collection = (DataCollection) get(ORGANIZER_OF_CONFERENCE);
//
//        if (0 == collection.size()) {
//            return null;
//        } else {
//            DataObject dobj;
//
//            collection.next();
//            dobj = collection.getDataObject();
//            collection.close();
//
//            return (GenericOrganizationalUnitBundle) DomainObjectFactory
//                .newInstance(
//                    dobj);
//        }
//    }
//
//    public void setOrganizerOfConference(GenericOrganizationalUnit organizer) {
//        final GenericOrganizationalUnitBundle oldOrga
//                                                  = getOrganizerOfConference();
//
//        if (oldOrga != null) {
//            remove(ORGANIZER_OF_CONFERENCE, oldOrga);
//        }
//
//        if (null != organizer) {
//            Assert.exists(organizer, GenericOrganizationalUnit.class);
//            DataObject link = add(ORGANIZER_OF_CONFERENCE,
//                                  organizer.getGenericOrganizationalUnitBundle());
//            link.set("organizerOrder", 1);
//            link.save();
//        }
//    }

}
