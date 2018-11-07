package org.librecms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.kernel.KernelConfig;

import com.fasterxml.jackson.core.JsonGenerator;
import org.libreccm.core.AbstractCcmObjectsExporter;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FoldersExporter extends AbstractCcmObjectsExporter<Folder> {

    @Override
    protected void exportObjectProperties(final Folder folder,
                                          final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeStringField("uniqueId", generateUuid(folder));
        jsonGenerator.writeStringField("name", folder.getName());

        final ContentSection section = folder.getContentSection();
        if (section != null) {
            final String sectionUuid = generateUuid(section);
            jsonGenerator.writeStringField("contentSection", sectionUuid);
        }

        jsonGenerator.writeObjectFieldStart("title");
        jsonGenerator.writeStringField(
            KernelConfig.getConfig().getDefaultLanguage(),
            folder.getName());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeBooleanField("enabled", true);
        jsonGenerator.writeBooleanField("visible", true);
        jsonGenerator.writeBooleanField("abstractCategory", true);

        if (folder.getParent() != null) {
            jsonGenerator.writeStringField(
                "parentCategory",
                generateUuid(folder.getParent()));
        }

    }

    @Override
    public Class<Folder> exportsType() {

        return Folder.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return Folder.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.contentsection.Folder";
    }

}
