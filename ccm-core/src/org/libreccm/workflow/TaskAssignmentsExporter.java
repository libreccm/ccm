package org.libreccm.workflow;

import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;
import com.arsdigita.workflow.simple.UserTask;

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
public class TaskAssignmentsExporter
    extends AbstractDomainObjectsExporter<UserTask> {

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
        return "org.libreccm.workflow.TaskAssignment";
    }

    @Override
    protected List<String> exportDomainObject(final UserTask domainObject,
                                              final Path targetDir) {

        final GroupCollection groups = domainObject.getAssignedGroupCollection();
        final List<String> uuids = new ArrayList<>();
        while (groups.next()) {

            final RoleCollection roles = groups.getGroup().getRoles();

            while (roles.next()) {

                final String uuid = exportTaskAssignment(domainObject,
                                                         roles.getRole(),
                                                         targetDir);
                uuids.add(uuid);
            }
        }

        return uuids;
    }

    private String exportTaskAssignment(final UserTask task,
                                        final Role role,
                                        final Path targetDir) {

        final byte[] uuidSource = String.format(
            "%s/%s-%s",
            WebConfig.getInstanceOf().getSiteName(),
            task.getOID().toString(),
            role.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        final Path targetFilePath = targetDir
            .resolve("org.libreccm.workflow.TaskAssignments")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("taskAssignmentId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            final String taskUuid = generateUuid(task);
            jsonGenerator.writeStringField("task", taskUuid);

            final String roleUuid = generateUuid(role);
            jsonGenerator.writeStringField("role", roleUuid);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return uuid;

    }

}
