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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.TaskException;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.xml.Element;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import org.apache.log4j.Logger;

/**
 * This class contains the component which displays the information for a
 * particular lifecycle, with the ability to edit and delete. This information
 * also includes the associated phases for this lifecycle, also with the ability
 * to add, edit, and delete.
 *
 * @author Michael Pih
 * @author Jack Chung
 * @author Xixi D'Moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Jens Pelzetter jens@jp-digital.de
 * @version $Id: ItemLifecycleItemPane.java 1942 2009-05-29 07:53:23Z terry $
 */
class ItemLifecycleItemPane extends BaseItemPane {

    private static final Logger s_log = Logger.getLogger(
                         ItemLifecycleItemPane.class);
    private final ContentItemRequestLocal m_item;
    private final LifecycleRequestLocal m_lifecycle;
    private final SimpleContainer m_detailPane;

    public ItemLifecycleItemPane(final ContentItemRequestLocal item,
                                 final LifecycleRequestLocal cycle) {
        m_item = item;
        m_lifecycle = cycle;

        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        m_detailPane.add(new SummarySection());
        final Label lastPublishedLabel = new Label();
        lastPublishedLabel.addPrintListener(new PrintListener() {
            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final ContentItem item = m_item.getContentItem(state).
                    getLiveVersion();
                final Label label = (Label) event.getTarget();
                final String dateStr;
                if ((item == null) || item.getLastModifiedDate() == null) {
                    dateStr = "";
                } else {
                    dateStr = DateFormat.getDateTimeInstance(DateFormat.LONG,
                                                             DateFormat.SHORT,
                                                             GlobalizationHelper.
                        getNegotiatedLocale()).format(item.
                            getLastModifiedDate());
                }
                final String msg = String.format(
                    "%s %s",
                    new GlobalizedMessage(
                    "cms.ui.lifecycle.details.last_published",
                    "com.arsdigita.cms.CMSResources").localize(),
                    dateStr);
                label.setLabel(msg);
            }

        });
        m_detailPane.add(lastPublishedLabel);

