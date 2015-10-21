/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.shortcuts.ui;

import org.apache.log4j.Category;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

public class AdminPanel extends SimpleContainer {

    private ACSObjectSelectionModel m_shortcut = new ACSObjectSelectionModel(
        new BigDecimalParameter("shortcutID"));

    private static final Category log = Category.getInstance(AdminPanel.class
        .getName());

    public AdminPanel() {
        add(new ShortcutForm(m_shortcut));
        add(new ShortcutsTable(m_shortcut));
    }

    @Override
    public void register(Page p) {
        super.register(p);

        p.addGlobalStateParam(m_shortcut.getStateParameter());
    }

}
