package org.librecms.contenttypes;

import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.contenttypes.Event;

import com.fasterxml.jackson.core.JsonGenerator;
import org.librecms.contentsection.AbstractContentItemsExporter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EventExporter extends AbstractContentItemsExporter<Event> {

    @Override
    protected void exportContentItemProperties(
        final Event event, final JsonGenerator jsonGenerator)
        throws IOException {

        final DateTimeFormatter dateTimeFormatter
                                    = DateTimeFormatter.ISO_DATE_TIME;

        if (event.getEndDate() != null) {

            final LocalDateTime endDate = LocalDateTime
                    .ofInstant(event.getEndDate().toInstant(),
                               ZoneId.systemDefault());
            final LocalDateTime endDateTime;
            
            
            if (event.getEndTime() == null) {
                 endDateTime = endDate;
            } else {

                final LocalDateTime endTime = LocalDateTime
                    .ofInstant(event.getEndTime().toInstant(),
                               ZoneId.systemDefault());

                endDateTime = LocalDateTime.of(endDate.toLocalDate(), 
                                               endTime.toLocalTime());
            } 

           
            jsonGenerator.writeStringField(
                "endDate", dateTimeFormatter.format(endDateTime));
        }

        if (event.getStartDate() != null) {

            final LocalDateTime startDate = LocalDateTime
                    .ofInstant(event.getStartDate().toInstant(),
                               ZoneId.systemDefault());
            final LocalDateTime startDateTime;
            if (event.getStartTime() == null) {
                startDateTime = startDate;
            } else {
                
                final LocalDateTime startTime = LocalDateTime
                    .ofInstant(event.getStartTime().toInstant(),
                               ZoneId.systemDefault());

                startDateTime = LocalDateTime.of(startDate.toLocalDate(), 
                                                 startTime.toLocalTime());
            } 
            jsonGenerator.writeStringField(
                "startDate", dateTimeFormatter.format(startDateTime));
        }
        
        jsonGenerator.writeStringField("mapLink", event.getMapLink());
    }

    @Override
    protected Map<String, Map<Locale, String>> collectLocalizedValues(
        final ItemCollection instances) {

        final Map<Locale, String> eventDateValues = new HashMap<>();
        final Map<Locale, String> locationValues = new HashMap<>();
        final Map<Locale, String> mainContributorValues = new HashMap<>();
        final Map<Locale, String> eventTypeValues = new HashMap<>();
        final Map<Locale, String> leadValues = new HashMap<>();
        final Map<Locale, String> textValues = new HashMap<>();
        
        while(instances.next()) {
            
            final Event event = (Event) instances.getContentItem();
            final String lang = event.getLanguage();
            final Locale locale = new Locale(lang);
            
            final String eventDate = event.getEventDate();
            final String location = event.getLocation();
            final String mainContributor = event.getMainContributor();
            final String eventType = event.getEventType();
            final String lead = event.getLead();
            final String text;
            if (event.getTextAsset() == null) {
                text = "";
            } else {
                text = event.getTextAsset().getText();
            }
            
            eventDateValues.put(locale, eventDate);
            locationValues.put(locale, location);
            mainContributorValues.put(locale, mainContributor);
            eventTypeValues.put(locale, eventType);
            leadValues.put(locale, lead);
            textValues.put(locale, text);
        }
        
        instances.rewind();
        
        final Map<String, Map<Locale, String>> properties = new HashMap<>();
        properties.put("eventDate", eventDateValues);
        properties.put("location", locationValues);
        properties.put("mainContributor", mainContributorValues);
        properties.put("eventType", eventTypeValues);
        properties.put("description", leadValues);
        properties.put("text", textValues);
        
        return properties;
    }

    @Override
    public Class<Event> exportsType() {
        return Event.class;
    }

    @Override
    public String exportsBaseDataObjectType() {
        return Event.BASE_DATA_OBJECT_TYPE;
    }

    @Override
    public String convertsToType() {
        return "org.librecms.contenttypes.Event";
    }

}
