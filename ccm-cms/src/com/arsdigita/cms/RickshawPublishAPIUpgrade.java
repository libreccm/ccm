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
package com.arsdigita.cms;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.Phase;
import com.arsdigita.cms.lifecycle.PhaseCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.Startup;
import com.redhat.persistence.engine.rdbms.RDBMSException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * <p>Upgrade initializer for Rickshaw publishing API changes.</p>
 *
 * <p>Upgrade process is as follows</p>
 * <ol>
 *   <li> For each pending item <code>pending</code> where
 *        masterVersion is not null and parent is a Folder or
 *        ContentBundle (and is itself _not_ a Folder or
 *        ContentBundle):
 *     <ul>
 *       <li> Store Lifecycle reference in temporary
 *            UpgradeItemLifecycleMap (except for ReusableImageAsset,
 *            which will have no lifecycle -- store that in
 *            UpgradeItemNoLifecycleSet). </li>
 *       <li> pending.getDraftVersion().removePendingVersion(pending)</li>
 *     </ul>
 *   <li> For each live item <code>live</code> where
 *        masterVersion is not null and parent is a Folder or
 *        ContentBundle (and is itself _not_ a Folder or
 *        ContentBundle):
 *     <ul>
 *       <li> Store Lifecycle reference in temporary
 *            UpgradeItemLifecycleMap (except for ReusableImageAsset,
 *            which will have no lifecycle -- store that in
 *            UpgradeItemNoLifecycleSet). </li>
 *       <li> live.getDraftVersion().unpublish()</li>
 *     </ul>
 *   <li> unpublish any remaining pending/live items (random folders,
 *        etc. which may have been missed. Don't republish these.
 *
 *   <li> For each draft item <code>item</code> in
 *        UpgradeItemNoLifecycleSet, call:  item.createLiveVersion()</li>
 *   <li> For each draft item <code>item</code>, <code>lifecycle</code> in
 *        UpgradeItemLifecycleMap, call:
 *        <code><pre>
 *          item.publish(lifecycle.getLifecycleDefinition(),
 *                       lifecycle.getStartDate());
 *          // now set stop date and individual phase start/stop dates
 *          // based on the LifecycleDefinition
 *        </pre></code>
 *   </li>
 *   <li> Clear UpgradeItemLifecycleMap and
 *        UpgradeItemNoLifecycleSet</li>
 *   <li> Remove orphaned lifecycles (those with no associated LifecycleService)</li>
 * </ol>
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 */
public class RickshawPublishAPIUpgrade extends com.arsdigita.kernel.BaseInitializer {

    private static Logger s_log = Logger.getLogger(RickshawPublishAPIUpgrade.class);

    private Configuration m_conf = new Configuration();
    private TransactionContext m_txn = null;
    private int m_txnCounter = 0;
    private int m_itemsPerTransaction;

    private static final String ITEMS_PER_TRANSACTION = "itemsPerTransaction";


    public static final String PACKAGE_NAME = "com.arsdigita.cms";
    public static final String UPGRADE_ITEM_NO_LIFECYCLE_SET_TYPE =
        PACKAGE_NAME + ".UpgradeItemNoLifecycleSet";
    public static final String UPGRADE_ITEM_LIFECYCLE_MAP_TYPE =
        PACKAGE_NAME + ".UpgradeItemLifecycleMap";
    public static final String UPGRADE_ITEM = "item";
    public static final String UPGRADE_LIFECYCLE = "lifecycle";


    public static final String UPGRADE_PROGRESS_TYPE =
        PACKAGE_NAME + ".UpgradeProgress";
    public static final String UPGRADE_PROGRESS_ID = "id";
    public static final BigDecimal UPGRADE_PROGRESS_ID_VALUE = new BigDecimal(1);
    public static final String PENDING_UNPUBLISH_DONE = "pendingUnpublishDone";
    public static final String LIVE_UNPUBLISH_DONE = "liveUnpublishDone";
    public static final String CLEANUP_UNPUBLISH_DONE = "cleanupUnpublishDone";
    public static final String NON_LIFECYCLE_PUBLISH_DONE = "nonLifecyclePublishDone";
    public static final String LIFECYCLE_PUBLISH_DONE = "lifecyclePublishDone";
    public static final String REMOVE_ORPHANED_LIFECYCLES_DONE = "removeOrphanedLifecyclesDone";

    public RickshawPublishAPIUpgrade() throws InitializationException {
        m_conf.initParameter(
            ITEMS_PER_TRANSACTION,
            "Number of items to process per transaction. ",
            Integer.class, new Integer(25));

    }
    public Configuration getConfiguration() {
        return m_conf;
    }

    protected void doStartup() {
        //disable p2fs and lifecycle during upgrade
        s_log.info("disabling PublishToFilesystem and Lifecycle during publishing API content upgrade...");
        com.arsdigita.cms.publishToFile.QueueManager.stopWatchingQueue();
        com.arsdigita.cms.lifecycle.Scheduler.stopTimer();

        s_log.info("Starting publishing API content upgrade");

        m_itemsPerTransaction = ((Integer) m_conf.getParameter(ITEMS_PER_TRANSACTION)).intValue();

        m_txn = SessionManager.getSession().getTransactionContext();

        m_txn.beginTxn();

        if (!isUnpublishPendingItemsCompleted()) {
            s_log.info("Unpublishing pending items");
            unpublishPendingItems();
        } else {
            s_log.info("Pending items already unpublished");
        }
        if (!isUnpublishLiveItemsCompleted()) {
            s_log.info("Unpublishing live items");
            unpublishLiveItems();
        } else {
            s_log.info("Live items already unpublished");
        }
        if (!isCleanupPublishedItemsCompleted()) {
            s_log.info("Cleanup of any remaining live/pending items");
            cleanupPublishedItems();
        } else {
            s_log.info("Cleanup of any remaining live/pending items already completed");
        }
        if (!isPublishNonLifecycleItemsCompleted()) {
            s_log.info("Publishing non-lifecycle items");
            publishNonLifecycleItems();
        } else {
            s_log.info("non-lifecycle items already published");
        }
        if (!isPublishLifecycleItemsCompleted()) {
            s_log.info("Publishing lifecycle items");
            publishLifecycleItems();
        } else {
            s_log.info("lifecycle items already published");
        }
        if (!isRemoveOrphanedLifecyclesCompleted()) {
            s_log.info("Removing orphaned lifecycles");
            removeOrphanedLifecycles();
        } else {
            s_log.info("orphaned lifecycles already removed");
        }
        m_txn.commitTxn();
        s_log.info("Finishing publishing API content upgrade");
    }

    protected void doShutdown() {
        /* Empty */
    }

    public static final void main(final String[] args) {
        System.out.println("Starting publishing API content upgrade");

        new Startup().run();

        final RickshawPublishAPIUpgrade upgrade =
            new RickshawPublishAPIUpgrade();

        upgrade.doStartup();

        System.out.println("Publishing API content upgrade complete");
    }

    /**
     *  For each pending item <code>pending</code> where
     *  masterVersion is not null and parent is a Folder or
     *  ContentBundle (and is itself _not_ a Folder or
     *  ContentBundle):
     *  <ul>
     *    <li> Store Lifecycle reference in temporary
     *         UpgradeItemLifecycleMap (except for ReusableImageAsset,
     *         which will have no lifecycle -- store that in
     *         UpgradeItemNoLifecycleSet). </li>
     *    <li> pending.getDraftVersion().removePendingVersion(pending)</li>
     *  </ul>
     */
    private void unpublishPendingItems() {
        Session session = SessionManager.getSession();
        for (int pass = 0; pass < 2; pass++) {
            DataCollection items = session.retrieve(ContentItem.BASE_DATA_OBJECT_TYPE);
            items.addEqualsFilter(ContentItem.VERSION, ContentItem.PENDING);
            items.addNotEqualsFilter(ContentItem.DRAFT_VERSION, null);
            items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE);
            items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE);
            CompoundFilter orFilter = items.getFilterFactory().or();
            orFilter.addFilter(items.getFilterFactory().equals
                               (ContentItem.PARENT + "." + ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE));
            orFilter.addFilter(items.getFilterFactory().equals
                               (ContentItem.PARENT + "." + ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE));
            items.addFilter(orFilter);
            while (items.next()) {
                try {
                    DataObject dataObj = items.getDataObject();
                    if (dataObj.isDeleted()) { continue; }
                    ContentItem item = (ContentItem)DomainObjectFactory
                        .newInstance(dataObj);
                    Lifecycle lifecycle = item.getLifecycle();
                    OID mapOID = null;
                    if (item instanceof ReusableImageAsset || (lifecycle==null)) {
                        mapOID = new OID(UPGRADE_ITEM_NO_LIFECYCLE_SET_TYPE);
                    } else {
                        mapOID = new OID(UPGRADE_ITEM_LIFECYCLE_MAP_TYPE);
                        mapOID.set(UPGRADE_LIFECYCLE,
                                   DomainServiceInterfaceExposer.getDataObject(lifecycle));
                    }
                    ContentItem draft = item.getDraftVersion();
                    s_log.debug("Unpublishing pending item: draft ID=" + draft.getID() +
                                ", pending ID=" + item.getID() + ", name=" + item.getName()+
                                ", lifecycle ID=" + (lifecycle == null ? "" : lifecycle.getID().toString()));
                    mapOID.set(UPGRADE_ITEM,
                               DomainServiceInterfaceExposer.getDataObject(draft));
                    DataObject mapObj = session.retrieve(mapOID);
                    if (mapObj == null) {
                        mapObj = session.create(mapOID);
                    }
                    draft.removePendingVersion(item);
                    maybeCommit();
                } catch (RDBMSException e) {
                    if (pass != 0) {
                        throw(e);
                    }
                }
            }
        }
        recordUnpublishPendingItemsCompletion();
    }

    /**
     *  For each live item <code>live</code> where
     *  masterVersion is not null and parent is a Folder or
     *  ContentBundle (and is itself _not_ a Folder or
     *  ContentBundle):
     *  <ul>
     *    <li> Store Lifecycle reference in temporary
     *         UpgradeItemLifecycleMap (except for ReusableImageAsset,
     *         which will have no lifecycle -- store that in
     *         UpgradeItemNoLifecycleSet). </li>
     *    <li> live.getDraftVersion().unpublish()</li>
     *  </ul>
     */
    private void unpublishLiveItems() {
        Session session = SessionManager.getSession();
        DataCollection items = session.retrieve(ContentItem.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
        items.addNotEqualsFilter(ContentItem.DRAFT_VERSION, null);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE);
        CompoundFilter orFilter = items.getFilterFactory().or();
        orFilter.addFilter(items.getFilterFactory().equals
                           (ContentItem.PARENT + "." + ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE));
        orFilter.addFilter(items.getFilterFactory().equals
                           (ContentItem.PARENT + "." + ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE));
        items.addFilter(orFilter);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            Lifecycle lifecycle = item.getLifecycle();
            OID mapOID = null;
            if (item instanceof ReusableImageAsset || (lifecycle==null)) {
                mapOID = new OID(UPGRADE_ITEM_NO_LIFECYCLE_SET_TYPE);
            } else {
                mapOID = new OID(UPGRADE_ITEM_LIFECYCLE_MAP_TYPE);
                mapOID.set(UPGRADE_LIFECYCLE,
                           DomainServiceInterfaceExposer.getDataObject(lifecycle));
            }
            ContentItem draft = item.getDraftVersion();
            s_log.debug("Unpublishing live item: draft ID=" + draft.getID() +
                        ", live ID=" + item.getID() + ", name=" + item.getName()+
                        ", lifecycle ID=" + (lifecycle == null ? "" : lifecycle.getID().toString()));
            mapOID.set(UPGRADE_ITEM,
                       DomainServiceInterfaceExposer.getDataObject(draft));
            DataObject mapObj = session.retrieve(mapOID);
            if (mapObj == null) {
                mapObj = session.create(mapOID);
            }
            draft.unpublish();
            maybeCommit();
        }
        recordUnpublishLiveItemsCompletion();
    }


    /**
     *  unpublish any remaining pending/live items (random folders,
     *  etc. which may have been missed. Don't republish these.
     */
    private void cleanupPublishedItems() {
        Session session = SessionManager.getSession();

        // pending non-folder, non-bundle
        DataCollection items = session.retrieve(ContentItem.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.PENDING);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem draft = item.getDraftVersion();
            if (draft == null) {
                s_log.debug("Unpublishing pending item: pending ID=" + item.getID() +
                            ", name=" + item.getName() + ": NOT to be republished");
                item.delete();
            } else {
                s_log.debug("Unpublishing pending item: draft ID=" + draft.getID() +
                            ", pending ID=" + item.getID() + ", name=" + item.getName()
                            + ": NOT to be republished");
                draft.removePendingVersion(item);
            }
            maybeCommit();
        }

        // live non-folder, non-bundle
        items = session.retrieve(ContentItem.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, Folder.BASE_DATA_OBJECT_TYPE);
        items.addNotEqualsFilter(ACSObject.OBJECT_TYPE, ContentBundle.BASE_DATA_OBJECT_TYPE);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem draft = item.getDraftVersion();
            if (draft == null) {
                s_log.debug("Unpublishing live item: live ID=" + item.getID() +
                            ", name=" + item.getName() + ": NOT to be republished");
                item.delete();
            } else {
                s_log.debug("Unpublishing live item: draft ID=" + draft.getID() +
                            ", live ID=" + item.getID() + ", name=" + item.getName()
                            + ": NOT to be republished");
                draft.unpublish();
            }
            maybeCommit();
        }

        // pending bundles
        items = session.retrieve(ContentBundle.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.PENDING);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem draft = item.getDraftVersion();
            if (draft == null) {
                s_log.debug("Unpublishing pending bundle: pending ID=" + item.getID() +
                            ", name=" + item.getName());
                item.delete();
            } else {
                s_log.debug("Unpublishing pending bundle: draft ID=" + draft.getID() +
                            ", pending ID=" + item.getID() + ", name=" + item.getName());
                draft.removePendingVersion(item);
                draft.removePendingVersion(item);
            }
            maybeCommit();
        }

        // live bundles
        items = session.retrieve(ContentBundle.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem draft = item.getDraftVersion();
            if (draft == null) {
                s_log.debug("Unpublishing live bundle: live ID=" + item.getID() +
                            ", name=" + item.getName());
                item.delete();
            } else {
                s_log.debug("Unpublishing live bundle: draft ID=" + draft.getID() +
                            ", live ID=" + item.getID() + ", name=" + item.getName());
                draft.unpublish();
            }
            maybeCommit();
        }

        // live folders
        items = session.retrieve(Folder.BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
        while (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem draft = item.getDraftVersion();
            if (draft == null) {
                s_log.debug("Unpublishing live folder: live ID=" + item.getID() +
                            ", name=" + item.getName());
                item.delete();
            } else {
                s_log.debug("Unpublishing live folder: draft ID=" + draft.getID() +
                            ", live ID=" + item.getID() + ", name=" + item.getName());
                draft.unpublish();
            }
            maybeCommit();
        }
        recordCleanupPublishedItemsCompletion();
    }

    /**
     * For each draft item <code>item</code> in
     * UpgradeItemNoLifecycleSet, call:  item.createLiveVersion()</li>
     */
    private void publishNonLifecycleItems() {
        Session session = SessionManager.getSession();
        DataCollection coll = session.retrieve(UPGRADE_ITEM_NO_LIFECYCLE_SET_TYPE);
        while (coll.next()) {
            DataObject dataObj = (DataObject) coll.get(UPGRADE_ITEM);
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            ContentItem live = item.createLiveVersion();
            coll.getDataObject().delete();
            s_log.debug("Publishing item with no lifecycle: draft ID=" + item.getID() +
                        ", published ID=" + live.getID() + ", name=" + item.getName());
            maybeCommit();
        }
        recordPublishNonLifecycleItemsCompletion();
    }

    /**
     * For each draft item <code>item</code>, <code>lifecycle</code> in
     * UpgradeItemLifecycleMap, call:
     * <code><pre>
     *   item.publish(lifecycle.getLifecycleDefinition(),
     *                                  lifecycle.getStartDate());
     *   // now set stop date and individual phase start/stop dates
     *   // based on the LifecycleDefinition
     * </pre></code>
     *
     */
    private void publishLifecycleItems() {
        Session session = SessionManager.getSession();
        DataCollection coll = session.retrieve(UPGRADE_ITEM_LIFECYCLE_MAP_TYPE);
        while (coll.next()) {
            DataObject dataObj = (DataObject) coll.get(UPGRADE_ITEM);
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);
            dataObj = (DataObject) coll.get(UPGRADE_LIFECYCLE);
            Lifecycle lifecycle = new Lifecycle(dataObj);
            LifecycleDefinition cycleDef = lifecycle.getLifecycleDefinition();
            ContentItem pending = item.publish(cycleDef,lifecycle.getStartDate());
            Lifecycle newLifecycle = pending.getLifecycle();
            s_log.debug("Publishing item: draft ID=" + item.getID() +
                        ", published ID=" + pending.getID() + ", name=" + item.getName()+
                        ", lifecycle ID=" + newLifecycle.getID());
            newLifecycle.setEndDate(lifecycle.getEndDate());
            PhaseCollection oldPhases = lifecycle.getPhases();
            PhaseCollection newPhases = newLifecycle.getPhases();
            while (newPhases.next()) {
                if (oldPhases.next()) {
                    Phase newPhase = newPhases.getPhase();
                    Phase oldPhase = oldPhases.getPhase();
                    newPhase.setStartDate(oldPhase.getStartDate());
                    newPhase.setEndDate(oldPhase.getEndDate());
                }
            }
            newPhases.close();
            oldPhases.close();
            coll.getDataObject().delete();
            maybeCommit();
        }
        recordPublishLifecycleItemsCompletion();
    }

    /**
     *  Remove orphaned lifecycles (those with no associated LifecycleService)
     */
    private void removeOrphanedLifecycles() {
        DataCollection coll = SessionManager.getSession().
            retrieve(Lifecycle.BASE_DATA_OBJECT_TYPE);
        coll.addNotInSubqueryFilter
            ("id", "com.arsdigita.cms.getConnectedLifecycles");
        while (coll.next()) {
            DataObject dataObj = coll.getDataObject();
            Lifecycle lifecycle = new Lifecycle(dataObj);
            s_log.debug("Deleting orphaned lifecycle: ID=" + lifecycle.getID());
            lifecycle.delete();
            maybeCommit();
        }
        recordRemoveOrphanedLifecyclesCompletion();
    }

    private void maybeCommit() {
        m_txnCounter++;
        if (m_txnCounter == m_itemsPerTransaction) {
            s_log.debug("Publish API upgrade: transaction commit");
            m_txn.commitTxn();
            m_txn.beginTxn();
            m_txnCounter = 0;
        }
    }

    private void forceCommit() {
        s_log.debug("Publish API upgrade: transaction commit");
        m_txn.commitTxn();
        m_txn.beginTxn();
        m_txnCounter = 0;
    }


    private boolean isUnpublishPendingItemsCompleted() {
        return ((Boolean)getUpgradeProgress().get(PENDING_UNPUBLISH_DONE)).booleanValue();
    }
    private boolean isUnpublishLiveItemsCompleted() {
        return ((Boolean)getUpgradeProgress().get(LIVE_UNPUBLISH_DONE)).booleanValue();
    }
    private boolean isCleanupPublishedItemsCompleted() {
        return ((Boolean)getUpgradeProgress().get(CLEANUP_UNPUBLISH_DONE)).booleanValue();
    }
    private boolean isPublishNonLifecycleItemsCompleted() {
        return ((Boolean)getUpgradeProgress().get(NON_LIFECYCLE_PUBLISH_DONE)).booleanValue();
    }
    private boolean isPublishLifecycleItemsCompleted() {
        return ((Boolean)getUpgradeProgress().get(LIFECYCLE_PUBLISH_DONE)).booleanValue();
    }
    private boolean isRemoveOrphanedLifecyclesCompleted() {
        return ((Boolean)getUpgradeProgress().get(REMOVE_ORPHANED_LIFECYCLES_DONE)).booleanValue();
    }

    private void recordUnpublishPendingItemsCompletion() {
        getUpgradeProgress().set(PENDING_UNPUBLISH_DONE,Boolean.TRUE);
        forceCommit();
    }
    private void recordUnpublishLiveItemsCompletion() {
        getUpgradeProgress().set(LIVE_UNPUBLISH_DONE,Boolean.TRUE);
        forceCommit();
    }
    private void recordCleanupPublishedItemsCompletion() {
        getUpgradeProgress().set(CLEANUP_UNPUBLISH_DONE,Boolean.TRUE);
        forceCommit();
    }
    private void recordPublishNonLifecycleItemsCompletion() {
        getUpgradeProgress().set(NON_LIFECYCLE_PUBLISH_DONE,Boolean.TRUE);
        forceCommit();
    }
    private void recordPublishLifecycleItemsCompletion() {
        getUpgradeProgress().set(LIFECYCLE_PUBLISH_DONE,Boolean.TRUE);
        forceCommit();
    }
    private void recordRemoveOrphanedLifecyclesCompletion() {
        getUpgradeProgress().set(REMOVE_ORPHANED_LIFECYCLES_DONE,Boolean.TRUE);
        forceCommit();
    }

    private DataObject getUpgradeProgress() {
        Session session = SessionManager.getSession();
        DataCollection coll = session.retrieve(UPGRADE_PROGRESS_TYPE);
        DataObject progressObj = null;
        if (coll.next()) {
            progressObj = coll.getDataObject();
            coll.close();
        } else {
            OID oid = null;
            oid = new OID(UPGRADE_PROGRESS_TYPE);
            oid.set(UPGRADE_PROGRESS_ID,UPGRADE_PROGRESS_ID_VALUE);
            progressObj = session.create(oid);
            progressObj.set(PENDING_UNPUBLISH_DONE,Boolean.FALSE);
            progressObj.set(LIVE_UNPUBLISH_DONE,Boolean.FALSE);
            progressObj.set(CLEANUP_UNPUBLISH_DONE,Boolean.FALSE);
            progressObj.set(NON_LIFECYCLE_PUBLISH_DONE,Boolean.FALSE);
            progressObj.set(LIFECYCLE_PUBLISH_DONE,Boolean.FALSE);
            progressObj.set(REMOVE_ORPHANED_LIFECYCLES_DONE,Boolean.FALSE);
        }
        return progressObj;
    }
}
