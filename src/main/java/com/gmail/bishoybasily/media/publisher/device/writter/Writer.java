/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device.writter;


import com.gmail.bishoybasily.media.publisher.utility.Data;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author bishoy
 */
public abstract class Writer<T> {

    public final BooleanProperty propertyWriting = new SimpleBooleanProperty(false);

    public abstract void startWriting();

    public abstract void stopWriting();

    public abstract void write(Data<T> data) throws Exception;

}
