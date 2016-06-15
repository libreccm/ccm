/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.modules.core.categorization;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.modules.core.core.CcmObject;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class Categorization implements Identifiable {

    public String trunkClass;

    private long categorizationId;
    private Category category;
    private CcmObject categorizedObject;
    private boolean index;
    private long categoryOrder;
    private long objectOrder;


    public Categorization() {

    }

    @Override
    public String getTrunkClass() {
        return this.trunkClass;
    }

    @Override
    public void setTrunkClass(String trunkClass) {
        this.trunkClass = trunkClass;
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new CategorizationMarshaller();
    }
}
