package it.polito.ezshop.data;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDB {
    static final String JDBC_DB_NAME = "EZShopDB.db";
    static final String JDBC_DB_URL = "jdbc:sqlite:src/" + JDBC_DB_NAME;
    static final long MAX_CARDS = 9999999999L;
    public static final int defaultID = -1;
    static final int defaultValue = 0;
    private static final int INTEGER = 4; // see https://docs.oracle.com/javase/8/docs/api/constant-values.html#java.sql.Types.INTEGER

    // Define tables names
    public static final String tBalanceOperations = "BalanceOperations";
    public static final String tCards = "Cards";
    public static final String tCustomers = "Customers";
    public static final String tOrders = "Orders";
    public static final String tProductsPerSale = "ProductsPerSale";
    public static final String tProductTypes = "ProductTypes";
    public static final String tTransactions = "Transactions";
    public static final String tUsers = "Users";
    public static final String tProducts = "Products";
    public static final String[] tables =  new String [] {tBalanceOperations, tCards, tCustomers, tOrders, tProductsPerSale,
                                                          tProductsPerSale, tProductTypes, tTransactions, tUsers, tProducts};

    static private Connection dbConnection = null;

    /**
     ** Connect to the DB
     */
    public boolean connect() {
        if (dbConnection != null)
            return true;

        try {
            // create a connection to the database
            dbConnection = DriverManager.getConnection(JDBC_DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.createNewDatabase();
        }

        return dbConnection != null;
    }

    public boolean isConnected() {
        if (dbConnection != null) {
            try {
                return dbConnection.isValid(1);
            }
            catch (SQLException e) {
                return false;
            }
        }

        return false;
    }

    public void closeConnection() {
        try {
            if (dbConnection != null) {
                dbConnection.close();
                dbConnection = null;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     ** Create the DB
     */
    private void createNewDatabase() {
        if (dbConnection == null)
            return;

        try {
            dbConnection = DriverManager.getConnection(JDBC_DB_URL);
            if (dbConnection != null) {
                DatabaseMetaData meta = dbConnection.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Initialize DB
     */
    public void initDatabase() {
        // Create tables if they do not exist already
        this.createBalanceOperationsTable();
        this.createCardsTable();
        this.createCustomersTable();
        this.createOrdersTable();
        this.createProductsPerSaleTable();
        this.createProductTypesTable();
        this.createTransactionsTable();
        this.createUsersTable();
        this.createProductsTable();
    }

    public boolean clearDatabase() {
        if (!this.isConnected())
            return false;

        boolean cleared = this.clearTable(SQLiteDB.tBalanceOperations);
        cleared &= this.clearTable(SQLiteDB.tCards);
        cleared &= this.clearTable(SQLiteDB.tCustomers);
        cleared &= this.clearTable(SQLiteDB.tOrders);
        cleared &= this.clearTable(SQLiteDB.tProductTypes);
        cleared &= this.clearTable(SQLiteDB.tProductsPerSale);
        cleared &= this.clearTable(SQLiteDB.tTransactions);
        cleared &= this.clearTable(SQLiteDB.tUsers);
        cleared &= this.clearTable(SQLiteDB.tProducts);

        return cleared;
  }

  public boolean clearTable(String tableName) {

        // Validate tableName, in order to prevent SQL injections
        boolean isValidTable = false;
        for (String tName : SQLiteDB.tables)
            isValidTable |= tableName.equals(tName);

        if (!isValidTable)
            return false;

        String sql =  "DELETE FROM " + tableName + " ;";

        try{
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
  }

    /**
     ** Returns the id of the last inserted row, no matter the table
     */
    private long lastInsertRowId() {
        if (dbConnection == null)
            return defaultID;

        String sql = "SELECT last_insert_rowid() AS id;";
        long lastId = defaultID;

        try{
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                lastId = rs.getLong("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return lastId;
    }

    /**
     ** Create a new Customers table
     ** EZCustomer(Integer id, String customerName, String customerCard)
     */
    private void createCustomersTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Customer table
        String sql = "CREATE TABLE IF NOT EXISTS Customers (\n"
                   + " id integer PRIMARY KEY,\n"
                   + " name text NOT NULL UNIQUE,\n"
                   + " card integer UNIQUE,\n"
                   + "FOREIGN KEY(card) REFERENCES Cards(id)\n"
                   + ");";

        try{
            Statement stmt = dbConnection.createStatement();
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
        String sql = "SELECT Customers.id, name, card, Cards.points \n"
                   + "FROM Customers \n"
                   + "LEFT JOIN Cards ON Customers.card = Cards.id;";

        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String name = rs.getString("name");
                Integer card = rs.getInt("card");
                String cardCode = card != 0 ? String.format("%10d", card).replace(' ', '0') : "";
                Integer points = rs.getInt("points");
                customers.put(id, new EZCustomer(id, name, cardCode, points));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customers;
    }

    /**
     ** Insert new Customer record
     */
    public Integer insertCustomer(String customerName, String customerCard) {
        if (dbConnection == null || customerName == null || customerCard == null)
            return defaultID;

        String sql = "INSERT INTO Customers(name, card) VALUES(?,?)";
        int customerId = defaultID;

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            if (customerCard.isEmpty())
                pstmt.setNull(2, INTEGER);
            else
                pstmt.setInt(2, Integer.parseInt(customerCard));
            pstmt.executeUpdate();

            customerId = (int)this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return customerId;
    }

    /**
     ** Delete Customer record with given id
     */
    public boolean deleteCustomer(Integer id) {
        if (dbConnection == null || id == null)
            return false;

        String sql = "DELETE FROM Customers WHERE id=?";
        boolean deleted = false;

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Customer record
     */
    public boolean updateCustomer(Integer customerId, String customerName, String customerCard) {
        if (dbConnection == null || customerId == null || customerName == null || customerCard == null)
            return false;

        if (customerName.isEmpty())
            return false;

        boolean updated = false;
        String sql = "UPDATE Customers\n"
                   + "SET name = ?, card = ?\n"
                   + "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, customerName);
            if (customerCard.isEmpty())
                pstmt.setNull(2, INTEGER);
            else
                pstmt.setInt(2, Integer.parseInt(customerCard));
            pstmt.setInt(3, customerId);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new BalanceOperations table
     ** EZBalanceOperation (int balanceId, LocalDate date, double money, String type)
     */
    private void createBalanceOperationsTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new BalanceOperations table
        String sql = "CREATE TABLE IF NOT EXISTS BalanceOperations (\n"
                + " id integer PRIMARY KEY,\n"
                + " date text,\n"
                + " money real,\n"
                + " type text\n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
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
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                int id = rs.getInt("id");
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
        if (dbConnection == null || date == null || type == null)
            return defaultID;

        String sql = "INSERT INTO BalanceOperations(date, money, type) VALUES(?,?,?)";
        int balanceOperationId = defaultID;

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, date.toString());
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.executeUpdate();

            balanceOperationId = (int)this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balanceOperationId;
    }

    /**
     ** Delete BalanceOperation record with given id
     */
    public boolean deleteBalanceOperation(Integer id) {
        if (dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM BalanceOperations WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update BalanceOperation record
     */
    public boolean updateBalanceOperation(Integer id, LocalDate date, double money, String type) {
        if (dbConnection == null || id == null || date == null || type == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE BalanceOperations\n" +
                     "SET date = ?, money = ?, type = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, date.toString());
            pstmt.setDouble(2, money);
            pstmt.setString(3, type);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /**
     ** Select totalBalance
     */
    public double selectTotalBalance() {
        String sql = "SELECT SUM(money) as totalBalance\n" +
                     "FROM BalanceOperations;";

        if (dbConnection == null)
            return defaultValue;

        double totalBalance = 0;

        try{
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            if (rs.next())
                totalBalance = rs.getDouble("totalBalance");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalBalance;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Orders table
     ** EZOrder (Integer orderId, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status)
     */
    private void createOrdersTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Orders (\n"
                + " id integer PRIMARY KEY,\n"
                + " balanceId integer,\n"
                + " productCode text,\n"
                + " pricePerUnit real,\n"
                + " quantity integer,\n"
                + " status text,\n"
                + "FOREIGN KEY(productCode) REFERENCES ProductTypes(barCode),  \n"
                + "FOREIGN KEY(balanceId) REFERENCES BalanceOperations(id) \n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
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
            Statement stmt  = dbConnection.createStatement();
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
        if (dbConnection == null || balanceId == null || productCode == null || status == null)
            return defaultID;

        String sql = "INSERT INTO Orders(balanceId, productCode, pricePerUnit, quantity, status) VALUES(?,?,?,?,?)";
        int orderId = defaultID;

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            if (balanceId > 0)
                pstmt.setInt(1, balanceId);
            else
                pstmt.setNull(1, INTEGER);

            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status);
            pstmt.executeUpdate();

            orderId = (int)this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return orderId;
    }

    /**
     ** Delete Order record with given id
     */
    public boolean deleteOrder(Integer id) {
        if (dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Orders WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Order record
     */
    public boolean updateOrder(Integer id, Integer balanceId, String productCode, double pricePerUnit, int quantity, String status) {
        if (dbConnection == null || id == null || balanceId == null || productCode == null || status == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Orders\n" +
                     "SET balanceId = ?, productCode = ?, pricePerUnit = ?, quantity = ?, status = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, balanceId);
            pstmt.setString(2, productCode);
            pstmt.setDouble(3, pricePerUnit);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, status);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Users table
     ** EZUser (Integer id, String userName, String password, String role)
     */
    private void createUsersTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + " id integer PRIMARY KEY,\n"
                + " userName text NOT NULL UNIQUE,\n"
                + " password text NOT NULL,\n"
                + " role text NOT NULL\n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
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
            Statement stmt  = dbConnection.createStatement();
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
        if (dbConnection == null || userName == null || password == null || role == null)
            return defaultID;

        int userId = defaultID;
        String sql = "INSERT INTO Users(userName, password, role) VALUES(?,?,?)";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();

            userId = (int)this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userId;
    }

    /**
     ** Delete User record with given id
     */
    public boolean deleteUser(Integer id) {
        if (dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Users WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update User record
     */
    public boolean updateUser(Integer id, String userName, String password, String role) {
        if (dbConnection == null || id == null || userName == null || password == null || role == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Users\n" +
                     "SET userName = ?, password = ?, role = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Cards table
     ** Card (String cardCode, Integer customerId, Integer points)
     */
    private void createCardsTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Cards table
        String sql = "CREATE TABLE IF NOT EXISTS Cards (\n"
                + " id BIGINT PRIMARY KEY,\n"
                + " points integer\n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Select all Cards records
     */
    public HashMap<String, Integer> selectAllCards(){
        HashMap<String, Integer> cards = new HashMap<>();
        String sql = "SELECT * FROM Cards";

        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Long id = rs.getLong("id");
                String strId = String.format("%10d", id).replace(' ', '0');
                Integer points = rs.getInt("points");
                cards.put(strId, points);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cards;
    }

    /**
     ** Insert new Card record
     */
    public String insertCard(Integer points) {
        if (dbConnection == null || points == null)
            return "";

        String sqlCount = "SELECT COUNT(*) AS cardsN FROM Cards;";
        long cardsN = 0;
        boolean counted = false;
        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sqlCount);

            if (rs.next())
                cardsN = rs.getLong("cardsN");
            counted = true;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        if (!counted || cardsN >= MAX_CARDS)
            return "";

        String sql = "INSERT INTO Cards(id, points) VALUES(?,?)";
        String cardCode = "";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setLong(1, cardsN + 1);
            pstmt.setInt(2, points);
            pstmt.executeUpdate();

            Long cardId = this.lastInsertRowId();
            cardCode = String.format("%10d", cardId).replace(' ', '0');
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cardCode;
    }

    /**
     ** Delete Card record with given cardCode
     */
    public boolean deleteCard(String cardCode) {
        if (dbConnection == null || cardCode == null || cardCode.isEmpty())
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Cards WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(cardCode));
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update Card record
     */
    public boolean updateCard(String cardCode, Integer points) {
        if (dbConnection == null || cardCode == null || points == null)
            return false;

        if (cardCode.isEmpty())
            return false;

        boolean deleted = false;
        int cardId = Integer.parseInt(cardCode);
        String sql = "UPDATE Cards\n" +
                "SET points = ?\n" +
                "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, points);
            pstmt.setInt(2, cardId);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductTypes table
     ** Card EZProductType (Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit)
     */
    private void createProductTypesTable() {
        // SQL statement for creating a new ProductTypes table
        String sql = "CREATE TABLE IF NOT EXISTS ProductTypes (\n"
                + " id integer PRIMARY KEY,\n"
                + " quantity integer,\n"
                + " location text,\n"
                + " note text,\n"
                + " productDescription text,\n"
                + " barCode text UNIQUE,\n"
                + " pricePerUnit real\n"
                + ");";

        if (dbConnection == null)
            return;

        try{
            Statement stmt = dbConnection.createStatement();
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
            Statement stmt  = dbConnection.createStatement();
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
        if (dbConnection == null)
            return defaultID;

        if (quantity == null || location == null || note == null || productDescription == null || barCode == null)
            return defaultID;

        int id = defaultID;
        String sql = "INSERT INTO ProductTypes(quantity, location, note, productDescription, barCode, pricePerUnit) \n"
                   + "VALUES(?,?,?,?,?,?);";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.executeUpdate();

            id = (int)this.lastInsertRowId();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    /**
     ** Delete ProductType record with given id
     */
    public boolean deleteProductType(Integer id) {
        if (dbConnection == null || id == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductTypes WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update ProductType record
     */
    public boolean updateProductType(Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        if (dbConnection == null)
            return false;

        if (id == null || quantity == null || location == null || productDescription == null || barCode == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE ProductTypes\n" +
                     "SET quantity = ?, location = ?, note = ?, productDescription = ?, barCode = ?, pricePerUnit = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setString(2, location);
            pstmt.setString(3, note);
            pstmt.setString(4, productDescription);
            pstmt.setString(5, barCode);
            pstmt.setDouble(6, pricePerUnit);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Transactions table
     ** EZSaleTransaction (Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price)
     */
    private void createTransactionsTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Transactions table
        String sql = "CREATE TABLE IF NOT EXISTS Transactions (\n"
                + " id integer PRIMARY KEY,\n"
                + " discountRate real,\n"
                + " price real,\n"
                + " saleID integer,\n"
                + " status text\n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
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
        String sql = "SELECT id, discountRate, price, status \n"
                   + "FROM Transactions \n"
                   + "WHERE saleID IS NULL \n"
                   + "  AND status <> \"OPENED\";";

        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs1    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs1.next()) {
                Integer transactionID = rs1.getInt("id");
                double saleDiscountRate = rs1.getDouble("discountRate");
                double price = rs1.getDouble("price");
                String status = rs1.getString("status");

                // Internal query to retrieve ticketEntry (productPerSale)
                LinkedList<TicketEntry> entries = new LinkedList<>();
                String sql2 = "SELECT ProductsPerSale.barCode, amount, discountRate, productDescription, pricePerUnit \n"
                            + "FROM ProductsPerSale \n"
                            + "INNER JOIN ProductTypes ON ProductsPerSale.barCode = ProductTypes.barCode \n"
                            + "WHERE ProductsPerSale.transactionID = ? ;";

                PreparedStatement pstmt = dbConnection.prepareStatement(sql2);
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

                saleTransactions.put(transactionID, new EZSaleTransaction(transactionID, entries, saleDiscountRate, price, status));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return saleTransactions;
    }

    /**
     ** Select all ReturnTransactions records
     */
    public HashMap<Integer, EZReturnTransaction> selectAllReturnTransactions(){
        HashMap<Integer, EZReturnTransaction> returnTransactions = new HashMap<>();
        String sql = "SELECT id, price, saleID, status \n"
                   + "FROM Transactions \n"
                   + "WHERE saleID IS NOT NULL \n"
                   + "  AND status <> \"OPENED\";";

        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs1    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs1.next()) {
                Integer transactionID = rs1.getInt("id");
                double price = rs1.getDouble("price");
                int saleID = rs1.getInt("saleID");
                String status = rs1.getString("status");

                // Internal query to retrieve ticketEntry (productPerSale)
                LinkedList<TicketEntry> entries = new LinkedList<>();
                String sql2 = "SELECT ProductsPerSale.barCode, amount, discountRate, productDescription, pricePerUnit \n"
                        + "FROM ProductsPerSale \n"
                        + "INNER JOIN ProductTypes ON ProductsPerSale.barCode = ProductTypes.barCode \n"
                        + "WHERE ProductsPerSale.transactionID = ? ;";

                PreparedStatement pstmt = dbConnection.prepareStatement(sql2);
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

                returnTransactions.put(transactionID, new EZReturnTransaction(transactionID, saleID, entries, price, status));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return returnTransactions;
    }

    /**
     ** Insert new SaleTransaction record
     */
    public Integer insertSaleTransaction(List<TicketEntry> entries, double discountRate, double price, String status) {
        if (dbConnection == null || status == null)
            return defaultID;

        int transactionID = defaultID;
        String sql = "INSERT INTO Transactions(discountRate, price, status) \n"
                   + "VALUES(?,?,?);";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.setString(3, status);
            pstmt.executeUpdate();

            transactionID = (int)this.lastInsertRowId();

            // Save in DB all the entries of the sale
            if (entries != null && !entries.isEmpty()) {
                for (TicketEntry entry : entries)
                    this.insertProductPerSale(entry.getBarCode(), transactionID, entry.getAmount(), entry.getDiscountRate());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transactionID;
    }

    /**
     ** Insert new SaleTransaction record
     */
    public Integer insertReturnTransaction(List<TicketEntry> entries, int saleID, double price, String status) {
        if (dbConnection == null || status == null)
            return defaultID;

        int transactionID = defaultID;
        String sql = "INSERT INTO Transactions(saleID, price, status) \n"
                   + "VALUES(?,?,?);";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, saleID);
            pstmt.setDouble(2, price);
            pstmt.setString(3, status);
            pstmt.executeUpdate();

            transactionID = (int)this.lastInsertRowId();

            // Save in DB all the entries of the sale
            if (entries != null && !entries.isEmpty()) {
                for (TicketEntry entry : entries)
                    this.insertProductPerSale(entry.getBarCode(), transactionID, entry.getAmount(), entry.getDiscountRate());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return transactionID;
    }

    /**
     ** Delete Transaction record with given id
     */
    public boolean deleteTransaction(Integer transactionID) {
        if (dbConnection == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Transactions WHERE id=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
            deleted = true;

            // Delete all productPerSale relied to this transaction
            this.deleteAllProductsPerSale(transactionID);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update SaleTransaction record
     */
    public boolean updateSaleTransaction(Integer transactionID, double discountRate, double price, String status) {
        if (dbConnection == null || transactionID == null || status == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Transactions\n" +
                     "SET discountRate = ?, price = ?, status = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, discountRate);
            pstmt.setDouble(2, price);
            pstmt.setString(3, status);
            pstmt.setInt(4, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }

    /**
     ** Update SaleTransaction record
     */
    public boolean updateReturnTransaction(Integer transactionID, double price, String status) {
        if (dbConnection == null || transactionID == null || status == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Transactions\n" +
                     "SET price = ?, status = ?\n" +
                     "WHERE id = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setDouble(1, price);
            pstmt.setString(2, status);
            pstmt.setInt(3, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new ProductsPerSale table
     ** EZTicketEntry (String barCode, String productDescription, int amount, double pricePerUnit, double discountRate)
     */
    private void createProductsPerSaleTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new ProductsPerSale table
        String sql = "CREATE TABLE IF NOT EXISTS ProductsPerSale (\n"
                   + " barCode text NOT NULL, \n"
                   + " transactionID integer NOT NULL, \n"
                   + " amount integer, \n"
                   + " discountRate real, \n"
                   + "CONSTRAINT PK_ProductPerSale PRIMARY KEY (barCode, transactionID), \n"
                   + "FOREIGN KEY(barCode) REFERENCES ProductTypes(barCode),  \n"
                   + "FOREIGN KEY(transactionID) REFERENCES Transactions(id) \n"
                   + ");";

        try{
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("createProductsPerSaleTable" + e.getMessage());
        }
    }

    /**
     ** Insert new ProductPerSale record
     */
    public boolean insertProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        if (dbConnection == null || barCode == null || transactionID == null)
            return false;

        boolean inserted = false;
        String sql = "INSERT INTO ProductsPerSale(barCode, transactionID, amount, discountRate) \n"
                   + "VALUES(?,?,?,?);";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
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
    public boolean deleteProductPerSale(String barCode, Integer transactionID) {
        if (dbConnection == null || barCode == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductsPerSale WHERE barCode=? AND transactionID=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setString(1, barCode);
            pstmt.setInt(2, transactionID);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Delete all ProductsPerSale records related to the sale with given transactionID
     */
    public boolean deleteAllProductsPerSale(Integer transactionID) {
        if (dbConnection == null || transactionID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM ProductsPerSale WHERE transactionID=?";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, transactionID);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Update ProductPerSale record
     */
    public boolean updateProductPerSale(String barCode, Integer transactionID, int amount, double discountRate) {
        if (dbConnection == null || transactionID == null || barCode == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE ProductsPerSale\n" +
                     "SET amount = ?, discountRate = ?\n" +
                     "WHERE barCode = ? AND transactionID = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, amount);
            pstmt.setDouble(2, discountRate);
            pstmt.setString(3, barCode);
            pstmt.setInt(4, transactionID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }


    /** ---------------------------------------------------------------------------------------------------------------
     ** Create a new Products table
     ** EZProduct (String RFID, Integer prodTypeID)
     */
    private void createProductsTable() {
        if (dbConnection == null)
            return;

        // SQL statement for creating a new Orders table
        String sql = "CREATE TABLE IF NOT EXISTS Products (\n"
                + " rfid BIGINT PRIMARY KEY,\n"
                + " prodTypeID integer NOT NULL,\n"
                + " saleID integer,\n"
                + " returnID integer\n"
                + ");";

        try{
            Statement stmt = dbConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     ** Insert new Product record
     */
    public boolean insertProduct(Long RFID, Integer prodTypeID, Integer saleID, Integer returnID) {
        if (dbConnection == null || RFID == null || prodTypeID == null)
            return false;

        boolean inserted = false;
        String sql = "INSERT INTO Products(rfid, prodTypeID, saleID, returnID) \n"
                   + "VALUES(?,?,?,?);";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setLong(1, RFID);
            pstmt.setInt(2, prodTypeID);
            pstmt.setInt(3, (saleID != null) ? saleID : defaultID);
            pstmt.setInt(4, (returnID != null) ? returnID : defaultID);
            pstmt.executeUpdate();

            inserted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return inserted;
    }

    /**
     ** Delete Product record with given RFID
     */
    public boolean deleteProduct(Long RFID) {
        if (dbConnection == null || RFID == null)
            return false;

        boolean deleted = false;
        String sql = "DELETE FROM Products WHERE rfid=? ;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setLong(1, RFID);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deleted;
    }

    /**
     ** Select all Customers records
     */
    public HashMap<Long, EZProduct> selectAllProducts() {
        HashMap<Long, EZProduct> products = new HashMap<>();
        String sql = "SELECT rfid, prodTypeID, saleID, returnID \n"
                   + "FROM Products ;";

        try {
            Statement stmt  = dbConnection.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                Long rfid = rs.getLong("rfid");
                Integer prodTypeID = rs.getInt("prodTypeID");
                Integer saleID = rs.getInt("saleID");
                Integer returnID = rs.getInt("returnID");

                String strRFID = String.format("%12d", rfid).replace(' ', '0');
                products.put(rfid, new EZProduct(strRFID, prodTypeID, saleID, returnID));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return products;
    }

    /**
     ** Update Product record
     */
    public boolean updateProduct(Long RFID, Integer prodTypeID, Integer saleID, Integer returnID) {
        if (dbConnection == null || RFID == null || prodTypeID == null)
            return false;

        boolean updated = false;
        String sql = "UPDATE Products\n" +
                "SET prodTypeID = ?, saleID = ?, returnID = ?\n" +
                "WHERE rfid = ?;";

        try{
            PreparedStatement pstmt = dbConnection.prepareStatement(sql);
            pstmt.setInt(1, prodTypeID);
            pstmt.setInt(2, (saleID != null) ? saleID : defaultID);
            pstmt.setInt(3, (returnID != null) ? returnID : defaultID);
            pstmt.setLong(4, RFID);
            pstmt.executeUpdate();
            updated = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updated;
    }
}
