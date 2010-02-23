
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

//        m_dataCollection.addOrder(ORDER);
    }

    // Get the label
    public String getLabel() {
        return (String) m_dataCollection.get(SurveyAnswer.LABEL);
    }

    // Get the widget
    public String getWidget() {
        return (String) m_dataCollection.get(SurveyAnswer.WIDGET);
    }

    // Get the value
    public String getValue() {
        return (String) m_dataCollection.get(SurveyAnswer.VALUE);
    }

    public SurveyAnswer getSurveyAnswer() {
        return new SurveyAnswer(m_dataCollection.getDataObject());
    }

}
