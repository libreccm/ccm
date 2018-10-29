package org.librecms.contentsection;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.Workflow;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContentItemsExporter<T extends ContentPage>
    extends AbstractDomainObjectsExporter<T> {

    @Override
    protected final List<String> exportDomainObject(final T contentItem,
                                                    final Path targetDir) {

        final ContentBundle contentBundle = contentItem.getContentBundle();

        if (contentBundle == null) {
            return Collections.emptyList();
        }
        
        final ItemCollection instances = contentBundle.getInstances();

        final String uuid = generateUuid(contentBundle);
        final String itemUuid = generateUuid(contentBundle.getDraftVersion());
        final Path targetFilePath = generateTargetFilePath(
            targetDir, uuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("objectId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("displayName",
                                           contentBundle.getDisplayName());

            jsonGenerator.writeStringField("itemUuid", itemUuid);

            final Map<Locale, String> titles = new HashMap<>();
            final Map<Locale, String> descriptions = new HashMap<>();
            while (instances.next()) {

                final ContentPage instance = (ContentPage) instances
                    .getContentItem();

                final String lang = instance.getLanguage();
                final Locale locale = new Locale(lang);

                titles.put(locale, instance.getTitle());
                descriptions.put(locale, instance.getDescription());
            }

            instances.rewind();
            
            jsonGenerator.writeObjectFieldStart("title");
            for (final Map.Entry<Locale, String> title : titles.entrySet()) {

                jsonGenerator.writeStringField(title.getKey().toString(),
                                               title.getValue());

            }
            jsonGenerator.writeEndObject();

            jsonGenerator.writeObjectFieldStart("description");
            for (final Map.Entry<Locale, String> desc : descriptions.entrySet()) {

                jsonGenerator.writeStringField(desc.getKey().toString(),
                                               desc.getValue());

            }
            jsonGenerator.writeEndObject();

            if (contentItem.getVersion() == null) {
                jsonGenerator.writeStringField("version", "DRAFT");
            } else {
                jsonGenerator.writeStringField("version",
                                               contentItem.getVersion());
            }

            final DateTimeFormatter dateTimeFormatter
                                        = DateTimeFormatter.ISO_DATE_TIME;

            if (contentItem.getLaunchDate() != null) {
                final LocalDateTime launchDate = LocalDateTime
                    .ofInstant(contentItem.getLaunchDate().toInstant(),
                               ZoneId.systemDefault());
                jsonGenerator.writeStringField("launchDate",
                                               dateTimeFormatter.format(
                                                   launchDate));
            }

            jsonGenerator.writeStringField(
                "creationDate",
                dateTimeFormatter.format(
                    LocalDateTime.ofInstant(
                        contentItem.getCreationDate().toInstant(),
                        ZoneId.systemDefault())));

            jsonGenerator.writeStringField(
                "lastModified",
                dateTimeFormatter.format(
                    LocalDateTime.ofInstant(
                        contentItem.getLastModifiedDate().toInstant(),
                        ZoneId.systemDefault())));

            final Lifecycle lifecyle = contentItem.getLifecycle();
            if (lifecyle != null) {
                jsonGenerator.writeStringField(
                    "lifecycle",
                    generateUuid(contentItem.getLifecycle()));
            }

            final Workflow workflow = Workflow.getObjectWorkflow(contentItem);
            if (workflow != null) {
                jsonGenerator.writeStringField("workflow",
                                               generateUuid(workflow));
            }

            if (contentItem.getCreationUser() != null) {
                jsonGenerator.writeStringField(
                    "creationUserName",
                    contentItem.getCreationUser().getName());
            }

            if (contentItem.getLastModifiedUser() != null) {
                jsonGenerator.writeStringField(
                    "lastModifyingUserName",
                    contentItem.getLastModifiedUser().getName());
            }

            exportContentItemProperties(contentItem, jsonGenerator);

            final Map<String, Map<Locale, String>> localizedProperties
                                                       = collectLocalizedValues(
                    instances);

            for (final Map.Entry<String, Map<Locale, String>> property
                     : localizedProperties.entrySet()) {

                jsonGenerator.writeObjectFieldStart(property.getKey());

                for (final Map.Entry<Locale, String> localizedValue : property
                    .getValue().entrySet()) {

                    jsonGenerator.writeStringField(
                        localizedValue.getKey().toString(),
                        localizedValue.getValue());
                }

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndObject();
        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

    /**
     * Exports not localised properties of a content items.
     *
     * @param contentItem   The item providing the data.
     * @param jsonGenerator The {@link JsonGenerator} to use.
     *
     * @throws IOException
     */
    protected abstract void exportContentItemProperties(
        T contentItem, JsonGenerator jsonGenerator)
        throws IOException;

    /**
     * This method is used to collect localised properties. The
     * {@link #exportDomainObject(com.arsdigita.cms.ContentPage, java.nio.file.Path)}
     * method takes care for writing the values to the export file. The
     * properties {@code title} and {@code description} are already handled by
     * {@link #exportDomainObject(com.arsdigita.cms.ContentPage, java.nio.file.Path)}.
     *
     * If the {@code ContentItem} to export does not have any localised
     * properties the implementation of this method should return an empty map.
     * The most efficient way to create an empty map is the use
     * {@link Collections#emptyMap()}.
     *
     * @param instances The instances of the content item.
     *
     * @return A {@link Map} of {@link Maps} with the localised values. The key
     *         of the outer map is the name of the property, the value are the
     *         localised values. The inner map contains the localised values.
     *         The key of this map is the locale of the value.
     */
    protected abstract Map<String, Map<Locale, String>> collectLocalizedValues(
        ItemCollection instances);

}
