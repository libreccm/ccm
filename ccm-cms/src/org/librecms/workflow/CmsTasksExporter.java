package org.librecms.workflow;

import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.workflow.simple.UserTask;

import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.workflow.AbstractAssignableTasksExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CmsTasksExporter extends AbstractAssignableTasksExporter<CMSTask> {

    @Override
    public Class<CMSTask> exportsType() {
        return CMSTask.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return UserTask.BASE_DATA_OBJECT_TYPE;

    }

    @Override
    public String convertsToType() {
        return "org.libreccm.workflow.CmsTask";
    }

    @Override
    protected void exportAssignableTaskProperties(
        final CMSTask task, final JsonGenerator jsonGenerator)
        throws IOException {

        final CMSTaskType taskType = task.getTaskType();

        final String type;
        switch (taskType.getName().toUpperCase()) {
            case "AUTHOR":
                type = "AUTHOR";
                break;
            case "EDIT":
                type = "EDIT";
                break;
            case "DEPLOY":
                type = "DEPLOY";
                break;
            default:
                type = "AUTHOR";
                break;
        }
        
        jsonGenerator.writeStringField("taskType", type);
    }

}
