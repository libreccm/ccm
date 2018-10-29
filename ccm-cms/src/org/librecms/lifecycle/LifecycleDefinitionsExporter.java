package org.librecms.lifecycle;

import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.PhaseDefinitionCollection;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LifecycleDefinitionsExporter
    extends AbstractDomainObjectsExporter<LifecycleDefinition> {

    @Override
    public Class<LifecycleDefinition> exportsType() {
        return LifecycleDefinition.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return LifecycleDefinition.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.lifecycle.LifecycleDefinition";
    }

    @Override
    protected List<String> exportDomainObject(
        final LifecycleDefinition lifecycleDefinition, final Path targetDir) {

        final String uuid = generateUuid(lifecycleDefinition);

        final Path targetFilePath = targetDir
            .resolve("org.librecms.lifecycle.LifecycleDefinition")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);
            
            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("definitionId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeObjectFieldStart("label");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                lifecycleDefinition.getLabel());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                lifecycleDefinition.getDescription());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeStringField(
                "defaultListener",
                lifecycleDefinition.getDefaultListener());

            final PhaseDefinitionCollection phaseDefinitions
                                                = lifecycleDefinition
                    .getPhaseDefinitions();
            jsonGenerator.writeStartArray();
            while (phaseDefinitions.next()) {
                jsonGenerator
                    .writeString(generateUuid(
                        phaseDefinitions.getPhaseDefinition()));
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();;

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});

    }

}
