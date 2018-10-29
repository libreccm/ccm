package org.librecms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.kernel.KernelConfig;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentTypesExporter extends AbstractDomainObjectsExporter<ContentType> {

    @Override
    public Class<ContentType> exportsType() {
        return ContentType.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return ContentSection.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.contentsection.ContentType";
    }

    @Override
    protected List<String> exportDomainObject(final ContentType contentType,
                                              final Path targetDir) {

        final DataCollection contentSectionsCollections = ContentTypesService
            .getContentSections(contentType);

        final JsonFactory jsonFactory = new JsonFactory();

        final List<String> uuids = new ArrayList<>();
        while (contentSectionsCollections.next()) {

            final String className = contentType.getClassName();
            final ContentSection contentSection = new ContentSection(
                contentSectionsCollections.getDataObject());

            final byte[] uuidSource = String.format(
                "%s/%s-%s-%s",
                WebConfig.getInstanceOf().getSiteName(),
                contentType.getOID().toString(),
                contentSection.getOID().toString(),
                className)
                .getBytes(StandardCharsets.UTF_8);
            final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();
            uuids.add(uuid);

            final String contentSectionUuid = generateUuid(contentSection);

            final Path targetFilePath = targetDir
                .resolve("org.librecms.contentsection.ContentType")
                .resolve(String.format("%s.json", uuid));
            final File targetFile = targetFilePath.toFile();

            try (JsonGenerator jsonGenerator = jsonFactory
                .createGenerator(targetFile, JsonEncoding.UTF8)) {
                
                setPrettyPrinter(jsonGenerator);

                jsonGenerator.writeStartObject();

                jsonGenerator.writeNumberField(
                    "objectId",
                    IdSequence.getInstance().nextId());
                jsonGenerator.writeStringField("uuid", uuid);

                jsonGenerator.writeStringField("contentItemClass",
                                               className);
                jsonGenerator.writeStringField("contentSection",
                                               contentSectionUuid);

                jsonGenerator.writeObjectFieldStart("label");;
                jsonGenerator.writeStringField(
                    KernelConfig.getConfig().getDefaultLanguage(),
                    (String) contentType.getLabel().localize(
                        new Locale(KernelConfig
                            .getConfig()
                            .getDefaultLanguage())));
                jsonGenerator.writeEndObject();

                jsonGenerator.writeObjectFieldStart("description");
                jsonGenerator.writeStringField(
                    KernelConfig.getConfig().getDefaultLanguage(),
                    contentType.getDescription());
                jsonGenerator.writeEndObject();

                jsonGenerator.writeStringField("mode", contentType.getMode());

                jsonGenerator.writeEndObject();
            } catch(IOException ex) {
                throw new UncheckedWrapperException(ex);
            }

        }

        return uuids;
    }

}
