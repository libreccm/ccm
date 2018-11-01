package org.libreccm.core;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;
import org.libreccm.security.PermissionsExporter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for exporting types which extend {@link ACSObject}. The
 * implementation of
 * {@link #exportDomainObject(com.arsdigita.kernel.ACSObject, java.nio.file.Path)}
 * in this class takes care of the basic stuff for export an ACSObject/CcmObject
 * including exporting the properties shared by all ACSObjects/CcmObjects.
 *
 * Please note that this exporter does not handle permissions and
 * categorizations for the object. This is done by the
 * {@link PermissionsExporter} and the {@link CategorizationsExporter}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractCcmObjectsExporter<T extends ACSObject>
    extends AbstractDomainObjectsExporter<T> {

    /**
     * Implement this method to export the properties specific for the type.
     * 
     * @param ccmObject The {@link ACSObject} to export.
     * @param jsonGenerator The {@link JsonGenerator} to use.
     * @throws java.io.IOException
     */
    protected abstract void exportObjectProperties(
        T ccmObject, final JsonGenerator jsonGenerator)
        throws IOException ;

    @Override
    public final List<String> exportDomainObject(final T domainObject,
                                                 final Path targetDir) {

        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = generateTargetFilePath(
            targetDir, convertsToType(), uuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("objectId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("displayName",
                                           domainObject.getDisplayName());

            exportObjectProperties(domainObject, jsonGenerator);

            jsonGenerator.writeEndObject();
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});

    }

}
