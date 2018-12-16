/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.director;


import com.gmail.bishoybasily.media.publisher.device.Device;
import com.gmail.bishoybasily.media.publisher.device.DeviceCamera;
import com.gmail.bishoybasily.media.publisher.device.DirectorListener;
import com.gmail.bishoybasily.media.publisher.device.presenter.Presenter;
import com.gmail.bishoybasily.media.publisher.device.presenter.PresenterVideo;
import com.gmail.bishoybasily.media.publisher.device.transmitter.Transmitter;
import com.gmail.bishoybasily.media.publisher.device.transmitter.TransmitterVideo;
import com.gmail.bishoybasily.media.publisher.device.writter.Writer;
import com.gmail.bishoybasily.media.publisher.device.writter.WriterVideo;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThread;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThreadListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author bishoy
 */
public class DirectorVideo implements Director {

    private final DeviceCamera deviceCamera;
    private final PresenterVideo presenter;
    private final Set<DirectorListener> directorListeners = new HashSet<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private NotifyingThread worker;
    private Writer writer;
    private Transmitter transmitter;
    private BooleanProperty presenting = new SimpleBooleanProperty(false);
    private boolean localPreview = true;

    public DirectorVideo(DeviceCamera deviceCamera, PresenterVideo presenter) {

        String url = "rtmp://" + Globals.RTMP_SERVER_HOST + ":" + Globals.RTMP_SERVER_PORT + File.separator + Globals.RTMP_SERVER_APPLICATION + File.separator + Globals.IDENTITY + "_" + deviceCamera.getName();

        this.deviceCamera = deviceCamera;
        this.presenter = presenter;
        this.transmitter = new TransmitterVideo(url, true, "flv", deviceCamera);
        this.writer = new WriterVideo(deviceCamera, "flv", true);

    }

    @Override
    public void startPresenting() {
        presenting.set(true);
        worker = new NotifyingThread(() -> {

            deviceCamera.startWork();
            presenter.start();
            writer.startWriting();
            Long start = System.currentTimeMillis();

            directorListeners.stream().forEach((directorListener) -> directorListener.onDeviceStarted());

            while (presenting.get()) {

                try {
                    Frame frame = deviceCamera.grapData();
                    if (localPreview) {
                        presenter.present(new Data(new Java2DFrameConverter().convert(frame)));
                    }

                    if ((System.currentTimeMillis() - start) < (Globals.VIDEO_FILES_LENGTH)) {
                        writer.write(new Data(frame));
                    } else {
                        start = System.currentTimeMillis();
                        writer.stopWriting();
                        writer.startWriting();
                    }

                    if (transmitter.propertyConnection.get()) {
                        transmitter.onTransmit(new Data(frame));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    presenting.set(false);
                }
            }

            writer.stopWriting();
            presenter.stop();
            deviceCamera.stopWork();
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

    @Override
    public Transmitter getTransmitter() {
        return transmitter;
    }

    public Device getDevice() {
        return deviceCamera;
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
    public Presenter getPresenter() {
        return presenter;
    }
}
