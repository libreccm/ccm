package org.libreccm.security;

import com.arsdigita.kernel.Group;
import com.arsdigita.util.UncheckedWrapperException;

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
public class GroupsExporter extends AbstractDomainObjectsExporter<Group> {

    @Override
    public Class<Group> exportsType() {
        return Group.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Group.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.security.Group";
    }

    @Override
    protected List<String> exportDomainObject(final Group domainObject,
                                              final Path targetDir) {

        final JsonFactory jsonFactory = new JsonFactory();
        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = generateTargetFilePath(
            targetDir, "org.libreccm.security.Group", uuid);
        final File targetFile = targetFilePath.toFile();

        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("partyId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("name", domainObject.getName());
            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
