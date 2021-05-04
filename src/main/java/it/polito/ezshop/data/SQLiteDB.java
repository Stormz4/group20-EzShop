package it.polito.ezshop.data;

import sun.util.resources.LocaleData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
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
     ** Create a new Customers table
     ** EZCustomer(Integer id, String customerName, String customerCard, Integer points)
     */
    public void createCustomersTable() {
        // SQL statement for creating a new Customer table
        // TODO: consider having customerCard UNIQUE
        String sql = "CREATE TABLE IF NOT EXISTS Customers (\n"
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
     ** Select all Customers records
     */
    public HashMap<Integer, EZCustomer> selectAllCustomers(){
        HashMap<Integer, EZCustomer> customers = new HashMap<>();
        String sql = "SELECT * FROM Customers";

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
        String sql = "INSERT INTO Customers(name, card, points) VALUES(?,?,?)";
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
        String sql = "DELETE FROM Customers WHERE id=?";

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
        String sql = "UPDATE Customers\n" +
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

    /**
     ** Create a new BalanceOperations table
     ** EZBalanceOperation (int balanceId, LocalDate date, double money, String type)
     */
    public void createBalanceOperationsTable() {
        // SQL statement for creating a new BalanceOperations table
        String sql = "CREATE TABLE IF NOT EXISTS BalanceOperations (\n"
                + " id integer PRIMARY KEY,\n"
                + " date text,\n"
                + " money real,\n"
                + " type text\n"
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
     ** Select all BalanceOperations records
     */
    public HashMap<Integer, EZBalanceOperation> selectAllBalanceOperations(){
        HashMap<Integer, EZBalanceOperation> balanceOperations = new HashMap<>();
        String sql = "SELECT * FROM BalanceOperations";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String strDate = rs.getString("date");
                LocalDate date = (strDate != null) ? LocalDate.parse(strDate) : null;
                double money = rs.getDouble("money");
                String type = rs.getString("type");
                balanceOperations.put(id, new EZBalanceOperation(id, date, money, type));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balanceOperations;
    }

    /**
     ** Insert new BalanceOperation record
     */
    public Integer insertBalanceOperation(LocalDate date, double money, String type) {
        String sql = "INSERT INTO BalanceOperations(date, money, type) VALUES(?,?,?)";
        Integer balanceOperationId = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        String strDate = null;
        if (date != null)
            strDate = date.toString();

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, strDate);
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.executeUpdate();

            balanceOperationId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balanceOperationId;
    }

    /**
     ** Delete BalanceOperation record with given id
     */
    public void deleteBalanceOperation(Integer id) {
        String sql = "DELETE FROM BalanceOperations WHERE id=?";

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
     ** Update BalanceOperation record
     */
    public void updateBalanceOperation(Integer id, LocalDate date, double money, String type) {
        String sql = "UPDATE BalanceOperations\n" +
                     "SET date = ?, money = ?, type = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null || id == null)
            return;

        String strDate = null;
        if (date != null)
            strDate = date.toString();

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, strDate);
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Create a new Orders table
     ** EZOrder (Integer orderId, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
     */
    public void createOrdersTable() {
        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Orders (\n"
                + " id integer PRIMARY KEY,\n"
                + " balanceId integer,\n"
                + " productCode text,\n"
                + " pricePerUnit real\n"
                + " quantity integer\n"
                + " status text\n"
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
     ** Select all Orders records
     */
    public HashMap<Integer, EZOrder> selectAllOrders(){
        HashMap<Integer, EZOrder> orders = new HashMap<>();
        String sql = "SELECT * FROM Orders";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer balanceId = rs.getInt("balanceId");
                String productCode = rs.getString("productCode");
                double pricePerUnit = rs.getDouble("pricePerUnit");
                Integer quantity = rs.getInt("quantity");
                String status = rs.getString("status");
                orders.put(id, new EZOrder(id, balanceId, productCode, pricePerUnit, quantity, status));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orders;
    }

    /**
     ** Insert new Order record
     */
    public Integer insertOrder(Integer balanceId, String productCode, double pricePerUnit, int quantity, String status) {
        String sql = "INSERT INTO Orders(balanceId, productCode, pricePerUnit, quantity, status) VALUES(?,?,?,?,?)";
        Integer orderId = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, balanceId);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status);
            pstmt.executeUpdate();

            orderId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderId;
    }

    /**
     ** Delete Order record with given id
     */
    public void deleteOrder(Integer id) {
        String sql = "DELETE FROM Orders WHERE id=?";

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
     ** Update Order record
     */
    public void updateOrder(Integer id, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status) {
        String sql = "UPDATE Orders\n" +
                     "SET balanceId = ?, productCode = ?, pricePerUnit = ?, quantity = ?, status = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null || id == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, balanceId);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
