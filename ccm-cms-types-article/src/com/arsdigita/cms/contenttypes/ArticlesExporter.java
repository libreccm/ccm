package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ItemCollection;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractContentItemsExporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ArticlesExporter extends AbstractContentItemsExporter<Article> {

    @Override
    protected void exportContentItemProperties(
        final Article contentItem, final JsonGenerator jsonGenerator)
        throws IOException {

    }

    @Override
    protected Map<String, Map<Locale, String>> collectLocalizedValues(
        final ItemCollection instances) {

        final Map<Locale, String> leadPropertyValues = new HashMap<>();
        final Map<Locale, String> textPropertyValues = new HashMap<>();

        while (instances.next()) {

            final Article article = (Article) instances.getContentItem();
            final String lang = article.getLanguage();
            final Locale locale = new Locale(lang);
            final String lead = article.getLead();
            final String text;
            if (article.getTextAsset() == null) {
                text = "";
            } else {
                text = article.getTextAsset().getText();
            }

            leadPropertyValues.put(locale, lead);
            textPropertyValues.put(locale, text);
        }

        instances.rewind();

        final Map<String, Map<Locale, String>> properties = new HashMap<>();
        properties.put("description", leadPropertyValues);
        properties.put("text", textPropertyValues);

        return properties;
    }

    @Override
    public Class<Article> exportsType() {
        return Article.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Article.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.contenttypes.Article";
    }

}
