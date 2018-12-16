/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.utility.framework.concurrency;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bishoy
 */
public class NotifyingThread extends Thread {

    private final Set<NotifyingThreadListener> listeners = new HashSet<>();
    private final NotifyingRunnable notifyingRunnable;

    public NotifyingThread(NotifyingRunnable notifyingRunnable) {
        this.notifyingRunnable = notifyingRunnable;
    }

    public final void addListener(NotifyingThreadListener notifyingThreadListener) {
        listeners.add(notifyingThreadListener);
    }

    public final void removeListener(NotifyingThreadListener notifyingThreadListener) {
        listeners.remove(notifyingThreadListener);
    }

    @Override
    public void run() {
        listeners.stream().forEach((listener) -> {
            listener.beforeExecute();
        });
        try {
            notifyingRunnable.run();
        } catch (Exception e) {
        }
        listeners.stream().forEach((listener) -> {
            listener.afterExecute();
        });
    }

}
