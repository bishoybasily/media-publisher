/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.director;

import com.gmail.bishoybasily.media.publisher.device.Device;
import com.gmail.bishoybasily.media.publisher.device.presenter.Presenter;
import com.gmail.bishoybasily.media.publisher.device.transmitter.Transmitter;
import javafx.beans.property.BooleanProperty;

/**
 * @author bishoy
 */
public interface Director {

    void startPresenting();

    BooleanProperty propertyPresenting();

    void stopPresenting();

    Device getDevice();

    Presenter getPresenter();

    Transmitter getTransmitter();

    boolean getLocalPreview();

    void setLocalPreview(boolean allow);

}
