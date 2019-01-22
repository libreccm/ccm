package org.libreccm.categorization;

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.persistence.DataObject;
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
public class DomainsExporter extends AbstractDomainObjectsExporter<Domain> {

    @Override
    public Class<Domain> exportsType() {
        return Domain.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Domain.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.categorization.Domain";
    }

    @Override
    protected List<String> exportDomainObject(final Domain domainObject,
                                              final Path targetDir) {

        final String uuid = generateUuid(domainObject);

        final Path targetFilePath = targetDir
            .resolve("org.libreccm.categorization.Categorization")
            .resolve(String.format("%s.json", uuid));
        final File targetFile = targetFilePath.toFile();

        final JsonFactory jsonFactory = new JsonFactory();
        try (JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFile, JsonEncoding.UTF8)) {
            
            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("objectId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", uuid);
            jsonGenerator.writeStringField("domainKey", domainObject.getKey());
            jsonGenerator.writeStringField("uri",
                                           domainObject.getURL().toString());

            jsonGenerator.writeObjectFieldStart("title");
            jsonGenerator.writeStringField(
                KernelConfig.getConfig().getDefaultLanguage(),
                domainObject.getTitle());
            jsonGenerator.writeEndObject();

            final Category root = (Category) DomainObjectFactory
                .newInstance((DataObject) domainObject.get("model"));
            final String rootCategoryUuid = generateUuid(root);
            jsonGenerator.writeStringField("rootCategory", rootCategoryUuid);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
