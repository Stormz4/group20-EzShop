package it.polito.ezshop.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDB {
    Connection dbConnection = null;

    /**
     ** Connect to the DB
     */
    public void connect() {
        String dbName = "EZShopDB.db";
        try {
            // db parameters
            String url = "jdbc:sqlite:src/" + dbName;

            // create a connection to the database
            this.dbConnection = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.createNewDatabase(dbName);
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

    /** TODO: remove points from Customer (now in Card)
     ** Create a new Customers table
     ** EZCustomer(Integer id, String customerName, String customerCard)
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

    /** ---------------------------------------------------------------------------------------------------------------
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
     ** Select totalBalance
     */
    public void selectTotalBalance() {
        String sql = "SELECT SUM(money) as totalBalance\n" +
                     "FROM BalanceOperations;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        double totalBalance = 0;

        try{
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                totalBalance = rs.getDouble("totalBalance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Orders table
     ** EZOrder (Integer orderId, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
     */
    public void createOrdersTable() {
        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Orders (\n"
                + " id integer PRIMARY KEY,\n"
                + " balanceId integer,\n"
                + " productCode text,\n"
                + " pricePerUnit real,\n"
                + " quantity integer,\n"
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


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Users table
     ** EZUser (Integer id, String userName, String password, String role)
     */
    public void createUsersTable() {
        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + " id integer PRIMARY KEY,\n"
                + " userName text,\n"
                + " password text,\n"
                + " role text\n"
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
     ** Select all Users records
     */
    public HashMap<Integer, EZUser> selectAllUsers(){
        HashMap<Integer, EZUser> users = new HashMap<>();
        String sql = "SELECT * FROM Users";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String userName = rs.getString("userName");
                String password = rs.getString("password");
                String role = rs.getString("role");
                users.put(id, new EZUser(id, userName, password, role));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    /**
     ** Insert new User record
     */
    public Integer insertUser(String userName, String password, String role) {
        String sql = "INSERT INTO Users(userName, password, role) VALUES(?,?,?)";
        Integer userId = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();

            userId = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userId;
    }

    /**
     ** Delete User record with given id
     */
    public void deleteUser(Integer id) {
        String sql = "DELETE FROM Users WHERE id=?";

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
     ** Update User record
     */
    public void updateUser(Integer id, String userName, String password, String role) {
        String sql = "UPDATE Users\n" +
                     "SET userName = ?, password = ?, role = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null || id == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Cards table
     ** Card (String cardCode, Integer customerId, Integer points)
     */
    public void createCardsTable() {
        // SQL statement for creating a new Cards table
        String sql = "CREATE TABLE IF NOT EXISTS Cards (\n"
                + " id integer PRIMARY KEY,\n"
                + " customerId integer,\n"
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
     ** Select all Cards records
     */
    public HashMap<String, EZCard> selectAllCards(){
        HashMap<String, EZCard> cards = new HashMap<>();
        String sql = "SELECT * FROM Cards";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String strId = String.format("%10d", id);
                Integer customerId = rs.getInt("customerId");
                Integer points = rs.getInt("points");
                cards.put(strId, new EZCard(strId, customerId, points));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cards;
    }

    /**
     ** Insert new Card record
     */
    public String insertCard(Integer customerId, Integer points) {
        String sql = "INSERT INTO Cards(customerId, points) VALUES(?,?)";
        String cardCode = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, points);
            pstmt.executeUpdate();

            Integer cardId = this.lastInsertRowId();
            cardCode = String.format("%10d", cardId).replace(' ', '0');
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cardCode;
    }

    /**
     ** Delete Card record with given cardCode
     */
    public void deleteCard(String cardCode) {
        String sql = "DELETE FROM Cards WHERE id=?";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (cardCode == null || cardCode.length() == 0)
            return;

        int cardId = Integer.parseInt(cardCode);
        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, cardId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Update Card record
     */
    public void updateCard(String cardCode, Integer customerId, Integer points) {
        String sql = "UPDATE Cards\n" +
                "SET customerId = ?, points = ?\n" +
                "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null || cardCode == null)
            return;

        int cardId = Integer.parseInt(cardCode);
        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, points);
            pstmt.setInt(3, cardId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductTypes table
     ** Card EZProductType (Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit)
     */
    public void createProductTypesTable() {
        // SQL statement for creating a new ProductTypes table
        String sql = "CREATE TABLE IF NOT EXISTS ProductTypes (\n"
                + " id integer PRIMARY KEY,\n"
                + " quantity integer,\n"
                + " location text,\n"
                + " note text,\n"
                + " productDescription text,\n"
                + " barCode text,\n"
                + " pricePerUnit real,\n"
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
     ** Select all ProductTypes records
     */
    public HashMap<Integer, EZProductType> selectAllProductTypes(){
        HashMap<Integer, EZProductType> productTypes = new HashMap<>();
        String sql = "SELECT * FROM ProductTypes";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer quantity = rs.getInt("quantity");
                String location = rs.getString("location");
                String note = rs.getString("note");
                String productDescription = rs.getString("productDescription");
                String barCode = rs.getString("barCode");
                double pricePerUnit = rs.getDouble("pricePerUnit");

                productTypes.put(id, new EZProductType(id, quantity, location, note, productDescription, barCode, pricePerUnit));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return productTypes;
    }

    /**
     ** Insert new ProductType record
     */
    public Integer insertProductType(Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        String sql = "INSERT INTO ProductTypes(quantity, location, note, productDescription, barCode, pricePerUnit) \n"
                   + "VALUES(?,?,?,?,?,?);";
        Integer id = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.executeUpdate();

            id = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    /**
     ** Delete ProductType record with given id
     */
    public void deleteProductType(Integer id) {
        String sql = "DELETE FROM ProductTypes WHERE id=?";

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
     ** Update ProductType record
     */
    public void updateCard(Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        String sql = "UPDATE ProductTypes\n" +
                     "SET quantity = ?, location = ?, note = ?, productDescription = ?, barCode = ?, pricePerUnit = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (id == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new SaleTransactions table
     ** EZSaleTransaction (Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price)
     */
    public void createSaleTransactionsTable() {
        // SQL statement for creating a new SaleTransactions table
        String sql = "CREATE TABLE IF NOT EXISTS SaleTransactions (\n"
                + " id integer PRIMARY KEY,\n"
                + " discountRate real,\n"
                + " price real\n"
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
     ** Select all SaleTransactions records
     */
    public HashMap<Integer, EZSaleTransaction> selectAllSaleTransactions(){
        HashMap<Integer, EZSaleTransaction> saleTransactions = new HashMap<>();
        String sql = "SELECT * FROM SaleTransactions";

        try {
            Statement stmt  = this.dbConnection.createStatement();
            ResultSet rs1    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs1.next()) {
                Integer transactionID = rs1.getInt("id");
                double saleDiscountRate = rs1.getDouble("discountRate");
                double price = rs1.getDouble("price");

                // Internal query to retrieve ticketEntry (productPerSale)
                LinkedList<TicketEntry> entries = new LinkedList<>();
                String sql2 = "SELECT barCode, amount, discountRate, productDescription, pricePerUnit \n"
                            + "FROM ProductsPerSale \n"
                            + "INNER JOIN ProductTypes ON ProductsPerSale.barCode = ProductTypes.barCode \n"
                            + "WHERE ProductsPerSale.id = ? ;";

                PreparedStatement pstmt = this.dbConnection.prepareStatement(sql2);
                pstmt.setInt(1, transactionID);
                ResultSet rs2 = pstmt.executeQuery();

                while (rs2.next()) {
                    String barCode = rs2.getString("barCode");
                    int amount = rs2.getInt("amount");
                    double pDiscountRate = rs2.getDouble("discountRate");
                    String productDescription = rs2.getString("productDescription");
                    double pricePerUnit = rs2.getDouble("pricePerUnit");

                    EZTicketEntry product = new EZTicketEntry(barCode, productDescription, amount, pricePerUnit, pDiscountRate);
                    entries.add(product);
                }

                saleTransactions.put(transactionID, new EZSaleTransaction(transactionID, entries, saleDiscountRate, price));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return saleTransactions;
    }

    /**
     ** Insert new SaleTransaction record
     */
    public Integer insertSaleTransaction(List<TicketEntry> entries, double discountRate, double price) {
        String sql = "INSERT INTO SaleTransactions(discountRate, price) \n"
                   + "VALUES(?,?);";
        Integer transactionID = null;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return null;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();

            transactionID = this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Save on DB all the entries of the sale
        for (TicketEntry entry : entries)
            this.insertProductPerSale(entry.getBarCode(), transactionID, entry.getAmount(), entry.getDiscountRate());

        return transactionID;
    }

    /**
     ** Delete SaleTransaction record with given id
     */
    public void deleteSaleTransaction(Integer transactionID) {
        String sql = "DELETE FROM SaleTransactions WHERE id=?";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (transactionID == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Delete all productPerSale relied to this transaction
        this.deleteAllProductsPerSale(transactionID);
    }

    /**
     ** Update SaleTransaction record
     */
    public void updateSaleTransaction(Integer transactionID, double discountRate, double price) {
        String sql = "UPDATE SaleTransactions\n" +
                     "SET discountRate = ?, price = ?\n" +
                     "WHERE id = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (transactionID == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, transactionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductsPerSale table
     ** EZTicketEntry (String barCode, String productDescription, int amount, double pricePerUnit, double discountRate)
     */
    public void createProductsPerSaleTable() {
        // SQL statement for creating a new ProductsPerSale table
        String sql = "CREATE TABLE IF NOT EXISTS ProductsPerSale (\n"
                   + " barCode text NOT NULL, \n"
                   + " transactionID integer NOT NULL, \n"
                   + " amount integer, \n"
                   + " discountRate real, \n"
                   + "CONSTRAINT PK_ProductPerSale PRIMARY KEY (barCode, transactionID), \n"
                   + "FOREIGN KEY(barCode) REFERENCES ProductTypes(barCode),  \n"
                   + "FOREIGN KEY(transactionID) REFERENCES SaleTransactions(id) \n"
                   + ");";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        try{
            Statement stmt = this.dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("createProductsPerSaleTable" + e.getMessage());
        }
    }

    /**
     ** Insert new ProductPerSale record
     */
    public boolean insertProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        String sql = "INSERT INTO ProductsPerSale(barCode, transactionID, amount, discountRate) \n"
                   + "VALUES(?,?,?,?,?);";
        boolean inserted = false;

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return false;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, barCode);
            pstmt.setInt(2, transactionID);
            pstmt.setInt(3, amount);
            pstmt.setDouble(4, discountRate);
            pstmt.executeUpdate();

            inserted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return inserted;
    }

    /**
     ** Delete ProductPerSale record with given id
     */
    public void deleteProductPerSale(String barCode, Integer transactionID) {
        String sql = "DELETE FROM ProductsPerSale WHERE barCode=? AND transactionID=?";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (transactionID == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setString(1, barCode);
            pstmt.setInt(2, transactionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Delete all ProductsPerSale records related to the sale with given transactionID
     */
    public void deleteAllProductsPerSale(Integer transactionID) {
        String sql = "DELETE FROM ProductsPerSale WHERE transactionID=?";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (transactionID == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Update ProductPerSale record
     */
    public void updateProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        String sql = "UPDATE ProductsPerSale\n" +
                     "SET amount = ?, discountRate = ?\n" +
                     "WHERE barCode = ? AND transactionID = ?;";

        // TODO: Should handle this as an exception?
        if (this.dbConnection == null)
            return;

        if (transactionID == null)
            return;

        try{
            PreparedStatement pstmt = this.dbConnection.prepareStatement(sql);
            pstmt.setInt(1, amount);
            pstmt.setDouble(2, discountRate);
            pstmt.setString(3, barCode);
            pstmt.setInt(4, transactionID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
