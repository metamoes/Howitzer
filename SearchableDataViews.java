import java.sql.*;

public class SearchableDataViews {

    // Method to create a view in the database for CVE Description search
    public void createSearchableCVEDescriptionView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_cve_Description AS " +
                "SELECT `CVE Number`, `Status`, `Description`, `References` " +
                "FROM CVE " +
                "WHERE CVE.Description LIKE '%windows%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to create a view in the database for CVE References search
    public void createSearchableCVEReferencesView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_cve_References AS " +
                "SELECT `CVE Number`, `Status`, `Description`, `References` " +
                "FROM CVE " +
                "WHERE CVE.References LIKE '%windows%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to create a view in the database for combined CVE Description and References search
    public void createSearchableCVEDescAndRefView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_cve_DescAndRef AS " +
                "SELECT `CVE Number`, `Status`, `Description`, `References` " +
                "FROM CVE " +
                "WHERE CVE.Description LIKE '%windows 11%' OR CVE.References LIKE '%windows 11%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to create a view in the database for Portlist Description and Port search
    public void createSearchablePortlistDescAndPortView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_Portlist_DescAndPort AS " +
                "SELECT `Port`, `Protocol`, `Description`, `Service_Name` " +
                "FROM Portlist " +
                "WHERE Portlist.Description LIKE '%125%' OR Portlist.Port LIKE '%125%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to create a view in the database for Portlist Description search
    public void createSearchablePortlistDescriptionView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_Portlist_Description AS " +
                "SELECT `Protocol`, `Port`, `Description`, `Service_Name` " +
                "FROM Portlist " +
                "WHERE Portlist.Description LIKE '%Game%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to create a view in the database for Portlist Port search
    public void createSearchablePortlistPortView(Connection conn) throws SQLException {
        String query = "CREATE VIEW searchable_Portlist_Port AS " +
                "SELECT `Port`, `Protocol`, `Description`, `Service_Name` " +
                "FROM Portlist " +
                "WHERE Portlist.Port LIKE '125%'";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
    }

    // Method to export all available information from the views
    public void exportAllViewsData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs;

        rs = stmt.executeQuery("select * from searchable_cve_Description");
        // Process the result set

        rs = stmt.executeQuery("select * from searchable_cve_References");
        // Process the result set

        rs = stmt.executeQuery("select * from searchable_cve_DescAndRef");
        // Process the result set

        rs = stmt.executeQuery("select * from searchable_Portlist_DescAndPort");
        // Process the result set

        rs = stmt.executeQuery("select * from searchable_Portlist_Description");
        // Process the result set

        rs = stmt.executeQuery("select * from searchable_Portlist_Port");
        // Process the result set

        rs = stmt.executeQuery("select * from portlist");
        // Process the result set

        rs = stmt.executeQuery("select * from cve");
        // Process the result set
    }

    public static void main(String[] args) {
        // Establish database connection
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "username", "password");
            SearchableDataViews sdv = new SearchableDataViews();

            // Create views
            sdv.createSearchableCVEDescriptionView(conn);
            sdv.createSearchableCVEReferencesView(conn);
            sdv.createSearchableCVEDescAndRefView(conn);
            sdv.createSearchablePortlistDescAndPortView(conn);
            sdv.createSearchablePortlistDescriptionView(conn);
            sdv.createSearchablePortlistPortView(conn);

            // Export data from views
            sdv.exportAllViewsData(conn);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
