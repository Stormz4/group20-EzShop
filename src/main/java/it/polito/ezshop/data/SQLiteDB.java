package it.polito.ezshop.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class SQLiteDB {
    Connection dbConnection = null;
    private final String dbName = "EZShopDB.db";

    /**
     ** Connect to the DB
     */
    public void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:src/" + this.dbName;

            // create a connection to the database
            this.dbConnection = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.createNewDatabase(this.dbName);
        }
    }

    public void closeConnection() {
        try {
            if (this.dbConnection != null) {
                this.dbConnection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     ** Create the DB
     */
    private void createNewDatabase(String dbName) {
        String url = "jdbc:sqlite:C:src/" + dbName;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        try {
            this.dbConnection = DriverManager.getConnection(url);
            if (this.dbConnection != null) {
                DatabaseMetaData meta = this.dbConnection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer lastInsertRowId() {
        String sql = "SELECT last_insert_rowid() AS id;";
        Integer lastId = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                lastId = rs.getInt("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lastId;
    }

    /**
     ** Create a new Customer table
     ** EZCustomer(Integer id, String customerName, String customerCard, Integer points)
     */
    public void createCustomerTable() {
        // SQL statement for creating a new Customer table
        // TODO: consider having customerCard UNIQUE
        String sql = "CREATE TABLE IF NOT EXISTS customers (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n"
                + " card text,\n"
                + " points integer\n"
                + ");";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Customer records
     */
    public HashMap<Integer, EZCustomer> selectAllCustomers(){
        HashMap<Integer, EZCustomer> customers = new HashMap<>();
        String sql = "SELECT * FROM customers";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                String card = rs.getString("card");
                Integer points = rs.getInt("points");
                customers.put(id, new EZCustomer(id, name, card, points));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customers;
    }

    /**
     ** Insert new Customer record
     */
    public Integer insertCustomer(String customerName, String customerCard, Integer points) {
        String sql = "INSERT INTO customers(name, card, points) VALUES(?,?,?)";
        Integer customerId = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        if (customerName == null)
            customerName = "";

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            pstmt.setString(2, customerCard);
            pstmt.setInt(3, points);
            pstmt.executeUpdate();

            customerId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customerId;
    }

    /**
     ** Delete Customer record with given id
     */
    public void deleteCustomer(Integer id) {
        String sql = "DELETE FROM customers WHERE id=?";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (id == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Update Customer record
     */
    public void updateCustomer(Integer customerId, String customerName, String customerCard, Integer points) {
        String sql = "UPDATE customers\n" +
                     "SET name = ?, card = ?, points = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null || customerId == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            pstmt.setString(2, customerCard);
            pstmt.setInt(3, points);
            pstmt.setInt(4, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
