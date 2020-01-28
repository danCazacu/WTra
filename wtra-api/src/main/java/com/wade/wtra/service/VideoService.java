package com.wade.wtra.service;

import com.google.gson.Gson;
import com.wade.wtra.database.PostgresConnection;
import com.wade.wtra.pojo.VideoPOJO;
import javafx.util.Pair;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class VideoService {

    private static Connection connection = new PostgresConnection().getConnection();
    private static String QUERY = "insert into videos (id,name,email) values (?,?,?)";
    public static String EXCEPTION_NOT_READY = "Video not yet processed";
    public static String EXCEPTION_NOT_FOUND = "No video with this id";

    public static long add(String filename, String email) throws Exception {
        connection = new PostgresConnection().getConnection();
        if (connection == null)
            throw new Exception("No database connection");
        long id = System.currentTimeMillis();
        PreparedStatement st = connection.prepareStatement(QUERY);
        st.setString(1, "" + id);
        st.setString(2, filename);
        st.setString(3, email);
        st.executeUpdate();
        new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mock(id, filename, email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return id;
    }

    public static String getProcessed(Long id) throws Exception {
        connection = new PostgresConnection().getConnection();
        if (connection == null)
            throw new Exception("No database connection");
        PreparedStatement st = connection.prepareStatement("SELECT * from videos where id = ?");
        st.setString(1, "" + id);
        ResultSet result = st.executeQuery();
        if (result.next()) {
            String data = result.getString("data");
            if (data == null || data.isEmpty())
                throw new Exception(EXCEPTION_NOT_READY);
            return data;
        }
        throw new Exception(EXCEPTION_NOT_FOUND);
    }

    private static void mock(Long id, String filename, String email) throws Exception {
        int duration = ThreadLocalRandom.current().nextInt(60, 120);
        int signsCount = ThreadLocalRandom.current().nextInt(5, 20);
        double minLeft = 27.459026;
        double maxRight = 27.984794;
        double minDown = 47.111429;
        double maxTop = 47.3811429;
        Map<String, Object> json = new HashMap<>();
        String country = getRandomCountry();
        List<Object> detectedSigns = new ArrayList<>();
        json.put("id", id);
        json.put("name", filename);
        json.put("duration", duration);
        json.put("country", country);
        json.put("signs", detectedSigns);
        double randomX, randomY;
        Object randomSign;
        List<Object> allSignNames = getAllSignsForCountryAndTheirProperties(country);
        for (int i = 0; i < signsCount; i++) {
            Map<String, Object> sign = new HashMap<>();
            randomX = ThreadLocalRandom.current().nextDouble(minLeft, maxRight);
            randomY = ThreadLocalRandom.current().nextDouble(minDown, maxTop);
            randomSign = allSignNames.get(ThreadLocalRandom.current().nextInt(0, allSignNames.size()));
            sign.put("sign", randomSign);
            Map<String, Object> coord = new HashMap<>();
            coord.put("coord_long", randomX);
            coord.put("coord_lat", randomY);
            sign.put("location", coord);
            sign.put("video_timestamp", ThreadLocalRandom.current().nextInt(0, duration));
            detectedSigns.add(sign);
        }

        String data = new Gson().toJson(json);

        connection = new PostgresConnection().getConnection();
        if (connection == null)
            throw new Exception("No database connection");
        PreparedStatement st = connection.prepareStatement("UPDATE videos SET data = ? WHERE id = ?;");
        st.setString(1, data);
        st.setString(2, "" + id);
        st.executeUpdate();
    }

    private static List<String> getAllSignNames() {
        HttpResponse response;
        List<String> signNames = new ArrayList<>();
        try {
            response = StardogService.execute("select ?sign\n" +
                    "WHERE{\n" +
                    "    ?sign rdf:type wtra:Signs .\n" +
                    "}");
            String body = IOUtils.toString(response.getEntity().getContent());
            JSONObject jsonObject = new JSONObject(body);
            JSONArray binding = jsonObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < binding.length(); i++) {
                String name = binding.getJSONObject(i).getJSONObject("sign").getString("value");
                signNames.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signNames;
    }

    private static String getRandomCountry() {
        List<String> countryNames = Arrays.asList(
                "https://github.com/danCazacu/WTra#germany",
                "https://github.com/danCazacu/WTra#finland",
                "https://github.com/danCazacu/WTra#united_kindom",
                "https://github.com/danCazacu/WTra#ireland",
                "https://github.com/danCazacu/WTra#italy",
                "https://github.com/danCazacu/WTra#romania"
        );
        return countryNames.get(ThreadLocalRandom.current().nextInt(0, countryNames.size()));
    }

    private static List<Object> getAllSignsForCountryAndTheirProperties(String country) {
        String justCountry = country.split("#")[1];
        HttpResponse response;
        List<Object> allSignsForThisCountry = new ArrayList<>();
        try {
            response = StardogService.execute("select ?sign\n" +
                    "WHERE{\n" +
                    "    wtra:" + justCountry + " wtra:hasSign ?sign .\n" +
                    "    ?sign rdf:type wtra:Signs .\n" +
                    "}");
            String body = IOUtils.toString(response.getEntity().getContent());
            JSONObject jsonObject = new JSONObject(body);
            JSONArray binding = jsonObject.getJSONObject("results").getJSONArray("bindings");
            for (int i = 0; i < binding.length(); i++) {
                String signName = binding.getJSONObject(i).getJSONObject("sign").getString("value");
                String justSignName = signName.split("#")[1];
                Map<String, Object> signData = new HashMap<>();
                signData.put("name", signName);
                signData.put("moreAt", "/signs/"+justSignName);
                response = StardogService.execute("select ?signProperty ?signPropertyValue\n" +
                        "WHERE{\n" +
                        "    wtra:" + justSignName + " ?signProperty ?signPropertyValue\n" +
                        "}");
                String signPropertiesBody = IOUtils.toString(response.getEntity().getContent());
                JSONObject signPropertiesjsonObject = new JSONObject(signPropertiesBody);
                JSONArray signPropertiesbinding = signPropertiesjsonObject.getJSONObject("results").getJSONArray("bindings");
                List<Object> properties = new ArrayList<>();
                for (int j = 0; j < signPropertiesbinding.length(); j++) {
                    String propertyName = signPropertiesbinding.getJSONObject(j).getJSONObject("signProperty").getString("value");
                    String propertyValue = signPropertiesbinding.getJSONObject(j).getJSONObject("signPropertyValue").getString("value");
                    Map<String, Object> property = new HashMap<>();
                    property.put("property", propertyName);
                    property.put("value", propertyValue);
                    properties.add(property);
                }
                signData.put("properties", properties);
                allSignsForThisCountry.add(signData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return allSignsForThisCountry;
    }

    public static String getEmailByToken(String token) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users where session = ?");
        st.setString(1, token);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getString("email");
        }
        throw new Exception("Not logged in");
    }

    public static List<VideoPOJO> getVideos(String email) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM videos where email = ?");
        st.setString(1, email);
        ResultSet rs = st.executeQuery();
        List<VideoPOJO> videos = new ArrayList<>();
        while(rs.next()){
            videos.add(new VideoPOJO(rs.getString("name"),rs.getString("id")));
        }
        return videos;
    }
}
