package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTableCreation {
    // Run this as Java application to reset db schema.
    public static void main(String[] args) {
        try {
            // This is java.sql.Connection. Not com.mysql.jdbc.Connection.
            Connection conn = null;

            // Step 1 Connect to MySQL.
            try {
                System.out.println("Connecting to " + MySQLDBUtil.URL);
                /*
                 * Call constructor without reference to add this class to registeredDriver's
                 * list; the static block in Driver class runs when JVM loads the Class;
                 */
                Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
                /* A for loop iteration to find the mysql driver class added above */
                conn = DriverManager.getConnection(MySQLDBUtil.URL);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (conn == null) {
                return;
            }

            // Step 2 Drop tables in case there exists
            /*
             * DROP: DROP TABLE IF EXISTS table_name;
             */
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS categories";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS history";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS items";
            stmt.executeUpdate(sql);
            sql = "DROP TABLE IF EXISTS users";
            stmt.executeUpdate(sql);

            // Step 3: Create tables from scratch
            /*
             * CREATE TABLE table_name { column1 datatype column2 datatype ... }
             */
            sql = "CREATE TABLE items " + "(item_id VARCHAR(255) NOT NULL, " + " name VARCHAR(255), " + "rating FLOAT,"
                    + "address VARCHAR(255), " + "image_url VARCHAR(255), " + "url VARCHAR(255), " + "distance FLOAT, "
                    + " PRIMARY KEY ( item_id ))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE categories " + "(item_id VARCHAR(255) NOT NULL, " + " category VARCHAR(255) NOT NULL, "
                    + " PRIMARY KEY ( item_id, category), " + "FOREIGN KEY (item_id) REFERENCES items(item_id))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE users " + "(user_id VARCHAR(255) NOT NULL, " + " password VARCHAR(255) NOT NULL, "
                    + " first_name VARCHAR(255), last_name VARCHAR(255), " + " PRIMARY KEY ( user_id ))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE history " + "(user_id VARCHAR(255) NOT NULL , " + " item_id VARCHAR(255) NOT NULL, "
                    + "last_favor_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + " PRIMARY KEY (user_id, item_id)," + "FOREIGN KEY (item_id) REFERENCES items(item_id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id))";
            stmt.executeUpdate(sql);

            // Step 4: Insert a fake user
            /*
             * INSERT INTO table_name(column 1, column2, column3,...)
             * VALUES(value1,value2,value3)
             */
            sql = "INSERT INTO users VALUES (\"stack42\", \"3229c1097c00d497a0fd282d586be050\", \"Yijing\", \"Zhou\")";
            stmt.executeUpdate(sql);

            System.out.println("Import is done successfully.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
