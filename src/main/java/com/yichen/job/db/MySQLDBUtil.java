package com.yichen.job.db;

// implement JDBC API
public class MySQLDBUtil {
    // import Connectivity & security Info from AWS RDS
    private static final String INSTANCE = "";
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "";
    private static final String USERNAME = "";
    private static final String PASSWORD = "";
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";

}
