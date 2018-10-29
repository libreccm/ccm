package org.librecms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;

import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.web.AbstractCcmApplicationsExporter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionsExporter
    extends AbstractCcmApplicationsExporter<ContentSection> {

    @Override
    public Class<ContentSection> exportsType() {
        return ContentSection.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return ContentSection.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.contentsection.ContentSection";
    }

    @Override
    protected void exportApplicationProperties(
        final ContentSection section,
        final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeStringField("label", section.getTitle());

        final Folder rootDocsFolder = section.getRootFolder();
        final String rootDocsFolderUuid = generateUuid(rootDocsFolder);
        jsonGenerator.writeStringField("rootDocumentsFolder",
                                       rootDocsFolderUuid);

        jsonGenerator.writeStringField("pageResolverClass",
                                       section.getPageResolverClassName());
        jsonGenerator.writeStringField("itemResolverClass",
                                       section.getItemResolverClassName());
        jsonGenerator.writeStringField("templateResolverClass",
                                       section.getTemplateResolverClassName());
        jsonGenerator.writeStringField("xmlGeneratorClass",
                                       section.getXMLGeneratorClassName());

        jsonGenerator.writeArrayFieldStart("roles");
        final RoleCollection roles = section.getGroup().getRoles();
        while (roles.next()) {

            final Role role = roles.getRole();
            final String roleUuid = generateUuid(role);
            jsonGenerator.writeString(roleUuid);
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeStringField("defaultLocale",
                                       section.getDefaultLocale().toString());

        jsonGenerator.writeArrayFieldStart("contentTypes");
        final ContentTypeCollection types = section.getContentTypes();
        while (types.next()) {

            final ContentType type = types.getContentType();
            final String typeUuid = generateUuid(type);
            jsonGenerator.writeString(typeUuid);
        }
        jsonGenerator.writeEndArray();

        final LifecycleDefinitionCollection lifecycleDefs = section
            .getLifecycleDefinitions();
        jsonGenerator.writeArrayFieldStart("lifecycleDefinitions");
        while (lifecycleDefs.next()) {
            final LifecycleDefinition lifecycleDef = lifecycleDefs
                .getLifecycleDefinition();
            final String lifecycleDefUuid = generateUuid(lifecycleDef);
            jsonGenerator.writeString(lifecycleDefUuid);
        }
        jsonGenerator.writeEndArray();

        final TaskCollection workflowTemplates = section.getWorkflowTemplates();
        jsonGenerator.writeArrayFieldStart("workflowTemplates");
        while (workflowTemplates.next()) {

            final Task workflowTemplate = workflowTemplates.getTask();
            final String workflowTemplateUuid = generateUuid(workflowTemplate);
            jsonGenerator.writeString(workflowTemplateUuid);

        }

        jsonGenerator.writeEndArray();
    }

}
