package com.wtra.client.service;

import com.wtra.client.database.PostgresConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DatabaseService {

    private static Connection connection = new PostgresConnection().getConnection();


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

    public static Map<String, Object> getVideos(String email) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM videos where email = ?");
        st.setString(1, email);
        ResultSet rs = st.executeQuery();
        Map<String,Object> videos = new HashMap<>();
        while(rs.next()){
            videos.put(rs.getString("name"),rs.getString("data"));
        }
        return videos;
    }
}
