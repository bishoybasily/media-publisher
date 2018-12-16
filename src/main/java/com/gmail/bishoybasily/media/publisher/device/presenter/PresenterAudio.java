/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.presenter;

import com.gmail.bishoybasily.media.publisher.utility.Data;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author bishoy
 */
public class PresenterAudio extends Presenter<byte[]> {

    private final AudioFormat audioFormat = new AudioFormat(44100F, 16, 2, true, false);
    private SourceDataLine sourceDataLine;
    private BufferedImage bufferedImage;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (bufferedImage == null) {
            graphics.clearRect(0, 0, getWidth(), getHeight());
        } else {
            graphics.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    @Override
    public void start() {
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            bufferedImage = ImageIO.read(getClass().getResource("/image/speaker.png"));
            SwingUtilities.invokeLater(() -> repaint());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void present(Data<byte[]> data) {
        sourceDataLine.write(data.get(), 0, data.get().length);
    }

    @Override
    public void stop() {
        sourceDataLine.stop();
        sourceDataLine.close();
        bufferedImage = null;
        SwingUtilities.invokeLater(() -> repaint());
    }

}
