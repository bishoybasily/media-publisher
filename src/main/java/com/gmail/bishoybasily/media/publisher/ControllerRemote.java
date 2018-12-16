package com.gmail.bishoybasily.media.publisher;

import com.gmail.bishoybasily.media.publisher.device.Device;
import com.gmail.bishoybasily.media.publisher.device.director.Director;
import com.gmail.bishoybasily.media.publisher.device.transmitter.Transmitter;
import com.gmail.bishoybasily.media.publisher.utility.Globals;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThread;
import com.gmail.bishoybasily.media.publisher.utility.framework.concurrency.NotifyingThreadListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.stream.Collectors;

/**
 * Created by bishoy on 8/13/16.
 */
public class ControllerRemote {

    private static Socket socket;

    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

    private static NotifyingThread worker;
    private static boolean running;
    private static boolean alive;
    //***
    private static File file;

    public static void start() {
        alive = true;
        connect();
    }

    public static void stop() {
        alive = false;
        disconnect();
    }

    private static void connect() {
        running = true;
        worker = new NotifyingThread(() -> {
            try {
                socket = new Socket(Globals.COMMANDS_SERVER_HOST, Globals.COMMANDS_SERVER_PORT);
                socket.setSoTimeout(5000);

                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                new Thread(() -> {
                    //<editor-fold defaultstate="collapsed" desc="comment">
                    while (running) {
                        send("ping");
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }
                    }
                    //</editor-fold>
                }).start();

                JsonObject jsonObject = new JsonObject();
                JsonObject jsonObjectStartup = new JsonObject();
                jsonObjectStartup.addProperty("identity", Globals.IDENTITY);
                jsonObject.add("startup", jsonObjectStartup);
                send(jsonObject.toString());

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    switch (line) {
                        case "ping":
                            send("pong");
                            break;
                        case "pong":
                            break;
                        default:
                            receive(line);
                            break;
                    }
                }
            } catch (Exception e) {
                worker.addListener(new NotifyingThreadListener() {

                    @Override
                    public void afterExecute() {
                        reconnect();
                    }

                    @Override
                    public void beforeExecute() {

                    }
                });
            }
        });
        worker.start();
    }

    private static void disconnect() {
        running = false;
        try {
            socket.getInputStream().close();
        } catch (Exception e) {
        }
        try {
            socket.getOutputStream().close();
        } catch (Exception e) {
        }
        try {
            socket.close();
        } catch (Exception ex) {
        }
    }

    private static void reconnect() {
        if (alive) {
            disconnect();
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {

            }
            connect();
        }
    }

    private synchronized static void send(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            reconnect();
        }

    }

    private synchronized static void receive(String message) {
        Gson gson = new Gson();
        JsonObject jsonObjectRequest = gson.fromJson(message, JsonObject.class);
        switch (jsonObjectRequest.get("request").getAsString()) {
            case "getCamerasDevices":
                getCamerasDevices();
                break;
            case "getCameraFiles":
                getCameraFiles(jsonObjectRequest.get("name").getAsString());
                break;
            case "getMicrophonesDevices":
                getMicrophonesDevices();
                break;
            case "getMicrophoneFiles":
                getMicrophoneFiles(jsonObjectRequest.get("name").getAsString());
                break;
            case "publishDevice":
                publishDevice(jsonObjectRequest.get("name").getAsString(), jsonObjectRequest.get("type").getAsString());
                break;
            case "end":
                end();
                break;
            case "getFile":
                getFile(jsonObjectRequest.get("fileName").getAsString(), jsonObjectRequest.get("deviceName").getAsString(), jsonObjectRequest.get("deviceType").getAsString());
                break;
        }
    }

    private static void getFile(String fileName, String deviceName, String deviceType) {
        NotifyingThread notifyingThread = new NotifyingThread(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                FTPClient ftpc = new FTPClient();
                ftpc.connect(Globals.FTP_SERVER_HOST, Globals.FTP_SERVER_PORT);
                ftpc.login(Globals.FTP_SERVER_NAME, Globals.FTP_SERVER_PASSWORD);
                ftpc.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpc.storeFile(fileName, fileInputStream);
                fileInputStream.close();
                ftpc.logout();
                ftpc.disconnect();
            } catch (Exception e) {
            }
        });
        notifyingThread.addListener(new NotifyingThreadListener() {

            @Override
            public void beforeExecute() {
                switch (deviceType) {
                    case "audio":
                        file = new File(Globals.AUDIO_FILES_PATH + File.separator + deviceName + File.separator + fileName);
                        break;
                    case "video":
                        file = new File(Globals.VIDEO_FILES_PATH + File.separator + deviceName + File.separator + fileName);
                        break;
                }
            }

            @Override
            public void afterExecute() {
                JsonObject jsonObjectResponse = new JsonObject();
                try {
                    jsonObjectResponse.addProperty("status", true);
                    jsonObjectResponse.addProperty("file", "ftp://" + URLEncoder.encode(Globals.FTP_SERVER_NAME, "UTF-8") + ":" + URLEncoder.encode(Globals.FTP_SERVER_PASSWORD, "UTF-8") + "@" + Globals.FTP_SERVER_HOST + ":" + Globals.FTP_SERVER_PORT + File.separator + fileName);
                } catch (Exception ex) {
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("identity", Globals.IDENTITY);
                jsonObject.add("response", jsonObjectResponse);
                jsonObject.addProperty("request", "getFile");
                send(jsonObject.toString());
            }
        });
        notifyingThread.start();
    }

    private static void getCamerasDevices() {
        JsonArray jsonArray = new JsonArray();
        Globals.directors.stream().filter((Director d) -> d.getDevice().getType().equals(Device.Type.VIDEO) && d.propertyPresenting().get()).collect(Collectors.toSet()).stream().forEach((Director d) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", d.getDevice().getName());
            jsonArray.add(jsonObject);
        });
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.add("response", jsonArray);
        jsonObject.addProperty("request", "getCamerasDevices");
        send(jsonObject.toString());
    }

    private static void getMicrophonesDevices() {
        JsonArray jsonArray = new JsonArray();
        Globals.directors.stream().filter((Director d) -> d.getDevice().getType().equals(Device.Type.AUDIO) && d.propertyPresenting().get()).collect(Collectors.toSet()).stream().forEach((Director d) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", d.getDevice().getName());
            jsonArray.add(jsonObject);
        });
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.add("response", jsonArray);
        jsonObject.addProperty("request", "getMicrophonesDevices");
        send(jsonObject.toString());
    }

    private static void getCameraFiles(String name) {
        JsonArray jsonArray = new JsonArray();

        try {
            for (File f : new File(Globals.VIDEO_FILES_PATH + File.separator + name).listFiles()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", f.getName());
                jsonArray.add(jsonObject);
            }
        } catch (Exception e) {
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.add("response", jsonArray);
        jsonObject.addProperty("request", "getCameraFiles");
        send(jsonObject.toString());
    }

    private static void getMicrophoneFiles(String name) {
        JsonArray jsonArray = new JsonArray();

        try {
            for (File f : new File(Globals.AUDIO_FILES_PATH + File.separator + name).listFiles()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", f.getName());
                jsonArray.add(jsonObject);
            }
        } catch (Exception e) {
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.add("response", jsonArray);
        jsonObject.addProperty("request", "getMicrophoneFiles");
        send(jsonObject.toString());
    }

    private static void publishDevice(String name, String type) {
        endTransmitter();
        Director director = Globals.directors.stream().filter((Director d) -> d.getDevice().getName().toLowerCase().equals(name.toLowerCase()) && d.getDevice().getType().toString().toLowerCase().equals(type.toLowerCase())).collect(Collectors.toSet()).iterator().next();

        Transmitter transmitter = director.getTransmitter();

        JsonObject jsonObjectResponse = new JsonObject();

        if (director != null) {
            String file = transmitter.getURL().substring(transmitter.getURL().lastIndexOf(File.separator) + 1), application = transmitter.getURL().substring(0, transmitter.getURL().lastIndexOf(File.separator));
            try {
                Globals.transmitters.add(transmitter);
                jsonObjectResponse.addProperty("status", true);
                jsonObjectResponse.addProperty("file", file);
                jsonObjectResponse.addProperty("application", application);
                transmitter.connect();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (transmitter != null) {
                        transmitter.disconnect();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                jsonObjectResponse.addProperty("status", false);
                jsonObjectResponse.addProperty("reason", "error in connection");
            }
        } else {
            jsonObjectResponse.addProperty("status", false);
            jsonObjectResponse.addProperty("reason", "device not found");
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.add("response", jsonObjectResponse);
        jsonObject.addProperty("request", "publishDevice");
        send(jsonObject.toString());
    }

    private static void end() {
        endTransmitter();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("identity", Globals.IDENTITY);
        jsonObject.addProperty("response", "yes");
        jsonObject.addProperty("request", "end");
        send(jsonObject.toString());
    }

    private static void endTransmitter() {
        Globals.transmitters.stream().forEach((Transmitter transmitter) -> {
            if (transmitter.propertyConnection.get())
                try {
                    transmitter.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        });
        Globals.transmitters.clear();
    }

}
