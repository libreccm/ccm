package org.libreccm.core;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.ResourceType;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractResourcesExporter<T extends Resource>
    extends AbstractCcmObjectsExporter<T> {

    /**
     * Implement this method to export the properties specific for the type.
     *
     * @param resource      The {@link Resource} to export.
     * @param jsonGenerator The {@link JsonGenerator} to use.
     * @throws java.io.IOException
     */
    @Override
    protected final void exportObjectProperties(
        final T resource, final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeObjectFieldStart("title");
        jsonGenerator.writeStringField(
            KernelConfig.getConfig().getDefaultLanguage(),
            resource.getTitle());
        jsonGenerator.writeEndObject();
        
        jsonGenerator.writeObjectFieldStart("description");
        jsonGenerator.writeStringField(
            KernelConfig.getConfig().getDefaultLanguage(),
            resource.getDescription());
        jsonGenerator.writeEndObject();
        
        final ResourceType type = resource.getResourceType();
        final String typeUuid = generateUuid(type);
        jsonGenerator.writeStringField("resourceType", typeUuid);
        
        final Resource parent = resource.getParentResource();
        final String parentUuid = generateUuid(parent);
        jsonGenerator.writeStringField("parent", parentUuid);
        
        exportResourceProperties(resource, jsonGenerator);
    }

    protected abstract void exportResourceProperties(
        T resource, JsonGenerator jsonGenerator)
        throws IOException;

}
