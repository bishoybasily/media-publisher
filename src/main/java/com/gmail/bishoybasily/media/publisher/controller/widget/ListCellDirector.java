/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.controller.widget;

import com.gmail.bishoybasily.media.publisher.device.director.Director;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

/**
 * @author bishoy
 */
public class ListCellDirector extends ListCell<Director> {

    private AnchorPane wrapper;
    private SwingNode swingNode;
    private Button buttonStart, buttonStop;
    private ToggleButton toggleButtonLocalPreview;

    public ListCellDirector() {
        try {
            wrapper = FXMLLoader.load(getClass().getResource("/fxml/widget/ListCellDirector.fxml"));
            swingNode = (SwingNode) wrapper.lookup("#swingNode");
            toggleButtonLocalPreview = (ToggleButton) wrapper.lookup("#toggleButtonLocalPreview");
            buttonStart = (Button) wrapper.lookup("#buttonStart");
            buttonStop = (Button) wrapper.lookup("#buttonStop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Director item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            buttonStart.setOnAction((ActionEvent event) -> {
                if (!item.propertyPresenting().get()) {
                    item.startPresenting();
                }
            });
            buttonStop.setOnAction((ActionEvent event) -> {
                if (item.propertyPresenting().get()) {
                    item.stopPresenting();
                }
            });
            if (item.propertyPresenting().get()) {
                swingNode.setContent(item.getPresenter());
            }
            item.propertyPresenting().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    swingNode.setContent(item.getPresenter());
                } else {
                    swingNode.setContent(null);
                }
            });

            toggleButtonLocalPreview.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                item.setLocalPreview(newValue);
            });
            toggleButtonLocalPreview.setSelected(item.getLocalPreview());

            setGraphic(wrapper);
        }
    }

}
