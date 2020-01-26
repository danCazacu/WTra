package com.wtra.client.service;

import com.wtra.client.database.PostgresConnection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.wtra.client.controller.LoginController.TOKEN;

public class AuthenticationService {

    private static Connection connection = new PostgresConnection().getConnection();

    public static void invalidateSession(HttpServletResponse httpServletResponse){
        httpServletResponse.addCookie(new Cookie(TOKEN, ""));
    }

    public static boolean validate(String token) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users where session = ?");
        st.setString(1, token);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            LocalDateTime expires = LocalDateTime.parse(rs.getString("expires"));
            if (expires.isBefore(LocalDateTime.now(ZoneId.of("Europe/Bucharest"))))
                return false;
            else
                return true;
        } else {
            return false;
        }
    }

    public static String checkToken(String token, HttpServletResponse httpServletResponse, String thisPage) {
        try {
            if (AuthenticationService.validate(token)) {
                return thisPage;
            } else {
                invalidateSession(httpServletResponse);
                return "index";
            }
        } catch (Exception e) {
            return "index";
        }
    }

    public static boolean register(String username, String email, String password) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("INSERT INTO users(name,email,password) values (?,?,?)");
        st.setString(1, username);
        st.setString(2, email);
        st.setString(3, password);
        int rows = st.executeUpdate();
        return rows > 0;
    }

    public static boolean isAlreadyRegistered(String email) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users where email = ?");
        st.setString(1, email);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

}
