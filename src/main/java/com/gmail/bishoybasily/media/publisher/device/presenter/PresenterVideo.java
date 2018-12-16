/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.presenter;

import com.gmail.bishoybasily.media.publisher.utility.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author bishoy
 */
public class PresenterVideo extends Presenter<BufferedImage> {

    private BufferedImage bufferedImage;

    @Override
    public void start() {
        bufferedImage = null;
        SwingUtilities.invokeLater(() -> repaint());
    }

    @Override
    public void present(Data<BufferedImage> data) {
        bufferedImage = data.get();
        SwingUtilities.invokeLater(() -> repaint());
    }

    @Override
    public void stop() {
        bufferedImage = null;
        SwingUtilities.invokeLater(() -> repaint());
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (bufferedImage == null) {
            graphics.clearRect(0, 0, getWidth(), getHeight());
        } else {
            graphics.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

}
