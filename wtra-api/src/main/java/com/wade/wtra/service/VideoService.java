package com.wade.wtra.service;

import com.google.gson.Gson;
import com.wade.wtra.database.PostgresConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VideoService {

    private static Connection connection = new PostgresConnection().getConnection();
    private static String QUERY = "insert into videos (id,name,email) values (?,?,?)";
    public static String EXCEPTION_NOT_READY = "Video not yet processed";
    public static String EXCEPTION_NOT_FOUND = "No video with this id";

    public static long add(String filename, String email) throws SQLException {
        long id = System.currentTimeMillis();
        PreparedStatement st = connection.prepareStatement(QUERY);
        st.setString(1,""+id);
        st.setString(2,filename);
        st.setString(3,email);
        st.executeUpdate();
        new Thread(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mock(id,filename,email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return id;
    }

    public static String getProcessed(Long id) throws Exception {
        PreparedStatement st = connection.prepareStatement("SELECT * from videos where id = ?");
        st.setString(1,""+id);
        ResultSet result = st.executeQuery();
        if(result.next()){
            String data = result.getString("data");
            if(data == null || data.isEmpty())
                throw new Exception(EXCEPTION_NOT_READY);
            return data;
        }
        throw new Exception(EXCEPTION_NOT_FOUND);
    }

    private static void mock(Long id, String filename, String email) throws Exception {
        int duration = 120;
        Map<String, Object> json = new HashMap<>();
        json.put("id", id);
        json.put("name", filename);
        json.put("duration", duration);
        //TODO COMPLETE MOCK
        String data = new Gson().toJson(json);

        PreparedStatement st = connection.prepareStatement("UPDATE videos SET data = ? WHERE id = ?;");
        st.setString(1,data);
        st.setString(2,""+id);
        st.executeUpdate();
    }
}
