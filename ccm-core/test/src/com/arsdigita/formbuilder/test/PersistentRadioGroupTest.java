/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder.test;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentOption;
import com.arsdigita.formbuilder.PersistentRadioGroup;
import org.apache.log4j.Logger;

/**
 * Test the PersistentRadioGroup class with the test pattern defined in
 * the PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentRadioGroupTest.java 1940 2009-05-29 07:15:05Z terry $
 *
 */
public class PersistentRadioGroupTest extends PersistentComponentTestCase {


    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentRadioGroupTest.class.getName());

    // Properties of the radio
    private String m_radioName = "radio_name";

    // Names and labels for the options
    private String m_name1 = "option_name1";
    private String m_label1 = "Option Label 1";

    private String m_name2 = "option_name2";
    private String m_label2 = "Option Label 2";

    private String m_name3 = "option_name3";
    private String m_label3 = "Option Label 3";

    private String m_name4 = "option_name4";
    private String m_label4 = "Option Label 4";

    /**
     * JUnit needs this constructor
     */
    public PersistentRadioGroupTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentRadioGroup radio = PersistentRadioGroup.create(m_radioName);

        // Create a couple of options
        PersistentOption option1 = PersistentOption.create(m_name1, m_label1);
        PersistentOption option2 = PersistentOption.create(m_name2, m_label2);
        PersistentOption option3 = PersistentOption.create(m_name3, m_label3);
        PersistentOption option4 = PersistentOption.create(m_name4, m_label4);

        // Test adding and removing options
        radio.addOption(option1, true);

        radio.addOption(option2, 1, false);

        radio.addOption(option3);

        radio.addOption(option4, 4);

        // Test selecting one of the options
        radio.setOptionSelected(option1, false);
        radio.setOptionSelected(option2, true);

        // Now option 2 is selected. The order is 2-1-3-4

        return radio;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        RadioGroup radio = new RadioGroup(m_radioName);

        Option option1 = new Option(m_name1, m_label1);
        Option option2 = new Option(m_name2, m_label2);
        Option option3 = new Option(m_name3, m_label3);
        Option option4 = new Option(m_name4, m_label4);


        radio.addOption(option2);
        radio.addOption(option1);
        radio.addOption(option3);
        radio.addOption(option4);

        radio.setOptionSelected(option2);

        return radio;
    }
}
