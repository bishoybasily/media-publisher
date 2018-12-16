package com.gmail.bishoybasily.media.publisher.utility.framework.forms;

import javafx.scene.layout.StackPane;

public class FormsController extends StackPane {

    private String visibleFormClassName;

    public void loadForm(Class<? extends ControlledForm> c) throws Exception {
        getChildren().setAll(c.getConstructor().newInstance().loadScreen());
        visibleFormClassName = c.getName();

    }

    /**
     * @return the visibleScreenClassName
     */
    public String getVisibleScreenClassName() {
        return visibleFormClassName;
    }

}

