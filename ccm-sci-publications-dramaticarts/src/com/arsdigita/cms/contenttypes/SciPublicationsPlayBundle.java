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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.XMLDeliveryCache;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPlayBundle extends PublicationWithPublisherBundle {

    public static final String BASE_BASE_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.SciPublicationsPlayBundle";
    public static final String PRODUCTION_THEATER = "productionTheater";
    public static final String PRODUCTION_THEATER_ORDER = "theaterOrder";

    public SciPublicationsPlayBundle(final ContentItem primary) {

        super(BASE_BASE_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        setName(primary.getName());
    }

    public SciPublicationsPlayBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public SciPublicationsPlayBundle(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_BASE_OBJECT_TYPE, id));
    }

    public SciPublicationsPlayBundle(final DataObject dataObject) {
        super(dataObject);
    }

    public SciPublicationsPlayBundle(final String type) {
        super(type);
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {

        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {

            final PublicationBundle pubBundle = (PublicationBundle) source;

            if (PRODUCTION_THEATER.equals(attribute)) {

                final DataCollection theatres = (DataCollection) pubBundle.get(PRODUCTION_THEATER);

                while (theatres.next()) {
                    createProductionTheaterAssoc(theatres);
                }

                return true;

            } else {
                return super.copyProperty(source, property, copier);
            }

        } else {
            return super.copyProperty(source, property, copier);
        }

    }

    private void createProductionTheaterAssoc(final DataCollection theatres) {

        final GenericOrganizationalUnitBundle draftTheater
                                                  = (GenericOrganizationalUnitBundle) DomainObjectFactory
            .newInstance(theatres.getDataObject());
        final GenericOrganizationalUnitBundle liveTheater
                                                  = (GenericOrganizationalUnitBundle) draftTheater
            .getLiveVersion();

        if (liveTheater != null) {
            final DataObject link = add(PRODUCTION_THEATER, liveTheater);

            link.set(PRODUCTION_THEATER_ORDER, theatres.get("link." + PRODUCTION_THEATER_ORDER));

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

            if ("producedPlay".equals(attribute)
                    && (source instanceof GenericOrganizationalUnitBundle)) {

                final GenericOrganizationalUnitBundle theaterBundle
                                                          = (GenericOrganizationalUnitBundle) source;
                final DataCollection plays = (DataCollection) theaterBundle.get("producedPlay");

                while (plays.next()) {
                    createTheaterPlayAssoc(plays, (GenericOrganizationalUnitBundle) liveItem);
                }

                return true;

            } else {
                return super.copyReverseProperty(source, liveItem, property, copier);
            }

        } else {
            return super.copyReverseProperty(source, liveItem, property, copier);
        }

    }

    private void createTheaterPlayAssoc(final DataCollection plays,
                                        final GenericOrganizationalUnitBundle theater) {

        final PublicationBundle draftPlay = (PublicationBundle) DomainObjectFactory.newInstance(
            plays.getDataObject());
        final PublicationBundle livePlay = (PublicationBundle) draftPlay.getLiveVersion();

        if (livePlay != null) {

            final DataObject link = theater.add("producedPlay", livePlay);
            link.set(PRODUCTION_THEATER_ORDER, plays.get("link." + PRODUCTION_THEATER_ORDER));
            link.save();

            XMLDeliveryCache.getInstance().removeFromCache(livePlay.getOID());
        }

    }

    public DomainCollection getProductionTheateres() {

        return new DomainCollection((DataCollection) get(PRODUCTION_THEATER));

    }

    public void addProducationTheater(final GenericOrganizationalUnit theater) {

        Assert.exists(theater, GenericOrganizationalUnit.class);

        final DataObject link = add(PRODUCTION_THEATER,
                                    theater.getGenericOrganizationalUnitBundle());
        link.set(PRODUCTION_THEATER, Integer.valueOf((int) getProductionTheateres().size()));
        link.save();
    }

    public void removeProductionTheater(final GenericOrganizationalUnit theater) {

        Assert.exists(theater, GenericOrganizationalUnit.class);

        remove(PRODUCTION_THEATER, theater.getContentBundle());

    }

    public static PublicationBundleCollection getProducedPlays(
        final GenericOrganizationalUnit theater) {

        final GenericOrganizationalUnitBundle theaterBundle = theater
            .getGenericOrganizationalUnitBundle();

        final DataCollection collection = (DataCollection) theaterBundle.get("producedPlay");

        return new PublicationBundleCollection(collection);

    }

    public SciPublicationsPlay getPlay() {
        return (SciPublicationsPlay) getPrimaryInstance();
    }

    public SciPublicationsPlay getPlay(final String language) {

        SciPublicationsPlay result = (SciPublicationsPlay) getInstance(language);
        if (result == null) {
            result = getPlay();
        }

        return result;

    }

}
