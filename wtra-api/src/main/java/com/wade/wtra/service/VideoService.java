package com.wade.wtra.service;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class VideoService {

    private static Map<String, Long> videoData = new HashMap<>();
    private static Map<Long, Object> processedVideoData = new HashMap<>();

    public static long add(String filename) {
        long id = System.currentTimeMillis();
        videoData.put(filename, id);
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    mock(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return id;
    }

    public static boolean isProcessed(Long id) {
        return processedVideoData.getOrDefault(id, null) != null;
    }

    public static Object getProcessed(Long id) {
        return processedVideoData.getOrDefault(id, null);
    }

    private static void mock(Long id) throws Exception {
        int duration = 120;
        Map<String, Object> json = new HashMap<>();

//        JSONObject jsonObject = new JSONObject();
        json.put("id", id);
        json.put("name", getNameById(id));
        json.put("duration", duration);
        //TODO COMPLETE MOCK
        processedVideoData.put(id, new Gson().toJson(json));
    }

    public static String getNameById(Long id) throws Exception {
        for (String name : videoData.keySet()) {
            if (videoData.get(name).equals(id))
                return name;
        }
        throw new Exception("There is no name for this id");
    }
}
