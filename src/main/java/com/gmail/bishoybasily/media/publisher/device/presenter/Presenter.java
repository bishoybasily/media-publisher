/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.presenter;

import com.gmail.bishoybasily.media.publisher.utility.Data;

import javax.swing.*;

/**
 * @author bishoy
 */
public abstract class Presenter<T> extends JPanel {

    public abstract void start();

    public abstract void present(Data<T> data);

    public abstract void stop();
}
