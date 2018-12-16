/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device;

/**
 * @author bishoy
 */
public abstract class Device<D, C> {


    public abstract Type getType();

    public abstract String getName();

    public abstract void startWork();

    public abstract boolean isWorking();

    public abstract void stopWork();

    public abstract D grapData() throws Exception;

    public abstract C getCore();

    public enum Type {

        AUDIO, VIDEO
    }


}
