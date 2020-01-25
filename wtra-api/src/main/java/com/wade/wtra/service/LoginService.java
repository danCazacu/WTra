package com.wade.wtra.service;

import com.wade.wtra.database.PostgresConnection;
import org.apache.tomcat.jni.Local;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Random;

import static com.wade.wtra.controller.LoginController.INVALID_CREDENTIALS;

public class LoginService {

    public static final String SESSION = "session";

    private static Connection connection = new PostgresConnection().getConnection();

    public static String getSessionId(String email, String password) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users where email=? and password=?");
        st.setString(1, email);
        st.setString(2, password);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            String session = rs.getString(SESSION);
            if (session == null || session.isEmpty()) {
                session = getAlphaNumericString(32);
                addSession(session, email);
            } else {
                String expires = rs.getString("expires");
                LocalDateTime timeOfExpiration = LocalDateTime.parse(expires);
                if (timeOfExpiration.isBefore(LocalDateTime.now())) {
                    session = getAlphaNumericString(32);
                    addSession(session, email);
                }
            }
            rs.close();
            st.close();
            return session;
        }
        throw new Exception(INVALID_CREDENTIALS);
    }

    private static void addSession(String session, String email) throws SQLException {
        PreparedStatement st = connection.prepareStatement("UPDATE users SET session = ?,expires = ? WHERE email = ?;");
        st.setString(1, session);
        st.setString(2, LocalDateTime.now().plusMinutes(1).toString());
        st.setString(3, email);
        int result = st.executeUpdate();
        System.out.println("ROWS UPDATED: " + result);
    }

    private static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static boolean validate(String token) throws Exception {
        if (connection == null)
            throw new Exception("No database connection. Try later.");
        PreparedStatement st = connection.prepareStatement("SELECT * FROM users where session = ?");
        st.setString(1, token);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            LocalDateTime expires = LocalDateTime.parse(rs.getString("expires"));
            if(expires.isBefore(LocalDateTime.now()))
                return false;
            else
                return true;
        } else {
            return false;
        }
    }
}
