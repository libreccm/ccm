/*
 * Copyright (C) 2009 Sören Bernstein, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contenttypes.*;
import org.apache.log4j.Logger;
import com.arsdigita.runtime.LegacyInitEvent;

/**
 * Initializer of the HealthCareFacility content type.
 *
 * @author Sören Bernstein
 */
public class HealthCareFacilityInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(HealthCareFacilityInitializer.class);

    /**
     * Constructor. calls only the constructor of the parent class with name of
     * the pdl.mf file of the content type an the BASIC_DATA_OBJECT_TYPE.
     */
    public HealthCareFacilityInitializer() {
        super("ccm-cms-types-healthCareFacility.pdl.mf",
                HealthCareFacility.BASE_DATA_OBJECT_TYPE);
    }

    /**
     *
     * @return path of the XSL stylesheet file. The stylesheet is very generic, because this
     * contenttype will be used with the new mandalay theme only.
     */
    public String getStylesheet() {
        return "static/content-types/com/arsdigita/cms/contenttypes/HealthCareFacility.xsl";
    }

    /**
     * Calls the init method of the parent class.
     *
     * @param evt The init event. LegacyInitEvent is marked deprecated. What should be used insted?
     */
    @Override
    public void init(LegacyInitEvent evt) {
        super.init(evt);

    }
}