package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.xml.Element;

/**
 * An example implementation of the {@link ContentGenerator} interface.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ExampleGenerator implements ContentGenerator {

    public void generateContent(final Element parent,
                                final GenericPerson person,
                                final PageState state) {
        Element message = parent.newChildElement("message");

        message.setText("Hello World!");
    }
}
