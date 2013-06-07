/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.Arrays;
import java.util.List;

/**
 * Builder for the <em>article</em> BibTeX type.
 *
 * @author Jens Pelzetter
 * @verson $Id$
 */
public class ArticleBuilder extends AbstractBibTeXBuilder {

    private final BibTeXField[] mandatoryFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.JOURNAL,
                                                   BibTeXField.YEAR};
    private final BibTeXField[] supportedFields = {BibTeXField.AUTHOR,
                                                   BibTeXField.TITLE,
                                                   BibTeXField.JOURNAL,
                                                   BibTeXField.YEAR,
                                                   BibTeXField.VOLUME,
                                                   BibTeXField.NUMBER,
                                                   BibTeXField.PAGES,
                                                   BibTeXField.MONTH,
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
        return "article";
    }

    @Override
    public String toString() {
        return toBibTeX();
    }
}
