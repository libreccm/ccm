package org.libreccm.workflow;

import com.arsdigita.workflow.simple.UserTask;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssignableTasksExporter
    extends AbstractAssignableTasksExporter<UserTask> {

    @Override
    protected void exportAssignableTaskProperties(
        final UserTask task, final JsonGenerator jsonGenerator)
        throws IOException {

        // Nothing
    }

    @Override
    public Class<UserTask> exportsType() {
        return UserTask.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return UserTask.BASE_DATA_OBJECT_TYPE;

    }

    @Override
    public String convertsToType() {
        return "org.libreccm.workflow.AssignableTask";
    }

}
