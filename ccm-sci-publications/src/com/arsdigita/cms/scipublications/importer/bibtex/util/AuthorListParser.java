package com.arsdigita.cms.scipublications.importer.bibtex.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AuthorListParser {

    public AuthorListParser() {
        super();
    }

    public List<Author> parse(final String authors) {
        final String[] authorTokens = authors.split("and");

        final List<Author> authorList = new ArrayList<Author>(authorTokens.length);

        for (final String authorToken : authorTokens) {
            parseAuthorToken(authorToken, authorList);
        }

        return authorList;
    }

    private void parseAuthorToken(final String authorToken, final List<Author> authorList) {
        final String token = authorToken.trim();

        final Author author = new Author();

        final String[] nameTokens = token.split(",");
        if (nameTokens.length == 2) {
            author.setFirst(nameTokens[1]);
            parseLastName(nameTokens[0], author);
        } else if (nameTokens.length > 2) {
            author.setFirst(nameTokens[2]);
            author.setSuffix(nameTokens[1]);
            parseLastName(nameTokens[0], author);
        } else if (nameTokens.length == 1) {
            final String[] nameParts = nameTokens[0].split(" ");

            if (nameParts.length == 1) {
                author.setLast(nameParts[0]);

                return;
            } else if (nameParts.length == 2) {
                author.setFirst(nameParts[0]);
                author.setLast(nameParts[1]);

                return;
            } else if (nameParts.length >= 3) {
                author.setFirst(nameParts[0]);
                author.setPreLast(nameParts[1]);
                author.setLast(nameParts[2]);

                return;
            }

        }

        authorList.add(author);

    }

    private void parseLastName(final String lastName, final Author author) {
        final String[] lastNameParts = lastName.split(" ");
        if (lastNameParts.length == 1) {
            author.setLast(lastNameParts[0]);

        } else if (lastNameParts.length == 2) {
            author.setPreLast(lastNameParts[0]);
            author.setLast(lastNameParts[1]);
        }
    }

}
