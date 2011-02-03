package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @param <T>
 * @author jensp
 */
public interface BibTeXConverter {

    String convert(Publication publication);

    String getCcmType();
}
