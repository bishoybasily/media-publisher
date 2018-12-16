/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gmail.bishoybasily.media.publisher.device;


import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bishoy
 */
public class DeviceCamera extends Device<Frame, OpenCVFrameGrabber> {

    private final String name;
    private final Type type = Type.VIDEO;
    private final OpenCVFrameGrabber openCVFrameGrabber;
    private boolean working = false;

    public DeviceCamera(String hardwareName, int index) {
        this.name = "camera_" + index;
        this.openCVFrameGrabber = new OpenCVFrameGrabber(index);
    }

    public static List<DeviceCamera> listCameras() {
        List<DeviceCamera> deviceCameras = new ArrayList<>();
        for (int i = 0; i < new File("/dev/").listFiles().length; i++) {
            File file = new File("/dev/").listFiles()[i];
            if (file.getName().toLowerCase().startsWith("video")) {
                deviceCameras.add(new DeviceCamera(file.getAbsolutePath(), Integer.parseInt(file.getName().replaceAll("video", ""))));
            }
        }
        return deviceCameras;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void startWork() {
        if (!working)
            try {
                openCVFrameGrabber.start();
                working = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void stopWork() {
        if (working) {
            try {
                openCVFrameGrabber.stop();
                working = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Frame grapData() throws Exception {
        return openCVFrameGrabber.grab();
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public OpenCVFrameGrabber getCore() {
        return openCVFrameGrabber;
    }
}
