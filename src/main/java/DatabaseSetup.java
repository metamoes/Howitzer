import java.sql.*;

public class DatabaseSetup {
    public static void main(String[] args) {
        String url = "jdbc:mariadb://localhost:3306/";
        String dbName = "HowitzerDB";
        String driver = "org.mariadb.jdbc.Driver";
        String userName = "root";
        String password = "";

        try {

            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, userName, password);
            Statement stmt = conn.createStatement();

            // Creating Initial Setup and importing data
            stmt.executeUpdate("DROP DATABASE IF EXISTS HowitzerDB");
            stmt.executeUpdate("CREATE DATABASE HowitzerDB");
            stmt.executeUpdate("USE HowitzerDB");

            stmt.executeUpdate("CREATE TABLE Portlist (" +
                    "`Port` INT," +
                    "`Protocol` VARCHAR(4)," +
                    "`Service_Name` TEXT," +
                    "`Description` TEXT," +
                    "PRIMARY KEY (Port, Protocol))");

            stmt.executeUpdate("CREATE TABLE CVE (" +
                    "`CVE Number` VARCHAR(16) PRIMARY KEY," +
                    "`Status` VARCHAR(10) NOT NULL," +
                    "`Description` TEXT," +
                    "`References` TEXT)");

            stmt.executeUpdate("CREATE TABLE Exploits (" +
                    "`ID` CHAR(14) PRIMARY KEY," +
                    "`File` VARCHAR(255)," +
                    "`Description` TEXT," +
                    "`Author` TEXT," +
                    "`Type` VARCHAR(50)," +
                    "`Platform` VARCHAR(50)," +
                    "`Port` INT," +
                    "`Verified` INT," +
                    "`Code_References` TEXT," +
                    "`tags` TEXT," +
                    "`aliases` TEXT," +
                    "`screenshot_url` TEXT," +
                    "`application_url` TEXT," +
                    "`Source_url` TEXT)");

            stmt.executeUpdate("LOAD DATA INFILE './service-names-port-numbers.csv' " +
                    "INTO TABLE Portlist " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n' " +
                    "IGNORE 1 LINES");

            stmt.executeUpdate("LOAD DATA INFILE './allitems.csv' " +
                    "INTO TABLE CVE " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n' " +
                    "IGNORE 10 LINES");

            stmt.executeUpdate("LOAD DATA INFILE './files_exploits.csv' " +
                    "INTO TABLE Exploits " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n' " +
                    "IGNORE 1 LINES");

            ResultSet resultCVE = stmt.executeQuery("SELECT * FROM CVE");
            while (resultCVE.next()) {
                // Process each row of the result set
            }

            ResultSet resultPortlist = stmt.executeQuery("SELECT * FROM Portlist");
            while (resultPortlist.next()) {
                // Process each row of the result set
            }

            ResultSet resultExploits = stmt.executeQuery("SELECT * FROM Exploits");
            while (resultExploits.next()) {
                // Process each row of the result set
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}