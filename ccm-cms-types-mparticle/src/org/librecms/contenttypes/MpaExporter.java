package org.librecms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.ArticleSectionCollection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractContentItemsExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MpaExporter extends AbstractContentItemsExporter<MultiPartArticle> {

    @Override
    public void exportContentItemProperties(
        final MultiPartArticle article, final JsonGenerator jsonGenerator)
        throws IOException {

        final ContentBundle bundle = article.getContentBundle();
        final ItemCollection instances = bundle.getInstances();
        
        final Map<Integer, MpaSection> mpaSections = new HashMap<>();
        while(instances.next()) {
            final ArticleSectionCollection sections = article.getSections();
            while(sections.next()) {
                
                final ArticleSection section = sections.getArticleSection();
                
                final MpaSection mpaSection;
                if (mpaSections.containsKey(section.getRank())) {
                    mpaSection = mpaSections.get(section.getRank());
                } else {
                    mpaSection = new MpaSection();
                    mpaSection.setRank(section.getRank());
                    mpaSection.setPageBreak(section.isPageBreak());
                    mpaSections.put(section.getRank(), mpaSection);
                }
                
                final String language = section.getLanguage();
                final Locale locale = new Locale(language);
                mpaSection.addTitle(locale, section.getTitle());
                mpaSection.addText(locale, section.getText().getText());
            }
        }
        instances.rewind();
        
        final List<MpaSection> sectionList = new ArrayList<>(
            mpaSections.values());
        sectionList
            .sort((section1, section2) -> Integer.compare(section1.getRank(), 
                                                          section2.getRank()));
        
        jsonGenerator.writeArrayFieldStart("sections");
        for(final MpaSection mpaSection: sectionList) {
            jsonGenerator.writeStartObject();
            exportLocalizedField(jsonGenerator, "title", mpaSection.getTitle());
            jsonGenerator.writeNumberField("rank", mpaSection.getRank());
            jsonGenerator.writeBooleanField("pageBreak", 
                                            mpaSection.isPageBreak());
            exportLocalizedField(jsonGenerator, "text", mpaSection.getText());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }

    @Override
    protected Map<String, Map<Locale, String>> collectLocalizedValues(
        final ItemCollection instances) {

        final Map<Locale, String> leadPropertyValues = new HashMap<>();
//        final Map<Locale, MpaSection> sections = new HashMap<>();
        
        while(instances.next()) {
            
            final MultiPartArticle article = (MultiPartArticle) instances
                .getContentItem();
            final String lang = article.getLanguage();
            final Locale locale = new Locale(lang);
            final String lead = article.getSummary();
            
            leadPropertyValues.put(locale, lead);
        }
        
        instances.rewind();
        
        final Map<String, Map<Locale, String>> properties = new HashMap<>();
        properties.put("lead", leadPropertyValues);
        
        return properties;
    }

    @Override
    public Class<MultiPartArticle> exportsType() {

        return MultiPartArticle.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return MultiPartArticle.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {

        return "org.librecms.contenttypes.MultiPartArticle";
    }
    
    private void exportLocalizedField(final JsonGenerator jsonGenerator,
                                      final String name,
                                      final Map<Locale, String> values) 
        throws IOException {
        
        jsonGenerator.writeObjectFieldStart(name);
        
        for(final Map.Entry<Locale, String> entry : values.entrySet()) {
            jsonGenerator.writeStringField(entry.getKey().toString(), 
                                           entry.getValue());
        }
        
        jsonGenerator.writeEndObject();
    }

    private class MpaSection {

        private Map<Locale, String> title;
        private int rank;
        private boolean pageBreak;
        private Map<Locale, String> text;
        
        public MpaSection() {
            title = new HashMap<>();
            text = new HashMap<>();
        }

        public Map<Locale, String> getTitle() {
            return Collections.unmodifiableMap(title);
        }
        
        public void addTitle(final Locale locale, final String value) {
            title.put(locale, value);
        }

        public void setTitle(final Map<Locale, String> title) {
            this.title = new HashMap<>(title);
        }

        public int getRank() {
            return rank;
        }

        public void setRank(final int rank) {
            this.rank = rank;
        }

        public boolean isPageBreak() {
            return pageBreak;
        }

        public void setPageBreak(final boolean pageBreak) {
            this.pageBreak = pageBreak;
        }

        public Map<Locale, String> getText() {
            return Collections.unmodifiableMap(text);
        }
        
        public void addText(final Locale locale, final String value) {
            text.put(locale, value);
        }

        public void setText(final Map<Locale, String> text) {
            this.text = new HashMap<>(text);
        }

    }

}
