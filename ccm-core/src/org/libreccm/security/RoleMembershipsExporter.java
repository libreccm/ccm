package org.libreccm.security;

import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.Role;
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
public class RoleMembershipsExporter
    extends AbstractDomainObjectsExporter<Role> {

    @Override
    public Class<Role> exportsType() {
        return Role.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Role.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.libreccm.security.RoleMembership";
    }

    private String exportRoleMembership(final Role role,
                                        final Party member,
                                        final Path targetDir) {

        final String roleUuid = generateUuid(role);
        final String memberUuid = generateUuid(member);

        final byte[] membershipUuidSource = String
            .format("%s/%s-%s",
                    WebConfig.getInstanceOf().getSiteName(),
                    role.getOID().toString(),
                    member.getOID().toString())
            .getBytes(StandardCharsets.UTF_8);
        final String membershipUuid = UUID
            .nameUUIDFromBytes(membershipUuidSource)
            .toString();
        
         final Path targetFilePath = generateTargetFilePath(
            targetDir,
            "org.libreccm.security.RoleMembership",
            membershipUuid);
        
        final JsonFactory jsonFactory = new JsonFactory();
        try(final JsonGenerator jsonGenerator = jsonFactory
            .createGenerator(targetFilePath.toFile(), JsonEncoding.UTF8)) {
            
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("membershipId",
                                           IdSequence.getInstance().nextId());
            jsonGenerator.writeStringField("uuid", membershipUuid);
            jsonGenerator.writeStringField("role", roleUuid);
            jsonGenerator.writeStringField("member", memberUuid);
            jsonGenerator.writeEndObject();
            
        } catch(IOException ex) {
            throw new UncheckedWrapperException(ex);
        }

        return membershipUuid;
    }

    @Override
    protected List<String> exportDomainObject(final Role domainObject,
                                              final Path targetDir) {

        final List<String> uuids = new ArrayList<>();
        final PartyCollection members = domainObject.getContainedParties();

        while (members.next()) {
            final Party member = members.getParty();
            final String uuid = exportRoleMembership(domainObject,
                                                     member,
                                                     targetDir);
            uuids.add(uuid);
        }

        return uuids;

    }

}
