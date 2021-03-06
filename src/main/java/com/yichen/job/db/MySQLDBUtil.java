package com.yichen.job.db;

// implement JDBC API
public class MySQLDBUtil {
    // import Connectivity & security Info from AWS RDS
    private static final String INSTANCE = "yichen-instance.cahdl1gftf0t.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "githubjob";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "0487561kao";
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";

}
