package org.libreccm.web;

import com.arsdigita.web.Application;

import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.core.AbstractResourcesExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractCcmApplicationsExporter<T extends Application>
    extends AbstractResourcesExporter<T> {

    @Override
    protected final void exportResourceProperties(
        final T resource, final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeStringField("applicationType", 
                                       resource.getApplicationType().getName());
        jsonGenerator.writeStringField("primaryUrl", 
                                       resource.getPath());
        
        exportApplicationProperties(resource, jsonGenerator);
    }

    protected abstract void exportApplicationProperties(
        T application, JsonGenerator jsonGenerator)
        throws IOException;

}
