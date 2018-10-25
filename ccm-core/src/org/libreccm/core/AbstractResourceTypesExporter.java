package org.libreccm.core;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.ResourceType;
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
 * @param <T>
 */
public abstract class AbstractResourceTypesExporter<T extends ResourceType>
    extends AbstractDomainObjectsExporter<T> {

    /**
     * Implement this method to export the properties specific for the type.
     *
     * @param resourceType  The {@link ResourceType} to export.
     * @param jsonGenerator The {@link JsonGenerator} to use.
     */
    protected abstract void exportResourceTypeProperties(
        T resourceType, JsonGenerator jsonGenerator);

    @Override
    public final List<String> exportDomainObject(final T domainObject,
                                                 final Path targetDir) {

        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("resourceTypeId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeObjectFieldStart("title");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                domainObject.getTitle());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                domainObject.getDescription());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeBooleanField("workspaceApplication", false);
            jsonGenerator.writeBooleanField("viewableAsFullPage", false);
            jsonGenerator.writeBooleanField("workspaceAsEmbedded", false);
            jsonGenerator.writeBooleanField("singleton", false);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
