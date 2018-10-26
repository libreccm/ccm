package org.libreccm.security;

import com.arsdigita.kernel.User;
import com.arsdigita.util.UncheckedWrapperException;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UsersExporter extends AbstractDomainObjectsExporter<com.arsdigita.kernel.User> {

    @Override
    public Class<com.arsdigita.kernel.User> exportsType() {
        return com.arsdigita.kernel.User.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return com.arsdigita.kernel.User.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.libreccm.security.User";
    }

    @Override
    protected List<String> exportDomainObject(final User domainObject,
                                              final Path targetDir) {

        final String uuid = generateUuid(domainObject);
        final Path targetFilePath = generateTargetFilePath(
            targetDir,
            "org.libreccm.security.User",
            uuid);
        final JsonFactory jsonFactory = new JsonFactory();

        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(),
                             JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();

            jsonGenerator.writeNumberField("partyId",
                                           IdSequence.getInstance().nextId());

            jsonGenerator.writeStringField("uuid", uuid);

            jsonGenerator.writeStringField("name", domainObject.getName());

            jsonGenerator.writeStringField(
                "givenName",
                domainObject.getPersonName().getGivenName());

            jsonGenerator.writeStringField(
                "familyName",
                domainObject.getPersonName().getFamilyName());

            jsonGenerator.writeFieldName("primaryEmailAddress");
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(
                "address",
                domainObject.getPrimaryEmail().getEmailAddress());
            jsonGenerator.writeBooleanField(
                "bouncing",
                domainObject.getPrimaryEmail().isBouncing());
            jsonGenerator.writeBooleanField(
                "verified",
                domainObject.getPrimaryEmail().isBouncing());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeBooleanField("banned", domainObject.isBanned());

            jsonGenerator.writeStringField("password", "");

            jsonGenerator.writeBooleanField("passwordResetRequired", true);

            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return Arrays.asList(new String[]{uuid});
    }

}
