/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.writter;


import com.gmail.bishoybasily.media.publisher.device.DeviceMicrophone;
import com.gmail.bishoybasily.media.publisher.utility.Data;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.io.File;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author bishoy
 */
public class WriterAudio extends Writer<ShortBuffer> {

    private final DeviceMicrophone deviceMicrophone;
    private final boolean interleaved;
    private final String format;
    private FFmpegFrameRecorder fFmpegFrameRecorder;
    private long startTime;

    public WriterAudio(DeviceMicrophone deviceMicrophone, String format, boolean interleaved) {
        this.deviceMicrophone = deviceMicrophone;
        this.format = format;
        this.interleaved = interleaved;

    }

    @Override
    public void startWriting() {
        if (!propertyWriting.get()) {
            try {
                String filePath = Globals.AUDIO_FILES_PATH + File.separator + deviceMicrophone.getName() + File.separator + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + "." + format);
                new File(filePath).getParentFile().mkdirs();
                fFmpegFrameRecorder = new FFmpegFrameRecorder(filePath, deviceMicrophone.getCore().getFormat().getChannels());
                //**
//                fFmpegFrameRecorder.setInterleaved(interleaved);
//                fFmpegFrameRecorder.setFormat(format);
//                fFmpegFrameRecorder.setAudioQuality(0);
//                fFmpegFrameRecorder.setAudioOption("crf", "0");
//                fFmpegFrameRecorder.setFrameRate(deviceMicrophone.getCore().getFormat().getFrameRate());
//                fFmpegFrameRecorder.setSampleRate((int) deviceMicrophone.getCore().getFormat().getSampleRate());
                //  fFmpegFrameRecorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
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
    public void write(Data<ShortBuffer> data) {
        if (propertyWriting.get()) {
            try {
                long videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);
                if (videoTimestamp > fFmpegFrameRecorder.getTimestamp()) {
                    fFmpegFrameRecorder.setTimestamp(videoTimestamp);
                }
                fFmpegFrameRecorder.recordSamples(data.get());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
