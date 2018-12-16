package com.gmail.bishoybasily.media.publisher;

import com.gmail.bishoybasily.media.publisher.controller.ControllerMain;
import com.gmail.bishoybasily.media.publisher.controller.ControllerSettings;
import com.gmail.bishoybasily.media.publisher.device.DeviceCamera;
import com.gmail.bishoybasily.media.publisher.device.DeviceMicrophone;
import com.gmail.bishoybasily.media.publisher.device.director.DirectorAudio;
import com.gmail.bishoybasily.media.publisher.device.director.DirectorVideo;
import com.gmail.bishoybasily.media.publisher.device.presenter.PresenterAudio;
import com.gmail.bishoybasily.media.publisher.device.presenter.PresenterVideo;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {
        //timeToWork();
        launch(args);
        timeToRelax();
    }

    private static boolean thereAreValidVariables() {
        boolean result = false;
        try {
            JsonObject jsonObject = new Gson().fromJson(new Scanner(new File(Globals.CONFIGURATION_FILE_PATH)).useDelimiter("\\Z").next(), JsonObject.class);

            Globals.COMMANDS_SERVER_HOST = jsonObject.getAsJsonObject("commands").get("host").getAsString();
            Globals.COMMANDS_SERVER_PORT = jsonObject.getAsJsonObject("commands").get("port").getAsInt();

            Globals.RTMP_SERVER_HOST = jsonObject.getAsJsonObject("rtsp").get("host").getAsString();
            Globals.RTMP_SERVER_PORT = jsonObject.getAsJsonObject("rtsp").get("port").getAsInt();
            Globals.RTMP_SERVER_APPLICATION = jsonObject.getAsJsonObject("rtsp").get("application").getAsString();

            Globals.FTP_SERVER_HOST = jsonObject.getAsJsonObject("ftp").get("host").getAsString();
            Globals.FTP_SERVER_PORT = jsonObject.getAsJsonObject("ftp").get("port").getAsInt();
            Globals.FTP_SERVER_NAME = jsonObject.getAsJsonObject("ftp").get("name").getAsString();
            Globals.FTP_SERVER_PASSWORD = jsonObject.getAsJsonObject("ftp").get("password").getAsString();

            Globals.IDENTITY = jsonObject.get("identity").getAsString();

            Globals.VIDEO_FILES_PATH = jsonObject.get("videoFilesPath").getAsString();
            Globals.AUDIO_FILES_PATH = jsonObject.get("audioFilesPath").getAsString();
            Globals.VIDEO_FILES_LENGTH = jsonObject.get("videoFilesLength").getAsLong();
            Globals.AUDIO_FILES_LENGTH = jsonObject.get("audioFilesLength").getAsLong();

            result = true;
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
        return result;
    }

    public static void timeToWork() {
        ControllerRemote.start();

        DeviceCamera.listCameras().stream().forEach((dc) -> {
            int width = 75;
            int height = 75;
            PresenterVideo presenter = new PresenterVideo();
            presenter.setMaximumSize(new Dimension(width, height));
            presenter.setPreferredSize(new Dimension(width, height));
            presenter.setMinimumSize(new Dimension(width, height));
            Globals.directors.add(new DirectorVideo(dc, presenter));
        });
        DeviceMicrophone.listMicrophones().stream().forEach((dm) -> {
            int width = 75;
            int height = 75;
            PresenterAudio presenter = new PresenterAudio();
            presenter.setMaximumSize(new Dimension(width, height));
            presenter.setPreferredSize(new Dimension(width, height));
            presenter.setMinimumSize(new Dimension(width, height));
            Globals.directors.add(new DirectorAudio(dm, presenter));
        });
    }

    private static void timeToRelax() {
        ControllerRemote.stop();

        Globals.transmitters.stream().forEach((t) -> {
            if (t.propertyConnection.get())
                try {
                    t.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
        Globals.transmitters.clear();

        Globals.directors.stream().forEach((d) -> {
            if (d.propertyPresenting().get()) {
                d.stopPresenting();
            }
        });
        Globals.directors.clear();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(Globals.screensController, 900, 600);
        primaryStage.setTitle("Media publisher - ID: " + ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        primaryStage.setScene(scene);
        primaryStage.show();

        Globals.screensController.loadForm(thereAreValidVariables() ? ControllerMain.class : ControllerSettings.class);

    }

}
