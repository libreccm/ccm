package org.librecms.lifecycle;

import com.arsdigita.cms.lifecycle.PhaseDefinition;
import com.arsdigita.kernel.KernelConfig;
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
public class PhaseDefinitionsExporter
    extends AbstractDomainObjectsExporter<PhaseDefinition> {

    @Override
    public Class<PhaseDefinition> exportsType() {
        return PhaseDefinition.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return PhaseDefinition.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.lifecycle.PhaseDefinition";
    }

    @Override
    protected List<String> exportDomainObject(
        final PhaseDefinition phaseDefinition, final Path targetDir) {

        final String uuid = generateUuid(phaseDefinition);
        final Path targetFilePath = targetDir
            .resolve("org.librecms.lifecycle.PhaseDefinition")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("definitionId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeObjectFieldStart("label");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                phaseDefinition.getLabel());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                phaseDefinition.getDescription());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeNumberField("defaultDelay",
                                           phaseDefinition.getDefaultDelay());
            if (phaseDefinition.getDefaultDuration() != null) {
                jsonGenerator.writeNumberField(
                    "defaultDuration",
                    phaseDefinition.getDefaultDuration());
            }

            jsonGenerator.writeStringField("defaultListener",
                                           phaseDefinition.getDefaultListener());

            jsonGenerator.writeEndObject();
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
