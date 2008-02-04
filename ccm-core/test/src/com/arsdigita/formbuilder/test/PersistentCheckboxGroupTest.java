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
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.formbuilder.PersistentCheckboxGroup;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentOption;
import org.apache.log4j.Logger;

/**
 * Test the PersistentCheckboxGroup class with the test pattern defined in
 * the PersistentComponentTestCase.
 *
 * @author Peter Marklund
 * @version $Id: PersistentCheckboxGroupTest.java 741 2005-09-02 10:21:19Z sskracic $
 *
 */
public class PersistentCheckboxGroupTest extends PersistentComponentTestCase {

    public static final String versionId = "$Id: PersistentCheckboxGroupTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Logging
    private final static Logger s_log =
        Logger.getLogger(PersistentCheckboxGroupTest.class.getName());

    // Properties of the checkBox
    private String m_checkBoxName = "checkBox_name";

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
    public PersistentCheckboxGroupTest(String name) {
        super(name);
    }

    // *** Methods inherited from PersistentComponentTestCase

    /**
     * This method returns an instance of the appropriate factory.
     */
    protected PersistentComponent createPrimaryPersistentFactory() {

        PersistentCheckboxGroup checkBox = PersistentCheckboxGroup.create(m_checkBoxName);

        // Create a couple of options
        PersistentOption option1 = PersistentOption.create(m_name1, m_label1);
        PersistentOption option2 = PersistentOption.create(m_name2, m_label2);
        PersistentOption option3 = PersistentOption.create(m_name3, m_label3);
        PersistentOption option4 = PersistentOption.create(m_name4, m_label4);

        // Test adding and removing options
        checkBox.addOption(option1, true);

        checkBox.addOption(option2, 1, true);

        checkBox.addOption(option3);

        checkBox.addOption(option4, 4);

        // Test selecting one of the options
        checkBox.setOptionSelected(option2, false);
        checkBox.setOptionSelected(option4, true);

        // Now option 1 and 4 are selected. The order is 2-1-3-4

        return checkBox;
    }

    /**
     * This method creates a new reference component and populates it with
     * test data
     */
    protected Component createPrimaryReferenceComponent() {

        CheckboxGroup checkBox = new CheckboxGroup(m_checkBoxName);

        Option option1 = new Option(m_name1, m_label1);
        Option option2 = new Option(m_name2, m_label2);
        Option option3 = new Option(m_name3, m_label3);
        Option option4 = new Option(m_name4, m_label4);


        checkBox.addOption(option2);
        checkBox.addOption(option1);
        checkBox.addOption(option3);
        checkBox.addOption(option4);

        checkBox.setOptionSelected(option1);
        checkBox.setOptionSelected(option4);

        return checkBox;
    }
}
