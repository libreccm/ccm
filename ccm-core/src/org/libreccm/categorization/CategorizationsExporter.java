package org.libreccm.categorization;

import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizationsExporter
    extends AbstractDomainObjectsExporter<Category> {

    @Override
    public Class<Category> exportsType() {
        return Category.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Category.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.categorization.Categorization";
    }

    @Override
    protected List<String> exportDomainObject(final Category domainObject,
                                              final Path targetDir) {

        final CategorizedCollection categorized = domainObject
            .getObjects(ACSObject.BASE_DATA_OBJECT_TYPE);
        final List<String> uuids = new ArrayList<>();
        while (categorized.next()) {

            final String uuid = exportCategorization(targetDir,
                                                     domainObject,
                                                     categorized.getACSObject(),
                                                     categorized.getPosition());
            uuids.add(uuid);
        }

        return uuids;

    }

    private String exportCategorization(final Path targetDir,
                                        final Category category,
                                        final ACSObject object,
                                        final long position) {

        final String categoryUuid = generateUuid(category);
        final String objectUuid = generateUuid(object);

        final byte[] uuidSource = String
            .format("%s/%s-%s",
                    WebConfig.getInstanceOf().getSiteName(),
                    category.getOID().toString(),
                    object.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String uuid = UUID.nameUUIDFromBytes(uuidSource).toString();

        final ACSObject indexObj = category.getIndexObject();
        boolean isIndex = object.equals(indexObj);

        final Path targetFilePath = targetDir
            .resolve("org.libreccm.categorization.Categorization")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();
        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("categorizationId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("category", categoryUuid);
            jsonGenerator.writeStringField("categorizedObject", objectUuid);

            jsonGenerator.writeBooleanField("indexObject", isIndex);
            jsonGenerator.writeNumberField("objectOrder", position);

            jsonGenerator.writeEndObject();

            return uuid;

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

    }

}
