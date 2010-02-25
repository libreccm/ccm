
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;

/**
 *
 * @author Sören Bernstein
 */
public class SurveyAnswerCollection extends DomainCollection {

    /**
     * Creates a new instance of SurveyAnswerCollection
     */
    public SurveyAnswerCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(SurveyAnswer.QUESTION_NUMBER);
    }

    // Get the order
    public int getOrder() {
        return ((Integer) m_dataCollection.get(SurveyAnswer.QUESTION_NUMBER)).intValue();
    }

    // Get the key
    public String getKey() {
        return (String) m_dataCollection.get(SurveyAnswer.KEY);
    }

    // Get the value
    public String getValue() {
        return (String) m_dataCollection.get(SurveyAnswer.VALUE);
    }

    public SurveyAnswer getSurveyAnswer() {
        return new SurveyAnswer(m_dataCollection.getDataObject());
    }

}
