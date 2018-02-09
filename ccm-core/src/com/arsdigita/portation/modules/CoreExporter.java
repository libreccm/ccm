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
package com.arsdigita.portation.modules;

import com.arsdigita.portation.AbstractExporter;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.categorization.CategorizationMarshaller;
import com.arsdigita.portation.modules.core.categorization.CategoryMarshaller;
import com.arsdigita.portation.modules.core.security.*;
import com.arsdigita.portation.modules.core.workflow.*;

import java.util.ArrayList;

/**
 * Helper to implement the specifics for the exportation. Makes source code
 * in the cli-tool shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 25.07.2016
 */
public class CoreExporter extends AbstractExporter {

    private static CoreExporter instance;

    static {
        instance = new CoreExporter();
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static CoreExporter getInstance() {
        return instance;
    }

    @Override
    public void startMarshaller() {
        UserMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        GroupMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        GroupMembershipMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        RoleMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        RoleMembershipMarshaller.getInstance().
                marshallAll(format, pathName, indentation);

        CategoryMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        CategorizationMarshaller.getInstance().
                marshallAll(format, pathName, indentation);

        PermissionMarshaller.getInstance().
                marshallAll(format, pathName, indentation);

        WorkflowMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        TaskCommentMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        AssignableTaskMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        TaskDependencyMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        TaskAssignmentMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
    }
}
