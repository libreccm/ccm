
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
import java.util.Date;

/**
 *
 * @author Sören Bernstein
 */
public class SurveyResponseCollection extends DomainCollection {

    /**
     * Creates a new instance of SurveyResponseCollection
     */
    public SurveyResponseCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(SurveyResponse.ENTRY_DATE);
    }

    public SurveyResponseCollection(DataCollection dataCollection, User user) {
        this(dataCollection);

//        m_dataCollection.addFilter(SurveyResponse.USER);
    }

    // Get the entry date
    public Date getEntryDate() {
        return (Date) m_dataCollection.get(SurveyResponse.ENTRY_DATE);
    }

    // Get the user
    public User getUser() {
        return (User) m_dataCollection.get(SurveyResponse.USER);
    }

    public SurveyResponse getSurveyResponse() {
        return new SurveyResponse(m_dataCollection.getDataObject());
    }

}
