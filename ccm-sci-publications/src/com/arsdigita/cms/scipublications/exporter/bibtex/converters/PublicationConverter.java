package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class PublicationConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            PublicationConverter.class);

    @Override
    public String convert(final Publication publication) {
        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    @Override
    public String getBibTeXType() {
        return "misc";
    }

    @Override
    public String getCcmType() {
        return Publication.class.getName();
    }
}
