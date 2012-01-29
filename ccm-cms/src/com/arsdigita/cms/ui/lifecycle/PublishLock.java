package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import java.util.Calendar;

/**
 * Used by {@link ItemLifecycleSelectForm} and {@link ItemLifecycleItemPane} to
 * lock an item if threaded publishing is active.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublishLock {

    public final static String LOCK_OBJECT_TYPE =
                               "com.arsdigita.cms.PublishLock";
    public final static String ID = "id";
    public final static String LOCKED_OID = "lockedOid";
    public final static String TIMESTAMP = "timestamp";
    public final static String ACTION = "action";
    public final static String ERROR = "error";
    private static PublishLock instance = new PublishLock();

    private PublishLock() {
    }

    protected static synchronized PublishLock getInstance() {
        return instance;
    }

    protected synchronized void lock(final ContentItem item) {
        lock(item, "publish");
    }

    protected synchronized void lock(final ContentItem item,
                                     final String action) {
        SessionManager.getSession().getTransactionContext().beginTxn();
        final DataObject lock = SessionManager.getSession().create(
                LOCK_OBJECT_TYPE);
        lock.set(ID, item.getID());
        lock.set(LOCKED_OID, item.getOID().toString());
        lock.set(TIMESTAMP, Calendar.getInstance().getTime());
        lock.set(ACTION, action);
        lock.save();
        SessionManager.getSession().getTransactionContext().commitTxn();
    }

    protected synchronized void unlock(final ContentItem item) {
        SessionManager.getSession().getTransactionContext().beginTxn();
        final DataCollection collection = SessionManager.getSession().retrieve(
                LOCK_OBJECT_TYPE);
        collection.addFilter(String.format("%s = '%s'", LOCKED_OID,
                                           item.getOID().toString()));
        if (!collection.isEmpty()) {
            collection.next();
            final DataObject lock = collection.getDataObject();
            if (!(ERROR.equals(lock.get(ACTION)))) {
                lock.delete();
            }
        }
        collection.close();
        SessionManager.getSession().getTransactionContext().commitTxn();
    }

    protected synchronized boolean isLocked(final ContentItem item) {
        final DataCollection collection = SessionManager.getSession().retrieve(
                LOCK_OBJECT_TYPE);
        collection.addFilter(String.format("%s = '%s'", LOCKED_OID,
                                           item.getOID().toString()));
        if (collection.isEmpty()) {
            collection.close();
            return false;
        } else {
            collection.close();
            return true;
        }
    }

    protected synchronized void setError(final ContentItem item) {
        SessionManager.getSession().getTransactionContext().beginTxn();
        final DataCollection collection = SessionManager.getSession().retrieve(
                LOCK_OBJECT_TYPE);
        collection.addFilter(String.format("%s = '%s'", LOCKED_OID,
                                           item.getOID().toString()));

        if (!collection.isEmpty()) {
            collection.next();

            final DataObject lock = collection.getDataObject();
            lock.set(ACTION, ERROR);
            lock.save();
        }
        collection.close();
        SessionManager.getSession().getTransactionContext().commitTxn();
    }

    protected synchronized boolean hasError(final ContentItem item) {
        final DataCollection collection = SessionManager.getSession().retrieve(
                LOCK_OBJECT_TYPE);
        collection.addFilter(String.format("%s = '%s'", LOCKED_OID,
                                           item.getOID().toString()));
        if (collection.isEmpty()) {
            collection.close();
            return false;
        } else {
            collection.next();

            final DataObject lock = collection.getDataObject();
            if (ERROR.equals(lock.get(ACTION).toString())) {
                collection.close();
                return true;
            } else {
                collection.close();
                return false;
            }
        }
    }
}
