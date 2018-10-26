package org.libreccm.workflow;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskComment;
import com.arsdigita.workflow.simple.Workflow;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractTasksExporter<T extends Task>
    extends AbstractDomainObjectsExporter<T> {

    protected abstract void exportTaskProperties(
        T task, JsonGenerator jsonGenerator)
        throws IOException;

    @Override
    public final List<String> exportDomainObject(final T task,
                                                 final Path targetDir) {

        final String uuid = generateUuid(task);
        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("taskId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeObjectFieldStart("label");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                task.getLabel());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                task.getDescription());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeBooleanField("active", task.isActive());

            jsonGenerator.writeStringField("taskState",
                                           task.getStateString());

            final Workflow workflow = task.getWorkflow();
            final String workflowUuid = generateUuid(workflow);
            jsonGenerator.writeStringField("workflow", workflowUuid);

            jsonGenerator.writeArrayFieldStart("comments");
            final Iterator<?> comments = task.getComments();
            while (comments.hasNext()) {

                final TaskComment comment = (TaskComment) comments.next();
                final String commentUuid = generateUuid(comment);
                jsonGenerator.writeString(commentUuid);
            }
            jsonGenerator.writeEndArray();

            exportTaskProperties(task, jsonGenerator);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
