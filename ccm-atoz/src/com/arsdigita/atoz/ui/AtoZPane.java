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
 */

package com.arsdigita.atoz.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.dispatcher.SiteProxyPanel;

import com.arsdigita.kernel.Kernel;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZEntry;
import com.arsdigita.atoz.AtoZAtomicEntry;
import com.arsdigita.atoz.AtoZCompoundEntry;
import com.arsdigita.atoz.AtoZGenerator;

import com.arsdigita.xml.Element;

public class AtoZPane extends SimpleContainer {

    private static final String XMLNS = "http://xmlns.redhat.com/atoz/1.0";

    private StringParameter m_letter;

    public AtoZPane(StringParameter letter) {
        m_letter = letter;
    }

    public void generateXML(PageState state, Element parent) {
        AtoZ atoz = (AtoZ) Kernel.getContext().getResource();
        AtoZGenerator[] generators = atoz.getGenerators();
        String currentLetter = (String) state.getValue(m_letter);
        if (currentLetter != null) {
            currentLetter = currentLetter.toLowerCase();
        }

        Element content = AtoZ.newElement("atoz");

        for (int i = 0; i < 26; i++) {
            String letter = new String(new char[] { (char) ((int) 'a' + i) });

            Element el = AtoZ.newElement("letter");
            el.setText(letter);
            if (letter.equals(currentLetter)) {
                el.addAttribute("isSelected", "yes");
            }
            content.addContent(el);
        }
        parent.addContent(content);

        if (currentLetter == null) {
            return;
        }

        for (int i = 0; i < generators.length; i++) {
            generateAtoZ(generators[i], currentLetter, content);
        }
    }

    private void generateAtoZ(AtoZGenerator generator, String letter,
            Element parent) {
        AtoZEntry[] entries = generator.getEntries(letter);
        Element content = AtoZ.newElement("provider");
        content.addAttribute("title", generator.getTitle());
        content.addAttribute("description", generator.getDescription());

        generateAtoZEntries(entries, content);

        parent.addContent(content);
    }

    public void generateAtoZEntries(AtoZEntry[] entries, Element parent) {
        for (int i = 0; i < entries.length; i++) {
            AtoZEntry entry = entries[i];
            if (entry instanceof AtoZAtomicEntry) {
                generateAtoZAtomicEntry((AtoZAtomicEntry) entry, parent);
            } else if (entry instanceof AtoZCompoundEntry) {
                generateAtoZCompoundEntry((AtoZCompoundEntry) entry, parent);
            } else {
                throw new RuntimeException("Unknown A-Z entry type "
                        + entry.getClass());
            }
        }
    }

    private void generateAtoZAtomicEntry(AtoZAtomicEntry entry, Element parent) {
        Element content = AtoZ.newElement("atomicEntry");
        content.addAttribute("title", entry.getTitle());
        content.addAttribute("description", entry.getDescription());
        content.addAttribute("url", entry.getLink());
        if (entry.getContent() != null)
            content.addContent(entry.getContent());
        parent.addContent(content);
    }

    private void generateAtoZCompoundEntry(AtoZCompoundEntry entry,
            Element parent) {
        Element content = AtoZ.newElement("compoundEntry");
        content.addAttribute("title", entry.getTitle());
        content.addAttribute("description", entry.getDescription());
        parent.addContent(content);

        generateAtoZEntries(entry.getEntries(), content);
    }
}
