package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jensp
 */
public class MiscBuilder extends AbstractBibTeXBuilder {

    private final BibTeXField[] mandatoryFields = {};
    private final BibTeXField[] supportedFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.HOWPUBLISHED,
                                                   BibTeXField.MONTH,
                                                   BibTeXField.YEAR,
                                                   BibTeXField.NOTE};

    @Override
    protected List<BibTeXField> getMandatoryFields() {
        return Arrays.asList(mandatoryFields);
    }

    @Override
    protected boolean isFieldSupported(BibTeXField name) {
        return Arrays.asList(supportedFields).contains(name);
    }

    @Override
    public String getBibTeXType() {
        return "misc";
    }

    @Override
    public String toString() {
        return toBibTeX();
    }
}
