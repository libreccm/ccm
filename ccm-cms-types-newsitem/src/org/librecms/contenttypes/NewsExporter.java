package org.librecms.contenttypes;

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.NewsItem;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractContentItemsExporter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class NewsExporter extends AbstractContentItemsExporter<NewsItem> {

    @Override
    protected void exportContentItemProperties(
        final NewsItem newsItem, final JsonGenerator jsonGenerator)
        throws IOException {

        final DateTimeFormatter dateTimeFormatter
                                    = DateTimeFormatter.ISO_DATE_TIME;
        if (newsItem.getNewsDate() != null) {
            final LocalDateTime newsDate = LocalDateTime
                .ofInstant(newsItem.getNewsDate().toInstant(),
                           ZoneId.systemDefault());
            jsonGenerator.writeStringField(
                "releaseDate", dateTimeFormatter.format(newsDate));
        }
        jsonGenerator.writeBooleanField("homepage", newsItem.isHomepage());
    }

    @Override
    protected Map<String, Map<Locale, String>> collectLocalizedValues(
        final ItemCollection instances) {

        final Map<Locale, String> leadPropertyValues = new HashMap<>();
        final Map<Locale, String> textPropertyValues = new HashMap<>();

        while (instances.next()) {

            final NewsItem news = (NewsItem) instances.getContentItem();
            final String lang = news.getLanguage();
            final Locale locale = new Locale(lang);
            final String lead = news.getLead();
            final String text;
            if (news.getTextAsset() == null) {
                text = "";
            } else {
                text = news.getTextAsset().getText();
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
    public Class<NewsItem> exportsType() {

        return NewsItem.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return NewsItem.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.contenttypes.News";
    }

}
