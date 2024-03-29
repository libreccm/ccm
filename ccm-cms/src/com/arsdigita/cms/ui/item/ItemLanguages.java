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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Pair;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.TooManyListenersException;

/**
 * Displays the "Language instances" pane, with all the language instances in the Bundle.
 *
 * @author Alan Pevec (apevec@redhat.com)
 * @version $Id: ItemLanguages.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemLanguages extends LayoutPanel {

    private static final Logger s_log = Logger.getLogger(ItemLanguages.class);

    private final ItemSelectionModel m_model;
    private final LanguageWidget m_language;
    private final Submit m_change, m_create;

    /**
     * Constucts a new <code>ItemLanguages</code>.
     *
     * @param model the {@link ItemSelectionModel} which will supply
     * the current item
     */
    public ItemLanguages(final ItemSelectionModel model) {
        m_model = model;

        final Section section = new Section(gz("cms.ui.item.languages"));
        setBody(section);

        final ActionGroup group = new ActionGroup();
        section.setBody(group);

        group.setSubject(new ItemLanguagesTable(m_model));

        final Form form = new Form("newLanguage", new BoxPanel(BoxPanel.HORIZONTAL));
        group.addAction(form);

        form.setRedirecting(true);
        m_language = new LanguageWidget(ContentItem.LANGUAGE) {
                protected void setupOptions() {
                    // Don't do anything.
                }
            };

        try {
            m_language.addPrintListener(new OptionPrinter());
        } catch (TooManyListenersException tmle) {
            new UncheckedWrapperException(tmle);
        }

        form.add(m_language);
        m_change = new Submit("change", gz("cms.ui.item.language.change"));
        form.add(m_change);
        m_create = new Submit("create", gz("cms.ui.item.language.add"));
        form.add(m_create);
        form.addProcessListener(new ProcessListener());
    }

    /**
     * Offers only languages not yet present in the bundle.
     */
    private class OptionPrinter implements PrintListener {
        public final void prepare(final PrintEvent e) {
            final PageState state = e.getPageState();
            final OptionGroup optionGroup = (OptionGroup) e.getTarget();
            final ContentPage item = (ContentPage) m_model.getSelectedItem
                (state);
            final Collection languages = LanguageUtil.convertToG11N
                (LanguageUtil.getCreatableLanguages(item));

            for (Iterator iter = languages.iterator(); iter.hasNext(); ) {
                final Pair pair = (Pair) iter.next();
                final String langCode = (String) pair.getKey();
                final GlobalizedMessage langName =
                    (GlobalizedMessage) pair.getValue();
                optionGroup.addOption
                    (new Option(langCode, new Label(langName)));
            }
        }
    }

    /**
     * Adds a new language instance to the bundle.
     */
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            PageState state = e.getPageState();
            String lang = (String) m_language.getValue(state);
            ContentPage item = (ContentPage) m_model.getSelectedItem(state);
            ContentBundle bundle = item.getContentBundle();
            String name = bundle.getName();

            if (m_create.isSelected(state)) {
                ContentSection section = item.getContentSection();

                Assert.exists(section, ContentSection.class);

                ContentType type = item.getContentType();

                item = (ContentPage) item.copy(lang);
                item.setLanguage(lang);
                item.setName(name);

                // Apply default workflow
                WorkflowTemplate template =
                ContentTypeWorkflowTemplate
                    .getWorkflowTemplate(section, type);
                if ( template != null ) {
                    Workflow w = template.instantiateNewWorkflow();
                    w.setObjectID(item.getID());
                    w.start(Kernel.getContext().getUser());
                    w.save();
                }

                m_model.setSelectedObject(state, item);

                // redirect to ContentItemPage.AUTHORING_TAB of the new instance
                final String target = URL.getDispatcherPath() +
                    ContentItemPage.getItemURL(item,
                                               ContentItemPage.AUTHORING_TAB);

                throw new RedirectSignal(target, true);
            } else if (m_change.isSelected(state)) {
                String oldLang = item.getLanguage();
                item.setLanguage(lang);
                // propagate language change to the Name attribute
                item.setName(name);
                item.save();

                // if the item being changed is the default, update the bundle
                // to keep this item as the default
                if (bundle.getDefaultLanguage().equals(oldLang)) {
                    bundle.setDefaultLanguage(lang);
                    bundle.save();
                }
            }
        }
    }

    protected static final GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected static final String lz(final String key) {
        return (String) gz(key).localize();
    }
}
