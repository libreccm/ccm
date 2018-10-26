package org.libreccm.categorization;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryLocalization;
import com.arsdigita.categorization.CategoryLocalizationCollection;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.arsdigita.categorization.Category.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoriesExporter extends AbstractDomainObjectsExporter<Category> {

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
        return "org.libreccm.categorization.Category";
    }

    @Override
    protected List<String> exportDomainObject(final Category category,
                                              final Path targetDir) {

        final String uuid = generateUuid(category);

        final Path targetFilePath = generateTargetFilePath(targetDir, uuid);
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("objectId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("uniqueId", uuid);

            jsonGenerator.writeStringField("name", category.getName());

            final CategoryLocalizationCollection localizations = category
                .getCategoryLocalizationCollection();
            final Map<Locale, String> titles = new HashMap<>();
            final Map<Locale, String> descriptions = new HashMap<>();
            while (localizations.next()) {

                final CategoryLocalization localization = localizations
                    .getCategoryLocalization();
                final Locale locale = new Locale(localization.getLocale());
                final String title = localization.getName();
                final String description = localization.getDescription();

                titles.put(locale, title);
                descriptions.put(locale, description);
            }

            jsonGenerator.writeObjectFieldStart("title");
            for (Map.Entry<Locale, String> title : titles.entrySet()) {
                jsonGenerator.writeStringField(title.getKey().toString(),
                                               title.getValue());
            }
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            for (Map.Entry<Locale, String> desc : descriptions.entrySet()) {
                jsonGenerator.writeStringField(desc.getKey().toString(),
                                               desc.getValue());
            }
            jsonGenerator.writeEndObject();

            jsonGenerator.writeBooleanField("enabled",
                                            category.isEnabled());
            jsonGenerator.writeBooleanField("visible",
                                            category.isVisible());
            jsonGenerator.writeBooleanField("abstractCategory",
                                            category.isAbstract());

            if (hasParentCategory(category)) {
                jsonGenerator.writeStringField(
                    "parentCategory",
                    generateUuid(category.getDefaultParentCategory()));
                jsonGenerator.writeNumberField(
                    "categoryOrder",
                    category
                        .getDefaultParentCategory()
                        .getSortKey(category));
            }

            jsonGenerator.writeEndObject();

            return Arrays.asList(new String[]{uuid});

        } catch (IOException ex) {

            throw new UncheckedWrapperException(ex);
        }
    }

    private boolean hasParentCategory(final Category category) {

        final DataAssociationCursor cursor = ((DataAssociation) category
                                              .get(PARENTS)).cursor();

        cursor.addEqualsFilter("link.isDefault", Boolean.TRUE);

        final boolean result = cursor.next();
        cursor.close();

        return result;
    }

}
