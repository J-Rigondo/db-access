package com.jun.dbaccess.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.jun.dbaccess.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("conn:{}",connection);

            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
