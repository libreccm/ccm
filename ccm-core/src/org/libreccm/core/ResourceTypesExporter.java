package org.libreccm.core;

import com.arsdigita.kernel.ResourceType;

import com.fasterxml.jackson.core.JsonGenerator;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ResourceTypesExporter
    extends AbstractResourceTypesExporter<ResourceType> {

    @Override
    public Class<ResourceType> exportsType() {
        return ResourceType.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return ResourceType.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.core.ResourceType";
    }

    protected void exportResourceTypeProperties(
        final ResourceType resourceType, final JsonGenerator jsonGenerator) {

        //Nothing
    }

}
