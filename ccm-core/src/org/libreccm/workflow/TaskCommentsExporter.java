package org.libreccm.workflow;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.TaskComment;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class TaskCommentsExporter
    extends AbstractDomainObjectsExporter<TaskComment> {

    @Override
    public Class<TaskComment> exportsType() {
        return TaskComment.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return TaskComment.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.workflow.TaskComment";
    }

    @Override
    protected List<String> exportDomainObject(final TaskComment domainObject,
                                              final Path targetDir) {

        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = targetDir
            .resolve("org.libreccm.workflow.TaskComment")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField(
                "taskCommentId",
                IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeStringField("comment",
                                           domainObject.getComment());

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
