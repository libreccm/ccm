package org.librecms.lifecycle;

import com.arsdigita.cms.lifecycle.Phase;
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
public class PhasesExporter extends AbstractDomainObjectsExporter<Phase> {

    @Override
    public Class<Phase> exportsType() {
        return Phase.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Phase.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librcms.lifecycle.Phase";
    }

    @Override
    protected List<String> exportDomainObject(final Phase phase,
                                              final Path targetDir) {

        final String uuid = generateUuid(phase);
        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("phaseId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            final DateTimeFormatter dateTimeFormatter
                                        = DateTimeFormatter.ISO_DATE_TIME;

            jsonGenerator.writeStringField(
                "startDateTime",
                dateTimeFormatter.format(
                    LocalDateTime.ofInstant(phase.getStartDate().toInstant(),
                                            ZoneId.systemDefault())));

            if (phase.getEndDate() != null) {
                jsonGenerator.writeStringField(
                    "endDateTime",
                    dateTimeFormatter.format(
                        LocalDateTime.ofInstant(phase.getEndDate().toInstant(),
                                                ZoneId.systemDefault())));
            }

            jsonGenerator.writeStringField("listener",
                                           phase.getListenerClassName());

            jsonGenerator.writeBooleanField("started", phase.hasBegun());
            jsonGenerator.writeBooleanField("finished", phase.hasEnded());

            jsonGenerator.writeStringField(
                "phaseDefinition",
                generateUuid(phase.getPhaseDefinition()));

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
