package org.librecms.lifecycle;

import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.PhaseCollection;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LifecyclesExporter extends AbstractDomainObjectsExporter<Lifecycle> {

    @Override
    protected List<String> exportDomainObject(final Lifecycle lifecycle,
                                              final Path targetDir) {

        final String uuid = generateUuid(lifecycle);

        final Path targetFilePath = targetDir
            .resolve("org.libreccm.lifecycle.Lifecycle")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);
            
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("lifecycleId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            final DateTimeFormatter dateTimeFormatter
                                        = DateTimeFormatter.ISO_DATE_TIME;

            jsonGenerator.writeStringField(
                "startDateTime",
                dateTimeFormatter.format(
                    LocalDateTime.ofInstant(
                        lifecycle.getStartDate().toInstant(),
                        ZoneId.systemDefault())));
            jsonGenerator.writeStringField(
                "endDateTime",
                dateTimeFormatter.format(
                    LocalDateTime.ofInstant(
                        lifecycle.getEndDate().toInstant(),
                        ZoneId.systemDefault())));

            jsonGenerator.writeStringField("listener",
                                           lifecycle.getListenerClassName());

            jsonGenerator.writeBooleanField("started", lifecycle.hasBegun());
            jsonGenerator.writeBooleanField("finished", lifecycle.hasEnded());

            jsonGenerator.writeStringField(
                "lifecycleDefinition",
                generateUuid(lifecycle.getLifecycleDefinition()));

            final PhaseCollection phases = lifecycle.getPhases();
            jsonGenerator.writeStartArray();
            while (phases.next()) {
                jsonGenerator.writeString(generateUuid(phases.getPhase()));
            }
            jsonGenerator.writeEndArray();
            
            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

    @Override
    public Class<Lifecycle> exportsType() {

        return Lifecycle.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return Lifecycle.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.lifecycle.Lifecycle";

    }
}