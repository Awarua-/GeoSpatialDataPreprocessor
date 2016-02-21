package controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;


public class ConfigLoader {

    private static ConfigLoader configLoader;
    private Gson gson;
    private Config config;
    private String DEFAULT_FILE_PATH = "./";
    private String DEFAULT_FILE_NAME = "config.json";
    private String filePath;

    private ConfigLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static ConfigLoader getInstance() {
        if (configLoader == null) {
            configLoader = new ConfigLoader();
        }
        return configLoader;
    }

    public ConfigLoader getInstance(String filePath) {
        if (configLoader == null) {
            configLoader = new ConfigLoader();
        }
        this.filePath = filePath;
        return configLoader;
    }

    private void loadConfig() {
        String path = DEFAULT_FILE_PATH;
        if (filePath != null) {
            path = filePath;
        }
        try {
            JsonReader reader = new JsonReader(new FileReader(path + DEFAULT_FILE_NAME));
            config = gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            //TODO graceful error handling and feedback
            e.printStackTrace();
        }
    }

    public Config getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }
}
