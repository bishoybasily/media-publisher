/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.utility;


import com.gmail.bishoybasily.media.publisher.device.director.Director;
import com.gmail.bishoybasily.media.publisher.device.transmitter.Transmitter;
import com.gmail.bishoybasily.media.publisher.utility.framework.forms.FormsController;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author bishoy
 */
public class Globals {

    public final static String CONFIGURATION_FILE_PATH = System.getProperty("user.home") + File.separator + "MediaPublisher" + File.separator + "configuration.json";
    public static final Set<Director> directors = new HashSet<>();
    public static final Set<Transmitter> transmitters = new HashSet<>();
    public static final FormsController screensController = new FormsController();
    public static String COMMANDS_SERVER_HOST;
    public static Integer COMMANDS_SERVER_PORT;
    public static String RTMP_SERVER_HOST;
    public static Integer RTMP_SERVER_PORT;
    public static String RTMP_SERVER_APPLICATION;
    public static String FTP_SERVER_HOST;
    public static Integer FTP_SERVER_PORT;
    public static String FTP_SERVER_NAME;
    public static String FTP_SERVER_PASSWORD;
    public static String IDENTITY;
    public static String VIDEO_FILES_PATH;
    public static String AUDIO_FILES_PATH;
    public static Long VIDEO_FILES_LENGTH;
    public static Long AUDIO_FILES_LENGTH;
}
