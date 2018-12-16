/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.transmitter;


import com.gmail.bishoybasily.media.publisher.utility.Data;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author bishoy
 */
public abstract class Transmitter<T> {

    public final BooleanProperty propertyConnection = new SimpleBooleanProperty(false);
    protected long startTime = 0;

    public final void connect() throws Exception {
        if (!propertyConnection.get()) {
            startTime = System.currentTimeMillis();
            onConnected();
            propertyConnection.set(true);
        }
    }

    protected abstract void onConnected() throws Exception;

    public final void disconnect() throws Exception {
        if (propertyConnection.get()) {

            onDisconnected();
            propertyConnection.set(false);
        }
    }

    protected abstract void onDisconnected() throws Exception;

    public void transmit(Data<T> data) throws Exception {
        if (propertyConnection.get())
            onTransmit(data);
    }

    public abstract void onTransmit(Data<T> data) throws Exception;

    public abstract String getURL();

}
