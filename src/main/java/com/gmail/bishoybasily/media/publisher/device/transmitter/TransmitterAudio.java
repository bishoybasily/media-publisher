/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.transmitter;


import com.gmail.bishoybasily.media.publisher.device.DeviceMicrophone;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.nio.ShortBuffer;

/**
 * @author bishoy
 */
public class TransmitterAudio extends Transmitter<ShortBuffer> {

    private final String url;
    private final boolean interleaved;
    private final String format;
    private final DeviceMicrophone deviceMicrophone;
    private FFmpegFrameRecorder fFmpegFrameRecorder;

    public TransmitterAudio(String url, boolean interleaved, String format, DeviceMicrophone deviceMicrophone) {
        this.url = url;
        this.interleaved = interleaved;
        this.format = format;
        this.deviceMicrophone = deviceMicrophone;
    }

    @Override
    public void onConnected() throws Exception {
        fFmpegFrameRecorder = new FFmpegFrameRecorder(url, deviceMicrophone.getCore().getFormat().getChannels());

//        fFmpegFrameRecorder.setInterleaved(interleaved);
//        fFmpegFrameRecorder.setAudioQuality(0);
//        fFmpegFrameRecorder.setAudioOption("crf", "0");
//        fFmpegFrameRecorder.setFormat(format);
//        fFmpegFrameRecorder.setFrameRate(deviceMicrophone.getCore().getFormat().getFrameRate());
//        fFmpegFrameRecorder.setSampleRate((int) deviceMicrophone.getCore().getFormat().getSampleRate());
        //  fFmpegFrameRecorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

        fFmpegFrameRecorder.start();

    }

    @Override
    public void onDisconnected() throws Exception {
        fFmpegFrameRecorder.stop();
        fFmpegFrameRecorder.release();
    }

    @Override
    public void onTransmit(Data<ShortBuffer> data) throws Exception {
        long videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
        if (videoTimestamp > fFmpegFrameRecorder.getTimestamp()) {
            fFmpegFrameRecorder.setTimestamp(videoTimestamp);
        }
        fFmpegFrameRecorder.recordSamples(data.get());
    }

    @Override
    public String getURL() {
        return url;
    }
}
