/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.faq;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.TypedText;

import java.math.BigDecimal;

/**
 * Represents the question-answer pairs making up a FAQ
 *
 */


public class QAPair extends ACSObject {

    public static final String versionId = "$Id: //apps/faq/dev/src/com/arsdigita/faq/QAPair.java#4 $ by $Author: dennis $, $DateTime: 2004/08/17 23:26:27 $";

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(QAPair.class);

    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.faq.QAPair";

    private Faq m_faq = null;

    public QAPair() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public QAPair(String typeName) {
        super(typeName);
    }

    public QAPair(ObjectType type) {
        super(type);
    }

    public QAPair(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public QAPair(BigDecimal key) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    public QAPair(DataObject dataObj) {
        super(dataObj);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    public String getQuestion() {
        return (String) get("question");
    }

    public void setQuestion(String question) {
        set("question", question);
    }

    public Faq getFaq() {
        if (m_faq == null) {
            DataObject faqData = (DataObject) get("faq");
            if (faqData != null) {
                m_faq = new Faq(faqData);
            }
        }
        return m_faq;
    }

    public void setFaq(Faq faq) {
        m_faq = faq;
        setAssociation("faq", faq);
    }

    public Integer getSortKey() {
        return (Integer) get("sortKey");
    }

    public void setSortKey(Integer sortKey) {
        set("sortKey", sortKey);
    }

    public TypedText getAnswer() {
        return new TypedText((String)get("answer"),
                             (String)get("answerFormat"));
    }

    public void setAnswer(TypedText answer) {
        set("answer", answer.getText());
        set("answerFormat", answer.getType());
    }
}
