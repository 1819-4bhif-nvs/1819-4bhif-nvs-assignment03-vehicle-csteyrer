package at.htl.vehicle;

import java.sql.*;
import org.junit.*;

import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VehicleTest {

    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    public static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db";
    public static final String USER = "app";
    public static final String PASSWORD = "app";
    public static Connection conn;

    @BeforeClass
    public static void initJDBC(){
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Verbindung zur Datenbank nicht möglich\n" + e.getMessage() + "\n");
            System.exit(1);
        }

        try {
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE vehicle (" +
                    "id INT CONSTRAINT vehicle_pk PRIMARY KEY," +
                    "brand VARCHAR(255) NOT NULL," +
                    "type VARCHAR(255) NOT NULL" +
                    ")";

            stmt.execute(sql);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @AfterClass
    public static void teardownJDBC(){

        try{
            conn.createStatement().execute("DROP TABLE vehicle");
            System.out.println("Tabelle VEHICLE geöscht");
        } catch (SQLException e) {
            System.out.println("Tabelle VEHICLE konnte nicht gelöscht werden:\n"
            + e.getMessage());
        }


        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
                System.out.println("Good bye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void firstTest(){

        fail();
    }


    @Test
    public void dml() {

        //Daten einfügen
        int countInserts = 0;
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO vehicle (id, brand, type) VALUES (1, 'Opel','Commodore')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (2, 'Opel','Kapitän')";
            countInserts += stmt.executeUpdate(sql);
            sql = "INSERT INTO vehicle (id, brand, type) VALUES (3, 'Opel','Kadett')";
            countInserts += stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        assertThat(countInserts, is(3));

        //Daten abfragen
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, brand, type FROM vehicle");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kadett"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
