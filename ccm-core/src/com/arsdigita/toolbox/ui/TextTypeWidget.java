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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.MessageType;

/**
 * Provides a widget for selecting between HTML, plain text, and
 * preformatted text.
 *
 * $Id: TextTypeWidget.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class TextTypeWidget extends SingleSelect {

    private Option m_html  = new Option(MessageType.TEXT_HTML,
                                        "HTML");
    private Option m_plain = new Option(MessageType.TEXT_PLAIN,
                                        "Plain");
    private Option m_pre   = new Option(MessageType.TEXT_PREFORMATTED,
                                        "Preformatted Text");
    private Option m_smart = new Option(MessageType.TEXT_SMART,
                                        "Smart Text");

    /**
     * Constructor.
     */

    public TextTypeWidget(ParameterModel textTypeParameter)
    {
        super(textTypeParameter);

        // Add options
        addOption(m_html);
        addOption(m_plain);
        addOption(m_pre);
        addOption(m_smart);
    }

    /**
     * Constructor with one additional parameter to set the correct
     * default value, which should be one of the constants defined by
     * the {@link MessageType} interface. Example:
     *
     * <pre>
     * TextTextWidget ttw = new TextTypeWidget(model, MessageType.TEXT_PLAIN);
     * </pre>
     */

    public TextTypeWidget(ParameterModel textTypeParameter,
                          String defaultType)
    {
        this(textTypeParameter);

        // Set default value
        setOptionSelected(defaultType);
    }
}
