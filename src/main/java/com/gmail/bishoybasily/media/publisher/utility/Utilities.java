/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.utility;

/**
 * @author bishoy
 */
public class Utilities {

    public static void run(Runnable runnable) {
        new Thread(runnable).start();
    }

}
