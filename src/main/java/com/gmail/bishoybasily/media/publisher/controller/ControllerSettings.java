/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.controller;


import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.gmail.bishoybasily.media.publisher.utility.framework.forms.ControlledForm;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * FXML Controller class
 *
 * @author bishoy
 */
public class ControllerSettings implements Initializable, ControlledForm {

    @FXML
    private ImageView imageViewDevices;
    @FXML
    private Button buttonSave;
    @FXML
    private TextField textFieldCommandsServerHost, textFieldCommandsServerPort,
            textFieldRTSPServerHost, textFieldRTSPServerPort, textFieldRTSPServerApplication,
            textFieldFTPServerHost, textFieldFTPServerPort, textFieldFTPServerName, textFieldFTPServerPassword,
            textFieldIdentity,
            textFieldVideoFilesPath,
            textFieldVideoFilesLength,
            textFieldAudioFilesPath,
            textFieldAudioFilesLength;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        imageViewDevices.setOnMouseClicked((event) -> {
            try {
                Globals.screensController.loadForm(ControllerMain.class);
            } catch (Exception exception) {
                // exception.printStackTrace();
            }
        });

        try {
            JsonObject jsonObject = new Gson().fromJson(new Scanner(new File(Globals.CONFIGURATION_FILE_PATH)).useDelimiter("\\Z").next(), JsonObject.class);

            textFieldCommandsServerHost.setText(Globals.COMMANDS_SERVER_HOST = jsonObject.getAsJsonObject("commands").get("host").getAsString());
            textFieldCommandsServerPort.setText(String.valueOf(Globals.COMMANDS_SERVER_PORT = jsonObject.getAsJsonObject("commands").get("port").getAsInt()));

            textFieldRTSPServerHost.setText(Globals.RTMP_SERVER_HOST = jsonObject.getAsJsonObject("rtsp").get("host").getAsString());
            textFieldRTSPServerPort.setText(String.valueOf(Globals.RTMP_SERVER_PORT = jsonObject.getAsJsonObject("rtsp").get("port").getAsInt()));
            textFieldRTSPServerApplication.setText(Globals.RTMP_SERVER_APPLICATION = jsonObject.getAsJsonObject("rtsp").get("application").getAsString());

            textFieldFTPServerHost.setText(Globals.FTP_SERVER_HOST = jsonObject.getAsJsonObject("ftp").get("host").getAsString());
            textFieldFTPServerPort.setText(String.valueOf(Globals.FTP_SERVER_PORT = jsonObject.getAsJsonObject("ftp").get("port").getAsInt()));
            textFieldFTPServerName.setText(Globals.FTP_SERVER_NAME = jsonObject.getAsJsonObject("ftp").get("name").getAsString());
            textFieldFTPServerPassword.setText(Globals.FTP_SERVER_PASSWORD = jsonObject.getAsJsonObject("ftp").get("password").getAsString());

            textFieldIdentity.setText(Globals.IDENTITY = jsonObject.get("identity").getAsString());

            textFieldVideoFilesPath.setText(Globals.VIDEO_FILES_PATH = jsonObject.get("videoFilesPath").getAsString());
            textFieldAudioFilesPath.setText(Globals.AUDIO_FILES_PATH = jsonObject.get("audioFilesPath").getAsString());
            textFieldVideoFilesLength.setText(String.valueOf(Globals.VIDEO_FILES_LENGTH = jsonObject.get("videoFilesLength").getAsLong()));
            textFieldAudioFilesLength.setText(String.valueOf(Globals.AUDIO_FILES_LENGTH = jsonObject.get("audioFilesLength").getAsLong()));

        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        textFieldVideoFilesPath.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                try {
                    textFieldVideoFilesPath.setText(new DirectoryChooser().showDialog(Globals.screensController.getScene().getWindow()).getAbsolutePath());
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
            }
        });

        textFieldAudioFilesPath.setOnMouseClicked((e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                try {
                    textFieldAudioFilesPath.setText(new DirectoryChooser().showDialog(Globals.screensController.getScene().getWindow()).getAbsolutePath());
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
            }
        });

        buttonSave.setOnAction((e) -> {
            JsonObject jsonObject = new JsonObject();

            JsonObject jsonObjectCommands = new JsonObject();
            jsonObjectCommands.addProperty("host", textFieldCommandsServerHost.getText());
            jsonObjectCommands.addProperty("port", Integer.valueOf(textFieldCommandsServerPort.getText()));

            JsonObject jsonObjectRTSP = new JsonObject();
            jsonObjectRTSP.addProperty("host", textFieldRTSPServerHost.getText());
            jsonObjectRTSP.addProperty("port", Integer.valueOf(textFieldRTSPServerPort.getText()));
            jsonObjectRTSP.addProperty("application", textFieldRTSPServerApplication.getText());

            JsonObject jsonObjectFTP = new JsonObject();
            jsonObjectFTP.addProperty("host", textFieldFTPServerHost.getText());
            jsonObjectFTP.addProperty("port", Integer.valueOf(textFieldFTPServerPort.getText()));
            jsonObjectFTP.addProperty("name", textFieldFTPServerName.getText());
            jsonObjectFTP.addProperty("password", textFieldFTPServerPassword.getText());

            jsonObject.addProperty("identity", textFieldIdentity.getText());

            jsonObject.addProperty("audioFilesPath", textFieldAudioFilesPath.getText());
            jsonObject.addProperty("videoFilesPath", textFieldVideoFilesPath.getText());
            jsonObject.addProperty("audioFilesLength", Long.valueOf(textFieldVideoFilesLength.getText()));
            jsonObject.addProperty("videoFilesLength", Long.valueOf(textFieldAudioFilesLength.getText()));

            jsonObject.add("commands", jsonObjectCommands);
            jsonObject.add("rtsp", jsonObjectRTSP);
            jsonObject.add("ftp", jsonObjectFTP);

            File file = new File(Globals.CONFIGURATION_FILE_PATH);
            new File(file.getParent()).mkdirs();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(jsonObject.toString());
                fileWriter.flush();
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        });
    }

    @Override
    public Parent loadScreen() throws Exception {
        return FXMLLoader.load(getClass().getResource("/fxml/FormSettings.fxml"));
    }

}
