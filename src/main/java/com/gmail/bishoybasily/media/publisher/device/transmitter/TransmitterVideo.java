/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.transmitter;


import com.gmail.bishoybasily.media.publisher.device.DeviceCamera;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * @author bishoy
 */
public class TransmitterVideo extends Transmitter<Frame> {

    private final String url;
    private final boolean interleaved;
    private final String format;
    private final DeviceCamera deviceCamera;
    private FFmpegFrameRecorder fFmpegFrameRecorder;

    public TransmitterVideo(String url, boolean interleaved, String format, DeviceCamera deviceCamera) {
        this.url = url;
        this.interleaved = interleaved;
        this.format = format;
        this.deviceCamera = deviceCamera;
    }

    @Override
    public void onConnected() throws Exception {
        fFmpegFrameRecorder = new FFmpegFrameRecorder(url, deviceCamera.getCore().getImageWidth(), deviceCamera.getCore().getImageHeight());
        //**
//        fFmpegFrameRecorder.setVideoOption("tune", "zerolatency");
//        fFmpegFrameRecorder.setVideoOption("preset", "ultrafast");
//        fFmpegFrameRecorder.setVideoOption("crf", "28");
//        fFmpegFrameRecorder.setVideoBitrate(deviceCamera.getCore().getVideoBitrate());
//        fFmpegFrameRecorder.setFormat(format);
        //  fFmpegFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        fFmpegFrameRecorder.setFrameRate(deviceCamera.getCore().getFrameRate());
        //**
        fFmpegFrameRecorder.start();
    }

    @Override
    public void onDisconnected() throws Exception {
        fFmpegFrameRecorder.stop();
        fFmpegFrameRecorder.release();
    }

    @Override
    public void onTransmit(Data<Frame> data) throws Exception {
        long videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
        if (videoTimestamp > fFmpegFrameRecorder.getTimestamp()) {
            fFmpegFrameRecorder.setTimestamp(videoTimestamp);
        }
        fFmpegFrameRecorder.record(data.get());
    }

    @Override
    public String getURL() {
        return url;
    }
}
