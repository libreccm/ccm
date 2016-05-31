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
package com.arsdigita.portation.categories.Group;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 31.05.16
 */
public class Group implements Identifiable {

    private String trunkClass;

    public Group(com.arsdigita.kernel.Group sysGroup) {
        this.trunkClass = sysGroup.getClass().getName();
    }

    @Override
    public String getTrunkClass() {
        return trunkClass;
    }

    @Override
    public void setTrunkClass(String trunkClass) {
        this.trunkClass = trunkClass;
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return null;
    }
}
