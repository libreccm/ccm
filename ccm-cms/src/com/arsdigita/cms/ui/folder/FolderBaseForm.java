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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import org.apache.log4j.Logger;

/**
 * Class FolderForm implements the basic form for creating or renaming
 * folders.
 *
 * @author Jon Orris &lt;jorris@redhat.com&gt;
 * @version $Id: FolderBaseForm.java 543 2005-06-03 23:03:45Z sskracic $
 */
abstract class FolderBaseForm extends CMSForm {
    public static final String versionId =
        "$Id: FolderBaseForm.java 543 2005-06-03 23:03:45Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static Logger s_log = Logger.getLogger(FolderBaseForm.class);

    public static final String NAME = ContentItem.NAME;
    public static final String TITLE = ContentPage.TITLE;

    private static final String TITLE_ON_FOCUS =
        "if (this.form." + NAME + ".value == '') {" +
        "    defaulting = true;" +
        "    this.form." + NAME + ".value = urlize(this.value);" +
        "}";

    private static final String TITLE_ON_KEY_UP =
        "if (defaulting) {" +
        "    this.form." + NAME + ".value = urlize(this.value)" +
        "}";

    private static final String FRAGMENT_ON_FOCUS = "defaulting = false";

    private static final String FRAGMENT_ON_BLUR =
        "if (this.value == '') {" +
        "    defaulting = true;" +
        "    this.value = urlize(this.form." + TITLE + ".value)" +
        "} else {" +
        "    this.value = urlize(this.value);" +
        "}";

	private Label m_script = new Label("<script language=\"javascript\" src=\"/javascript/manipulate-input.js\"></script>", false);
		

    final TextField m_title;
    final TextField m_fragment;
    final SaveCancelSection m_submits;

    public FolderBaseForm(final String name) {
        super(name);

        add(m_script, GridPanel.FULL_WIDTH);

        // Title

        add(new Label(gz("cms.ui.folder.name")));

        m_title = new TextField(new TrimmedStringParameter(TITLE));
        add(m_title);

        m_title.addValidationListener(new NotNullValidationListener());
        m_title.setOnFocus(TITLE_ON_FOCUS);

        m_title.setOnFocus(TITLE_ON_FOCUS);
        m_title.setOnKeyUp(TITLE_ON_KEY_UP);

        // Fragment

        add(new Label(gz("cms.ui.folder.fragment")));

        m_fragment = new TextField(new TrimmedStringParameter(NAME));
        add(m_fragment);

        m_fragment.addValidationListener(new NotNullValidationListener());

        m_fragment.setOnFocus(FRAGMENT_ON_FOCUS);
        m_fragment.setOnBlur(FRAGMENT_ON_BLUR);

        m_submits = new SaveCancelSection();
        add(m_submits, GridPanel.FULL_WIDTH);
    }

    public final boolean isCancelled(final PageState state) {
        return m_submits.getCancelButton().isSelected(state);
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
