/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.writter;


import com.gmail.bishoybasily.media.publisher.device.DeviceCamera;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author bishoy
 */
public class WriterVideo extends Writer<Frame> {

    private final DeviceCamera deviceCamera;
    private final boolean interleaved;
    private final String format;
    private FFmpegFrameRecorder fFmpegFrameRecorder;
    private long startTime;

    public WriterVideo(DeviceCamera deviceCamera, String format, boolean interleaved) {
        this.deviceCamera = deviceCamera;
        this.format = format;
        this.interleaved = interleaved;
    }

    @Override
    public void startWriting() {
        if (!propertyWriting.get()) {
            try {
                String filePath = Globals.VIDEO_FILES_PATH + File.separator + deviceCamera.getName() + File.separator + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "." + format);
                new File(filePath).getParentFile().mkdirs();
                fFmpegFrameRecorder = new FFmpegFrameRecorder(filePath, deviceCamera.getCore().getImageWidth(), deviceCamera.getCore().getImageHeight(), 0);
                //**
//                fFmpegFrameRecorder.setVideoOption("tune", "zerolatency");
//                fFmpegFrameRecorder.setVideoOption("preset", "ultrafast");
//                fFmpegFrameRecorder.setVideoOption("crf", "28");
//                fFmpegFrameRecorder.setVideoBitrate(deviceCamera.getCore().getVideoBitrate());
//                fFmpegFrameRecorder.setFormat(format);
//                fFmpegFrameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//                fFmpegFrameRecorder.setFrameRate(deviceCamera.getCore().getFrameRate());
                //**
                fFmpegFrameRecorder.start();

                startTime = System.currentTimeMillis();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            propertyWriting.set(true);
        }
    }

    @Override
    public void stopWriting() {
        if (propertyWriting.get()) {
            try {
                fFmpegFrameRecorder.stop();
                fFmpegFrameRecorder.release();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            propertyWriting.set(false);
        }

    }

    @Override
    public void write(Data<Frame> data) {
        if (propertyWriting.get()) {
            try {
                long videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
                if (videoTimestamp > fFmpegFrameRecorder.getTimestamp()) {
                    fFmpegFrameRecorder.setTimestamp(videoTimestamp);
                }
                fFmpegFrameRecorder.record(data.get());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
