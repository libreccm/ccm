/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class GenericOrgaUnitTextAssetConfig extends AbstractConfig {

    private static GenericOrgaUnitTextAssetConfig INSTANCE;

    private final Parameter assetStepSortKey =  new IntegerParameter(
            "com.arsdigita.cms.contentassets.orgaunit_textasset.asset_step_sortkey",
            Parameter.REQUIRED,
            5);
    
    public GenericOrgaUnitTextAssetConfig() {
        
        super();
        
        register(assetStepSortKey);
        
        loadInfo();
    }
    
    public static final GenericOrgaUnitTextAssetConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GenericOrgaUnitTextAssetConfig();
            INSTANCE.load();
        }
        
        return INSTANCE;
    }
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(assetStepSortKey);
    }

}
