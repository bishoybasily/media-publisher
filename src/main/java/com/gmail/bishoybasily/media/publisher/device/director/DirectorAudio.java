/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.director;


import com.gmail.bishoybasily.media.publisher.device.Device;
import com.gmail.bishoybasily.media.publisher.device.DeviceMicrophone;
import com.gmail.bishoybasily.media.publisher.device.DirectorListener;
import com.gmail.bishoybasily.media.publisher.device.presenter.Presenter;
import com.gmail.bishoybasily.media.publisher.device.presenter.PresenterAudio;
import com.gmail.bishoybasily.media.publisher.device.transmitter.Transmitter;
import com.gmail.bishoybasily.media.publisher.device.transmitter.TransmitterAudio;
import com.gmail.bishoybasily.media.publisher.device.writter.Writer;
import com.gmail.bishoybasily.media.publisher.device.writter.WriterAudio;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThread;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThreadListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author bishoy
 */
public class DirectorAudio implements Director {

    private final DeviceMicrophone deviceMicrophone;
    private final PresenterAudio presenter;
    private final Set<DirectorListener> directorListeners = new HashSet<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private NotifyingThread worker;
    private volatile Writer writer;
    private volatile Transmitter transmitter;
    private BooleanProperty presenting = new SimpleBooleanProperty(false);
    private boolean localPreview = true;

    public DirectorAudio(DeviceMicrophone deviceMicrophone, PresenterAudio presenter) {

        String url = "rtmp://" + Globals.RTMP_SERVER_HOST + ":" + Globals.RTMP_SERVER_PORT + File.separator + Globals.RTMP_SERVER_APPLICATION + File.separator + Globals.IDENTITY + "_" + deviceMicrophone.getName();

        this.deviceMicrophone = deviceMicrophone;
        this.presenter = presenter;
        this.transmitter = new TransmitterAudio(url, true, "flv", deviceMicrophone);
        this.writer = new WriterAudio(deviceMicrophone, "aac", true);
    }

    @Override
    public void startPresenting() {
        presenting.set(true);
        worker = new NotifyingThread(() -> {

            deviceMicrophone.startWork();
            presenter.start();
            writer.startWriting();
            Long start = System.currentTimeMillis();

            directorListeners.stream().forEach((directorListener) -> directorListener.onDeviceStarted());

            while (presenting.get()) {

                try {
                    byte[] audioBytes = deviceMicrophone.grapData();

                    short[] samples = new short[audioBytes.length / 2];
                    ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                    ShortBuffer shortBuffer = ShortBuffer.wrap(samples, 0, audioBytes.length / 2);

                    if (localPreview) {
                        presenter.present(new Data(audioBytes));
                    }

                    if ((System.currentTimeMillis() - start) < (Globals.AUDIO_FILES_LENGTH)) {
                        writer.write(new Data(shortBuffer));
                    } else {
                        start = System.currentTimeMillis();
                        writer.stopWriting();
                        writer.startWriting();
                    }

                    if (transmitter.propertyConnection.get()) {
                        transmitter.transmit(new Data(shortBuffer));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    presenting.set(false);
                }
            }
            presenter.stop();
            deviceMicrophone.stopWork();
            directorListeners.stream().forEach((directorListener) -> directorListener.onDeviceStopped());

        });
        worker.addListener(new NotifyingThreadListener() {

            @Override
            public void afterExecute() {
                countDownLatch.countDown();
            }

            @Override
            public void beforeExecute() {

            }
        });
        worker.start();
    }

    @Override
    public BooleanProperty propertyPresenting() {
        return presenting;
    }

    @Override
    public void stopPresenting() {
        presenting.set(false);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Device getDevice() {
        return deviceMicrophone;
    }

    @Override
    public boolean getLocalPreview() {
        return localPreview;
    }

    @Override
    public void setLocalPreview(boolean allow) {
        localPreview = allow;
    }

    @Override
    public Transmitter getTransmitter() {
        return transmitter;
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }
}
