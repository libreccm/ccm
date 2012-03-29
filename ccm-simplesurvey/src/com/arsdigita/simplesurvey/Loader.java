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
package com.arsdigita.simplesurvey;

import com.arsdigita.formbuilder.util.FormbuilderSetup;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Initial load (non-recurring) at install time for ccm-simplesurvey. Creates
 * application type in database.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 759 2005-09-02 15:25:32Z sskracic $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    /** 
     * 
     * @param ctx
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                setupSimpleSurveyPackage();

            }
        }.run();
    }

    /**
     * Helper method which does the actual work of preparing an application
     * type and stores it in the database.
     */
    private void setupSimpleSurveyPackage() {

        /** List of widgets used in survey application forms. Each widget is
            described by application indicator, widget name (singular & plural),
            model class name and model ui class name.
            These are really not user or administrator configurabel and
            therefore not implemented as ccm parameter.                       */
        List widgetTypes = Arrays.asList(
            Arrays.asList(
                   "Survey", "One line Answer", "One line Answers",
                   "com.arsdigita.formbuilder.PersistentTextField",
                   "com.arsdigita.simplesurvey.ui.widgets.OneLineWidgetForm" ),
            Arrays.asList(
                   "Survey", "Essay Answer", "Essay Answers",
                   "com.arsdigita.formbuilder.PersistentTextArea",
                   "com.arsdigita.simplesurvey.ui.widgets.EssayWidgetForm" ),
            Arrays.asList(
                   "Survey", "Date Answer", "Date Answers",
                   "com.arsdigita.formbuilder.PersistentDate",
                   "com.arsdigita.simplesurvey.ui.widgets.DateWidgetForm" ),
            Arrays.asList(
                   "Survey","Multiple Choice (one or more answers)","Multiple Choices",
                   "com.arsdigita.formbuilder.PersistentCheckboxGroup",
                   "com.arsdigita.simplesurvey.ui.widgets.CheckboxEditor" ),
            Arrays.asList(
                   "Survey", "Multiple Choice (only one answer)","Single Choices",
                   "com.arsdigita.formbuilder.PersistentRadioGroup",
                   "com.arsdigita.simplesurvey.ui.widgets.RadioEditor" ),
            Arrays.asList(
                   "Poll", "Multiple Choice (only one answer)","Single Choices",
                   "com.arsdigita.formbuilder.PersistentRadioGroup",
                   "com.arsdigita.simplesurvey.ui.widgets.RadioEditor" )
        );

        /* Create new type legacy free application type                 
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Content Center" will become "content-center".                   */
        ApplicationType type = new 
                               ApplicationType("Simple Survey",
                                               SimpleSurvey.BASE_DATA_OBJECT_TYPE );
        type.setDescription("An application to conduct a simple survey.");
        type.save();
        
        if (!Application.isInstalled(SimpleSurvey.BASE_DATA_OBJECT_TYPE,
                                     "/simplesurvey/")) {
            Application app = Application.createApplication(type,
                                                            "simplesurvey",
                                                            "Simplesurvey",
                                                            null);
            app.setDescription("The default Survey application instance.");
            app.save();
        }

        // Load the widgets types (i.e. description and class names to be 
        // instantiated at runtime) used in survey forms into database.
        FormbuilderSetup fbs = new FormbuilderSetup();
        fbs.setup(widgetTypes, null, null);

    }
}
