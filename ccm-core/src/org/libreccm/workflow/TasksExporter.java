package org.libreccm.workflow;

import com.arsdigita.workflow.simple.Task;

import com.fasterxml.jackson.core.JsonGenerator;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TasksExporter extends AbstractTasksExporter<Task> {

    @Override
    public Class<Task> exportsType() {
        return Task.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return Task.BASE_DATA_OBJECT_TYPE;

    }

    @Override
    public  String convertsToType() {
        return "org.libreccm.workflow.Task";
    }

    @Override
    protected void exportTaskProperties(final Task task, 
                                        final JsonGenerator jsonGenerator) {

        // Nothing
    }

}
