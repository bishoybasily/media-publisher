/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device;


import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bishoy
 */
public class DeviceMicrophone extends Device<byte[], TargetDataLine> {

    private final String name;
    private final Type type = Type.AUDIO;
    private final TargetDataLine targetDataLine;
    private final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    private boolean working = false;

    public DeviceMicrophone(TargetDataLine targetDataLine, String name) {
        this.targetDataLine = targetDataLine;
        this.name = name;
    }

    public static List<DeviceMicrophone> listMicrophones() {
        List<DeviceMicrophone> deviceMicrophones = new ArrayList<>();
        AudioFormat audioFormat = new AudioFormat(44100F, 16, 2, true, false);
        for (int i = 0; i < AudioSystem.getMixerInfo().length; i++) {
            Mixer.Info info = AudioSystem.getMixerInfo()[i];
            Mixer mixer = AudioSystem.getMixer(info);
            Line.Info[] lineInfos;
            //**
            lineInfos = mixer.getSourceLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                try (Line line = mixer.getLine(lineInfo)) {
                    ((SourceDataLine) line).open(audioFormat);
                    line.close();
                } catch (Exception e) {

                }
            }
            //**
            lineInfos = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                try (Line line = mixer.getLine(lineInfo)) {
                    ((TargetDataLine) line).open(audioFormat);
                    line.close();
                    deviceMicrophones.add(new DeviceMicrophone((TargetDataLine) line, ("microphone_" + i)));
                } catch (Exception e) {

                }
            }
        }
        return deviceMicrophones;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void startWork() {
        if (!working) {
            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                working = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] grapData() throws Exception {
        byte[] audioBytes = new byte[(((int) targetDataLine.getFormat().getSampleRate()) * targetDataLine.getFormat().getChannels())];
        int read = targetDataLine.read(audioBytes, 0, targetDataLine.available());
        byte[] finalAudioBytes = new byte[read];
        System.arraycopy(audioBytes, 0, finalAudioBytes, 0, read);
        return finalAudioBytes;
    }

    @Override
    public void stopWork() {
        targetDataLine.close();
        targetDataLine.stop();
        working = false;
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public TargetDataLine getCore() {
        return targetDataLine;
    }
}