        m_detailPane.add(
            new PhaseSection());
    }

    private class SummarySection extends Section {

        public SummarySection() {
            setHeading(new Label(gz("cms.ui.lifecycle.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            if (CMS.getConfig().getUseOldStyleItemLifecycleItemPane()) {
                group.setSubject(new Properties());
                group.addAction(new UnpublishLink());
                group.addAction(new RepublishLink());
                if (!ContentSection.getConfig().hideResetLifecycleLink()) {
                    group.addAction(new RepublishAndResetLink());
                }
            } else {
                group.addAction(new ActionForm());
            }

        }

        private class Properties extends PropertyList {

            @Override
            protected final java.util.List properties(final PageState state) {
                final java.util.List props = super.properties(state);
                final Lifecycle cycle = m_lifecycle.getLifecycle(state);

                final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                                                         ContentSection.getConfig().
                    getHideTimezone()
                                                                         ? DateFormat.SHORT
                                                                         : DateFormat.FULL);

                props.add(new Property(gz("cms.ui.name"),
                                       cycle.getLabel()));
                props.add(new Property(gz("cms.ui.item.lifecycle.start_date"),
                                       format.format(cycle.getStartDate())));

                final java.util.Date endDate = cycle.getEndDate();

                if (endDate == null) {
                    props.add(new Property(gz("cms.ui.item.lifecycle.end_date"),
                                           lz("cms.ui.none")));
                } else {
                    props.add(new Property(gz("cms.ui.item.lifecycle.end_date"),
                                           format.format(endDate)));
                }

                return props;
            }

        }
    }

    private class PublishLink extends ActionLink {

        private RequestLocal m_canPublish = new RequestLocal();

        PublishLink(Component c) {
            super(c);
        }

        @Override
        public void generateXML(PageState ps, Element parent) {
            Boolean canPublish = (Boolean) m_canPublish.get(ps);
            if (null == canPublish) {
                SecurityManager sm = Utilities.getSecurityManager(ps);
                ContentItem item = m_item.getContentItem(ps);
                if (sm.canAccess(ps.getRequest(),
                                 SecurityManager.PUBLISH,
                                 item)) {
                    canPublish = Boolean.TRUE;
                } else {
                    canPublish = Boolean.FALSE;
                }

                m_canPublish.set(ps, canPublish);
            }

            if (canPublish.booleanValue()) {
                if (s_log.isDebugEnabled()) {
                    ContentItem item = m_item.getContentItem(ps);
                    s_log.debug("User can publish " + item.getOID());
                }

                super.generateXML(ps, parent);
            } else if (s_log.isDebugEnabled()) {
                ContentItem item = m_item.getContentItem(ps);
                s_log.debug("User cannot publish " + item.getOID());
            }
        }

    }

    private class UnpublishLink extends PublishLink {

        UnpublishLink() {
            super(new Label(gz("cms.ui.item.lifecycle.unpublish")));

            addActionListener(new Listener());
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();
                final ContentItem item = m_item.getContentItem(state);

                item.unpublish();

                final String target = URL.getDispatcherPath()
                                      + ContentItemPage.getItemURL(item,
                                                                   ContentItemPage.AUTHORING_TAB);

                throw new RedirectSignal(target, true);
            }

        }
    }

    private static void republish(ContentItem item, boolean reset, User user) {
        item.republish(reset);
        Workflow workflow = Workflow.getObjectWorkflow(item);
        try {
            ItemLifecycleSelectForm.finish(workflow, item, user);
        } catch (TaskException te) {
            throw new UncheckedWrapperException(te);
        }
    }

    private class RepublishLink extends PublishLink {

        RepublishLink() {
            super(new Label(gz("cms.ui.item.lifecycle.republish")));

            addActionListener(new Listener());
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();
                final ContentItem item = m_item.getContentItem(state);
                final User user = Web.getWebContext().getUser();

                /*
                 * jensp 2011-12-14: Check is threaded publishing is active. If yes, execute publishing in a thread.
                 */
                if (CMSConfig.getInstanceOf().getThreadedPublishing()) {
                    final Republisher republisher = new Republisher(item, user);
                    final Thread thread = new Thread(republisher);
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);
                            
                            PublishLock.getInstance().setError(item, strWriter.toString());
                            s_log.error(String.format(
                                "An error occurred while "
                                + "publishing the item '%s': ",
                                item.getOID().toString()),
                                        ex);

                            if ((CMSConfig.getInstanceOf().
                                 getPublicationFailureSender()
                                 == null)
                                && (CMSConfig.getInstanceOf().
                                    getPublicationFailureReceiver() == null)) {
                                return;
                            }

                            final PartyCollection receiverParties = Party.retrieveAllParties();
                            Party receiver = null;
                            receiverParties.addEqualsFilter("primaryEmail",
                                                            CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (receiverParties.next()) {
                                receiver = receiverParties.getParty();
                            }
                            receiverParties.close();

                            final PartyCollection senderParties = Party.retrieveAllParties();
                            Party sender = null;
                            senderParties.addEqualsFilter("primaryEmail",
                                                          CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (senderParties.next()) {
                                sender = senderParties.getParty();
                            }
                            senderParties.close();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Notification notification = new Notification(
                                    sender,
                                    receiver,
                                    String.format(
                                    "Failed to publish item '%s'",
                                    item.getOID().toString()),
                                    String.format("Publishing item '%s' failed "
                                                  + "with error message: %s.\n\n"
                                                  + "Stacktrace:\n%s",
                                                  item.getOID().toString(),
                                                  ex.getMessage(),
                                                  traceWriter.toString()));
                                notification.save();
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(item,
                                                     ContentItemPage.PUBLISHING_TAB),
                        true);
                    /*
                     * jensp 2011-12-14 end
                     */
                } else {
                    republish(item, false, user);
                    if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                        throw new RedirectSignal(
                            URL.there(state.getRequest(),
                                      ContentCenter.getURL()), true);
                    }
                }
            }

        }

        /**
         * @author Jens Pelzetter
         */
        private class Republisher implements Runnable {

            /**
             * Saves OID of item as a string. This is necessary because it is not possible to access
             * to same data object instance from multiple threads. So we have to create a new
             * instance a the data object in the run method. To avoid any sort a problems, we store
             * the OID as a string and convert it back to an OID in the run method.
             */
            private final String itemOid;
            private final User user;

            private Republisher(final ContentItem item, User user) {
                itemOid = item.getOID().toString();
                this.user = user;
            }

            @Override
            public void run() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                PublishLock.getInstance().lock(item);
                republish(item, false, user);
                PublishLock.getInstance().unlock(item);
            }

        }
    }

    private class RepublishAndResetLink extends PublishLink {

        RepublishAndResetLink() {
            super(new Label(gz("cms.ui.item.lifecycle.republish_and_reset")));

            addActionListener(new Listener());
			// warning gets a bit annoying, and link should be descriptive
            // enough that it is not required
            // setConfirmation("This will reset all your publication dates, are
            // you sure you want to continue?");
        }

        private class Listener implements ActionListener {

            @Override
            public final void actionPerformed(final ActionEvent e) {
                final PageState state = e.getPageState();
                final ContentItem item = m_item.getContentItem(state);
                final User user = Web.getWebContext().getUser();

                /**
                 * jensp 2011-12-14: Execute is a thread if threaded publishing is active.
                 */
                if (CMSConfig.getInstanceOf().getThreadedPublishing()) {
                    final Republisher republisher = new Republisher(item, user);
                    final Thread thread = new Thread(republisher);
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);
                            
                            PublishLock.getInstance().setError(item, strWriter.toString());
                            s_log.error(String.format(
                                "An error occurred while "
                                + "publishing the item '%s': ",
                                item.getOID().toString()),
                                        ex);

                            if ((CMSConfig.getInstanceOf().
                                 getPublicationFailureSender()
                                 == null)
                                && (CMSConfig.getInstanceOf().
                                    getPublicationFailureReceiver() == null)) {
                                return;
                            }

                            final PartyCollection receiverParties = Party.retrieveAllParties();
                            Party receiver = null;
                            receiverParties.addEqualsFilter("primaryEmail",
                                                            CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (receiverParties.next()) {
                                receiver = receiverParties.getParty();
                            }
                            receiverParties.close();

                            final PartyCollection senderParties = Party.retrieveAllParties();
                            Party sender = null;
                            senderParties.addEqualsFilter("primaryEmail",
                                                          CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (senderParties.next()) {
                                sender = senderParties.getParty();
                            }
                            senderParties.close();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Notification notification = new Notification(
                                    sender,
                                    receiver,
                                    String.format(
                                    "Failed to publish item '%s'",
                                    item.getOID().toString()),
                                    String.format("Publishing item '%s' failed "
                                                  + "with error message: %s.\n\n"
                                                  + "Stacktrace:\n%s",
                                                  item.getOID().toString(),
                                                  ex.getMessage(),
                                                  traceWriter.toString()));
                                notification.save();
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(item,
                                                     ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    /**
                     * jensp 2011-12-14 end
                     */
                    republish(item, true, user);
                    if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                        throw new RedirectSignal(
                            URL.there(state.getRequest(),
                                      ContentCenter.getURL()), true);
                    }
                }
            }

        }

        /**
         * @author Jens Pelzetter
         */
        private class Republisher implements Runnable {

            /**
             * Saves OID of item as a string. This is necessary because it is not possible to access
             * to same data object instance from multiple threads. So we have to create a new
             * instance a the data object in the run method. To avoid any sort a problems, we store
             * the OID as a string and convert it back to an OID in the run method.
             */
            private final String itemOid;
            private final User user;

            private Republisher(final ContentItem item, User user) {
                itemOid = item.getOID().toString();
                this.user = user;
            }

            @Override
            public void run() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                PublishLock.getInstance().lock(item);
                republish(item, true, user);
                PublishLock.getInstance().unlock(item);
            }

        }
    }

    private class PhaseSection extends Section {

        PhaseSection() {
            super(gz("cms.ui.lifecycle.phases"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new PhaseTable());
        }

    }

    private class PhaseTable extends Table {

        PhaseTable() {
            super(new ItemPhaseTableModelBuilder(m_lifecycle),
                  new String[]{
                      lz("cms.ui.name"),
                      lz("cms.ui.description"),
                      lz("cms.ui.item.lifecycle.start_date"),
                      lz("cms.ui.item.lifecycle.end_date")
                  });
        }

    }

    /**
     * New style pane. Uses a select box for the action to avoid wrong clicks on unpublish.
     *
     * @author Jens Pelzetter
     */
    private class ActionForm
        extends Form
        implements FormProcessListener,
                   FormInitListener {

        private static final String LIFECYCLE_ACTION = "itemLifecycleItemPaneActionSelect";
        private static final String REPUBLISH = "republish";
        private static final String UNPUBLISH = "unpublish";
        private static final String REPUBLISH_AND_RESET = "republishAndReset";
        private final Submit submit;
        private final Label notAuthorized;

        public ActionForm() {
            super("itemLifecycleItemPaneActionForm");

            final BoxPanel actionPanel = new BoxPanel(BoxPanel.HORIZONTAL);
            final SingleSelect actionSelect = new SingleSelect(
                LIFECYCLE_ACTION);

            actionSelect.addOption(new Option(REPUBLISH, (String) gz(
                "cms.ui.item.lifecycle.republish").localize()));
            if (!ContentSection.getConfig().hideResetLifecycleLink()) {
                actionSelect.addOption(new Option(REPUBLISH_AND_RESET,
                                                  (String) gz(
                    "cms.ui.item.lifecycle.republish_and_reset").
                    localize()));
            }
            actionSelect.addOption(new Option(UNPUBLISH, (String) gz(
                "cms.ui.item.lifecycle.unpublish").localize()));

            submit = new Submit(gz("cms.ui.item.lifecycle.do"));
            notAuthorized = new Label(gz(
                "cms.ui.item.lifecycle.do.not_authorized"));

            actionPanel.add(actionSelect);
            actionPanel.add(submit);
            actionPanel.add(notAuthorized);
            add(actionPanel);

            addInitListener(this);
            addProcessListener(this);
        }

        @Override
        public void init(FormSectionEvent fse) throws FormProcessException {
            final PageState state = fse.getPageState();
            final ContentItem item = m_item.getContentItem(state);

            final SecurityManager sm = Utilities.getSecurityManager(state);

            if (sm.canAccess(state.getRequest(),
                             SecurityManager.PUBLISH,
                             item)) {
                submit.setVisible(state, true);
                notAuthorized.setVisible(state, false);
            } else {
                submit.setVisible(state, false);
                notAuthorized.setVisible(state, true);
            }
        }

        @Override
        public void process(final FormSectionEvent fse) throws
            FormProcessException {
            final PageState state = fse.getPageState();
            final FormData data = fse.getFormData();
            final User user = Web.getWebContext().getUser();

            String selected = (String) data.get(LIFECYCLE_ACTION);
            final ContentItem item = m_item.getContentItem(state);

            /**
             * Republish/Republish and Reset are executed in the thread if threaded publishing is
             * active.
             */
            if (REPUBLISH.equals(selected)) {
                if (CMSConfig.getInstanceOf().getThreadedPublishing()) {
                    final RepublishRunner runner = new RepublishRunner(item, user);
                    final Thread thread = new Thread(runner);
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);
                            
                            PublishLock.getInstance().setError(item, strWriter.toString());
                            s_log.error(String.format(
                                "An error occurred while "
                                + "publishing the item '%s': ",
                                item.getOID().toString()),
                                        ex);

                            if ((CMSConfig.getInstanceOf().
                                 getPublicationFailureSender()
                                 == null)
                                && (CMSConfig.getInstanceOf().
                                    getPublicationFailureReceiver() == null)) {
                                return;
                            }

                            final PartyCollection receiverParties = Party.retrieveAllParties();
                            Party receiver = null;
                            receiverParties.addEqualsFilter("primaryEmail",
                                                            CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (receiverParties.next()) {
                                receiver = receiverParties.getParty();
                            }
                            receiverParties.close();

                            final PartyCollection senderParties = Party.retrieveAllParties();
                            Party sender = null;
                            senderParties.addEqualsFilter("primaryEmail",
                                                          CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (senderParties.next()) {
                                sender = senderParties.getParty();
                            }
                            senderParties.close();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Notification notification = new Notification(
                                    sender,
                                    receiver,
                                    String.format(
                                    "Failed to publish item '%s'",
                                    item.getOID().toString()),
                                    String.format("Publishing item '%s' failed "
                                                  + "with error message: %s.\n\n"
                                                  + "Stacktrace:\n%s",
                                                  item.getOID().toString(),
                                                  ex.getMessage(),
                                                  traceWriter.toString()));
                                notification.save();
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(item,
                                                     ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    republish(item, false, user);

                    if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                        throw new RedirectSignal(
                            URL.there(state.getRequest(),
                                      ContentCenter.getURL()), true);
                    }
                }
            } else if (REPUBLISH_AND_RESET.equals(selected)) {
                if (CMSConfig.getInstanceOf().getThreadedPublishing()) {
                    final RepublishAndResetRunner runner = new RepublishAndResetRunner(item, user);
                    final Thread thread = new Thread(runner);
                    thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(final Thread thread,
                                                      final Throwable ex) {
                            final StringWriter strWriter = new StringWriter();
                            final PrintWriter writer = new PrintWriter(strWriter);
                            ex.printStackTrace(writer);
                            
                            PublishLock.getInstance().setError(item, strWriter.toString());
                            s_log.error(String.format(
                                "An error occurred while "
                                + "publishing the item '%s': ",
                                item.getOID().toString()),
                                        ex);

                            if ((CMSConfig.getInstanceOf().
                                 getPublicationFailureSender()
                                 == null)
                                && (CMSConfig.getInstanceOf().
                                    getPublicationFailureReceiver() == null)) {
                                return;
                            }

                            final PartyCollection receiverParties = Party.retrieveAllParties();
                            Party receiver = null;
                            receiverParties.addEqualsFilter("primaryEmail",
                                                            CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (receiverParties.next()) {
                                receiver = receiverParties.getParty();
                            }
                            receiverParties.close();

                            final PartyCollection senderParties = Party.retrieveAllParties();
                            Party sender = null;
                            senderParties.addEqualsFilter("primaryEmail",
                                                          CMSConfig.getInstanceOf().
                                getPublicationFailureReceiver());
                            if (senderParties.next()) {
                                sender = senderParties.getParty();
                            }
                            senderParties.close();

                            if ((sender != null) && (receiver != null)) {
                                final Writer traceWriter = new StringWriter();
                                final PrintWriter printWriter = new PrintWriter(
                                    traceWriter);
                                ex.printStackTrace(printWriter);

                                final Notification notification = new Notification(
                                    sender,
                                    receiver,
                                    String.format(
                                    "Failed to publish item '%s'",
                                    item.getOID().toString()),
                                    String.format("Publishing item '%s' failed "
                                                  + "with error message: %s.\n\n"
                                                  + "Stacktrace:\n%s",
                                                  item.getOID().toString(),
                                                  ex.getMessage(),
                                                  traceWriter.toString()));
                                notification.save();
                            }
                        }

                    });

                    thread.start();

                    throw new RedirectSignal(
                        URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(item,
                                                     ContentItemPage.PUBLISHING_TAB),
                        true);
                } else {
                    republish(item, true, user);

                    if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                        throw new RedirectSignal(
                            URL.there(state.getRequest(),
                                      ContentCenter.getURL()), true);
                    }
                }
            } else if (UNPUBLISH.equals(selected)) {
                item.unpublish();
            } else {
                throw new IllegalArgumentException("Illegal selection");
            }
        }

        private class RepublishRunner implements Runnable {

            /**
             * Saves OID of item as a string. This is necessary because it is not possible to access
             * to same data object instance from multiple threads. So we have to create a new
             * instance a the data object in the run method. To avoid any sort a problems, we store
             * the OID as a string and convert it back to an OID in the run method.
             */
            private final String itemOid;
            private final User user;

            private RepublishRunner(final ContentItem item, User user) {
                itemOid = item.getOID().toString();
                this.user = user;
            }

            private void doRepublish() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                republish(item, false, user);
            }

            @Override
            public void run() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                PublishLock.getInstance().lock(item);
                doRepublish();
                PublishLock.getInstance().unlock(item);
            }

        }

        private class RepublishAndResetRunner implements Runnable {

            /**
             * Saves OID of item as a string. This is necessary because it is not possible to access
             * to same data object instance from multiple threads. So we have to create a new
             * instance a the data object in the run method. To avoid any sort a problems, we store
             * the OID as a string and convert it back to an OID in the run method.
             */
            private final String itemOid;
            private final User user;

            private RepublishAndResetRunner(final ContentItem item, User user) {
                itemOid = item.getOID().toString();
                this.user = user;
            }

            private void doRepublishAndReset() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                republish(item, true, user);
            }

            @Override
            public void run() {
                final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(OID.valueOf(
                    itemOid));
                PublishLock.getInstance().lock(item);
                doRepublishAndReset();
                PublishLock.getInstance().unlock(item);
            }

        }
    }
}
