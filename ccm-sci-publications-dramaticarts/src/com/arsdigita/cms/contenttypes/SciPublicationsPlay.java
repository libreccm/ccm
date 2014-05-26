/*
 * Copyright (c) 2014 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.ui.SciPublicationsPlayExtraXMLGenerator;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPlay extends PublicationWithPublisher {
    
    public static final String BASE_DATA_OBJECT_TYPE
                                   = "com.arsdigita.cms.contenttypes.SciPublicationsPlay";
    public static final String FIRST_PRODUCTION_YEAR = "firstProductionYear";
    
    public SciPublicationsPlay() {
        super(BASE_DATA_OBJECT_TYPE);
    }
    
    public SciPublicationsPlay(final BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    public SciPublicationsPlay(final OID oid) {
        super(oid);
    }
    
    public SciPublicationsPlay(final DataObject obj) {
        super(obj);
    }
    
    public SciPublicationsPlay(final String type) {
        super(type);
    }
    
    public SciPublicationsPlayBundle getSciPublicationsPlayBundle() {
        return (SciPublicationsPlayBundle) getContentBundle();
    }
    
    public Integer getFirstProductionYear() {
        return (Integer) get(FIRST_PRODUCTION_YEAR);
    }
    
    public void setFirstProductionYear(final Integer year) {
        set(FIRST_PRODUCTION_YEAR, year);
    }
    
    public DomainCollection getProductionTheateres() {
        return getSciPublicationsPlayBundle().getProductionTheateres();
    }
    
    public void addProductionTheater(final GenericOrganizationalUnit theater) {
        getSciPublicationsPlayBundle().addProducationTheater(theater);
    }
    
    public void removeProductionTheater(final GenericOrganizationalUnit theater) {
        getSciPublicationsPlayBundle().removeProductionTheater(theater);
    }
    
    public boolean hasProductionTheaters() {
        return !getProductionTheateres().isEmpty();
    }
    
       @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(new SciPublicationsPlayExtraXMLGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraListXMLGenerators();
        generators.add(new SciPublicationsPlayExtraXMLGenerator());
        return generators;
    }
}
