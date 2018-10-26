package org.libreccm.security;

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;

import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.WebConfig;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.export.AbstractDomainObjectsExporter;
import org.libreccm.export.IdSequence;

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
public class GroupMembershipsExporter
    extends AbstractDomainObjectsExporter<Group> {

    @Override
    public Class<Group> exportsType() {
        return Group.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Group.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.security.GroupMembership";
    }

    private String exportGroupMembership(final Group group,
                                         final User member,
                                         final Path targetDir) {

        final String groupUuid = generateUuid(group);
        final String memberUuid = generateUuid(member);

        final byte[] membershipUuidSource = String
            .format("%s/%s-%s",
                    WebConfig.getInstanceOf().getSiteName(),
                    group.getOID().toString(),
                    member.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String membershipUuid = UUID
            .nameUUIDFromBytes(membershipUuidSource)
            .toString();

        final Path targetFilePath = generateTargetFilePath(
            targetDir,
            "org.libreccm.security.GroupMembership",
            membershipUuid);

        final JsonFactory jsonFactory = new JsonFactory();
        try (final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {

            setPrettyPrinter(jsonGenerator);

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("membershipId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", membershipUuid);
            jsonGenerator.writeStringField("group", groupUuid);
            jsonGenerator.writeStringField("member", memberUuid);
            jsonGenerator.writeEndObject();

        } catch (IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return membershipUuid;

    }

    @Override
    protected List<String> exportDomainObject(final Group domainObject,
                                              final Path targetDir) {

        final List<String> uuids = new ArrayList<>();
        final UserCollection members = domainObject.getAllMemberUsers();
//
        while (members.next()) {
            final User member = members.getUser();
            final String uuid = exportGroupMembership(domainObject,
                                                      member,
                                                      targetDir);
            uuids.add(uuid);
        }

        return uuids;
    }

}
