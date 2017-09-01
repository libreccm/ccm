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
package com.arsdigita.portation.conversion;

import com.arsdigita.portation.AbstractConverter;
import com.arsdigita.portation.conversion.core.categorization.CategoryConversion;
import com.arsdigita.portation.conversion.core.security.GroupConversion;
import com.arsdigita.portation.conversion.core.security.PermissionConversion;
import com.arsdigita.portation.conversion.core.security.RoleConversion;
import com.arsdigita.portation.conversion.core.security.UserConversion;
import com.arsdigita.portation.conversion.core.workflow.AssignableTaskConversion;
import com.arsdigita.portation.conversion.core.workflow.WorkflowConversion;
import com.arsdigita.portation.modules.core.security.Permission;


/**
 * This core converter class calls all the conversions from trunk-objects
 * to ng-objects in a specific order to guarantee a correct dependency
 * recreation in the ng-objects. All the created objects are going to be
 * stored in maps as <id, object>-pairs in the {@link NgCoreCollection}-class.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 6/27/16
 */
public class CoreConverter extends AbstractConverter {

    private static CoreConverter instance;

    static {
        instance = new CoreConverter();
    }

    /**
     * Method, to start all the different converter classes in a specific
     * order, so that dependencies can only be set, where the objects have
     * already been created.
     */
    @Override
    public void startConversionToNg() {
        UserConversion.convertAll();
        GroupConversion.convertAll();

        RoleConversion.convertAll();

        CategoryConversion.convertAll();
        NgCoreCollection.sortCategories();
        // Verify categories
        /*for (Category category : NgCoreCollection.sortedCategories) {
            System.err.printf("\t\t\tCategory %s with parent category %s\n",
                    category.getName(), category.getParentCategory().getName()
            );
        }*/

        WorkflowConversion.convertAll();
        AssignableTaskConversion.convertAll();

        PermissionConversion.convertAll();

        // Verify permissions
        for (Permission permission : NgCoreCollection.permissions.values()) {
            if (permission.getGrantee() == null) {
                System.err.printf("CoreConverter: Grantee for permission %d " +
                        "is null.%n", permission.getPermissionId());
                System.exit(-1);
            }
        }
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static CoreConverter getInstance() {
        return instance;
    }
}