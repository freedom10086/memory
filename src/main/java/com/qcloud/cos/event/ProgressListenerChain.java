package com.qcloud.cos.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An implementation of ProgressListener interface that delegates
 * progressChanged callback to multiple listeners. It also takes
 * an optional ProgressEventFilter to filter incoming events before
 * passing them to the listeners.
 */
public class ProgressListenerChain implements ProgressListener, DeliveryMode {
    private static final Logger log = LoggerFactory.getLogger(ProgressListenerChain.class);

    private final List<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();
    private final ProgressEventFilter progressEventFilter;
    /**
     * True if synchronous callback to every listener in this chain is safe with
     * no risk of incurring undue latency.
     *
     * @see SDKProgressPublisher
     */
    private volatile boolean syncCallSafe = true;

    /**
     * Create a listener chain that directly passes all the progress events to
     * the specified listeners.
     */
    public ProgressListenerChain(ProgressListener... listeners) {
        this(null, listeners);
    }

    /**
     * Create a listener chain with a ProgressEventFilter.
     */
    public ProgressListenerChain(ProgressEventFilter progressEventFilter, ProgressListener... listeners) {
        if (listeners == null) {
            throw new IllegalArgumentException(
                    "Progress Listeners cannot be null.");
        }
        for (ProgressListener listener : listeners)
            addProgressListener(listener);
        this.progressEventFilter = progressEventFilter;
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) 
            return;
        if (syncCallSafe)
            syncCallSafe = DeliveryMode.Check.isSyncCallSafe(listener);
        this.listeners.add(listener);
    }

    public synchronized void removeProgressListener(ProgressListener listener) {
        if (listener == null) return;
        this.listeners.remove(listener);
    }

    /**
     * Returns the listeners associated with this listener chain.
     */
    protected List<ProgressListener> getListeners() {
        return listeners;
    }

    public void progressChanged(final ProgressEvent progressEvent) {
        ProgressEvent filteredEvent = progressEvent;
        if (progressEventFilter != null) {
            filteredEvent = progressEventFilter.filter(progressEvent);
            if (filteredEvent == null) return;
        }

        for ( ProgressListener listener : listeners ) {
            try {
                listener.progressChanged(filteredEvent);
            } catch ( RuntimeException e ) {
                log.warn("Couldn't update progress listener", e);
            }
        }
    }

    @Override public boolean isSyncCallSafe() { return syncCallSafe; }
}
