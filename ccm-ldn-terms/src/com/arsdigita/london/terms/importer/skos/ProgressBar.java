/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package com.arsdigita.london.terms.importer.skos;

import org.apache.log4j.Logger;

import com.arsdigita.util.Assert;

/**
 * A simple progress bar that outputs 0%, 5%, 10%, using a specified logger.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
class ProgressBar {
    public ProgressBar(int length, Logger logger) {
        Assert.isTrue(length > 0);
        Assert.isTrue(logger != null);

        m_length = length;
        m_logger = logger;
        m_position = 0;
        m_percentage = -1;
    }

    public void next() {
        m_position++;

        int percentage = (m_position * 100) / m_length;

        if (percentage != m_percentage && percentage % 5 == 0) {
            m_percentage = percentage;
            m_logger.info("    " + m_percentage + "% (" + m_position + ")");
        }
    }

    public void reset() {
        m_position = 0;
        m_percentage = -1;
    }

    private int m_position;

    private int m_percentage;

    private final int m_length;

    private final Logger m_logger;
}
