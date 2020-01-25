package com.wade.wtra.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    private static String RDS_HOSTNAME = "wtra.c140oi0f34za.eu-west-1.rds.amazonaws.com";
    private static String RDS_DB_NAME = "wtra";
    private static String RDS_USERNAME = "postgres";
    private static String RDS_PASSWORD = "Mamaaremere1943";
    private static String RDS_PORT = "5432";
    private Connection connection;

    public PostgresConnection() {
        this.connection = getRemoteConnection();
    }

    private Connection getRemoteConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            String jdbcUrl = "jdbc:postgresql://" + RDS_HOSTNAME + ":" + RDS_PORT + "/" + RDS_DB_NAME + "?user=" + RDS_USERNAME + "&password=" + RDS_PASSWORD;
            Connection con = DriverManager.getConnection(jdbcUrl);
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        int retries = 0;
        while (connection == null && retries <= 5) {
            this.connection = getRemoteConnection();
            retries++;
        }
        return connection;
    }
}