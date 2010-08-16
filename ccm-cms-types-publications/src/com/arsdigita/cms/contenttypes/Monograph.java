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
 *
 * @author Jens Pelzetter
 */
public class Monograph extends PublicationWithPublisher {

    public static final String VOLUME = "volume";
    public static final String NUMBER_OF_VOLUMES = "numberOfVolumes";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String EDITION = "edition";
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.Monograph";

    public Monograph() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Monograph(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Monograph(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Monograph(DataObject dataObject) {
        super(dataObject);
    }

    public Monograph(String type) {
        super(type);
    }

    public Integer getVolume() {
        return (Integer) get(VOLUME);
    }

    public void setVolume(Integer volume) {
        set(VOLUME, volume);
    }

    public Integer getNumberOfVolumes() {
        return (Integer) get(NUMBER_OF_VOLUMES);
    }

    public void setNumberOfVolumes(Integer numberOfVolumes) {
        set(NUMBER_OF_VOLUMES, numberOfVolumes);
    }

    public Integer getNumberOfPages() {
        return (Integer) get(NUMBER_OF_PAGES);
    }

    public void setNumberOfPages(Integer numberOfPages) {
        set(NUMBER_OF_PAGES, numberOfPages);
    }

    public String getEdition() {
        return (String) get(EDITION);
    }

    public void setEdition(String edition) {
        set(EDITION, edition);
    }
}
