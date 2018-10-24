package org.libreccm.workflow;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TaskDependenciesExporter
    extends AbstractDomainObjectsExporter<Task> {

    @Override
    public Class<Task> exportsType() {
        return Task.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Task.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.workflow.TaskDependency";
    }

    @Override
    protected List<String> exportDomainObject(final Task domainObject,
                                              final Path targetDir) {

        final TaskCollection blockingTasks = domainObject.getRequiredTasks();
        final List<String> uuids = new ArrayList<>();
        while (blockingTasks.next()) {

            final Task blockingTask = blockingTasks.getTask();

            final String uuid = exportTaskDependency(domainObject,
                                                     blockingTask,
                                                     targetDir);
            uuids.add(uuid);

        }

        return uuids;
    }

    private String exportTaskDependency(final Task task,
                                        final Task blockingTask,
                                        final Path targetDir) {

        final byte[] uuidSource = String.format(
            "%s/%s-%s",
            WebConfig.getInstanceOf().getSiteName(),
            task.getOID().toString(),
            blockingTask.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        final Path targetFilePath = targetDir
            .resolve("org.libreccm.workflow.TaskDependency")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("taskDependencyId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            final String taskUuid = generateUuid(task);
            jsonGenerator.writeStringField("blockedTask", taskUuid);

            final String blockingTaskUuid = generateUuid(blockingTask);
            jsonGenerator.writeStringField("blockingTask", blockingTaskUuid);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return uuid;
    }

}
