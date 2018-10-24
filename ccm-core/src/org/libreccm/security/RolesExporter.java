package org.libreccm.security;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.Role;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RolesExporter extends AbstractDomainObjectsExporter<Role> {

    @Override
    public Class<Role> exportsType() {
        return Role.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Role.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.security.Role";
    }

    @Override
    protected List<String> exportDomainObject(final Role domainObject,
                                              final Path targetDir) {

        final JsonFactory jsonFactory = new JsonFactory();
        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = generateTargetFilePath(
            targetDir, "org.libreccm.security.Role", uuid);

        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("roleId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("name", domainObject.getName());

            jsonGenerator.writeFieldName("description");
            jsonGenerator.writeStartObject();
            jsonGenerator
                .writeStringField(KernelConfig.getConfig().getDefaultLanguage(),
                                  domainObject.getDescription());

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
