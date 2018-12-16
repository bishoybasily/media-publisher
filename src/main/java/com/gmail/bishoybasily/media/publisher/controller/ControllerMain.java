/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.controller;


import com.gmail.bishoybasily.media.publisher.Main;
import com.gmail.bishoybasily.media.publisher.controller.widget.ListCellDirector;
import com.gmail.bishoybasily.media.publisher.device.Device;
import com.gmail.bishoybasily.media.publisher.device.director.Director;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.gmail.bishoybasily.media.publisher.utility.framework.forms.ControlledForm;
import com.sun.management.UnixOperatingSystemMXBean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author bishoy
 */
public class ControllerMain implements Initializable, ControlledForm {

    @FXML
    private ListView<Director> listViewCameras, listViewMicrophones;

    @FXML
    private ImageView imageViewSettings;

    @FXML
    private Label labelThreads, labelOpenFiles;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listViewCameras.setCellFactory((ListView<Director> param) -> new ListCellDirector());
        listViewMicrophones.setCellFactory((ListView<Director> param) -> new ListCellDirector());

        imageViewSettings.setOnMouseClicked((e) -> {
            try {
                Globals.screensController.loadForm(ControllerSettings.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        new Thread(() -> {
            while (Globals.screensController.getVisibleScreenClassName() != null && Globals.screensController.getVisibleScreenClassName().equals(this.getClass().getName()) && Globals.screensController.getScene().getWindow().isShowing()) {
                Platform.runLater(() -> {
                    labelThreads.setText("Threads: " + Thread.activeCount());
                    labelOpenFiles.setText("Open files: " + ((UnixOperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getOpenFileDescriptorCount());
                });
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }).start();

        Main.timeToWork();

        listViewCameras.setItems(FXCollections.observableArrayList(Globals.directors.stream().filter((d) -> d.getDevice().getType().equals(Device.Type.VIDEO)).collect(Collectors.toList())));
        listViewMicrophones.setItems(FXCollections.observableArrayList(Globals.directors.stream().filter((d) -> d.getDevice().getType().equals(Device.Type.AUDIO)).collect(Collectors.toList())));


    }


    @Override
    public Parent loadScreen() throws Exception {
        return FXMLLoader.load(getClass().getResource("/fxml/FormMain.fxml"));
    }

}
