package org.openjfx.controller.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GetDBConnection {

    public static Connection connectDB(String DBName, String user, String password) throws SQLException, ClassNotFoundException {

        Connection con = null;

        // 反射加载驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/" + DBName + "?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
        con = DriverManager.getConnection(url, user, password);
        return con;
    }
}
